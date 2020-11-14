package com.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.example.R
import kotlinx.android.synthetic.main.activity_add_pin.*


class AddPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pin)
        setTitle("Add Pin")
        addPinView.adapter=UserFormAdapter(this)

    }

}