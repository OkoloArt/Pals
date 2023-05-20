package com.sinch.android.rtc.sample.push

import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Hmac {

    fun hmacSha256(key: ByteArray, message: String): ByteArray {
        require(key.isNotEmpty()) { "Invaid input key to HMAC-256" }
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            val keySpec = SecretKeySpec(key, "HmacSHA256")
            mac.init(keySpec)
            mac.doFinal(message.toByteArray(Charsets.UTF_8))
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
    }

}