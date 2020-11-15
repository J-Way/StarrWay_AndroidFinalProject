package com.example
import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.time.LocalDate

class Pin {
    var title:String=""
    var description:String=""
    @RequiresApi(Build.VERSION_CODES.O)
    var date: String=LocalDate.now().toString()
    var photoPath:String=""
    constructor(){}
    constructor(title:String, description:String, date: String, photoPath: String){
        this.title=title
        this.description=description
        this.date=date
        this.photoPath=photoPath
    }
}