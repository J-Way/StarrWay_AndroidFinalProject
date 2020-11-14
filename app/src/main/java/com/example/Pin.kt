package com.example
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class Pin {
    var title:String=""
    var description:String=""
    @RequiresApi(Build.VERSION_CODES.O)
    var date: LocalDate=LocalDate.now()
    var photoPath:String=""
    constructor(){}
}