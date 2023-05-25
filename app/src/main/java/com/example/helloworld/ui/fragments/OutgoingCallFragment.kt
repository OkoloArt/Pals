package com.example.helloworld.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.helloworld.databinding.FragmentOutgoingCallBinding
import com.example.helloworld.ui.fragments.ChatFragment.Companion.sinchClient
import com.sinch.android.rtc.calling.Call


/**
 * A simple [Fragment] subclass.
 * Use the [OutgoingCallFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OutgoingCallFragment : BaseFragment() {

    private var _binding : FragmentOutgoingCallBinding? = null
    private val binding get() = _binding!!

    private val safeArgs : OutgoingCallFragmentArgs by navArgs()

    private lateinit var callId: String
    private lateinit var call: Call

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle? , ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOutgoingCallBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        callId = safeArgs.callId
        call = sinchClient?.callController?.getCall(callId)!!

        Toast.makeText(requireContext(), callId, Toast.LENGTH_SHORT).show()

        binding.endCall.setOnClickListener {
            Toast.makeText(requireContext(), call.remoteUserId, Toast.LENGTH_SHORT).show()

        }
    }
}