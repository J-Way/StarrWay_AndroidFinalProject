package com.example.starrway_androidfinalproject

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.R
import kotlinx.android.synthetic.main.activity_add_pin.*
import java.io.File


class AddPinActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    @RequiresApi(Build.VERSION_CODES.O)
    val dbHandler:DbasHandler=DbasHandler(this)
    lateinit var sharedPrefHandler:SharedPrefHandler
    companion object{
        private val dateSeparator:String="-"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pin)

        sharedPrefHandler = SharedPrefHandler(this)

        btnAddPin.setText(MapsActivity.activePin.dbasModification() + " Pin")
        setTitle(btnAddPin.text.toString()+ " User Form")
        etDate.setText( MapsActivity.activePin.date)
        etDescription.setText(MapsActivity.activePin.description)
        etTitle.setText( MapsActivity.activePin.title)
        tvPhotoPath.setText(MapsActivity.activePin.photoPath)
        tvGPS.setText(MapsActivity.activePin.gpsFormatted())
        tvPrimaryKey.setText(MapsActivity.activePin.pk.toString())
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
                x.pk=MapsActivity.activePin.pk
                if (MapsActivity.activePin.dbasModification().equals("Edit")){
                    val success = dbHandler.editPin(x)
                    if(success == 1){
                        sharedPrefHandler.saveLastEdited(this?.getString(R.string.last_modified_key), x.pk.toLong())
                    }
                }
                else{
                    val id = dbHandler.addPin(x)
                    sharedPrefHandler.saveLastEdited(this?.getString(R.string.last_modified_key), id)
                }

                btnCancel.performClick()
            }

        }
        btnDatePicker.setOnClickListener {
            val dateString=etDate.text.toString().trim()
            val year=stringToDatePart(dateString,0)
            val month=stringToDatePart(dateString,1)
            val dayOfMonth=stringToDatePart(dateString,2)
            DatePickerDialog(this,this,year,month-1,dayOfMonth).show()
        }
        etDate.setOnClickListener {
            Toast.makeText(this,"Users cannot change the date by typing it manually. Please use the date picker button to change the date.", Toast.LENGTH_LONG).show()
        }
        if(MapsActivity.activePin.dbasModification().equals("Edit")){
            btnDeletePin.setVisibility(View.VISIBLE)
            btnDeletePin.setOnClickListener {
                dbHandler.deletePin(MapsActivity.activePin.pk)
                Toast.makeText(this,"Pin #"+MapsActivity.activePin.pk.toString()+" successfully deleted!", Toast.LENGTH_LONG).show()
                btnCancel.performClick()
            }
        }
        else {
            btnDeletePin.setVisibility(View.INVISIBLE)
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        etDate.setText(datePartToString(year)+ dateSeparator+datePartToString(1+month)+ dateSeparator+datePartToString(dayOfMonth))
    }
    fun datePartToString(datePartValue:Int):String{
        var result:String=datePartValue.toString()
        if (result.length==1) result="0"+result
        return result
    }
    fun stringToDatePart(dateString:String,datePartIndex:Int):Int{
        val dateParts=dateString.split(dateSeparator).toTypedArray()
        return dateParts[datePartIndex].toInt()
    }

}