package com.example.helloworld.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.adapter.ChatAdapter
import com.example.helloworld.adapter.StatusAdapter
import com.example.helloworld.common.Constants.CHAT_LIST
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.utils.AppUtil.Companion.getMobileContacts
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.data.model.ChatList
import com.example.helloworld.data.model.ImageStatus
import com.example.helloworld.data.model.User
import com.example.helloworld.data.model.UserStatus
import com.example.helloworld.databinding.FragmentChatBinding
import com.example.helloworld.ui.viewmodel.ChatViewModel
import com.example.helloworld.ui.viewmodel.ContactViewModel
import com.example.helloworld.ui.viewmodel.ProfileViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel : ChatViewModel by viewModels()
    private val profileViewModel : ProfileViewModel by viewModels()
    private val contactViewModel : ContactViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var statusAdapter: StatusAdapter
    private var user : User? = null

    private var imageUri: Uri? = null
    private var imageString : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        binding.addMessage.setOnClickListener {
            val action = ChatFragmentDirections.actionChatFragmentToContactsFragment()
            findNavController().navigate(action)
        }
        readChat()
        setUpStatusRecyclerview()

        profileViewModel.getUser().observe(viewLifecycleOwner){ currentUser ->
            bindDetail(currentUser)
            user = currentUser
        }
        binding.status.setOnClickListener{
            uploadImageStatus()
        }
    }

    private fun setUpStatusRecyclerview() {
        val mobileContacts = getMobileContacts(requireContext())
        val userStatus = mutableSetOf<UserStatus>()
        profileViewModel.getUser().observe(viewLifecycleOwner) { currentUser ->
            contactViewModel.getAppContact(currentUser, mobileContacts) { user ->
                for (i in user.indices) {
                    if (user[i].imageStatus!!.isNotEmpty()) {
                        userStatus.add(UserStatus(user[i].username, user[i].imageStatus!!.toList()))
                    }
                }
                // Move the return statement inside the observe block
                val userStatusList = userStatus.toList()
                statusAdapter = StatusAdapter(userStatusList) { num ->
                    val action = ChatFragmentDirections.actionChatFragmentToStatusFragment(num , userStatusList.toTypedArray())
                    findNavController().navigate(action)
                }
                binding.statusRecyclerview.apply {
                    layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                    setHasFixedSize(true)
                    adapter=statusAdapter
                }
            }
        }
    }

    private fun readChat(){
        val dbChats = firebaseDatabase.child(CHAT_LIST).child(getUID()!!)
        val options = FirebaseRecyclerOptions.Builder<ChatList>()
            .setLifecycleOwner(this)
            .setQuery(dbChats, ChatList::class.java)
            .build()

        chatAdapter = ChatAdapter(options, chatViewModel){ user ->
            val action = ChatFragmentDirections.actionChatFragmentToMessageFragment(user)
            findNavController().navigate(action)
        }
        binding.chatRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = chatAdapter
            itemAnimator = null
        }
    }

    private fun getUID(): String? {
        return firebaseAuth.uid
    }

    override fun onResume() {
        super.onResume()
        if (this::chatAdapter.isInitialized){
            chatAdapter.startListening()
        }
        updateOnlineStatus("online")
    }

    override fun onPause() {
        super.onPause()
        if (this::chatAdapter.isInitialized){
            chatAdapter.stopListening()
        }
        updateOnlineStatus("offline")
    }

    private fun updateOnlineStatus(status: String) {
        val databaseReference = firebaseDatabase.child(USERS).child(
                firebaseAuth.uid!!).child("online")
        databaseReference.setValue(status)
    }

    private fun bindDetail(user: User){
        binding.apply {
            username.text = user.username
            userImage.setOnClickListener {
                val action = ChatFragmentDirections.actionChatFragmentToProfileFragment()
                findNavController().navigate(action)
            }
        }
    }

    private var pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            imageUri = result.data?.data
            if (imageUri == null) return@registerForActivityResult
            requireActivity().contentResolver.takePersistableUriPermission(imageUri!! , Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imageString = imageUri.toString()
            binding.status.setImageURI(imageUri)
            setImageStatus(user!!)
//            imageUri?.let { lifecycleScope.launch { userPreferences.saveProfilePic(it.toString()) }}
        }
    }

    private fun uploadImageStatus() {
        val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        pickImageLauncher.launch(gallery)
    }

    private fun setImageStatus(user: User){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd" , Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val newImage = ImageStatus(imageString!! , currentDate)
        user.imageStatus!!.add(newImage)
        profileViewModel.updateUser(user)
    }

}
