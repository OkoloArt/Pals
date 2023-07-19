package com.example.helloworld.ui.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.R
import com.example.helloworld.adapter.MessageAdapter
import com.example.helloworld.common.Constants.CHATS
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.datastore.UserPreferences
import com.example.helloworld.common.services.SinchService
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.data.model.Message
import com.example.helloworld.databinding.FragmentMessageBinding
import com.example.helloworld.ui.fragments.ChatFragment.Companion.sinchClient
import com.example.helloworld.ui.viewmodel.MessageViewModel
import com.example.helloworld.ui.viewmodel.ProfileViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.sinch.android.rtc.calling.MediaConstraints
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class MessageFragment : BaseFragment() {

    private var _binding : FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private val safeArgs : MessageFragmentArgs by navArgs()
    private var chatId : String? = null

    private val messageViewModel : MessageViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentMessageBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val receiver = safeArgs.contact

        bindService()

        if (receiver.chatId != null){
            readMessage(receiver.chatId!!)
        }

        binding.sendMessage.setOnClickListener {
            val message = binding.messageEdittext.editText?.text.toString().trim()
            if (message.isBlank()) {
                Toast.makeText(requireContext() , "Enter Message" , Toast.LENGTH_SHORT).show()
            }
            else{
                messageViewModel.sendMessage(message , receiver.userId!! , chatId)
                messageViewModel.getCurrentUser(){ user ->
                    messageViewModel.getToken(message,receiver,user,chatId!!,requireContext())
                }
                binding.messageEdittext.editText?.text?.clear()
            }
            it.hideKeyboard()
        }
        binding.messageEdittext.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence? , start: Int , count: Int , after: Int) {
                if (s.toString().trim().isEmpty()){
                    updateTypingStatus(false)
                }else{
                    updateTypingStatus(true)
                }
            }

            override fun onTextChanged(s: CharSequence? , start: Int , before: Int , count: Int) {
                updateTypingStatus(true)
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isEmpty()){
                    updateTypingStatus(false)
                }
            }

        })
        bindDetails(receiver.userId!!)

        if (chatId == null)
            messageViewModel.checkChat(receiver.userId!!){
                chatId = it
                readMessage(chatId!!)
            }

        onBackPressed()
      //  sinchClient!!.callController.addCallControllerListener(ChatFragment.Companion.SinchCallControllerListener(requireContext()))
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
                if (user.online.equals("online")){
                    receiverStatus.setTextColor(Color.parseColor("#00FF00"))
                }else {
                    receiverStatus.setTextColor(Color.parseColor("#FF0000"))
                }
                receiverStatus.text = user.online
                receiverImage.setOnClickListener { view ->
                    // Initializing the popup menu and giving the reference as current context
                    val popupMenu = PopupMenu(requireContext(),view )

                    // Inflating popup menu from popup_menu.xml file
                    popupMenu.menuInflater.inflate(R.menu.call_menu , popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when(menuItem.itemId){
                            R.id.profile -> {
                                val action = MessageFragmentDirections.actionMessageFragmentToContactInfoFragment(user)
                                findNavController().navigate(action)
                            }
                            R.id.audio_Call -> {
                                makeAudioCall(user.userId!!)
                            }
                            R.id.video_call -> {
                                Toast.makeText(requireContext(),menuItem.title, Toast.LENGTH_SHORT).show()
                            }
                        }
                        true
                    }
                    // Showing the popup menu
                    popupMenu.show()
                }
                if (user.typingStatus == true) {
                    animationView.visibility = View.VISIBLE
                    animationView.playAnimation()
                } else {
                    animationView.cancelAnimation()
                    animationView.visibility = View.GONE
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

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun updateTypingStatus(status: Boolean) {
        val databaseReference = firebaseDatabase.child(USERS).child(
                firebaseAuth.uid!!).child("typingStatus")
        databaseReference.setValue(status)
    }

    override fun onResume() {
        super.onResume()
        updateOnlineStatus("online")
    }

    private fun updateOnlineStatus(status: String) {
        val databaseReference = firebaseDatabase.child(USERS).child(
                firebaseAuth.uid!!).child("online")
        databaseReference.setValue(status)
    }

    private fun logoutButtonClicked() {
        sinchServiceInterface?.unregisterPushToken()
        sinchServiceInterface?.stopClient()
    }

    private fun makeAudioCall(userName: String) {
        if (sinchServiceInterface != null) {
            val call = sinchClient?.callController?.callUser(userName , MediaConstraints(false))
            val callId = call?.callId
            if (callId != null) {
                val action = MessageFragmentDirections.actionMessageFragmentToOutgoingCallFragment(callId)
                findNavController().navigate(action)
            } else {
                Log.e(TAG , "Failed to initiate call, callId is null")
            }
        } else {
            Toast.makeText(requireContext(), "Service not connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindService() {
        val serviceIntent = Intent(requireContext(), SinchService::class.java)
        requireActivity().applicationContext.bindService(serviceIntent , this , Context.BIND_AUTO_CREATE)

    }

    companion object{
        private val TAG = MessageFragment::class.java.simpleName
    }



}

