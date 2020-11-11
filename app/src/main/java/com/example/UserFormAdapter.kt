package com.example

import android.app.Activity
import android.text.Layout
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.starrway_androidfinalproject.R

class UserFormAdapter(private val context: Activity)
    :ArrayAdapter<String>(context,R.layout.form_view, arrayOf("Placeholder")){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater=context.layoutInflater
        val rowView=inflater.inflate(R.layout.form_view,null,true)
        return rowView
    }
}