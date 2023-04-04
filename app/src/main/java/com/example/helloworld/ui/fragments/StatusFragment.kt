package com.example.helloworld.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.helloworld.adapter.StoriesAdapter
import com.example.helloworld.data.model.ImageStatus
import com.example.helloworld.data.model.UserStatus
import com.example.helloworld.databinding.FragmentStatusBinding

/**
 * A simple [Fragment] subclass.
 * Use the [StatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatusFragment : Fragment(){

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: StoriesAdapter

    // Retrieve the arguments passed from ChatFragment
    private val args: StatusFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View?
    {
        // Inflate the layout for this fragment
        _binding = FragmentStatusBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        loadCards()
    }

    private fun loadCards() {

        val itemPosition = args.itemPosition
        val userStatus = args.status.toList()

        val viewPager = binding.viewPager
        val pagerAdapter = StoriesAdapter(requireContext(), userStatus, findNavController(), viewPager)
        viewPager.adapter = pagerAdapter
        viewPager.setCurrentItem(itemPosition, true)
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
                ImageStatus("https://images.unsplash.com/photo-1563889362352-b0492c224f62?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80" , "2022-01-01") ,
                ImageStatus("https://images.unsplash.com/photo-1547407139-3c921a66005c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80" , "2022-01-02")
        )
        )

        val user3 = UserStatus(
                "Death" , listOf(
                ImageStatus("https://images.unsplash.com/photo-1563889362352-b0492c224f62?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80" , "2022-01-01") ,
                ImageStatus("https://images.unsplash.com/photo-1547407139-3c921a66005c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80" , "2022-01-02")
        )
        )

        return listOf(user1 , user2, user3)
    }

}
