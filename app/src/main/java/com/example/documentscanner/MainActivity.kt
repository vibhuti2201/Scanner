package com.example.documentscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationMenu)

        bottomNavigationView.setOnItemSelectedListener { menuItem->


            when(menuItem.itemId)
            {
                R.id.bottom_menu_images->{
                  loadImagesFragment()
                }
                R.id.bottom_menu_pdfs->{
                  loadPdfFragment()
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun loadImagesFragment(){

        title="Images"
        val imageListFragment = ImageListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,imageListFragment,"ImageListFragment")
        fragmentTransaction.commit()
    }
    private fun loadPdfFragment(){
        title="PDF List"
        val pdfListFragment = PdfListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,pdfListFragment,"PdfListFragment")
        fragmentTransaction.commit()
    }
}