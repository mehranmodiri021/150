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
            val key = generatePublicKey(base64PublicKey)
            verify(key, signedData, signature)
        } catch (e: Exception) {
            Log.e(TAG, "Purchase verification exception: ${e.message}")
            false
        }
    }

    private fun generatePublicKey(encodedPublicKey: String): PublicKey {
        val decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT)
        val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
        return keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
    }

    private fun verify(publicKey: PublicKey, signedData: String, signature: String): Boolean {
        return try {
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray(Charsets.UTF_8))
            val signatureBytes = Base64.decode(signature, Base64.DEFAULT)
            signatureAlgorithm.verify(signatureBytes)
        } catch (e: Exception) {
            Log.e(TAG, "Signature verification error: ${e.message}")
            false
        }
    }
}
