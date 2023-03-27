package com.example.helloworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.BR
import com.example.helloworld.common.Constants.MESSAGE_TYPE_LEFT
import com.example.helloworld.common.Constants.MESSAGE_TYPE_RIGHT
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.data.model.Message
import com.example.helloworld.databinding.MessageLeftBinding
import com.example.helloworld.databinding.MessageRightBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class MessageAdapter(private val options: FirebaseRecyclerOptions<Message> , private val onItemClicked: (Message) -> Unit) :
    FirebaseRecyclerAdapter<Message, MessageAdapter.ViewHolder>(options) {

    class ViewHolder(var view: ViewDataBinding) : RecyclerView.ViewHolder(view.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
        var viewDataBinding: ViewDataBinding? = null
        if (viewType == 0) {
            viewDataBinding = MessageRightBinding.inflate(
                    LayoutInflater.from(parent.context) ,
                    parent ,
                    false
            )
        }
        if (viewType == 1) {
            viewDataBinding = MessageLeftBinding.inflate(
                    LayoutInflater.from(parent.context) ,
                    parent ,
                    false
            )
        }
        return ViewHolder(viewDataBinding!!)
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == firebaseAuth.uid){
            MESSAGE_TYPE_RIGHT
        }else{
            MESSAGE_TYPE_LEFT
        }
    }

    override fun onBindViewHolder(holder: ViewHolder , position: Int , model: Message) {
        if (getItemViewType(position) == MESSAGE_TYPE_RIGHT) {
            holder.view.setVariable(BR.message , model)
        }
        if (getItemViewType(position) == MESSAGE_TYPE_LEFT) {
            holder.view.setVariable(BR.message, model)
        }

    }

}