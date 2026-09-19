package com.example.billing.security

import android.util.Base64
import android.util.Log
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

/**
 * Cafe Bazaar Purchase Verification Security Helper.
 * Verifies purchase signatures cryptographically using the Cafe Bazaar RSA Public Key.
 * Protects against client-side billing tampering and spoofing.
 */
object SecurityHelper {
    private const val TAG = "BazaarSecurity"
    private const val KEY_FACTORY_ALGORITHM = "RSA"
    private const val SIGNATURE_ALGORITHM = "SHA1withRSA"

    fun verifyPurchase(base64PublicKey: String, signedData: String, signature: String): Boolean {
        if (base64PublicKey.isBlank() || signedData.isBlank() || signature.isBlank()) {
            Log.e(TAG, "Purchase verification failed: null or blank parameters")
            return false
        }
        return try {
            val key = generatePublicKey(base64PublicKey) ?: return false
            verify(key, signedData, signature)
        } catch (e: Exception) {
            Log.e(TAG, "Purchase verification exception: ${e.message}")
            false
        }
    }

    private fun generatePublicKey(encodedPublicKey: String): PublicKey? {
        return try {
            // استفاده از NO_WRAP برای جلوگیری از خطاهای احتمالی کاراکترهای کنترلی در Base64
            val decodedKey = Base64.decode(encodedPublicKey, Base64.NO_WRAP)
            val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
            keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Base64 decoding failed for public key: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error generating public key from spec: ${e.message}")
            null
        }
    }

    private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
        return try {
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray(Charsets.UTF_8))
            
            val signatureBytes = Base64.decode(signature, Base64.NO_WRAP)
            signatureAlgorithm.verify(signatureBytes)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Base64 decoding failed for signature bytes: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Signature verification error: ${e.message}")
            false
        }
    }
}
