package com.example.documentscanner

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class PdfListFragment : Fragment() {

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext= context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf_list, container, false)
    }


}