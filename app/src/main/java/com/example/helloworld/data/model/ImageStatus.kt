package com.example.helloworld.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class UserStatus(var name: String? , var status: List<@RawValue ImageStatus>) : Parcelable

data class ImageStatus(var image: String , var date : String)
