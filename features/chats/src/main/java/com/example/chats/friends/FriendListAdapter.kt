package com.example.chats.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chats.databinding.FragmentFriendsListBinding
import com.example.chats.databinding.FriendsListBinding
import com.example.model.Friends
import java.util.ArrayList

class FriendListAdapter(private val dataSet: List<Friends> , private val onItemClicked: (Friends) -> Unit) :
    RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>(){

    inner class FriendViewHolder(private val binding: FriendsListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(friends: Friends){
            binding.friendName.text = friends.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): FriendViewHolder {
        return FriendViewHolder(FriendsListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: FriendViewHolder , position: Int) {
        val current = dataSet[position]
        holder.bind(current)
    }

    override fun getItemCount(): Int = dataSet.size

}