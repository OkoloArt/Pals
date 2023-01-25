package com.example.authentication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.example.authentication.R
import com.example.authentication.databinding.FragmentSignUpBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment()
{
    private var _binding : FragmentSignUpBinding? = null
    private val binding get() = _binding!!

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
            signUpTapped()
        }
    }

    private fun signUpTapped() {
        val user = User()
        user.uid = "08144643504"
        user.name = "Okolo"
        registerUser(user)
    }


    private fun registerUser(user: User) {
        CometChat.createUser(user , "87332f07829cb386f05f3695529fe06966bda1c1" , object : CometChat.CallbackListener<User>() {
            override fun onSuccess(user: User) {
                login(user)
            }
            override fun onError(e: CometChatException) {
                Toast.makeText(requireContext() , e.localizedMessage , Toast.LENGTH_LONG).show()

            }
        })
    }

    private fun login(user: User) {
        CometChat.login(user.uid , "87332f07829cb386f05f3695529fe06966bda1c1" , object : CometChat.CallbackListener<User?>() {
            override fun onSuccess(user: User?) {
                Toast.makeText(requireContext() , user!!.uid , Toast.LENGTH_LONG).show()

            }
            override fun onError(e: CometChatException) {
                Toast.makeText(requireContext() , e.localizedMessage , Toast.LENGTH_LONG).show()
            }
        })
    }
}