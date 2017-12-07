package com.srinivas.apps.sr500pxapplication.photos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.srinivas.apps.sr500pxapplication.R;
import com.srinivas.apps.sr500pxapplication.Zen3TaskApplication;
import com.srinivas.apps.sr500pxapplication.login.dao.HomeResponse;
import com.srinivas.apps.sr500pxapplication.utils.Constants;
import com.srinivas.apps.sr500pxapplication.utils.LoaderUtil;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Srinivas Rupani on 12/7/2017.
 * Copyright (c) 2017 Promantra Inc, All rights reserved.
 */
public class PhotosSearchActivity extends AppCompatActivity {
    private EditText mTerm, mTag, mPage, mRpp, mTags;
    private Button mSearch;
    private Intent mIntent;
    private String term, tag, page, rpp, tags;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_search);
        intiView();
    }

    private void intiView() {
        mTerm = findViewById(R.id.photoTerm);
        mTag = findViewById(R.id.photoTag);
        mPage = findViewById(R.id.photoPage);
        mRpp = findViewById(R.id.photoRPP);
        mTags = findViewById(R.id.photoTags);
        mSearch = findViewById(R.id.doPhotoSearch);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePhotosSearch();
            }
        });
    }

    private void handlePhotosSearch() {
        LoaderUtil.getInstance().showProgress(this, "Photos searching..!");
        if (mTerm.getText().toString() != null && mTerm.getText().toString().trim().length() > 0) {
            term = mTerm.getText().toString().trim();
        }
        if (mTag.getText().toString() != null && mTag.getText().toString().trim().length() > 0) {
            term = term + "&tag=" + mTag.getText().toString();
        }
        if (mPage.getText().toString() != null && mPage.getText().toString().trim().length() > 0) {
            term = term + "&page=" + mPage.getText().toString();
        }
        if (mRpp.getText().toString() != null && mRpp.getText().toString().trim().length() > 0) {
            term = term + "&rpp=" + mRpp.getText().toString();
        }
        if (mTags.getText().toString() != null && mTags.getText().toString().trim().length() > 0) {
            term = term + "&tags" + mTags.getText().toString();
        }

        Call<HomeResponse> homeResponseCall = Zen3TaskApplication.getInstance().initRetrofit().doPhotosSearch(term,
            getResources().getString(R.string.px_consumer_key));
        homeResponseCall.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                Log.e("PX", "PS-OK" + response.code());
                LoaderUtil.getInstance().hideProgress();
                if (response.code() == Constants.OK) {
                    Zen3TaskApplication.getInstance().setHomeResponse(response.body());
                    EventBus.getDefault().postSticky(response.body());
                    mIntent = new Intent(PhotosSearchActivity.this, PhotosGridActivity.class);
                    mIntent.putExtra("term", mTerm.getText().toString());
                    startActivity(mIntent);
                } else {
                    Toast.makeText(PhotosSearchActivity.this, "Photos search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Log.e("PX", "PS-FAIL" + t.getMessage());
                LoaderUtil.getInstance().hideProgress();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
    }
}
