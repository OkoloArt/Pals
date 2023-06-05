package com.example.helloworld.ui.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.helloworld.R
import com.example.helloworld.adapter.StoriesAdapter
import com.example.helloworld.data.model.ImageStatus
import com.example.helloworld.databinding.FragmentStatusBinding
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


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

    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? , savedInstanceState: Bundle? , ): View? {
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

        binding.downloadCurrentImage.setOnClickListener {
            onDownloadButtonClicked()
        }
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

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "download_channel"
            val channelName = "Downloads"
            val channelDescription = "Channel for download notifications"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showDownloadProgressNotification(context: Context, progress: Int) {
        val channelId = "download_channel"
        val notificationId = 1

        val notificationBuilder = NotificationCompat.Builder(context , channelId)
            .setContentTitle("Downloading Status")
            .setContentText("Download in progress")
            .setSmallIcon(R.drawable.ic_download)
            .setProgress(100, progress, false)
            .setOnlyAlertOnce(true)
            .setOngoing(true)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())
    }

    private fun showDownloadCompleteNotification(context: Context, fileUri: Uri) {
        val channelId = "download_channel"
        val notificationId = 1

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Download Complete")
            .setContentText("Status downloaded successfully")
            .setSmallIcon(R.drawable.ic_check)
            .setContentIntent(
                    PendingIntent.getActivity(
                            context,
                            0,
                            Intent(Intent.ACTION_VIEW, fileUri),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    )
            )
            .setAutoCancel(true)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())
    }

    fun saveImageToExternalStorage(context: Context, bitmap: Bitmap): Uri? {
        val fileName = "status_image_${System.currentTimeMillis()}.jpg"
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imagesDir, fileName)

        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()

            // Add the image to the gallery
            MediaStore.Images.Media.insertImage(context.contentResolver, imageFile.absolutePath, fileName, "")

            return Uri.fromFile(imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun downloadCurrentImage() {
        val currentImage = stories[counter].image

        Picasso.get().load(currentImage).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                bitmap?.let {
                    // Save the bitmap to external storage
                    val uri = saveImageToExternalStorage(requireContext(), it)

                    if (uri != null) {
                        showDownloadCompleteNotification(requireContext(), uri)
                    } else {
                        Toast.makeText(requireContext() , "Failed to save image" , Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Toast.makeText(requireContext(), "Failed to download image", Toast.LENGTH_SHORT).show()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        })
    }

    fun onDownloadButtonClicked() {
        // Check if the necessary permission is granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Create a notification channel if needed
            createNotificationChannel(requireContext())

            // Start the download and show progress notification
            showDownloadProgressNotification(requireContext(), 0)
            downloadCurrentImage()
        } else {
            // Request the necessary permission
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
            )
        }
    }

    companion object {
        private const val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start the download
                    showDownloadProgressNotification(requireContext(), 0)
                    downloadCurrentImage()
                } else {
                    // Permission denied, show a message or handle accordingly
                    Toast.makeText(
                            requireContext(),
                            "Permission denied",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


}
