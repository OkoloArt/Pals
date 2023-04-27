package com.example.helloworld.data.repository.message_repository

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.helloworld.common.Constants
import com.example.helloworld.common.Constants.CHATS
import com.example.helloworld.common.Constants.CHAT_LIST
import com.example.helloworld.common.Constants.NOTIFICATION_URL
import com.example.helloworld.common.Constants.SERVER_KEY
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.data.model.ChatList
import com.example.helloworld.data.model.Message
import com.example.helloworld.data.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor() : MessageRepository {

    private val userUid = firebaseAuth.uid!!
    private var dbChatList = firebaseDatabase.child(CHAT_LIST)
    private var dbChats = firebaseDatabase.child(CHATS)
    private var dbUser = firebaseDatabase.child(USERS)
    private var currentUser : User? = null

    override fun checkChat(receiverId: String , callback: (String) -> Unit) {
        val databaseReference = dbChatList.child(userUid)
        var chatId: String?
        val query = databaseReference.orderByChild("member").equalTo(receiverId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        val member = ds.child("member").value.toString()
                        if (member == receiverId) {
                            chatId = ds.key
                            callback(chatId!!)
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun createChat(lastMessage: String, receiverId: String){
        val chatId: String?
        var databaseReference = dbChatList.child(userUid)
        chatId = databaseReference.push().key

        val chatListMode = ChatList(chatId!! , lastMessage , System.currentTimeMillis().toString() , receiverId)
        databaseReference.child(chatId).setValue(chatListMode)

        databaseReference = dbChatList.child(receiverId)
        val chatList = ChatList(chatId , lastMessage , System.currentTimeMillis().toString() , userUid)
        databaseReference.child(chatId).setValue(chatList)

        databaseReference = dbChats.child(chatId)
        val messageModel = Message(userUid , receiverId , lastMessage , type = "text")
        databaseReference.push().setValue(messageModel)

    }

    override fun sendMessage(message: String, receiverId: String, chatId: String?) {
        if (chatId == null){
            createChat(message,receiverId)
        }else{
            var databaseReference = dbChats.child(chatId)

            val messageModel = Message(userUid , receiverId , message , System.currentTimeMillis().toString() , "text")

            databaseReference.push().setValue(messageModel)

            val map: MutableMap<String, Any> = HashMap()
            map["lastMessage"] = message
            map["date"] = System.currentTimeMillis().toString()

            databaseReference = dbChatList.child(userUid).child(chatId)

            databaseReference.updateChildren(map)

            databaseReference = dbChatList.child(receiverId).child(chatId)
            databaseReference.updateChildren(map)
            }
        }

    override fun getUser(member : String ,callback: (User) -> Unit) {
        dbUser.child(member)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val user = snapshot.getValue(User::class.java)
//                        user!!.userId = snapshot.key
                        callback(user!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun getCurrentUser(callback: (User) -> Unit) {
            dbUser.child(firebaseAuth.uid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        user?.userId = snapshot.key
                        callback(user!!)
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
    }

    override fun getToken(message: String, receiver : User, sender : User, chatId: String, context: Context) {
        val databaseReference = dbUser.child(receiver.userId!!)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val token = snapshot.child("token").value.toString()

                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("hisId", firebaseAuth.uid)
                    data.put("hisImage", sender.image)
                    data.put("title", sender.username)
                    data.put("message", message)
                    data.put("chatId", chatId)

                    to.put("to", token)
                    to.put("data", data)
                    sendNotification(to, context)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun sendNotification(token: JSONObject, context: Context) {
        val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST ,
                NOTIFICATION_URL ,
                token ,
                Response.Listener { response: JSONObject ->

                    Log.d("TAG" , "onResponse: $response")
                } ,
                Response.ErrorListener {

                    Log.d("TAG" , "onError: $it")
                }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()
                map["Authorization"] = "key=${SERVER_KEY}"
                map["Content-type"] = "application/json"
                return map
            }
            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        request.retryPolicy = DefaultRetryPolicy(
                30000 ,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES ,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }


}