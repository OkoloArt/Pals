package com.example.helloworld.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.helloworld.data.model.ImageStatus
import com.example.helloworld.data.model.User
import com.example.helloworld.databinding.FragmentProfileBinding
import com.example.helloworld.ui.viewmodel.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

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

        profileViewModel.getUser().observe(viewLifecycleOwner){ user ->
            binding.nameTextview.text = user.username
     //       Toast.makeText(requireContext(), user.imageStatus!!.size.toString(), Toast.LENGTH_SHORT).show()
            binding.editProfile.setOnClickListener {
                showDialog(user)
            }
        }
    }

    private var pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            imageUri = result.data?.data
            if (imageUri == null) return@registerForActivityResult
            requireActivity().contentResolver.takePersistableUriPermission(imageUri!! , Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //        binding.profileImage.setImageURI(imageUri)
//            imageUri?.let { lifecycleScope.launch { userPreferences.saveProfilePic(it.toString()) }}
        }
    }

    private fun setProfileImage() {
        val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT , MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        pickImageLauncher.launch(gallery)
    }

    private fun showDialog(user: User){

//        val newItem = ImageStatus("https://images.unsplash.com/photo-1629246999700-1e7ad7a1ba03?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1612&q=80" , "2023-04-10")
//        user.imageStatus?.add(newItem)
        user.username = "Death"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update")
            .setMessage("Do you want to update profile")
            .setNegativeButton("Cancel") { dialog , _ ->
                // Respond to neutral button press
                dialog.cancel()
            }
            .setPositiveButton("Ok") { dialog , _ ->
                // Respond to positive button press
                profileViewModel.updateUser(user)
                dialog.cancel()
            }
            .show()

    }


}