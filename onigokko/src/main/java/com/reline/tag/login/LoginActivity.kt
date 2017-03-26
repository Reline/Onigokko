package com.reline.tag.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.reline.tag.R
import kotterknife.bindView


class LoginActivity : AppCompatActivity(), ILoginView, GoogleApiClient.OnConnectionFailedListener {

    private val TAG = LoginActivity::class.java.simpleName

    /* Request code used to invoke sign in user interactions. */
    private val RC_GET_TOKEN = 9002

    private val presenter = LoginPresenter()

    /* Client used to interact with Google APIs. */
    private var mGoogleApiClient: GoogleApiClient? = null

    val signInButton: SignInButton by bindView(R.id.sign_in_button)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter.takeView(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .build()

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, presenter)
                .addConnectionCallbacks(presenter)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_GET_TOKEN)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult called with requestCode = $requestCode, resultCode = $resultCode, data = $data")

        if (requestCode == RC_GET_TOKEN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.d(TAG, "onActivityResult: result.isSuccess = ${result.isSuccess}")

            if (result.isSuccess) {
                val idToken = result.signInAccount!!.idToken
                presenter.onTokenReceived(idToken ?: String())
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, connectionResult.errorMessage)
    }
}