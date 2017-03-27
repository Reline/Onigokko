package com.reline.tag.database

import android.content.SharedPreferences
import com.squareup.sqlbrite.BriteDatabase

class SqlBriteDao(val briteDatabase: BriteDatabase, val sharedPreferences: SharedPreferences): DatabaseAccessObject {

    private val TOKEN = "TOKEN"

    override fun getToken(): String = sharedPreferences.getString(TOKEN, String())

    override fun saveToken(token: String) = sharedPreferences.edit().putString(TOKEN, token).apply()
}