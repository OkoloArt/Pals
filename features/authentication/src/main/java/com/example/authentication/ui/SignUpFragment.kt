package com.example.authentication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.example.authentication.R
import com.example.authentication.databinding.FragmentSignUpBinding
import com.example.authentication.viewmodel.CreateUserViewModel
import com.example.common.result.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

    private val viewModel: CreateUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View?
    {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)

        binding.signUp.setOnClickListener {
            val uid = binding.mobileNumber.editText?.text.toString()
            val name = binding.password.editText?.text.toString()

            registerUser(uid, name)
        }
    }

    private fun registerUser(uid:String, name: String){
        viewModel.createUser(uid,name)
       lifecycleScope.launch {
           repeatOnLifecycle(Lifecycle.State.STARTED) {
               viewModel.createUserState.collectLatest { result ->
                   when (result) {
                       is Resource.Loading -> {}

                       is Resource.Success -> {
                           Toast.makeText(requireContext(), "User Created Successfully", Toast.LENGTH_SHORT).show()
                       }

                       is Resource.Error -> {}
                   }
               }
           }
       }
    }
}