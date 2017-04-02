package com.reline.tag.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.reline.tag.Onigokko;
import com.reline.tag.Presenter;
import com.reline.tag.database.DatabaseAccessObject;
import com.reline.tag.injection.component.DaggerPresenterComponent;
import com.reline.tag.injection.module.DatabaseModule;
import com.reline.tag.injection.module.NetworkModule;
import com.reline.tag.model.Player;
import com.reline.tag.network.PlayerService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter extends Presenter<ILoginView> implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @Inject
    DatabaseAccessObject dao;

    @Inject
    PlayerService service;

    public LoginPresenter() {
        DaggerPresenterComponent.builder()
                .applicationComponent(Onigokko.component())
                .databaseModule(new DatabaseModule())
                .networkModule(new NetworkModule())
                .build().inject(this);
    }

    public void onTokenReceived(@NotNull String token, @NotNull String name) {
        Log.d(TAG, "onTokenReceived() called with: token = [" + token + "]");
        dao.saveToken(token);
        Player player = new Player(name);
        service.createPlayer(player)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String string = response.body() != null ? response.body().string() : "";
                            Log.d(TAG, "onResponse() called with: [" + string + "]");
                        } catch (IOException e) {
                            Log.e(TAG, "onResponse: " + e.getMessage(), e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage(), t);
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected() called with: bundle = [" + bundle + "]");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called with: i = [" + i + "]");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called with: connectionResult = [" + connectionResult + "]");
    }
}
