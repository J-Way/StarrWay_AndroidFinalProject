package com.example.starrway_androidfinalproject

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        val ContentValues= ContentValues()
        ContentValues.put(TITLE_NAME, x.title)
        ContentValues.put(DESCRIPTION_NAME, x.description)
        ContentValues.put(DATE_NAME, x.date)
        ContentValues.put(PATH_NAME, x.photoPath)
        ContentValues.put(LATITUDE_NAME, 43.469)
        ContentValues.put(LONGITUDE_NAME, -79.699)
        val success=db.insert(MY_TABLE,null,ContentValues)
        db.close()
        return success
    }
    fun addTester():Long{
        val db=this.writableDatabase
        val ContentValues= ContentValues()
        ContentValues.put(TITLE_NAME, "title placeholder")
        ContentValues.put(DESCRIPTION_NAME, "description placeholder")
        ContentValues.put(DATE_NAME, "date placeholder")
        ContentValues.put(PATH_NAME, "path placeholder")
        ContentValues.put(LATITUDE_NAME, 43.469)
        ContentValues.put(LONGITUDE_NAME, -79.699)
        val success=db.insert(MY_TABLE,null,ContentValues)
        db.close()
        return success
    }
    fun DeleteAll(){
        val db=this.writableDatabase
        db.execSQL("delete from " + MY_TABLE)
        db.close()
    }
    fun viewTester():List<String>{
        val giftList:ArrayList<String> =ArrayList<String>()
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
        var name=""
        var description=""
        var path=""
        if (cursor.moveToFirst()){
            do{

                name=cursor.getString(cursor.getColumnIndex(TITLE_NAME))
                description=cursor.getString(cursor.getColumnIndex(DESCRIPTION_NAME))
                path=cursor.getString(cursor.getColumnIndex(PK_NAME))

                giftList.add(path)
            }while (cursor.moveToNext())

        }
        return giftList
    }
}