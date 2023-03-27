package com.example.helloworld.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.helloworld.R
import com.example.helloworld.data.model.User
import com.example.helloworld.databinding.FragmentContactInfoBinding

/**
 * A simple [Fragment] subclass.
 * Use the [ContactInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactInfoFragment : Fragment() {

    private var _binding: FragmentContactInfoBinding? = null
    private val binding get() = _binding!!

    private val safeArgs : ContactInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactInfoBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val user = safeArgs.contact
        bind(user)
    }

    private fun bind(user: User){
        binding.apply {
            contactName.text = user.username
            contactUsername.text = user.username
            contactEmail.text = user.email
            expandCollaspe.setOnClickListener {
                if (expandableView.visibility == View.GONE) {
                    // Expand the card
                    expandableView.visibility = View.VISIBLE
                    expandCollaspe.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    // Collapse the card
                    expandableView.visibility = View.GONE
                    expandCollaspe.setImageResource(R.drawable.ic_arrow_up)
                }
            }
        }
    }

}