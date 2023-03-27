package com.example.helloworld

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.helloworld.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _ , nd: NavDestination , _ ->
            // the IDs of fragments as defined in the `navigation_graph`
            if (nd.id == R.id.chatFragment || nd.id == R.id.profileFragment) {
                Handler(Looper.getMainLooper()).postDelayed(
                        { binding.navView.visibility = View.VISIBLE
                            supportActionBar?.show()} , 200)
                when (nd.id) {
                    R.id.chatFragment -> {
                        binding.navView.setItemSelected(R.id.chats,true)
                    }
                    else -> {
                        binding.navView.setItemSelected(R.id.profile,true)
                    }
                }
            } else {
                binding.navView.visibility = View.GONE
            }
        }

        binding.navView.setOnItemSelectedListener { id ->
            when (id) {
                R.id.chats -> {
                    when (findNavController(R.id.nav_host_fragment).currentDestination?.id) {
                        R.id.chatFragment -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_chatFragment_self)
                        }
                        R.id.profileFragment -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_profileFragment_to_chatFragment)
                        }
                    }
                }
                R.id.profile -> {
                    when (findNavController(R.id.nav_host_fragment).currentDestination?.id) {
                        R.id.profileFragment -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_profileFragment_self)
                        }
                        R.id.chatFragment -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.action_chatFragment_to_profileFragment)
                        }
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
      // updateOnlineStatus("Online")
    }

}