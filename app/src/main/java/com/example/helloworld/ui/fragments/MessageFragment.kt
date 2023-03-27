package com.example.helloworld.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.adapter.MessageAdapter
import com.example.helloworld.common.Constants.CHATS
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.data.model.Message
import com.example.helloworld.databinding.FragmentMessageBinding
import com.example.helloworld.ui.viewmodel.MessageViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class MessageFragment : Fragment() {

    private var _binding : FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private val safeArgs : MessageFragmentArgs by navArgs()
    private var chatId : String? = null

    private val messageViewModel : MessageViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMessageBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val receiver = safeArgs.contact

        if (receiver.chatId != null){
            readMessage(receiver.chatId!!)
        }

        binding.sendMessage.setOnClickListener {
            val message = binding.messageEdittext.editText?.text.toString().trim()
            if (message.isBlank()) {
                Toast.makeText(requireContext() , "Enter Message" , Toast.LENGTH_SHORT).show()
            }
            else{
               messageViewModel.sendMessage(message , receiver.userId!!, chatId)
                binding.messageEdittext.editText?.text?.clear()
            }
            it.hideKeyboard()
        }

        bindDetails(receiver.userId!!)

        if (chatId == null)
            messageViewModel.checkChat(receiver.userId!!){
                chatId = it
                readMessage(chatId!!)
            }

        onBackPressed()
    }

    private fun readMessage(chatUid: String){
        val dbQuery = firebaseDatabase.child(CHATS).child(chatUid)
        val firebaseRecyclerOptions = FirebaseRecyclerOptions.Builder<Message>()
            .setLifecycleOwner(this)
            .setQuery(dbQuery, Message::class.java)
            .build()
        dbQuery.keepSynced(true)

        messageAdapter = MessageAdapter(firebaseRecyclerOptions){}
        binding.messageRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
            adapter = messageAdapter
            messageAdapter.startListening()
        }
    }

    private fun bindDetails(member : String){
        messageViewModel.getUser(member){ user ->
            binding.apply {
                receiverName.text = user.username
                receiverStatus.text = user.onlineStatus
                receiverImage.setOnClickListener {
                    val action = MessageFragmentDirections.actionMessageFragmentToContactInfoFragment(user)
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::messageAdapter.isInitialized){
            messageAdapter.stopListening()
        }
    }

    private fun onBackPressed(){
        activity?.onBackPressedDispatcher?.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        val action = MessageFragmentDirections.actionMessageFragmentToChatFragment()
                        findNavController().navigate(action)
                    }
                })
    }

    private fun getUid (): String{
        return firebaseAuth.uid!!
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}

