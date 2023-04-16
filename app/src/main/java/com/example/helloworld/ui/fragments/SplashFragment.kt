package com.example.helloworld.ui.fragments

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.helloworld.R
import com.example.helloworld.common.Constants
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.utils.FirebaseUtils
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.common.utils.FirebaseUtils.firebaseUser
import com.example.helloworld.databinding.FragmentSplashBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment()
{

    private var _binding : FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View?
    {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)


        binding.splashImage.addAnimatorListener(object : AnimatorListener{
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                goToNextScreen()
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })

    }

    private fun createToken(token: String) {
        val databaseReference = firebaseDatabase.child(USERS).child(
                firebaseAuth.uid!!).child("token")
        databaseReference.setValue(token)
    }

    private fun goToNextScreen(){
        if (firebaseUser != null) {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("TAG" , "getInstanceId failed" , task.exception)
                        return@OnCompleteListener
                    }
                    // Get new Instance ID token
                    val token = task.result
                    createToken(token)
                })
            findNavController().navigate(R.id.action_splashFragment_to_chatFragment)
        } else{
            findNavController().navigate(R.id.action_splashFragment_to_signUpFragment)
        }
    }
}