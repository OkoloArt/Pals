package com.example.helloworld.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.helloworld.common.datastore.UserPreferences
import com.example.helloworld.data.model.ImageStatus
import com.example.helloworld.data.model.User
import com.example.helloworld.databinding.FragmentSignUpBinding
import com.example.helloworld.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class SignUpFragment : Fragment()
{
    private var _binding : FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        binding.signUp.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val userEmail = binding.email.editText?.text.toString()
        val userName = binding.username.editText?.text.toString()
        val userPassword = binding.password.editText?.text.toString()
        if (userEmail.isBlank() && userName.isBlank() && userPassword.isBlank()){
            binding.apply {
                email.editText?.error = "Field cannot be blank"
                username.editText?.error = "Field cannot be blank"
                password.editText?.error = "Field cannot be blank"
            }
        }else{
            val result = userViewModel.createUser(userEmail,userPassword)
            result.addOnCompleteListener {
                if (it.isSuccessful) {
                    uploadData(userEmail,userName)
                    Toast.makeText(requireContext() , " Module Success" , Toast.LENGTH_SHORT).show()
                    val action = SignUpFragmentDirections.actionSignUpFragmentToChatFragment()
                    findNavController().navigate(action)
                }else{
                    Toast.makeText(requireContext() , it.exception?.message, Toast.LENGTH_SHORT).show()

                }
            }
        }

    }

    private fun uploadData(email:String, username: String){

        val imageStatus: MutableList<ImageStatus> = mutableListOf()

        val user = User("" , email , username , "" , "online" , false ,imageStatus , "" , "" , "")
        userViewModel.uploadData(user)
    }

}