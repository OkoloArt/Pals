package com.example.chats.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chats.R
import com.example.chats.databinding.FragmentFriendsListBinding
import com.example.chats.viewmodel.FriendsViewModel
import com.example.common.result.Resource
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [FriendsListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class FriendsListFragment : Fragment()
{
    private var _binding : FragmentFriendsListBinding? = null
    private val binding get() = _binding!!

    private val viewModel : FriendsViewModel by viewModels()

    private lateinit var friendListAdapter: FriendListAdapter

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View
    {
        // Inflate the layout for this fragment
        _binding = FragmentFriendsListBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        setUpRecyclerView()

    }

    private fun setUpRecyclerView(){
        viewModel.friendsList.observe(viewLifecycleOwner){ result ->
            when (result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    result.data?.let { friends ->
                        friendListAdapter = FriendListAdapter(friends){}
                        binding.friendsListRecyclerview.apply {
                            layoutManager =  LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL , false)
                            adapter = friendListAdapter
                        }
                    }
                }
                is Resource.Error -> {}
            }
        }
    }
}