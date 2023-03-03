package com.example.chats.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chats.R
import com.example.common.Constants.MESSAGE_TYPE_LEFT
import com.example.common.Constants.MESSAGE_TYPE_RIGHT
import com.example.model.Messages

class MessageAdapter(private val dataSet: MutableList<Messages> , private val onItemClicked: (Messages) -> Unit) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private var messageSet = mutableListOf<Messages>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): MessageAdapter.ViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.message_right , parent , false)
            ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.message_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder , position: Int) {
            val current = messageSet[position]
            holder.txtUserName.text = current.message
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position].isFromSender!!){
            MESSAGE_TYPE_RIGHT
        }else{
            MESSAGE_TYPE_LEFT
        }
    }

    override fun getItemCount(): Int {
        messageSet = dataSet
        messageSet.removeIf { it.action!! }
        return messageSet.size
    }

    fun addItem(item: Messages) {
        messageSet.add(item)
        notifyDataSetChanged()
    }
}