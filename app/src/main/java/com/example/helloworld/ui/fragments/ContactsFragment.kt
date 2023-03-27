package com.example.helloworld.ui.fragments

import android.Manifest
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.helloworld.adapter.ContactAdapter
import com.example.helloworld.data.model.User
import com.example.helloworld.databinding.FragmentContactsBinding
import com.example.helloworld.ui.viewmodel.ContactViewModel
import com.example.helloworld.ui.viewmodel.ProfileViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [ContactsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ContactsFragment : Fragment() {

    private var _binding : FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mobileContacts : MutableSet<User>
    private lateinit var contactAdapter: ContactAdapter

    private val contactViewModel : ContactViewModel by viewModels()
    private val profileViewModel : ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContactsBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        checkRuntimePermissions()
        setUpSearchView()
    }

    private fun getMobileContact(){
        mobileContacts = mutableSetOf()
        requireContext().contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
                null ,
                null ,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                mobileContacts.add(User(username = name))
            }
        }
        profileViewModel.getUser().observe(viewLifecycleOwner){ currentUser ->
            contactViewModel.getAppContact(currentUser,mobileContacts.toList()) { user ->
                binding.contactRecyclerview.apply {
                    layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL , false)
                    setHasFixedSize(true)
                    contactAdapter = ContactAdapter(user) {
                        val action = ContactsFragmentDirections.actionContactsFragmentToMessageFragment(it)
                        findNavController().navigate(action)
                    }
                    adapter = contactAdapter
                }
            }
        }
    }

    private fun checkRuntimePermissions(){
        Dexter.withContext(requireContext())
            .withPermissions(Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        getMobileContact()
                    }
                    if (report.isAnyPermissionPermanentlyDenied){}
                }
                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest> , permissionToken: PermissionToken) {
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(requireContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            .onSameThread().check()
    }

    private fun setUpSearchView(){
        binding.searchList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactAdapter.getFilter().filter(newText);
                return true
            }
        })
    }
}