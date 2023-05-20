package com.example.helloworld.ui.fragments

import androidx.fragment.app.Fragment
import java.util.*



/**
 * A simple [Fragment] subclass.
 * Use the [CallScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CallScreenFragment : BaseFragment() {

//    private val audioPlayer by lazy {
//        AudioPlayer(requireContext())
//    }
//    private var durationTask: UpdateCallDurationTask? = null
//    private var timer: Timer? = null
//
//    private val callId: String get() = requireArguments().getString(SinchService.CALL_ID).orEmpty()
//    private val call: com.example.helloworld.common.Call? get() = sinchServiceInterface?.getCall(callId)
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_call_screen, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        endCallButton.setOnClickListener { endCall() }
//        requireContext().startService(Intent(requireContext() , OngoingCallService::class.java))
//    }
//
//    override fun onServiceConnected() {
//        if (call != null) {
//            call?.addCallListener(SinchCallListener())
////            callerNameTextView.text = call?.remoteUserId
////            callStateTextView.text = call?.state.toString()
//        } else {
//            Log.e(TAG , "Started with invalid callId, aborting.")
//            requireContext().stopService(Intent(requireContext(), OngoingCallService::class.java))
//            requireActivity().finish()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        durationTask?.cancel()
//        timer?.cancel()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        durationTask = UpdateCallDurationTask()
//        timer = Timer().apply {
//            schedule(durationTask, 0, 500)
//        }
//    }
//
//    private fun endCall() {
//        audioPlayer.stopProgressTone()
//        call?.hangup()
//        requireContext().stopService(Intent(requireContext(), OngoingCallService::class.java))
//        requireActivity().finish()
//    }
//
//    private fun formatTimespan(totalSeconds: Int): String {
//        val minutes = (totalSeconds / 60).toLong()
//        val seconds = (totalSeconds % 60).toLong()
//        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
//    }
//
//    private fun updateCallDuration() {
//        callDurationTextView.text = formatTimespan(call?.details?.duration ?: 0)
//    }
//
//    private inner class UpdateCallDurationTask : TimerTask() {
//        override fun run() {
//            requireActivity().runOnUiThread { updateCallDuration() }
//        }
//    }
//
//    private inner class SinchCallListener : CallListener {
//
//        override fun onCallEnded(call: com.example.helloworld.common.Call) {
//            val cause = call.details.endCause
//            Log.d(TAG, "com.example.helloworld.common.Call ended. Reason: $cause")
//            audioPlayer.stopProgressTone()
//            requireActivity().volumeControlStream = AudioManager.USE_DEFAULT_STREAM_TYPE
//            val endMsg = "com.example.helloworld.common.Call ended: " + call.details.toString()
//            Toast.makeText(requireContext() , endMsg , Toast.LENGTH_LONG).show()
//            endCall()
//        }
//
//        override fun onCallEstablished(call: com.example.helloworld.common.Call) {
//            Log.d(TAG, "com.example.helloworld.common.Call established")
//            audioPlayer.stopProgressTone()
//            callStateTextView.text = call.state.toString()
//            volumeControlStream = AudioManager.STREAM_VOICE_CALL
//            sinchServiceInterface?.audioController?.disableSpeaker()
//        }
//
//        override fun onCallProgressing(call: com.example.helloworld.common.Call) {
//            Log.d(TAG, "com.example.helloworld.common.Call progressing")
//            audioPlayer.playProgressTone()
//        }
//    }
//
//    companion object {
//        val TAG: String = CallScreenFragment::class.java.simpleName
//    }
}
