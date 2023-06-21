package com.example.documentscanner

import android.net.Uri
import com.bumptech.glide.load.Key
import java.security.MessageDigest

class ModelImage(var imageUri: Uri) : Key {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        TODO("Not yet implemented")
    }
}