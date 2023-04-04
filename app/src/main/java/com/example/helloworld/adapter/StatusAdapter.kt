package com.example.helloworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.data.model.UserStatus
import com.example.helloworld.databinding.StatusItemLayoutBinding
import com.squareup.picasso.Picasso

class StatusAdapter(private val users: List<UserStatus> , private val onItemClicked: (Int) -> Unit):RecyclerView.Adapter<StatusAdapter.StatusViewHolder>(){

    class StatusViewHolder(private val bindStatusItemLayoutBinding: StatusItemLayoutBinding): RecyclerView.ViewHolder(bindStatusItemLayoutBinding.root) {
        fun bind(userStatus: UserStatus){
            Picasso.get().load(userStatus.status[0].image).into(bindStatusItemLayoutBinding.status)
            bindStatusItemLayoutBinding.username.text = userStatus.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): StatusViewHolder {
        val statusItemLayoutBinding = StatusItemLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return StatusViewHolder(statusItemLayoutBinding)
    }

    override fun onBindViewHolder(holder: StatusViewHolder , position: Int) {
        val current = users[position]
        holder.bind(current)
        holder.itemView.setOnClickListener {
            onItemClicked(position)
        }
    }

    override fun getItemCount(): Int = users.size
}