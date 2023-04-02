package com.example.helloworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.databinding.StatusItemLayoutBinding

class StatusAdapter(private val onItemClicked : (String) -> Unit):RecyclerView.Adapter<StatusAdapter.StatusViewHolder>(){

    class StatusViewHolder(private val bindStatusItemLayoutBinding: StatusItemLayoutBinding): RecyclerView.ViewHolder(bindStatusItemLayoutBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): StatusViewHolder {
        val statusItemLayoutBinding = StatusItemLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return StatusViewHolder(statusItemLayoutBinding)
    }

    override fun onBindViewHolder(holder: StatusViewHolder , position: Int) {
        holder.itemView.setOnClickListener {
            onItemClicked("")
        }
    }

    override fun getItemCount(): Int = 10
}