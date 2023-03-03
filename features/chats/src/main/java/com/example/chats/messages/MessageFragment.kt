package com.example.chats.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chats.databinding.FragmentMessageBinding
import com.example.chats.viewmodel.MessageViewModel
import com.example.common.result.Resource
import com.example.model.Conversations
import com.example.model.Messages
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.tinder.scarlet.Message as MessageScarlet


/**
 * A simple [Fragment] subclass.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class MessageFragment : Fragment() {

    private var _binding : FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private lateinit var messageAdapter: MessageAdapter
    private val messageViewModel : MessageViewModel by viewModels()

    private val safeArgs: MessageFragmentArgs by navArgs()

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

        val receiverDetail = safeArgs.conversation
        setUpRecyclerView()
        setUpReceiverDetails(receiverDetail)
        getEvent()

        binding.sendMessage.setOnClickListener {
            sendMessage()
        }
    }

    private fun setUpRecyclerView(){
        lifecycleScope.launch {
            messageViewModel.getMessages("superhero1")
            messageViewModel.messages.observe(viewLifecycleOwner) { result ->
                when(result){
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        result.data?.let { messages ->
                            messageAdapter = MessageAdapter(messages.toMutableList()){}
                            binding.messageRecyclerView.apply {
                                layoutManager =  LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL , false)
                                scrollToPosition(messages.size-1)
                                setHasFixedSize(true)
                                adapter = messageAdapter
                            }
                        }
                    }
                    is Resource.Error ->{}
                }
            }
        }
    }

    private fun setUpReceiverDetails(conversations: Conversations){
        binding.apply {
            Picasso.get().load(conversations.receiver_image).into(profileImage)
            receiverName.text = conversations.receiver_name
            receiverStatus.text = conversations.status
        }
    }

    private fun sendMessage(){
        val message = binding.messageEdittext.editText?.text.toString()
        lifecycleScope.launch{
            messageViewModel.setValue("superhero1","user","message","text",message)
            messageViewModel.sendMessage().collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        messageAdapter.addItem(Messages(result.data?.message , true, action = false))
                    }
                    is Resource.Error -> {}
                }
            }
        }
    }

    private fun handleOnMessageReceived(message: MessageScarlet) {
        messageAdapter.addItem(Messages(message.toValue() , false , action = false))
    }

    private fun getEvent(){
        lifecycleScope.launch {
            val event = messageViewModel.observeConnection()
            event.onEach { result ->
                when(result){
                    is Resource.Success -> {
                        result.data?.let { event ->
                            handleOnMessageReceived(messageViewModel.handleReceiveMessage(event)!!)
                        }
                    }
                    else -> {}
                }
            }
        }

    }

    private fun MessageScarlet.toValue(): String {
        return when (this) {
            is com.tinder.scarlet.Message.Text -> value
            is com.tinder.scarlet.Message.Bytes -> value.toString()
        }
    }

}