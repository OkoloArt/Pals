package com.example.helloworld.data.repository.user_repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.utils.AppUtil
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.data.model.ImageStatus
import com.example.helloworld.data.model.SecondUser
import com.example.helloworld.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {

    private var liveData : MutableLiveData<User>? = null
    private val dbUser = firebaseDatabase.child(USERS)

    val appUtil = AppUtil()

    override fun createUser(email: String , password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email , password)
    }

    override fun uploadData(user: User) {
        user.userId = firebaseAuth.uid
        dbUser.child(user.userId!!).setValue(user)
    }

    override fun updateUser(user: User) {
     dbUser.child(user.userId!!).setValue(user)
    }

    override fun getUser(): LiveData<User> {
        return if (liveData == null) {
            liveData = MutableLiveData()
            dbUser.child(firebaseAuth.uid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val secondUser = snapshot.getValue(SecondUser::class.java)
                        secondUser!!.userId = snapshot.key
                        val user = appUtil.mapSecondUserToUser(secondUser)
                        liveData!!.value = user
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            liveData!!
        } else {
            liveData!!
        }
    }


    override fun getAppContacts(user: User, mobileContacts: List<User>, callback: (ArrayList<User>) -> Unit) {
        val appContact = ArrayList<User>()
        val query = dbUser.orderByChild("number")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val number = data.child("number").value.toString()
                        mobileContacts.find { it.number == number && it.number != user.number }?.let {
                            val secondUser = data.getValue(SecondUser::class.java)
                            secondUser?.userId = data.key
                            val newUser = appUtil.mapSecondUserToUser(secondUser!!)
                            newUser.let { appContact.add(it) }
                        }
                    }
                    callback(appContact)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}