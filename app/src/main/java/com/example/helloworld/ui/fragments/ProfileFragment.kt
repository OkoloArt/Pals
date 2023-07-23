package com.example.helloworld.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.helloworld.R
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.datastore.UserPreferences
import com.example.helloworld.common.utils.FirebaseUtils
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.data.model.User
import com.example.helloworld.databinding.FragmentProfileBinding
import com.example.helloworld.ui.viewmodel.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel : ProfileViewModel by viewModels()

    private var imageUri: Uri? = null

    private var dayNightMode = false
    private var receiveNotifications = false

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        updateData()
        profileViewModel.getUser().observe(viewLifecycleOwner){ user ->
            binding.nameTextview.text = user.username
            binding.numberTextView.text = user.number
            if (!TextUtils.isEmpty(user.image)) {
                Picasso.get().load(user.image).into(binding.profileImage)
            } else {
                // Handle the case when user.image is empty or null
                Picasso.get().load(R.drawable.husky).into(binding.profileImage)
            }
            binding.editProfile.setOnClickListener {
                showDialog(user)
            }
        }
        binding.setImage.setOnClickListener {
            setProfileImage()
        }
        saveUserPreferenceData()
    }

    private var pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            imageUri = result.data?.data
            if (imageUri == null) return@registerForActivityResult
            requireActivity().contentResolver.takePersistableUriPermission(imageUri!! , Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    binding.profileImage.setImageURI(imageUri)
            uploadImage(imageUri!!)
        }
    }

    private fun setProfileImage() {
        val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        pickImageLauncher.launch(gallery)
    }

    private fun showDialog(user: User) {
        val view = requireActivity().layoutInflater.inflate(R.layout.update_user_layout, null)

        MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setTitle("Update")
            .setMessage("Do you want to update profile")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Ok") { dialog, _ ->
                user.apply {
                    username = view.findViewById<TextInputLayout>(R.id.name).editText?.text.toString()
                    email = view.findViewById<TextInputLayout>(R.id.email).editText?.text.toString()
                    number = view.findViewById<TextInputLayout>(R.id.mobile_number).editText?.text.toString()
                }
                profileViewModel.updateUser(user)
                dialog.cancel()
            }
            .show()
    }

    private fun uploadImage(image: Uri) {

        val number = Random.nextInt(1000)
        val databaseReference = firebaseDatabase.child(USERS).child(
                firebaseAuth.uid!!).child("image")

        // Create a reference to the image file in Firebase Cloud Storage
        val imageRef = FirebaseUtils.storageRef.child(getUID()!!+number)

        // Upload the image file to Firebase Cloud Storage
        imageRef.putFile(image)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val imageUrl = downloadUrl.toString()
                    // Store the image and date data in Firebase Firestore or Realtime Database, for example
                    // ...r
                    databaseReference.setValue(imageUrl)

                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the upload process
                // ...
            }
    }

    private fun saveUserPreferenceData(){
        binding.apply {
            receiveNotification.setOnCheckedChangeListener { _, isChecked ->
                receiveNotifications = isChecked
                lifecycleScope.launch {
                    userPreferences.saveAllowNotifications(isChecked)
                }
            }

            dayNightSwitch.setOnCheckedChangeListener { _, isChecked ->
                dayNightMode = isChecked
                lifecycleScope.launch {
                    userPreferences.saveDayNightTheme(dayNightMode)
                    AppCompatDelegate.setDefaultNightMode(
                            if (dayNightMode) AppCompatDelegate.MODE_NIGHT_YES
                            else AppCompatDelegate.MODE_NIGHT_NO
                    )
                }
            }
        }
    }

    private fun updateData(){
        userPreferences.allowNotifications.asLiveData().observe(viewLifecycleOwner){ allow ->
            allow?.let {
                receiveNotifications = allow
                binding.receiveNotification.isChecked = receiveNotifications
            }
        }
        userPreferences.dayNightTheme.asLiveData().observe(viewLifecycleOwner){ dayNight ->
            dayNight?.let {
//                dayNightMode = dayNight
                binding.dayNightSwitch.isChecked = dayNight
            }
        }
    }

    private fun getUID(): String? {
        return firebaseAuth.uid
    }


}