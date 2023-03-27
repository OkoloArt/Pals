package com.example.helloworld

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HelloWorldApplication : Application(){

    override fun onCreate() {
        super.onCreate()
    }
}