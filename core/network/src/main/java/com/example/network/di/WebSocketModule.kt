package com.example.network.di

import android.app.Application
import com.example.common.Constants.ECHO_URL
import com.example.network.retrofit.SocketService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.MessageAdapter
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    private val backoffStrategy = ExponentialWithJitterBackoffStrategy(5000 , 5000)

    @Singleton
    @Provides
    fun provideWebSocketService(scarlet: Scarlet): SocketService {
        return scarlet.create(SocketService::class.java)
    }

    @Singleton
    @Provides
    fun provideScarlet(client: OkHttpClient , lifecycle: Lifecycle , streamAdapterFactory: StreamAdapter.Factory, messageAdapter: MessageAdapter.Factory): Scarlet {
        return Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory(ECHO_URL))
            .addMessageAdapterFactory(messageAdapter)
            .lifecycle(lifecycle)
            .backoffStrategy(backoffStrategy)
            .addStreamAdapterFactory(streamAdapterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideLifeCycle(application: Application): Lifecycle = AndroidLifecycle.ofApplicationForeground(application)

    @Singleton
    @Provides
    fun provideStreamAdapterFactory(): StreamAdapter.Factory = RxJava2StreamAdapterFactory()

    @Singleton
    @Provides
    fun provideMoshiMessageAdapter(moshi: Moshi): MessageAdapter.Factory = MoshiMessageAdapter.Factory(moshi)

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}