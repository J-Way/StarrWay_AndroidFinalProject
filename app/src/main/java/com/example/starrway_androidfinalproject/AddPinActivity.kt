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
        //dbHandler.DeleteAll()
        dbHandler.addTester()
        dbHandler.addTester()
        setTitle("Add Pin "+  dbHandler.viewTester().size)

        etDate.setText( MapsActivity.activePin.date)
        etDescription.setText(MapsActivity.activePin.description)
        etTitle.setText( MapsActivity.activePin.title)
        tvPhotoPath.setText(MapsActivity.activePin.photoPath)

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
            MapsActivity.activePin =
                Pin(
                    etTitle.text.toString(),
                    etDescription.text.toString(),
                    etDate.text.toString(),
                    tvPhotoPath.text.toString()
                )
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        btnAddPin.setOnClickListener {
            var message:String
            MapsActivity.activePin.date=etDate.text.toString()
            if (etTitle.text.toString().trim().equals("")){
                message="Title is blank. Please try again"
            }
            else{
                message="DAT IS GOOD!"
            }
            Toast.makeText(this,message, Toast.LENGTH_LONG).show()
        }
    }

}