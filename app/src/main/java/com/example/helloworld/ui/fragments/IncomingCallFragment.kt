package com.example.helloworld.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.helloworld.common.services.SinchService
import com.example.helloworld.databinding.FragmentCallBinding
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallListener
import com.sinch.android.rtc.sample.push.AudioPlayer

/**
 * A simple [Fragment] subclass.
 * Use the [CallFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IncomingCallFragment : BaseFragment()
{
        private val audioPlayer by lazy {
        AudioPlayer(requireContext())
    }
//    private val callId: String get() = requireActivity().intent.getStringExtra(SinchService.CALL_ID).orEmpty()
//    private val call: Call? get() = sinchServiceInterface?.getCall(callId)

//    private val callId: String? get() = arguments?.getString(SinchService.CALL_ID)
//    private val call: Call? get() = sinchServiceInterface?.getCall(callId!!)

    private val safeArgs : IncomingCallFragmentArgs by navArgs()

    private val action: String? get() = requireActivity().intent.action

    private var _binding : FragmentCallBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle? , ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentCallBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val callSecond = safeArgs.callId

        Toast.makeText(requireContext(),callSecond, Toast.LENGTH_SHORT).show()

    }

//    override fun onServiceConnected() {
//        val call = call
//        if (call != null) {
//            call.addCallListener(SinchCallListener())
//            binding.remoteUser.text = call.remoteUserId
//            if (ACTION_ANSWER == action) {
//                answerClicked()
//            } else if (ACTION_IGNORE == action) {
//                declineClicked()
//            }
//        } else {
//            Log.e(TAG, "Started with invalid callId, aborting")
//            requireActivity().finish()
//        }
//    }
//
//    private fun answerClicked() {
//        val call = call
//      //  audioPlayer.stopRingtone()
//        if (call != null) {
//            Log.d(TAG, "Answering call")
//            call.answer()
//            val intent = Intent(requireContext(), CallScreenFragment::class.java)
//                .apply {
//                    putExtra(SinchService.CALL_ID, callId)
//                }
//            startActivity(intent)
//        } else {
//            requireActivity().finish()
//        }
//    }
//
//    private fun declineClicked() {
//     //   audioPlayer.stopRingtone()
//        call?.hangup()
//        requireActivity().finish()
//    }
//
//    private inner class SinchCallListener : CallListener {
//
//        override fun onCallEnded(call: Call) {
//            val cause = call.details.endCause
//            Log.d(TAG , "com.example.helloworld.common.Call ended, cause: $cause")
//           audioPlayer.stopRingtone()
//            requireActivity().finish()
//        }
//
//        override fun onCallEstablished(call: Call) {
//            Log.d(TAG, "com.example.helloworld.common.Call established")
//        }
//
//        override fun onCallProgressing(call: Call) {
//            Log.d(TAG, "com.example.helloworld.common.Call progressing")
//        }
//    }


    companion object {
        private val TAG: String = IncomingCallFragment::class.java.simpleName

        const val ACTION_ANSWER = "answer"
        const val ACTION_IGNORE = "ignore"
        const val EXTRA_ID = "id"
        const val MESSAGE_ID = 14
    }
}