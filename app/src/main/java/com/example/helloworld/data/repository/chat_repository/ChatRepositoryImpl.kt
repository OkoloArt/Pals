package com.example.helloworld.data.repository.chat_repository

import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.utils.AppUtil
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.data.model.Chat
import com.example.helloworld.data.model.ChatList
import com.example.helloworld.data.model.SecondUser
import com.example.helloworld.data.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor() : ChatRepository {

    val appUtil = AppUtil()
    private val dbChats = firebaseDatabase.child(USERS)

    override fun getChats(chatList: ChatList , callback: (Chat) -> Unit) {
        dbChats.child(chatList.member)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val secondUser = snapshot.getValue(SecondUser::class.java)
                        val date = appUtil.getTimeAgo(chatList.date.toLong())
                        val user = appUtil.mapSecondUserToUser(secondUser!!)
                        val chatModel = Chat(chatID = chatList.chatId , name = user.username , lastMessage = chatList.lastMessage , "" , date , user.online , user.typingStatus)

                        callback(chatModel)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun getUser(chatList: ChatList ,callback: (User) -> Unit) {
        dbChats.child(chatList.member)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val secondUser = snapshot.getValue(SecondUser::class.java)
                        secondUser!!.chatId = chatList.chatId
                        secondUser.userId = snapshot.key
                        val user = appUtil.mapSecondUserToUser(secondUser)
                        callback(user)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}