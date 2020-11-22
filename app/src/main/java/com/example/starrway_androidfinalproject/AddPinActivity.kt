package com.example.starrway_androidfinalproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.R
import kotlinx.android.synthetic.main.activity_add_pin.*
import java.io.File


class AddPinActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    val dbHandler:DbasHandler=DbasHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pin)

        setTitle("Add Pin Use Form")

        etDate.setText( MapsActivity.activePin.date)
        etDescription.setText(MapsActivity.activePin.description)
        etTitle.setText( MapsActivity.activePin.title)
        tvPhotoPath.setText(MapsActivity.activePin.photoPath)
        tvGPS.setText(MapsActivity.activePin.gpsFormatted())

        val minLength=7
        if (tvPhotoPath.text.toString().length>=minLength){
            var imgFile= File(tvPhotoPath.text.toString().substring(minLength))
            if (imgFile.exists()){
                var b: Bitmap = BitmapFactory.decodeFile(imgFile.toString())
                imgPath.setImageBitmap(b)
            }
        }
        btnCancel.setOnClickListener {
            MapsActivity.activePin =
                Pin()
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        btnCamera.setOnClickListener {
            MapsActivity.activePin.title=etTitle.text.toString().trim()
            MapsActivity.activePin.description=etDescription.text.toString().trim()
            MapsActivity.activePin.date= etDate.text.toString().trim()
            MapsActivity.activePin.photoPath= tvPhotoPath.text.toString().trim()
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        btnAddPin.setOnClickListener {
            var message:String
            MapsActivity.activePin.date=etDate.text.toString()
            if (etTitle.text.toString().trim().equals("")){
                message="Title is blank. Please try again"
                Toast.makeText(this,message, Toast.LENGTH_LONG).show()
            }
            else{
                val x=Pin()
                x.photoPath=tvPhotoPath.text.toString()
                x.title=etTitle.text.toString()
                x.description=etDescription.text.toString()
                x.date=etDate.text.toString()
                x.latLng=MapsActivity.activePin.latLng
                dbHandler.addPin(x)
                btnCancel.performClick()
            }

        }
    }

}