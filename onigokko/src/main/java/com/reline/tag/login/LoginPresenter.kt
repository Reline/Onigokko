package com.reline.tag.login

import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.reline.tag.Presenter
import com.reline.tag.injection.component.DaggerPresenterComponent
import com.reline.tag.network.HelloService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class LoginPresenter : Presenter<ILoginView>(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @Inject
    lateinit var service: HelloService

    init {
        DaggerPresenterComponent.builder().build().inject(this)
    }

    fun onTokenReceived(token: String) {
        // todo: store token
        Log.d(TAG, "Saying hello to the server")
        service.sayHello()
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val body = response.body().string()
                        Log.d(TAG, "The server said: $body")
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e(TAG, "The server doesn't seem too friendly...", t)
                    }
                })
        Log.d(TAG, "Waiting on a response from the server...")
    }

    override fun onConnected(bundle: Bundle?) {

    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }
}
