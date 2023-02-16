package com.example.chats.messages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chats.R
import com.example.chats.databinding.FragmentMessageBinding


/**
 * A simple [Fragment] subclass.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessageFragment : Fragment() {

    private var _binding : FragmentMessageBinding? = null
    private val binding get() = _binding!!

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
    }
}