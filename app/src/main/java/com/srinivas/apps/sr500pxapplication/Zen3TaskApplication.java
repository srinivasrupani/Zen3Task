package com.srinivas.apps.sr500pxapplication;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.fivehundredpx.api.auth.AccessToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.srinivas.apps.sr500pxapplication.login.dao.HomeResponse;
import com.srinivas.apps.sr500pxapplication.network.io.Zen3RestService;
import com.srinivas.apps.sr500pxapplication.network.utils.ZConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by Srinivas Rupani on 12/7/2017.
 * Copyright (c) 2017 Promantra Inc, All rights reserved.
 */

/**
 * @Information Application class is responsible to initialise APP (pre required loading objects,views,variables,classes...etc)
 */
public class Zen3TaskApplication extends Application {
    private static Zen3TaskApplication instance;
    private static Zen3RestService zen3RestService;
    private AccessToken loginResponse;
    private String TAG = "HMApp";
    public JSONObject errorMessage;
    private Intent mIntent;
    private HomeResponse homeResponse;

    public Zen3TaskApplication() {
        instance = this;
    }

    public static Zen3TaskApplication getInstance() {
        if (instance == null) {
            instance = new Zen3TaskApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getAppVersionCodeName();
    }

    public Zen3RestService initRetrofit() {
        OkHttpClient client = new OkHttpClient();
        //TODO : Adding Certification to OKHTTP (Optional)
        /*OkHttpClient client = new OkHttpClient.Builder()
        .certificatePinner(new CertificatePinner.Builder()
                .add("publicobject.com", "sha1/DmxUShsZuNiqPQsX2Oi9uv2sCnw=")
                .add("publicobject.com", "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=")
                .add("publicobject.com", "sha1/blhOM3W9V/bVQhsWAcLYwPU6n24=")
                .add("publicobject.com", "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c=")
                .build())
        .build();*/
        Gson gson = new GsonBuilder()
            .setDateFormat(ZConstants.GSON_DATE_FORMAT)
            .create();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client).build();//client we can replace with get_HTTPClient()
        //Log.e(TAG, "client:"+client.dispatcher().getMaxRequests());
        if (zen3RestService == null) {
            zen3RestService = retrofit.create(Zen3RestService.class);
        }
        return zen3RestService;
    }

    private static OkHttpClient get_HTTPClient(final Map<String, String> headers) {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers

                Request.Builder requestBuilder = original.newBuilder(); // <-- this is the important line

                for (Map.Entry<String, String> pairs : headers.entrySet()) {
                    if (pairs.getValue() != null) {
                        requestBuilder.header(pairs.getKey(), pairs.getValue());
                    }
                }

                requestBuilder.method(original.method(), original.body());
                Request request = requestBuilder.build();

                return chain.proceed(request);

            }
        });

        return httpClient.build();

    }

    private void getAppVersionCodeName() {
        int versionCode = BuildConfig.VERSION_CODE;//versionCode 1
        String versionName = BuildConfig.VERSION_NAME;//versionName "1.0"
        Log.e(TAG, "versionCode:" + versionCode + "  versionName:" + versionName);
    }

    public JSONObject setErrorMessage(String errorMessage1) {
        try {
            errorMessage = new JSONObject(errorMessage1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errorMessage;
    }

    public String getErrorMessage() {
        String error = null;
        try {
            if (errorMessage != null) {
                if (errorMessage.has("message")) {
                    error = errorMessage.getString("message");
                } else if (errorMessage.has("error")) {
                    error = errorMessage.getString("error");
                } else {
                    error = "No Error message...!";
                }
            } else {
                error = "Problem with server,\n Please try after some time...!";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return error;
    }

    public AccessToken getLoginResponse() {
        return loginResponse;
    }

    public void setLoginResponse(AccessToken loginResponse) {
        this.loginResponse = loginResponse;
    }

    public HomeResponse getHomeResponse() {
        return homeResponse;
    }

    public void setHomeResponse(HomeResponse homeResponse) {
        this.homeResponse = homeResponse;
    }
}
