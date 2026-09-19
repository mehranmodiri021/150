package com.example.billing

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.android.vending.billing.IInAppBillingService
import com.example.billing.security.SecurityHelper
import com.example.config.BazaarConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

sealed class PurchaseResult {
    // بازگرداندن PendingIntent واقعی برای اجرا در Activity (اصلاح مشکل عدم نمایش صفحه پرداخت)
    data class LaunchIntent(val pendingIntent: PendingIntent, val payload: String) : PurchaseResult()
    data class Success(val productId: String, val purchaseToken: String, val payload: String) : PurchaseResult()
    data class Error(val errorType: BillingError, val userFriendlyMessage: String) : PurchaseResult()
    data object Canceled : PurchaseResult()
}

enum class BillingError {
    NO_INTERNET,
    SERVICE_DISCONNECTED,
    BILLING_UNAVAILABLE,
    DEVELOPER_ERROR,
    USER_CANCELED,
    ITEM_ALREADY_OWNED,
    VERIFICATION_FAILED,
    RESTORE_FAILED,
    UNKNOWN
}

sealed class BillingState {
    data object Idle : BillingState()
    data class Connecting(val notice: String) : BillingState()
    data class Connected(val isSupported: Boolean) : BillingState()
    data class Purchasing(val productId: String) : BillingState()
    data class PurchaseSuccess(val productId: String, val purchaseToken: String) : BillingState()
    data class PurchaseFailed(val errorType: BillingError, val message: String) : BillingState()
    data class Restored(val activePurchases: List<String>) : BillingState()
}

/**
 * BazaarBillingManager handles real communication with Cafe Bazaar In-App Billing Service
 * via official AIDL (IInAppBillingService) and cryptographic signature verification.
 */
class BazaarBillingManager private constructor(private val appContext: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _billingState = MutableStateFlow<BillingState>(BillingState.Idle)
    val billingState: StateFlow<BillingState> = _billingState.asStateFlow()

    private var inAppBillingService: IInAppBillingService? = null
    private var isBound = false
    private var isConnected = false

    val productPrices: Map<String, String> = mapOf(
        BazaarConfig.PRODUCT_ID_VIP_MONTHLY to "۴۹,۰۰۰ تومان",
        BazaarConfig.PRODUCT_ID_VIP_YEARLY to "۲۹۹,۰۰۰ تومان",
        BazaarConfig.PRODUCT_ID_COINS_PACK_SMALL to "۱۹,۰۰۰ تومان",
        BazaarConfig.PRODUCT_ID_COINS_PACK_LARGE to "۵۹,۰۰۰ تومان",
        BazaarConfig.PRODUCT_ID_TICKETS_PACK to "۱۵,۰۰۰ تومان"
    )

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Cafe Bazaar Billing Service connected.")
            inAppBillingService = IInAppBillingService.Stub.asInterface(service)
            isConnected = true
            scope.launch(Dispatchers.IO) {
                val supported = checkBillingSupported()
                withContext(Dispatchers.Main) {
                    _billingState.value = BillingState.Connected(isSupported = supported)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.w(TAG, "Cafe Bazaar Billing Service disconnected.")
            inAppBillingService = null
            isConnected = false
            _billingState.value = BillingState.Idle
        }
    }

    init {
        initializeConnection()
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun checkBillingSupported(): Boolean {
        val service = inAppBillingService ?: return false
        return try {
            val response = service.isBillingSupported(3, appContext.packageName, "inapp")
            response == 0
        } catch (e: Exception) {
            Log.e(TAG, "Failed checking billing support: ${e.message}")
            false
        }
    }

    fun initializeConnection(onConnected: (() -> Unit)? = null) {
        if (!isNetworkAvailable()) {
            _billingState.value = BillingState.PurchaseFailed(
                BillingError.NO_INTERNET,
                "عدم دسترسی به اینترنت. لطفاً اتصال اینترنت خود را بررسی کنید."
            )
            return
        }

        _billingState.value = BillingState.Connecting("در حال اتصال به سرویس پرداخت کافه‌بازار...")

        try {
            val serviceIntent = Intent("ir.cafebazaar.pardakht.InAppBillingService.BIND").apply {
                setPackage("com.farsitel.bazaar")
            }
            val bound = appContext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            isBound = bound
            if (!bound) {
                Log.w(TAG, "Could not bind to Cafe Bazaar service. Cafe Bazaar may not be installed.")
                _billingState.value = BillingState.PurchaseFailed(
                    BillingError.BILLING_UNAVAILABLE,
                    "برنامه کافه‌بازار بر روی دستگاه نصب نیست یا سرویس پرداخت در دسترس نمی‌باشد."
                )
            } else {
                onConnected?.invoke()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception binding Cafe Bazaar billing service: ${e.message}")
            _billingState.value = BillingState.PurchaseFailed(
                BillingError.BILLING_UNAVAILABLE,
                "خطا در اتصال به کافه‌بازار: ${e.message}"
            )
        }
    }

    /**
     * Obtains real buy intent from Cafe Bazaar in-app billing service.
     */
    suspend fun getBuyIntentBundle(productId: String, developerPayload: String): Bundle? {
        val service = inAppBillingService ?: return null
        return withContext(Dispatchers.IO) {
            try {
                service.getBuyIntent(3, appContext.packageName, productId, "inapp", developerPayload)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting buy intent: ${e.message}")
                null
            }
        }
    }

    /**
     * Launches real purchase flow and returns PendingIntent to be launched by Activity.
     */
    suspend fun launchPurchase(
        productId: String,
        onResult: (PurchaseResult) -> Unit
    ) {
        if (!isNetworkAvailable()) {
            val err = PurchaseResult.Error(
                BillingError.NO_INTERNET,
                "اتصال اینترنت برقرار نیست."
            )
            _billingState.value = BillingState.PurchaseFailed(BillingError.NO_INTERNET, "اتصال اینترنت برقرار نیست.")
            onResult(err)
            return
        }

        if (!isConnected || inAppBillingService == null) {
            initializeConnection()
            if (inAppBillingService == null) {
                val err = PurchaseResult.Error(
                    BillingError.BILLING_UNAVAILABLE,
                    "سرویس پرداخت کافه‌بازار در دسترس نیست. لطفاً نصب بودن کافه‌بازار را بررسی کنید."
                )
                _billingState.value = BillingState.PurchaseFailed(
                    BillingError.BILLING_UNAVAILABLE,
                    "سرویس پرداخت کافه‌بازار در دسترس نیست."
                )
                onResult(err)
                return
            }
        }

        _billingState.value = BillingState.Purchasing(productId)

        withContext(Dispatchers.IO) {
            try {
                val service = inAppBillingService
                if (service == null) {
                    withContext(Dispatchers.Main) {
                        onResult(PurchaseResult.Error(BillingError.SERVICE_DISCONNECTED, "سرویس کافه‌بازار قطع شد."))
                    }
                    return@withContext
                }

                val payload = "payload_${System.currentTimeMillis()}_$productId"
                val buyIntentBundle = service.getBuyIntent(3, appContext.packageName, productId, "inapp", payload)
                val responseCode = buyIntentBundle?.getInt("RESPONSE_CODE", -1) ?: -1

                if (responseCode == 0) {
                    val pendingIntent = buyIntentBundle?.getParcelable<PendingIntent>("BUY_INTENT")
                    if (pendingIntent != null) {
                        Log.d(TAG, "Real Cafe Bazaar BuyIntent received successfully for $productId")
                        withContext(Dispatchers.Main) {
                            // بازگرداندن PendingIntent به اکتیویتی جهت اجرای startIntentSenderForResult
                            onResult(PurchaseResult.LaunchIntent(pendingIntent, payload))
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            _billingState.value = BillingState.PurchaseFailed(
                                BillingError.BILLING_UNAVAILABLE,
                                "خطا در دریافت اطلاعات پرداخت از کافه‌بازار."
                            )
                            onResult(PurchaseResult.Error(BillingError.BILLING_UNAVAILABLE, "خطا در دریافت اطلاعات پرداخت."))
                        }
                    }
                } else if (responseCode == 7) {
                    // Item already owned - restore it securely
                    Log.d(TAG, "Item already owned: $productId. Restoring...")
                    restorePurchases { success, purchases, _ ->
                        if (success && purchases.contains(productId)) {
                            _billingState.value = BillingState.PurchaseSuccess(productId, "restored_owned")
                            onResult(PurchaseResult.Success(productId, "restored_owned", payload))
                        } else {
                            onResult(PurchaseResult.Error(BillingError.ITEM_ALREADY_OWNED, "این محصول قبلاً خریداری شده است اما در بازیابی تأیید نشد."))
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _billingState.value = BillingState.PurchaseFailed(
                            BillingError.UNKNOWN,
                            "خطای پرداخت کافه‌بازار (کد: $responseCode)"
                        )
                        onResult(PurchaseResult.Error(BillingError.UNKNOWN, "خطای پرداخت کافه‌بازار (کد: $responseCode)"))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error executing real purchase: ${e.message}")
                withContext(Dispatchers.Main) {
                    _billingState.value = BillingState.PurchaseFailed(BillingError.UNKNOWN, "خطا در برقراری ارتباط با کافه‌بازار.")
                    onResult(PurchaseResult.Error(BillingError.UNKNOWN, "خطا در برقراری ارتباط با کافه‌بازار: ${e.message}"))
                }
            }
        }
    }

    /**
     * Real purchase verification and delivery.
     * Validates cryptographic signature using Cafe Bazaar Public Key securely.
     */
    fun handlePurchaseResult(
        purchaseData: String,
        dataSignature: String,
        onResult: (PurchaseResult) -> Unit
    ) {
        if (purchaseData.isBlank() || dataSignature.isBlank()) {
            onResult(PurchaseResult.Error(BillingError.VERIFICATION_FAILED, "اطلاعات امضای خرید نامعتبر است."))
            return
        }

        // بررسی امنیتی RSA: در صورت تنظیم نبودن کلید عمومی، خرید هرگز تأیید نمی‌شود (رفع باگ امنیتی پاسخ جعلی)
        val isVerified = if (BazaarConfig.isConfigured && BazaarConfig.BAZAAR_PUBLIC_KEY.isNotBlank()) {
            SecurityHelper.verifyPurchase(
                base64PublicKey = BazaarConfig.BAZAAR_PUBLIC_KEY,
                signedData = purchaseData,
                signature = dataSignature
            )
        } else {
            Log.e(TAG, "Bazaar public key is missing or not configured! Security verification rejected.")
            false
        }

        if (!isVerified) {
            Log.e(TAG, "Security verification failed for purchase data!")
            _billingState.value = BillingState.PurchaseFailed(
                BillingError.VERIFICATION_FAILED,
                "اعتبارسنجی امنیتی خرید در کافه‌بازار ناموفق بود."
            )
            onResult(PurchaseResult.Error(BillingError.VERIFICATION_FAILED, "اعتبارسنجی امنیتی خرید ناموفق بود."))
            return
        }

        try {
            val json = JSONObject(purchaseData)
            val productId = json.optString("productId")
            val purchaseToken = json.optString("purchaseToken")
            val purchaseState = json.optInt("purchaseState", 0)

            if (purchaseState == 0 && productId.isNotBlank() && purchaseToken.isNotBlank()) {
                _billingState.value = BillingState.PurchaseSuccess(productId, purchaseToken)
                onResult(PurchaseResult.Success(productId, purchaseToken, purchaseData))
            } else {
                onResult(PurchaseResult.Error(BillingError.UNKNOWN, "وضعیت خرید معتبر نیست."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing purchase JSON: ${e.message}")
            onResult(PurchaseResult.Error(BillingError.UNKNOWN, "خطا در پردازش اطلاعات خرید."))
        }
    }

    /**
     * Restores real purchases from Cafe Bazaar.
     * Queries getPurchases AIDL, verifies signatures securely, and extracts active items.
     */
    suspend fun restorePurchases(onResult: (Boolean, List<String>, String?) -> Unit) {
        if (!isNetworkAvailable()) {
            onResult(false, emptyList(), "اتصال اینترنت برقرار نیست.")
            return
        }

        val service = inAppBillingService
        if (service == null) {
            initializeConnection()
            if (inAppBillingService == null) {
                onResult(false, emptyList(), "سرویس کافه‌بازار در دسترس نیست.")
                return
            }
        }

        _billingState.value = BillingState.Connecting("در حال بازیابی خریدهای پیشین از کافه‌بازار...")

        withContext(Dispatchers.IO) {
            try {
                val currentService = inAppBillingService ?: return@withContext
                val ownedBundle = currentService.getPurchases(3, appContext.packageName, "inapp", null)
                val responseCode = ownedBundle.getInt("RESPONSE_CODE", -1)

                if (responseCode == 0) {
                    val ownedSkus = ownedBundle.getStringArrayList("INAPP_PURCHASE_ITEM_LIST") ?: arrayListOf()
                    val purchaseDataList = ownedBundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST") ?: arrayListOf()
                    val signatureList = ownedBundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST") ?: arrayListOf()

                    val verifiedPurchases = mutableListOf<String>()

                    for (i in purchaseDataList.indices) {
                        val data = purchaseDataList[i]
                        val sig = signatureList.getOrNull(i) ?: ""

                        // اعتبارسنجی امنیتی RSA برای اقلام بازیابی‌شده (جلوگیری از دور زدن امنیت)
                        val isVerified = if (BazaarConfig.isConfigured && BazaarConfig.BAZAAR_PUBLIC_KEY.isNotBlank()) {
                            SecurityHelper.verifyPurchase(BazaarConfig.BAZAAR_PUBLIC_KEY, data, sig)
                        } else {
                            false
                        }

                        if (isVerified) {
                            val json = JSONObject(data)
                            val sku = json.optString("productId")
                            val state = json.optInt("purchaseState", 0)
                            if (state == 0 && sku.isNotBlank()) {
                                verifiedPurchases.add(sku)
                            }
                        }
                    }

                    _billingState.value = BillingState.Restored(verifiedPurchases)
                    withContext(Dispatchers.Main) {
                        onResult(true, verifiedPurchases, null)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(false, emptyList(), "خطا در استعلام خریدهای کافه‌بازار (کد: $responseCode)")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error restoring purchases: ${e.message}")
                withContext(Dispatchers.Main) {
                    onResult(false, emptyList(), "خطا در بازیابی خریدها: ${e.message}")
                }
            }
        }
    }

    suspend fun consumePurchase(purchaseToken: String): Boolean {
        val service = inAppBillingService ?: return false
        return withContext(Dispatchers.IO) {
            try {
                val response = service.consumePurchase(3, appContext.packageName, purchaseToken)
                response == 0
            } catch (e: Exception) {
                Log.e(TAG, "Error consuming purchase: ${e.message}")
                false
            }
        }
    }

    fun release() {
        if (isBound) {
            try {
                appContext.unbindService(serviceConnection)
                isBound = false
                isConnected = false
                inAppBillingService = null
            } catch (e: Exception) {
                Log.e(TAG, "Error unbinding Bazaar service: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "BazaarBillingManager"

        @Volatile
        private var INSTANCE: BazaarBillingManager? = null

        fun getInstance(context: Context): BazaarBillingManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BazaarBillingManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

