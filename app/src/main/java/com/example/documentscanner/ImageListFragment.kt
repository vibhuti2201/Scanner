package com.example.documentscanner

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class ImageListFragment : Fragment() {

    companion object {
        private const val TAG = "IMAGE_LIST_TAG"
        private const val STORAGE_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
    }

    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>

    private var imageUri: Uri? = null

    private lateinit var addImageFab: FloatingActionButton

    private lateinit var mContext: Context


    private lateinit var allimageArrayList: ArrayList<ModelImage>

    private lateinit var adapterImage: AdapterImage

    private lateinit var imageRv:RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraPermission = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        addImageFab = view.findViewById(R.id.addImageFab)

        imageRv= view.findViewById(R.id.imageRv)

        val glideRegistry = Glide.get(mContext).registry
        glideRegistry.append(
            ModelImage::class.java,
            InputStream::class.java,
            ModelImageLoader.Factory()
        )

        loadImages()

        addImageFab.setOnClickListener {
            showInputImageDialog()
        }
    }

    private fun loadImages() {
        Log.d(TAG, "loadImages: ")

        allimageArrayList = ArrayList()
        adapterImage = AdapterImage(mContext, allimageArrayList)

        imageRv.adapter = adapterImage

        val folder = File(mContext.getExternalFilesDir(null), Constants.IMAGES_FOLDER)

        if (folder.exists()) {
            Log.d(TAG, "loadImages: Folder exists ")

            val files = folder.listFiles()
            if (files != null) {
                Log.d(TAG, "loadImages: Folder have files ")

                for (file in files) {
                    Log.d(TAG, "loadImages: fileName: ${file.name}")

                    imageUri = Uri.fromFile(file)

                    val modelImage = ModelImage(imageUri!!)

                    allimageArrayList.add(modelImage)

                    adapterImage.notifyItemInserted(allimageArrayList.size)
                }
            } else {
                Log.d(TAG, "loadImages: Folder exists but have no files")
            }
        } else {
            Log.d(TAG, "loadImages: Folder doesn't exist")
        }
    }

    private fun saveImageToAppLevelDirectory(imageUriToBeSaved: Uri) {
        Log.d(TAG, "saveImageToAppLevelDirectory: imageUriToBedSaved: $imageUriToBeSaved")

        try {
            val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        mContext.contentResolver,
                        imageUriToBeSaved
                    )
                )
            } else {
                MediaStore.Images.Media.getBitmap(mContext.contentResolver, imageUriToBeSaved)
            }

            val directory = File(mContext.getExternalFilesDir(null), Constants.IMAGES_FOLDER)
            directory.mkdirs()

            val timestamp = System.currentTimeMillis()
            val fileName = "$timestamp.jpeg"

            val file = File(mContext.getExternalFilesDir(null), "${Constants.IMAGES_FOLDER}/$fileName")
            try {

                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                Log.d(TAG, "saveImageToAppLevelDirectory: Saved")
                toast("Image Saved")
            } catch (e: Exception) {
                Log.d(TAG, "saveImageToAppLevelDirectory: ", e)
                Log.d(TAG, "saveImageToAppLevelDirectory: ${e.message}")

                toast("Failed to save image due to ${e.message}")
            }
        } catch (e: Exception) {
            Log.d(TAG, "saveImageToAppLevelDirectory: ", e)
            Log.d(TAG, "saveImageToAppLevelDirectory: ${e.message}")

            toast("Failed to prepare image due to ${e.message}")
        }
    }

    private fun showInputImageDialog() {
        Log.d(TAG, "howInputImageDialog")

        val popupMenu = PopupMenu(mContext, addImageFab)

        popupMenu.menu.add(Menu.NONE, 1, 1, "CAMERA")
        popupMenu.menu.add(Menu.NONE, 2, 2, "GALLERY")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->

            val itemId = menuItem.itemId

            if (itemId == 1) {

                Log.d(TAG, "showInputImageDialog: Camera is clicked, check if camera permissions are granted or not")
                if (checkCameraPermissions()) {
                    pickImageCamera()
                } else {
                    requestCameraPermission()
                }

            } else if (itemId == 2) {

                Log.d(TAG, "showInputImageDialog: Gallery is Clicked, check if storage permission is granted or not")
                if (checkStoragePermission()) {
                    pickImageGallery()
                } else {
                    requestStoragePermission()
                }
            }
            true
        }
    }

    private fun pickImageGallery() {
        Log.d(TAG, "pickImageGallery: ")

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imageUri = data?.data
                Log.d(TAG, "galleryActivityResultLauncher: Gallery Image: $imageUri")
                saveImageToAppLevelDirectory(imageUri!!)

                val modelImage = ModelImage(imageUri!!)
                allimageArrayList.add(modelImage)
                adapterImage.notifyItemInserted(allimageArrayList.size)

            } else {
                Log.d(TAG, "galleryActivityResultLauncher: Cancelled")
                toast("Cancelled")
            }
        }

    private fun pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ")

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP IMAGE TITLE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP IMAGE DESCRIPTION")

        imageUri = mContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "cameraActivityResultLauncher: Camera Image $imageUri")
                saveImageToAppLevelDirectory(imageUri!!)

                val modelImage = ModelImage(imageUri!!)
                allimageArrayList.add(modelImage)
                adapterImage.notifyItemInserted(allimageArrayList.size)

            } else {
                Log.d(TAG, "cameraActivityResultLauncher: Cancelled")
                toast("Cancelled...")
            }
        }

    private fun checkStoragePermission(): Boolean {
        Log.d(TAG, "checkStoragePermission")
        return ContextCompat.checkSelfPermission(
            mContext,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        Log.d(TAG, "requestStoragePermission: ")
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE)
    }

    private fun checkCameraPermissions(): Boolean {
        Log.d(TAG, "checkCameraPermissions: ")
        val cameraResult =
            ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val storageResult =
            ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return cameraResult && storageResult
    }

    private fun requestCameraPermission() {
        Log.d(TAG, "requestCameraPermissions: ")
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: ")

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted) {
                        Log.d(TAG, "onRequestPermissionsResult: both permissions(Camera & Gallery) are granted, we can launch camera intent")
                        pickImageCamera()
                    } else {
                        Log.d(TAG, "onRequestPermissionsResult: Camera & Storage permissions are  required")
                        toast("Camera & Storage permissions are  required")
                    }
                } else {
                    toast("Cancelled")
                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (storageAccepted) {
                        Log.d(TAG, "onRequestPermissionsResult: storage permission granted, we can launch gallery intent")
                        pickImageGallery()
                    } else {
                        toast("Storage permission is required")
                    }
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Neither allowed nor denied, rather cancelled")
                    toast("Cancelled")
                }
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }
}
