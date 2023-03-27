package com.example.helloworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.data.model.Chat
import com.example.helloworld.data.model.ChatList
import com.example.helloworld.data.model.User
import com.example.helloworld.databinding.ChatItemLayoutBinding
import com.example.helloworld.ui.viewmodel.ChatViewModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class ChatAdapter (options : FirebaseRecyclerOptions<ChatList> ,
                   private val chatViewModel: ChatViewModel,
                   private val onItemClicked: (User) -> Unit) : FirebaseRecyclerAdapter<ChatList, ChatAdapter.ChatViewHolder>(options){

    class ChatViewHolder(var chatItemLayoutBinding: ChatItemLayoutBinding) : RecyclerView.ViewHolder(chatItemLayoutBinding.root) {
        fun bind(chat: Chat){
            chatItemLayoutBinding.chatModel = chat
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ChatViewHolder {
        val chatLayoutBinding = ChatItemLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return ChatViewHolder(chatLayoutBinding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder , position: Int , model: ChatList) {
        chatViewModel.getChats(model){chat ->
            holder.bind(chat)
        }
        chatViewModel.getUser(model){ user ->
            holder.itemView.setOnClickListener {
                onItemClicked(user)
            }
        }
    }

}