package com.example.starrway_androidfinalproject

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbasHandler (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object{
        private val DATABASE_NAME="StarrDbas"
        private val DATABASE_VERSION =1
        private val MY_TABLE="StarrTable"
        private val KEY_NAME ="name"
        private val KEY_DESCRIPTION ="description"
        private val KEY_PATH="path"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TABLE=("CREATE TABLE " + MY_TABLE +"("
                + KEY_PATH + " TEXT PRIMARY KEY, " + KEY_NAME+ " TEXT, "+ KEY_DESCRIPTION+ " TEXT)")
        db?.execSQL(CREATE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS" + MY_TABLE)
        onCreate(db)
    }
    fun addTester(keyVal:String):Long{
        val db=this.writableDatabase
        val ContentValues= ContentValues()
        ContentValues.put(KEY_NAME, keyVal)
        ContentValues.put(KEY_DESCRIPTION, keyVal)
        ContentValues.put(KEY_PATH, keyVal)
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

                name=cursor.getString(cursor.getColumnIndex(KEY_NAME))
                description=cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))
                path=cursor.getString(cursor.getColumnIndex(KEY_PATH))

                giftList.add(path)
            }while (cursor.moveToNext())

        }
        return giftList
    }
}