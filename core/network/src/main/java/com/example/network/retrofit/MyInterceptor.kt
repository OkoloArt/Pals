package com.example.network.retrofit

import com.example.common.Constants
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class MyInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val apiKey = runBlocking { Constants.API_KEY
        }
        val request = chain.request()
            .newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("apiKey" , apiKey)
            .addHeader("onBehalfOf","09121338526")
            .addHeader("accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}