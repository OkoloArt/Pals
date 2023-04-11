package com.example.helloworld.data.repository.message_repository

import com.example.helloworld.common.Constants.CHATS
import com.example.helloworld.common.Constants.CHAT_LIST
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.data.model.ChatList
import com.example.helloworld.data.model.Message
import com.example.helloworld.data.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor() : MessageRepository {

    private val userUid = firebaseAuth.uid!!
    private var dbChatList = firebaseDatabase.child(CHAT_LIST)
    private var dbChats = firebaseDatabase.child(CHATS)
    private var dbUser = firebaseDatabase.child(USERS)

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

}