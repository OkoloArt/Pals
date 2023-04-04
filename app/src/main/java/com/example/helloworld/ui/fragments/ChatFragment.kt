package com.example.helloworld.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.adapter.ChatAdapter
import com.example.helloworld.adapter.StatusAdapter
import com.example.helloworld.common.Constants.CHAT_LIST
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.data.model.ChatList
import com.example.helloworld.data.model.ImageStatus
import com.example.helloworld.data.model.User
import com.example.helloworld.data.model.UserStatus
import com.example.helloworld.databinding.FragmentChatBinding
import com.example.helloworld.ui.viewmodel.ChatViewModel
import com.example.helloworld.ui.viewmodel.ProfileViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel : ChatViewModel by viewModels()
    private val profileViewModel : ProfileViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var statusAdapter: StatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateOnlineStatus("online")
    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        binding.addMessage.setOnClickListener {
            val action = ChatFragmentDirections.actionChatFragmentToContactsFragment()
            findNavController().navigate(action)
        }
        readChat()
        setUpStatusRecyclerview()

        profileViewModel.getUser().observe(viewLifecycleOwner){ user ->
            bindDetail(user)
        }
    }

    private fun setUpStatusRecyclerview() {
        statusAdapter = StatusAdapter(getUsers()) { num ->
            val action = ChatFragmentDirections.actionChatFragmentToStatusFragment(num , getUsers().toTypedArray())
            findNavController().navigate(action)
        }
        binding.statusRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            setHasFixedSize(true)
            adapter=statusAdapter
        }
    }

    private fun readChat(){

        val dbChats = firebaseDatabase.child(CHAT_LIST).child(getUID()!!)
        val options = FirebaseRecyclerOptions.Builder<ChatList>()
            .setLifecycleOwner(this)
            .setQuery(dbChats, ChatList::class.java)
            .build()

        chatAdapter = ChatAdapter(options, chatViewModel){ user ->
            val action = ChatFragmentDirections.actionChatFragmentToMessageFragment(user)
            findNavController().navigate(action)
        }
        binding.chatRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = chatAdapter
        }

    }

    private fun getUID(): String? {
        return firebaseAuth.uid
    }

    override fun onResume() {
        super.onResume()
        if (this::chatAdapter.isInitialized){
            chatAdapter.startListening()
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::chatAdapter.isInitialized){
            chatAdapter.stopListening()
        }
    }

    private fun updateOnlineStatus(status: String) {
        val databaseReference = firebaseDatabase.child(USERS).child(firebaseAuth.uid!!).child("onlineStatus")
        databaseReference.setValue(status)
    }

    private fun bindDetail(user: User){
        binding.apply {
            username.text = user.username
            userImage.setOnClickListener {
                val action = ChatFragmentDirections.actionChatFragmentToProfileFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun getUsers(): List<UserStatus> {
        // Replace this with your own code to retrieve the list of users
        val user1 = UserStatus(
                "Alice" , listOf(
                ImageStatus("https://images.unsplash.com/photo-1563889362352-b0492c224f62?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80" , "2022-01-01") ,
                ImageStatus("https://images.unsplash.com/photo-1547407139-3c921a66005c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80" , "2022-01-02")
        )
        )

        val user2 = UserStatus(
                "Bob" , listOf(
                ImageStatus("https://images.unsplash.com/photo-1616567214738-22fc0c6332b3?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80" , "2022-01-01") ,
                ImageStatus("https://images.unsplash.com/flagged/photo-1550973078-10a2d124c99c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=685&q=80" , "2022-01-02")
        )
        )

        val user3 = UserStatus(
                "Death" , listOf(
                ImageStatus("https://images.unsplash.com/photo-1561297108-a47d55d96a19?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80" , "2022-01-01") ,
                ImageStatus("https://images.unsplash.com/photo-1580825328373-ee07fad4b195?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80" , "2022-01-02")
        )
        )

        return listOf(user1 , user2, user3)
    }
}
