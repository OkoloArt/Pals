package com.example.chats.chats_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chats.databinding.ChatListBinding
import com.example.model.Conversations
import com.squareup.picasso.Picasso

class ChatAdapter(private val dataSet: List<Conversations> , private val onItemClicked: (Conversations) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){

    inner class ChatViewHolder(private val binding: ChatListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(conversations: Conversations){
            binding.lastMessage.text = conversations.lastMessage
            binding.receiverName.text = conversations.receiver_name
            binding.unreadCount.text = conversations.unreadMessageCount
            Picasso.get().load(conversations.receiver_image).into(binding.receiverImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ChatViewHolder {
        return ChatViewHolder(ChatListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ChatViewHolder , position: Int) {
        val current = dataSet[position]
        holder.bind(current)
    }

    override fun getItemCount(): Int = dataSet.size

}