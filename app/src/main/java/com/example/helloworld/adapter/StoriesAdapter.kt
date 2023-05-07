package com.example.helloworld.adapter

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.helloworld.R
import com.example.helloworld.data.model.UserStatus
import com.example.helloworld.ui.fragments.StatusFragmentDirections
import com.squareup.picasso.Picasso
import java.util.*


class StoriesAdapter(private val context: Context, private val users: List<UserStatus>, private val navController: NavController, private val viewPager: ViewPager) : PagerAdapter(), ViewPager.OnPageChangeListener {

    private var cPosition = 0

    init {
        viewPager.addOnPageChangeListener(this)
        viewPager.setPageTransformer(true, DepthPageTransformer())
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(context).inflate(R.layout.status_layout, container, false)

        // Get the user at the current position
        val user = users[position]

        // Set the user's name
        val name = itemView.findViewById<TextView>(R.id.status_name)
        val imageStatus = itemView.findViewById<ImageView>(R.id.image_status)

        name.text = user.name

        // Reset cPosition to 0 when a new user is displayed
        cPosition = 0

        // Set the user's first image status
        Picasso.get().load(user.status.first().image).into(imageStatus)

        // Set the position as a tag on the itemView for later use
        itemView.tag = position

        imageStatus.setOnClickListener {
            if (cPosition < user.status.lastIndex) {
                cPosition++
                Picasso.get().load(user.status[cPosition].image).into(imageStatus)
            } else {
                if (position < users.size - 1) {
                    // If this is not the last user, switch to the next user
                    cPosition = 0
                    viewPager.setCurrentItem(position+1,true)
                } else {
                }
            }
        }

        container.addView(itemView)

        return itemView
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return users.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun onPageScrollStateChanged(state: Int) {
        // Not needed
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        // Not needed
    }

//    override fun onPageSelected(position: Int) {
//        // Reset cPosition to 0 when a new user is displayed
//        cPosition = 0
//        // Get the current item view from the ViewPager
//        val itemView = viewPager.getChildAt(position)
//        // Get the ImageView for the current item view
//        val imageStatus = itemView?.findViewById<ImageView>(R.id.image_status)
//        val user = users[position]
//        // Check if the ImageView is not null before loading the image
//        imageStatus?.let {
//            val status = user.status[cPosition]
//            Picasso.get().load(status.image).into(it)
//        }
//    }

    override fun onPageSelected(position: Int) {
        // Reset cPosition to 0 when a new user is displayed
        cPosition = 0

        // Get the current item view for the selected position
        val itemView = instantiateItem(viewPager, position) as View

        // Get the ImageView for the current item view
        val imageStatus = itemView.findViewById<ImageView>(R.id.image_status)

        val user = users[position]

        // Check if the ImageView is not null before loading the image
        imageStatus?.let {
            val status = user.status[cPosition]
            Picasso.get().load(status.image).into(it)
        }
    }

}

class DepthPageTransformer : ViewPager.PageTransformer {

    companion object {
        private const val MIN_SCALE = 0.75f
    }

    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        when {
            position < -1 -> {
                // This page is way off-screen to the left.
                view.alpha = 0f
            }
            position <= 0 -> {
                // Use the default slide transition when moving to the left page.
                view.alpha = 1f
                view.translationX = 0f
                view.scaleX = 1f
                view.scaleY = 1f
            }
            position <= 1 -> {
                // Fade the page out.
                view.alpha = 1 - position

                // Counteract the default slide transition.
                view.translationX = pageWidth * -position

                // Scale the page down (between MIN_SCALE and 1).
                val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position))
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
            }
            else -> {
                // This page is way off-screen to the right.
                view.alpha = 0f
            }
        }
    }
}

