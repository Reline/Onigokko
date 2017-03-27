package com.reline.tag.database

interface DatabaseAccessObject {
    fun getToken(): String
    fun saveToken(token: String)
}