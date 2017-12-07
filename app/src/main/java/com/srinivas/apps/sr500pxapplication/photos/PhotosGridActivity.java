package com.srinivas.apps.sr500pxapplication.photos;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.srinivas.apps.sr500pxapplication.R;
import com.srinivas.apps.sr500pxapplication.Zen3TaskApplication;
import com.srinivas.apps.sr500pxapplication.login.dao.HomeResponse;
import com.srinivas.apps.sr500pxapplication.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Srinivas Rupani on 12/7/2017.
 * Copyright (c) 2017 Promantra Inc, All rights reserved.
 */
public class PhotosGridActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    private GridView mPhotosGrid;
    private PhotosGridAdapter photosGridAdapter;
    private int totalPages = Zen3TaskApplication.getInstance().getHomeResponse().getTotalPages(), mClickCount =
        Zen3TaskApplication.getInstance().getHomeResponse().getCurrentPage();
    private ProgressBar mProgressBar;
    private HomeResponse homeResponse = null;
    private String term;

    //Auto loading related
    private final int AUTOLOAD_THRESHOLD = 4;
    private final int MAXIMUM_ITEMS = Zen3TaskApplication.getInstance().getHomeResponse().getTotalItems();
    private Handler mHandler;
    private boolean mIsLoading = false;
    private boolean mMoreDataAvailable = true;
    private boolean mWasLoading = false;

    private Runnable mAddItemsRunnable = new Runnable() {
        @Override
        public void run() {
            handlePhotosSearch(mClickCount);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            term = extras.getString("term");
        }
        setContentView(R.layout.activity_photos_grid);
        initView();
    }

    private void initView() {
        mPhotosGrid = findViewById(R.id.photosGrid);
        mProgressBar = findViewById(R.id.progress);

        if (photosGridAdapter == null) {
            photosGridAdapter = new PhotosGridAdapter(this);
        }
        mProgressBar.setVisibility(View.GONE);
        mPhotosGrid.setVisibility(View.VISIBLE);
        mPhotosGrid.setAdapter(photosGridAdapter);
        mHandler = new Handler();
        photosGridAdapter.refresh(homeResponse.getPhotos());
        mPhotosGrid.setOnScrollListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN, priority = 1)
    public void onEvent(HomeResponse _homeResponse) {
        homeResponse = _homeResponse;
        EventBus.getDefault().removeStickyEvent(this);
    }

    private void handlePhotosSearch(final int mClickCount) {
        Log.e("PS1", "Page No : " + mClickCount);
        Call<HomeResponse> homeResponseCall = Zen3TaskApplication.getInstance().initRetrofit().doPhotosSearch(term,
            getResources().getString(R.string.px_consumer_key));
        homeResponseCall.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                Log.e("PX", "PS-OK" + response.code());
                if (response.code() == Constants.OK) {
                    Zen3TaskApplication.getInstance().setHomeResponse(response.body());
                    homeResponse = response.body();
                    if (totalPages >= PhotosGridActivity.this.mClickCount) {
                        PhotosGridActivity.this.mClickCount = PhotosGridActivity.this.mClickCount + 1;
                        Log.e("PS2", "Page No : " + mClickCount);
                        mProgressBar.setVisibility(View.GONE);
                        mPhotosGrid.setVisibility(View.VISIBLE);
                        photosGridAdapter.addMoreItems(homeResponse.getPhotos());
                        mIsLoading = false;
                        mMoreDataAvailable = true;
                    } else {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mPhotosGrid.setVisibility(View.GONE);
                        Toast.makeText(PhotosGridActivity.this, "No more photos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PhotosGridActivity.this, "Photos search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Log.e("PX", "PS-FAIL" + t.getMessage());
                mProgressBar.setVisibility(View.GONE);
                mPhotosGrid.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!mIsLoading && mMoreDataAvailable) {
            if (totalItemCount >= MAXIMUM_ITEMS) {
                mMoreDataAvailable = false;
                mProgressBar.setVisibility(View.GONE);
                mPhotosGrid.setVisibility(View.VISIBLE);
            } else if (totalItemCount - AUTOLOAD_THRESHOLD <= firstVisibleItem + visibleItemCount) {
                mIsLoading = true;
                mProgressBar.setVisibility(View.VISIBLE);
                mPhotosGrid.setVisibility(View.GONE);
                mHandler.postDelayed(mAddItemsRunnable, 1000);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mWasLoading) {
            mWasLoading = false;
            mIsLoading = true;
            mProgressBar.setVisibility(View.VISIBLE);
            mPhotosGrid.setVisibility(View.GONE);
            mHandler.postDelayed(mAddItemsRunnable, 1000);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mAddItemsRunnable);
        mWasLoading = mIsLoading;
        mIsLoading = false;
        mProgressBar.setVisibility(View.GONE);
        mPhotosGrid.setVisibility(View.VISIBLE);
    }
}
