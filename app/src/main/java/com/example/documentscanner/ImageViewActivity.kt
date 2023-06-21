package com.example.documentscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageViewActivity : AppCompatActivity() {

    private  lateinit var imageIv: ImageView

    private var imageUri=""
    companion object{
        private const val  TAG="IMAGE_VIEW_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        imageIv= findViewById(R.id.imageIv)

        imageUri= intent.getStringExtra("imageUri").toString()
        Log.d(TAG, "onCreate: imageUri: $imageUri")
        
        
        supportActionBar?.title=" Image View"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)



        Glide.with(this)
            .load(imageUri)
            .placeholder(R.drawable.ic_image_black)
            .into(imageIv)
    }

    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        return super.onSupportNavigateUp()
    }
}

