package com.reline.tag.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface HelloService {
    @GET("/onigokko")
    fun sayHello() : Call<ResponseBody>
}