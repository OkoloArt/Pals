package com.example.helloworld.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.helloworld.R
import com.example.helloworld.data.model.UserStatus
import com.squareup.picasso.Picasso


class StoriesAdapter(
private val context: Context,
private val users: List<UserStatus> ,
private val startingPosition: Int
) : PagerAdapter(), ViewPager.OnPageChangeListener {

    private var cPosition = 0
    private var previousPosition = startingPosition

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(context).inflate(R.layout.status_layout, container, false)

        // Get the user at the current position
        val user = users[position]

//        // Set the user's name
        val name = itemView.findViewById<TextView>(R.id.status_name)
        val imageStatus = itemView.findViewById<ImageView>(R.id.image_status)

        name.text = user.name

        // Set the user's first image status
        Picasso.get().load(user.status[cPosition].image).into(imageStatus)

        // Set the position as a tag on the itemView for later use
        itemView.tag = position

        // Add a click listener to the image view to switch to the next image status
//        imageStatus.setOnClickListener {
//            val currentPosition = cPosition
//            val currentStatusIndex = user.status.indexOfFirst { it.image == user.status[currentPosition].image }
//            if (currentStatusIndex != user.status.size) {
//                Picasso.get().load(user.status[currentStatusIndex + 1].image).into(imageStatus)
//                Toast.makeText(context,"Scrolling to next user", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(context,"Scrolling to next user", Toast.LENGTH_LONG).show()
//            }
//        }

        imageStatus.setOnClickListener {
            if (cPosition < user.status.lastIndex){
                cPosition++
            }else if (position < users.size - 1) {
                // If this is not the last user, switch to the next user
                cPosition = 0
                (container as ViewPager).setCurrentItem(position+1, true)
            }
            Picasso.get().load(user.status[cPosition].image).into(imageStatus)
        }

        // Set the ViewPager's OnPageChangeListener to this
        (container as ViewPager).addOnPageChangeListener(this)

        // Add the itemView to the container
        container.addView(itemView)

        return itemView
    }

    override fun getCount(): Int = users.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun onPageScrolled(position: Int , positionOffset: Float , positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (position > previousPosition) {
            // User scrolled forward
           cPosition = 0
            Toast.makeText(context, cPosition.toString(), Toast.LENGTH_LONG).show()
        } else if (position < previousPosition) {
            // User scrolled backward
            cPosition = 0
            Toast.makeText(context, cPosition.toString(), Toast.LENGTH_LONG).show()
        }

        previousPosition = position
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}


