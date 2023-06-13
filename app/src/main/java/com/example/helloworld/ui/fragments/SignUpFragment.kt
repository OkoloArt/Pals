package com.example.helloworld.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("969470895047-e0lfs8sf9ipb1e3vbgpenmv004s8a6si.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext() , gso)

        binding.apply {
            signUp.setOnClickListener {
                registerUser()
            }
            google.setOnClickListener {

            }
            apple.setOnClickListener {

            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google SignIn Successful
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)

            } catch (e: Exception) {
                //Failed Google Sign In
                Toast.makeText(requireContext(), "SignIn Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken , null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                //Login success
                Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()

                //check if user is new or existing
                if (authResult.additionalUserInfo!!.isNewUser) {
                    Toast.makeText(requireContext(), "Account Created", Toast.LENGTH_SHORT).show()
                }
//                else {
//                    Toast.makeText(requireContext(), "User Already Exists", Toast.LENGTH_SHORT)
//                        .show()
//                }

                // Start Home Fragment
             //   findNavController().navigate(R.id.action_registerFragment2_to_homeFragment)
            }
            .addOnFailureListener {
                // Login Failed
                Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
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
        private const val RC_SIGN_IN = 9001
    }

}