package com.example.starrway_androidfinalproject
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.time.LocalDate

class Pin {
    var title:String=""
    var description:String=""
    @RequiresApi(Build.VERSION_CODES.O)
    var date: String=LocalDate.now().toString()
    var photoPath:String="empty"
    var latitude:Double=0.0
    var longitude:Double=0.0
    var pk:Int=-1

    constructor(){}
    constructor(title:String, description:String, date: String, photoPath: String){
        this.title=title
        this.description=description
        this.date=date
        this.photoPath=photoPath
    }
}