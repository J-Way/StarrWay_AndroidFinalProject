package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.example.R
import java.time.LocalDateTime
class UserFormAdapter(private val context: Activity, private val x:Pin)
    :ArrayAdapter<String>(context,R.layout.form_view, arrayOf("Placeholder")){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater=context.layoutInflater
        val rowView=inflater.inflate(R.layout.form_view,null,true)

        val etDate=rowView.findViewById(R.id.etDate) as EditText
        etDate.setText(x.date)

        val etDescription=rowView.findViewById(R.id.etDescription) as EditText
        etDescription.setText(x.description)

        val etTitle=rowView.findViewById(R.id.etTitle) as EditText
        etTitle.setText(x.title)

        val tvPhotoPath=rowView.findViewById(R.id.tvPhotoPath) as TextView
        tvPhotoPath.setText(x.photoPath)

        val btnCancel=rowView.findViewById(R.id.btnCancel) as Button
        btnCancel.setOnClickListener {
            MapsActivity.activePin=Pin()
            val intent = Intent(context, MapsActivity::class.java)
            context.startActivity(intent)
        }

        val btnCamera=rowView.findViewById(R.id.btnCamera) as Button
        btnCamera.setOnClickListener {
            MapsActivity.activePin=Pin(etTitle.text.toString(), etDescription.text.toString(), etDate.text.toString(), tvPhotoPath.text.toString())
            val intent = Intent(context, CameraActivity::class.java)
            context.startActivity(intent)
        }


        return rowView
    }
}