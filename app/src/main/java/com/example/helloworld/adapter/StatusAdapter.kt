package com.example.helloworld.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.databinding.StatusItemLayoutBinding

class StatusAdapter():RecyclerView.Adapter<StatusAdapter.StatusViewHolder>(){

    class StatusViewHolder(private val bindStatusItemLayoutBinding: StatusItemLayoutBinding): RecyclerView.ViewHolder(bindStatusItemLayoutBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): StatusViewHolder {
        val statusItemLayoutBinding = StatusItemLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return StatusViewHolder(statusItemLayoutBinding)
    }

    override fun onBindViewHolder(holder: StatusViewHolder , position: Int) {
    }

    override fun getItemCount(): Int = 10
}