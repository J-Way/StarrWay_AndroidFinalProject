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

    var pk:Int=-1
    var latLng:LatLng = LatLng(0.0,0.0)

    var isLast:Boolean = false

    constructor(){}
    fun gpsFormatted():String{
        val digits=1000
        return "GPS = (" + Math.round(digits*this.latLng.latitude).toDouble()/digits + ", "+ Math.round(digits*this.latLng.longitude).toDouble()/digits + ")"
    }
    fun dbasModification():String{
        if (this.pk>=0) return "Edit"
        else return "Add"
    }
}