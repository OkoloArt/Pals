package com.example.helloworld.ui.fragments

import android.Manifest
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
import com.example.helloworld.R
import com.example.helloworld.adapter.ChatAdapter
import com.example.helloworld.adapter.StatusAdapter
import com.example.helloworld.common.Constants.CHAT_LIST
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.utils.AppUtil.Companion.getMobileContacts
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.common.utils.FirebaseUtils.storageRef
import com.example.helloworld.data.model.ChatList
import com.example.helloworld.data.model.ImageStatus
import com.example.helloworld.data.model.User
import com.example.helloworld.data.model.UserStatus
import com.example.helloworld.databinding.FragmentChatBinding
import com.example.helloworld.ui.viewmodel.ChatViewModel
import com.example.helloworld.ui.viewmodel.ContactViewModel
import com.example.helloworld.ui.viewmodel.ProfileViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


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

    private var imageUri: Uri? = null

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
        }
        binding.setStatus.setOnClickListener{
            uploadImageStatus()
        }
    }

    private fun setUpStatusRecyclerview() {
        val mobileContacts = getMobileContacts(requireContext())
        val userStatus = mutableSetOf<UserStatus>()
        profileViewModel.getUser().observe(viewLifecycleOwner) { currentUser ->
            contactViewModel.getAppContact(currentUser, mobileContacts) { user ->
                for (i in 0 until user.size) {
                    if (user[i].imageStatus != null && user[i].imageStatus!!.isNotEmpty()) {
                        userStatus.add(UserStatus(user[i].username , user[i].imageStatus!!))
                    }
                }
                val userStatusList = userStatus.toList().distinctBy { it.name }.toMutableList()
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
//            if (user.imageStatus.isNullOrEmpty()){
//                Picasso.get().load(R.drawable.husky).into(binding.status)
//            }
            val fullName = user.username!!.split(" ")
            userStatusName.text = fullName[0]
        }
    }


    private var pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            imageUri = result.data?.data
            if (imageUri == null) return@registerForActivityResult
            requireActivity().contentResolver.takePersistableUriPermission(imageUri!! , Intent.FLAG_GRANT_READ_URI_PERMISSION)
            binding.status.setImageURI(imageUri)
            uploadImage(imageUri!!)

        }
    }

    private fun uploadImageStatus() {
        val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        pickImageLauncher.launch(gallery)
    }

    private fun uploadImages(imageFiles: List<File> , dates: List<Date> , onComplete: () -> Unit) {
        val imageCount = imageFiles.size

        // Iterate over the list of image files and upload each one with its corresponding date
        imageFiles.forEachIndexed { index, file ->
            val imageUri = Uri.fromFile(file)
            val imageName = "image_$index.jpg"
            val date = dates[index]
            val dateStr = date.toString() // You can format the date string as desired

            // Create a reference to the image file in Firebase Cloud Storage
            val imageRef = storageRef!!.child("images/$imageName")

            // Upload the image file to Firebase Cloud Storage
            imageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Get the download URL of the uploaded image
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val imageUrl = downloadUrl.toString()

                        // Create a map to store the image and date data
                        val data = hashMapOf(
                                "image_url" to imageUrl,
                                "date" to dateStr
                        )

                        // Store the image and date data in Firebase Firestore or Realtime Database, for example
                        // ...

                        if (index == imageCount - 1) {
                            onComplete() // Call the onComplete callback when all images have been uploaded
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occur during the upload process
                    // ...
                }
        }
    }

    private fun uploadImage(image: Uri) {

        val number = Random.nextInt(1000)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd" , Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val databaseReference = firebaseDatabase.child(USERS).child(
                firebaseAuth.uid!!).child("imageStatus").push()


        // Create a reference to the image file in Firebase Cloud Storage
        val imageRef = storageRef.child(getUID()!!+number)

        // Upload the image file to Firebase Cloud Storage
        imageRef.putFile(image)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val imageUrl = downloadUrl.toString()
                    // Store the image and date data in Firebase Firestore or Realtime Database, for example
                    // ...r
                    val newImageStatus = ImageStatus(imageUrl , currentDate)
                    databaseReference.setValue(newImageStatus)

                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the upload process
                // ...
            }
    }


}
