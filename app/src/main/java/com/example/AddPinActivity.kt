package com.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.R
import kotlinx.android.synthetic.main.activity_add_pin.*


class AddPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pin)
        setTitle("Add Pin")
        addPinView.adapter=UserFormAdapter(this, MapsActivity.activePin)

        //val etTitle=findViewById(R.id.etTitle) as EditText
    }

}