package com.example.helloworld.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
            Toast.makeText(requireContext(), user.userId, Toast.LENGTH_SHORT).show()
            binding.editProfile.setOnClickListener {
                showDialog(user)
            }
        }
    }

    private fun showDialog(user: User){

        user.username = "Mugen Death"
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