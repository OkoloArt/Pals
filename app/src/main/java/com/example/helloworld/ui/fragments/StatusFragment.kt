package com.example.helloworld.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.helloworld.MainActivity
import com.example.helloworld.adapter.StoriesAdapter
import com.example.helloworld.data.model.ImageStatus
import com.example.helloworld.data.model.UserStatus
import com.example.helloworld.databinding.FragmentStatusBinding
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView


/**
 * A simple [Fragment] subclass.
 * Use the [StatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatusFragment : Fragment(), StoriesProgressView.StoriesListener {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: StoriesAdapter

    var pressTime = 0L
    var limit = 500L

    private var storiesProgressView: StoriesProgressView? = null
    private var image: ImageView? = null
    private var stories : List<ImageStatus> = emptyList()

    private var counter = 0

    private val onTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View? , event: MotionEvent?): Boolean {
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    storiesProgressView!!.pause()
                    return false
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    storiesProgressView!!.resume()
                    return limit < now - pressTime
                }
            }
            return false
        }
    }

    // Retrieve the arguments passed from ChatFragment
    private val args: StatusFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle? ,
    ): View?
    {
        // Inflate the layout for this fragment
        _binding = FragmentStatusBinding.inflate(layoutInflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val itemPosition = args.itemPosition
        val userStatus = args.status.toList()

        stories = userStatus[itemPosition].status

        loadCards()
    }

//    private fun loadCards() {
//
//        val itemPosition = args.itemPosition
//        val userStatus = args.status.toList()
//
//        val viewPager = binding.viewPager
//        val pagerAdapter = StoriesAdapter(requireContext(), userStatus, findNavController(), viewPager)
//        viewPager.adapter = pagerAdapter
//        viewPager.setCurrentItem(itemPosition, true)
//    }

    private fun loadCards() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN)

        storiesProgressView = binding.stories

        storiesProgressView!!.setStoriesCount(stories.size)
        storiesProgressView!!.setStoryDuration(5000L)
        storiesProgressView!!.setStoriesListener(this)
        storiesProgressView!!.startStories()

        image = binding.image
        Picasso.get().load(stories[counter].image).into(image)

        val reverse: View = binding.reverse

       reverse.setOnClickListener {
           storiesProgressView!!.reverse()
       }

        reverse.setOnTouchListener(onTouchListener)

        //Skip
        val skip = binding.skip
        skip.setOnClickListener {
            storiesProgressView!!.skip()
        }
        skip.setOnTouchListener(onTouchListener)
    }

    override fun onNext() {
        Picasso.get().load(stories[++counter].image).into(image)
    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        Picasso.get().load(stories[--counter].image).into(image)
    }

    override fun onComplete() {
        val action = StatusFragmentDirections.actionStatusFragmentToChatFragment()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        storiesProgressView!!.destroy()
        super.onDestroy()
    }

}
