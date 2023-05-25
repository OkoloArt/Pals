package com.example.helloworld.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.adapter.ChatAdapter
import com.example.helloworld.adapter.StatusAdapter
import com.example.helloworld.common.Constants
import com.example.helloworld.common.Constants.APP_KEY
import com.example.helloworld.common.Constants.CHAT_LIST
import com.example.helloworld.common.Constants.ENVIRONMENT
import com.example.helloworld.common.Constants.FCM_SENDER_ID
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.datastore.UserPreferences
import com.example.helloworld.common.services.SinchService
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
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sinch.android.rtc.*
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallController
import com.sinch.android.rtc.calling.CallControllerListener
import com.sinch.android.rtc.sample.push.JWT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.random.Random


/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ChatFragment : BaseFragment(),  SinchService.StartFailedListener,
    PushTokenRegistrationCallback , UserRegistrationCallback {

    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel : ChatViewModel by viewModels()
    private val profileViewModel : ProfileViewModel by viewModels()
    private val contactViewModel : ContactViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var statusAdapter: StatusAdapter

    @Inject
    lateinit var userPreferences: UserPreferences

    private var imageUri: Uri? = null

    companion object{
        var sinchClient : SinchClient? = null
        private val TAG = ChatFragment::class.java.simpleName


        class SinchCallControllerListener(val context: Context) : CallControllerListener {

            override fun onIncomingCall(callController: CallController , call: Call) {
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
            }

            private fun isAppOnForeground(context: Context): Boolean {
                val activityManager = context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
                val appProcesses = activityManager.runningAppProcesses ?: return false
                val packageName = context.packageName
                for (appProcess in appProcesses) {
                    if (appProcess.importance
                        == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && appProcess.processName == packageName
                    ) {
                        return true
                    }
                }
                return false
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle? , ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
       //  setClient()

        createAndStartSinchClient()

        binding.addMessage.setOnClickListener {
            val action = ChatFragmentDirections.actionChatFragmentToContactsFragment()
            findNavController().navigate(action)
        }
        readChat()
        checkRuntimePermissions()

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

    private fun checkRuntimePermissions(){
        Dexter.withContext(requireContext())
            .withPermissions(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS)
            .withListener(object : MultiplePermissionsListener
                          {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        setUpStatusRecyclerview()
                    }
                    if (report.isAnyPermissionPermanentlyDenied){}
                }
                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest> , permissionToken: PermissionToken) {
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(requireContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            .onSameThread().check()
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

    override fun onFailed(error: SinchError) {
        Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onStarted() {
        Toast.makeText(requireContext(),"Client Started Successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onServiceConnected() {
        if (sinchServiceInterface?.isStarted == true) {
            Toast.makeText(requireContext(),"Client Started Successfully", Toast.LENGTH_SHORT).show()
        } else {
            sinchServiceInterface?.setStartListener(this)
        }
    }

    private fun startSinchClient() {
        // Start Sinch Client, it'll result onStarted() callback from where the place call
        // activity will be started
        if (sinchServiceInterface?.isStarted == false) {
            sinchServiceInterface?.startClient()
        }
    }

    private fun setClient(){
        val userId = getUID()!!
        sinchServiceInterface?.username = userId

        val userController = try {
            UserController.builder()
                .context(requireContext())
                .applicationKey(APP_KEY)
                .userId(userId)
                .environmentHost(ENVIRONMENT)
                .build()
        } catch (e: IOException) {
            Log.e(TAG , "Error while building user controller" , e)
            return
        }
        userController.registerUser(this, this)
    }

    override fun onPushTokenRegistered() {
        startSinchClient()
    }

    override fun onPushTokenRegistrationFailed(error: SinchError) {

    }

    private fun createAndStartSinchClient(){
        lifecycleScope.launch {
            userPreferences.token.collect{ token ->

                val userId = firebaseAuth.uid!!
                sinchClient = SinchClient.builder()
                    .context(requireContext())
                    .applicationKey(APP_KEY)
                    .environmentHost("ocra.api.sinch.com")
                    .userId(userId)
                    .pushConfiguration(
                            PushConfiguration.fcmPushConfigurationBuilder()
                                .senderID(FCM_SENDER_ID)
                                .registrationToken(token.orEmpty()).build()
                    )
                    .pushNotificationDisplayName("User $userId")
                    .build()


                sinchClient!!.addSinchClientListener(object: SinchClientListener {
                    override fun onClientStarted(client: SinchClient) { }
                    override fun onClientFailed(client: SinchClient, error: SinchError) { }
                    override fun onCredentialsRequired(clientRegistration: ClientRegistration) {
                        // You have to implement this method, it can't be no-op.
                        clientRegistration.register(JWT.create(APP_KEY , Constants.APP_SECRET , userId.orEmpty()))
                    }
                    override fun onLogMessage(level: Int, area: String, message: String) { }
                    override fun onPushTokenRegistered() {
                    }

                    override fun onPushTokenRegistrationFailed(error: SinchError) {
                        Toast.makeText(requireContext(),"Push Token Failed", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPushTokenUnregistered() {
                    }

                    override fun onPushTokenUnregistrationFailed(error: SinchError) {
                    }

                    override fun onUserRegistered() {
                        Toast.makeText(requireContext(), "User Registered", Toast.LENGTH_LONG).show()
                    }

                    override fun onUserRegistrationFailed(error: SinchError) {

                    }
                })
                sinchClient!!.callController.addCallControllerListener(SinchCallControllerListener(requireContext()))

                sinchClient!!.start()

            }
        }

    }
    override fun onCredentialsRequired(clientRegistration: ClientRegistration) {
        val userId = firebaseAuth.uid!!
        clientRegistration.register(JWT.create(APP_KEY , Constants.APP_SECRET , userId.orEmpty()))
    }

    override fun onUserRegistered() {
        Toast.makeText(requireContext(), "Registration Successful!", Toast.LENGTH_LONG).show()
    }

    override fun onUserRegistrationFailed(error: SinchError) {
        Toast.makeText(requireContext(), "Registration failed!", Toast.LENGTH_LONG).show()
    }

}
