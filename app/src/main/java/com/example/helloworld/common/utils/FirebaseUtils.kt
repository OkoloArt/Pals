package com.example.helloworld.common.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

object FirebaseUtils {

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val firebaseDatabase : DatabaseReference = Firebase.database.reference
        val storageRef: StorageReference = Firebase.storage.reference;
}