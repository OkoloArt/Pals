package com.example.helloworld.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.example.helloworld.data.model.User
import com.example.helloworld.databinding.ContactLayoutBinding
import java.util.*

class ContactAdapter(private val dataSet: ArrayList<User> , private val onItemClicked: (User) -> Unit) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(){

    var initialContact = ArrayList<User>().apply {
        addAll(dataSet)
    }

    inner class ContactViewHolder(private val binding: ContactLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User){
            binding.contactName.text = user.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ContactViewHolder {
        return ContactViewHolder(ContactLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ContactViewHolder , position: Int) {
        val current = dataSet[position]
        holder.bind(current)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    fun getFilter(): Filter {
        return contactFilter
    }

    private val contactFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults
        {
            val filteredList: ArrayList<User> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                initialContact.let { filteredList.addAll(it) }
            } else {
                val query = constraint.toString().trim().lowercase()
                initialContact.forEach { user ->
                    if (user.username!!.lowercase(Locale.ROOT).contains(query)) {
                        filteredList.add(user)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence? , results: FilterResults?) {
            if (results?.values is ArrayList<*>) {
                dataSet.clear()
                dataSet.addAll(results.values as java.util.ArrayList<User>)
                notifyDataSetChanged()
            }
        }
    }
}