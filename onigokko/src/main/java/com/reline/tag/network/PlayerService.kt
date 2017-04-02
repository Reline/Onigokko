package com.reline.tag.network

import com.reline.tag.model.Player
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PlayerService {

    @GET("player")
    fun retrievePlayer(): Call<Player>

    @POST("player")
    fun createPlayer(@Body player: Player): Call<ResponseBody>
}