package com.example.helloworld

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.example.helloworld.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _ , nd: NavDestination , _ ->
            // the IDs of fragments as defined in the `navigation_graph`
                if (nd.id == com.example.authentication.R.id.signUpFragment || nd.id == com.example.chats.R.id.chatListFragment || nd.id == com.example.settings.R.id.profileFragment) {
                    Handler(Looper.getMainLooper()).postDelayed(
                            { binding.bottomNavigationView.visibility = View.VISIBLE
                                supportActionBar?.show()} , 200)
                } else {
                    binding.bottomNavigationView.visibility = View.GONE
                }
        }

    }

}