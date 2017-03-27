package com.reline.tag.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, NAME, null, VERSION) {

    companion object {
        private val VERSION = 1
        private val NAME = "onigokko.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // todo: create tables
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}