package com.example.chats.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chats.databinding.MessageListBinding
import com.example.model.Message

class MessageAdapter(private val dataSet: List<Message> , private val onItemClicked: (Message) -> Unit) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>()
{

    inner class MessageViewHolder(private val binding: MessageListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            if (message.receiver!!.isBlank()){
                binding.receiverMessage.visibility = View.GONE
            }else if (message.sender!!.isBlank()){
                binding.senderMessage.visibility = View.GONE
            }
            binding.receiverMessage.text = message.receiver
            binding.senderMessage.text = message.sender
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): MessageViewHolder {
        return MessageViewHolder(MessageListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MessageViewHolder , position: Int)
    {
        val current = dataSet[position]
        holder.bind(current)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
    }

    override fun getItemCount(): Int = dataSet.size
}