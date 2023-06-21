package com.example.documentscanner

import android.graphics.BitmapFactory
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.ModelLoaderRegistry
import java.io.InputStream

class ModelImageLoader : ModelLoader<ModelImage, InputStream> {

    override fun buildLoadData(
        model: ModelImage,
        width: Int,
        height: Int,
        options: BitmapFactory.Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(
            model,
            ModelImageDataFetcher(model)
        )
    }

    override fun handles(model: ModelImage): Boolean {
        return true
    }

    class Factory : ModelLoaderFactory<ModelImage, InputStream> {
        override fun build(registry: ModelLoaderRegistry): ModelLoader<ModelImage, InputStream> {
            return ModelImageLoader()
        }

        override fun teardown() {
            // No-op
        }
    }

    private class ModelImageDataFetcher(private val model: ModelImage) : DataFetcher<InputStream> {
        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
            // Here, you would implement the logic to load the image data based on the model information
            // For example, you can use the model's URI to open an InputStream to the image data
            val inputStream: Unit = // Implement your logic to obtain the InputStream for the image

                if (inputStream() != null) {
                    callback.onDataReady(inputStream())
                } else {
                    callback.onLoadFailed(Exception("Failed to load image data"))
                }

        }

        private fun inputStream(): InputStream? {
            TODO("Not yet implemented")
        }

        override fun cleanup() {
            // Cleanup any resources if needed
        }

        override fun cancel() {
            // Cancel the ongoing image loading process if needed
        }

        override fun getDataClass(): Class<InputStream> {
            return InputStream::class.java
        }

        override fun getDataSource(): DataSource {
            return DataSource.LOCAL
        }
    }

    override fun buildLoadData(
        model: ModelImage,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        TODO("Not yet implemented")
    }
}



