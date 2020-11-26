package com.example.starrway_androidfinalproject

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_add_pin.*

class DbasHandler (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private val DATABASE_NAME="StarrWayDbas"
        private val DATABASE_VERSION =1
        private val MY_TABLE="AddedPins"
        private val TITLE_NAME ="title"
        private val DESCRIPTION_NAME ="description"
        private val PK_NAME="pk"
        private val LATITUDE_NAME="latitude"
        private val LONGITUDE_NAME="longitude"
        private val PATH_NAME="path"
        private val DATE_NAME="date"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TABLE=("CREATE TABLE " + MY_TABLE +"("
                + PK_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE_NAME+ " TEXT, "+ DESCRIPTION_NAME+ " TEXT, "
                + LATITUDE_NAME + " REAL, "+ LONGITUDE_NAME + " REAL, "+ DATE_NAME + " TEXT, "+ PATH_NAME+ " TEXT)")
        db?.execSQL(CREATE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS" + MY_TABLE)
        onCreate(db)
    }
    fun addPin(x:Pin):Long{
        val db=this.writableDatabase
        val ContentValues=setContentValues(x)
        val success=db.insert(MY_TABLE,null,ContentValues)
        db.close()
        return success
    }
    fun editPin(x:Pin):Int{
        val db=this.writableDatabase
        val ContentValues= ContentValues()
        val success=db.update(MY_TABLE,ContentValues,"${PK_NAME} = ?", arrayOf(x.pk.toString()))
        db.close()
        return success
    }
    fun setContentValues(x:Pin):ContentValues{
        val result=ContentValues()
        result.put(TITLE_NAME, x.title)
        result.put(DESCRIPTION_NAME, x.description)
        result.put(DATE_NAME, x.date)
        result.put(PATH_NAME, x.photoPath)
        result.put(LATITUDE_NAME, x.latLng.latitude)
        result.put(LONGITUDE_NAME, x.latLng.longitude)
        return result
    }
    fun viewAll():List<Pin>{
        val result:ArrayList<Pin> =ArrayList<Pin>()
        val db=this.writableDatabase
        val selectQuery="Select * FROM $MY_TABLE"
        var cursor: Cursor?=null
        try {
            cursor=db.rawQuery(selectQuery,null)
        }
        catch (ex: SQLException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        if (cursor.moveToFirst()){
            do{
                val x=Pin()
                x.date=cursor.getString(cursor.getColumnIndex(DATE_NAME))
                x.description=cursor.getString(cursor.getColumnIndex(DESCRIPTION_NAME))
                x.title=cursor.getString(cursor.getColumnIndex(TITLE_NAME))
                x.photoPath=cursor.getString(cursor.getColumnIndex(PATH_NAME))
                x.pk=cursor.getInt(cursor.getColumnIndex(PK_NAME))

                // when going through PR
                // we need to feed the data as Lat then Long otherwise the values are mistaken / swapped
                x.latLng= LatLng(cursor.getDouble(cursor.getColumnIndex(LATITUDE_NAME)),
                    cursor.getDouble(cursor.getColumnIndex(LONGITUDE_NAME)))

                result.add(x)
            }while (cursor.moveToNext())

        }
        return result
    }
}