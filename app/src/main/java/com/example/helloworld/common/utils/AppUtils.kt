package com.example.helloworld.common.utils

class AppUtil {

    companion object {

        private const val SECOND_MILLIS: Int = 1000
        private const val MINUTE_MILLIS: Int = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS: Int = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS: Int = 24 * HOUR_MILLIS
    }

    fun getTimeAgo(time: Long): String? {

        var time = time

        if (time < 1000000000000L) {
            time *= 1000
        }
        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }

        val diff = now - time

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

}
