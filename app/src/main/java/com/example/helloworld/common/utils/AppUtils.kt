package com.example.helloworld.common.utils

import android.content.Context
import android.provider.ContactsContract
import com.example.helloworld.data.model.SecondUser
import com.example.helloworld.data.model.User

class AppUtil {

    companion object {

        private const val SECOND_MILLIS: Int = 1000
        private const val MINUTE_MILLIS: Int = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS: Int = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS: Int = 24 * HOUR_MILLIS

        @JvmStatic
        fun getMobileContacts(context: Context): List<User> {
            val mobileContacts = mutableListOf<User>()
            context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI ,
                    arrayOf(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    null ,
                    null ,
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    mobileContacts.add(User(username = name, number = number))
                }
            }
            return mobileContacts
        }
    }

    fun getTimeAgo(time: Long): String? {

        var currentTime = time

        if (currentTime < 1000000000000L) {
            currentTime *= 1000
        }
        val now = System.currentTimeMillis()
        if (currentTime > now || currentTime <= 0) {
            return null
        }

        val diff = now - currentTime

        return when {
            diff < MINUTE_MILLIS -> {
                "just now"
            }
            diff < 2 * MINUTE_MILLIS -> {
                "a minute ago"
            }
            diff < 50 * MINUTE_MILLIS -> {
                (diff / MINUTE_MILLIS).toString() + " mins ago"
            }
            diff < 90 * MINUTE_MILLIS -> {
                "an hour ago"
            }
            diff < 24 * HOUR_MILLIS -> {
                (diff / HOUR_MILLIS).toString() + " hrs ago"
            }
            diff < 48 * HOUR_MILLIS -> {
                "yesterday"
            }
            else -> {
                (diff / DAY_MILLIS).toString() + " days ago"
            }
        }
    }

    fun mapSecondUserToUser(secondUser: SecondUser): User {
        return User(
                userId = secondUser.userId,
                email = secondUser.email,
                username = secondUser.username,
                chatId = secondUser.chatId,
                online = secondUser.online,
                typingStatus = secondUser.typingStatus,
                imageStatus = secondUser.imageStatus?.values?.toList()?.toMutableList(),
                status = secondUser.status,
                image = secondUser.image,
                number = secondUser.number,
                token = secondUser.token,
        )
    }

}
