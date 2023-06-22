package com.example.helloworld.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.data.model.ImageStatus
import com.example.helloworld.data.model.User
import com.example.helloworld.databinding.FragmentSignUpBinding
import com.example.helloworld.ui.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient

    private var _binding : FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle? , ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        FirebaseApp.initializeApp(requireContext())

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1066930205282-ja9q0j52ujo3nlkehaq8jdh03o0v5rkf.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext() , gso)

        binding.apply {
            signUp.setOnClickListener {
                registerUser()
            }
            google.setOnClickListener {
                signInWithGoogle()
            }
            apple.setOnClickListener {

            }
        }
    }

    // onActivityResult() function : this is where
    // we provide the task and data for the Google Account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                // Get user information
                val displayName = account.displayName
                val email = account.email

                uploadData(email!!,displayName!!,"")
                val action = SignUpFragmentDirections.actionSignUpFragmentToChatFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun registerUser(){
        val userEmail = binding.email.editText?.text.toString()
        val userName = binding.username.editText?.text.toString()
        val userPassword = binding.password.editText?.text.toString()
        val userMobile = binding.mobile.editText?.text.toString()
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
                    uploadData(userEmail,userName,userMobile)
                    val action = SignUpFragmentDirections.actionSignUpFragmentToChatFragment()
                    findNavController().navigate(action)
                }else{
                    Toast.makeText(requireContext() , it.exception?.message, Toast.LENGTH_SHORT).show()

                }
            }
        }

    }

    private fun uploadData(email:String, username: String, mobile:String){

        val imageStatus: MutableList<ImageStatus> = mutableListOf()

        val user = User("" , email , username , "" , "online" , false ,imageStatus , "" , "" , mobile)
        userViewModel.uploadData(user)
    }


    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 1
    }

}