package com.example.chats.chats_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chats.R
import com.example.chats.databinding.FragmentChatListBinding
import com.example.chats.friends.FriendListAdapter
import com.example.chats.viewmodel.ChatViewModel
import com.example.common.result.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ChatListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ChatListFragment : Fragment()
{

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private val viewModel : ChatViewModel by viewModels()

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatListBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        binding.addChat.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_friendsListFragment)
        }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView(){
        lifecycleScope.launch {
            viewModel.conversationList.observe(viewLifecycleOwner){ result ->
                when (result){
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        result.data?.let{ conversations ->
                            chatAdapter = ChatAdapter(conversations){
                                findNavController().navigate(R.id.action_chatListFragment_to_messageFragment)
                            }
                            binding.chatListRecyclerview.apply {
                                layoutManager =  LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL , false)
                                adapter = chatAdapter
                            }
                        }
                    }
                    is Resource.Error -> {}
                }
            }
        }
    }

}