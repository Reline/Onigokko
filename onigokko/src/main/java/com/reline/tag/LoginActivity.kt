package com.reline.tag

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    /* Client used to interact with Google APIs. */
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestIdToken(getString(R.string.server_client_id))
                .build()

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        val signInButton = findViewById(R.id.sign_in_button) as SignInButton
        signInButton.setSize(SignInButton.SIZE_STANDARD)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, connectionResult.errorMessage)
    }

    companion object {

        private val TAG = LoginActivity::class.java.simpleName

        /* Request code used to invoke sign in user interactions. */
        private val RC_SIGN_IN = 0
    }
}