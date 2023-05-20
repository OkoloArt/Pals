package com.sinch.android.rtc.sample.push

import android.util.Base64
import com.sinch.android.rtc.sample.push.Hmac.hmacSha256
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * IMPORTANT
 * The JWT class serves as an example of how to produce and sign the registration token that
 * you need to initiate SinchClient, or UserController. Read more in the documentation online.
 *
 * DO NOT use this class in your application, instead implement the same functionality on
 * your backend.
 * It might be tempting to re-use this class and store the APPLICATION_SECRET in your app,
 * but that would greatly compromise security.
 *
 */
object JWT {

    fun create(appKey: String, appSecret: String?, userId: String): String {
        val header = JSONObject()
        val payload = JSONObject()
        val issuedAt = System.currentTimeMillis() / 1000
        val kid = "hkdfv1-" + formatDate(issuedAt)
        header.apply {
            put("alg", "HS256")
            put("typ", "JWT")
            put("kid", kid)
        }
        payload.apply {
            put("iss", "//rtc.sinch.com/applications/$appKey")
            put("sub", "//rtc.sinch.com/applications/$appKey/users/$userId")
            put("iat", issuedAt)
            put("exp", issuedAt + 600)
            put("nonce", UUID.randomUUID())
        }
        val headerStr = header.toString().trim { it <= ' ' }.replace("\\/", "/")
        val payloadStr = payload.toString().trim { it <= ' ' }.replace("\\/", "/")
        val headerBase64 = Base64.encodeToString(
            headerStr.toByteArray(),
            Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE
        )
        val payloadBase64 = Base64.encodeToString(
            payloadStr.toByteArray(),
            Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE
        )
        val jwtToSign = "$headerBase64.$payloadBase64"

        val origKey = Base64.decode(appSecret, Base64.DEFAULT)
        val signingKey = deriveSigningKey(origKey, issuedAt)
        val macData = hmacSha256(signingKey, jwtToSign)
        val signature = Base64.encodeToString(
            macData,
            Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE
        )
        return "$jwtToSign.$signature"
    }

    private fun formatDate(time: Long): String {
        val format = "yyyyMMdd"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(time * 1000))
    }

    private fun deriveSigningKey(key: ByteArray, issuedAt: Long): ByteArray {
        return hmacSha256(key, formatDate(issuedAt))
    }

}