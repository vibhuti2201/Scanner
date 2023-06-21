package com.example.documentscanner

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide

class AdapterImage(private  val context: Context,
                   private val imageArrayList:ArrayList<ModelImage>
                   ): RecyclerView.Adapter<AdapterImage.HolderImage>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImage {

        val view= LayoutInflater.from(context).inflate(R.layout.row_image,parent,false)

        return HolderImage(view)
    }



    override fun onBindViewHolder(holder: HolderImage, position: Int) {

        val modelImage= imageArrayList[position]

        val imageUri= imageArrayList[position]

        Glide.with(context)
            .load(imageUri)
            .placeholder(R.drawable.ic_image_black)
            .into(holder.imageIv)

        holder.itemView.setOnClickListener {
            val intent= Intent(context,ImageViewActivity::class.java)
            intent.putExtra("imageUri","$imageUri")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {

        return  imageArrayList.size
    }


    inner class HolderImage(itemView: View): ViewHolder(itemView){

        var imageIv = itemView.findViewById<ImageView>(R.id.imageIv)
    }

}