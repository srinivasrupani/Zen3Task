package com.srinivas.apps.sr500pxapplication.photos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.srinivas.apps.sr500pxapplication.R;
import com.srinivas.apps.sr500pxapplication.Zen3TaskApplication;
import com.srinivas.apps.sr500pxapplication.login.dao.Photo;
import com.srinivas.apps.sr500pxapplication.utils.Constants;
import com.srinivas.apps.sr500pxapplication.utils.LoaderUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Srinivas Rupani on 12/7/2017.
 * Copyright (c) 2017 Promantra Inc, All rights reserved.
 */

public class PhotosGridAdapter extends BaseAdapter {
    private List<Photo> photoList = new ArrayList<>();
    private Context mContext;
    private ViewHolder viewHolder;
    private Intent mIntent;
    private int mCount = 20;

    public PhotosGridAdapter(Activity activity) {
        mContext = activity;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.row_photos_list_item, null);
        }

        viewHolder = new ViewHolder();
        viewHolder.mImgName = view.findViewById(R.id.photoName);
        viewHolder.mImageView = view.findViewById(R.id.photo);

        viewHolder.mImgName.setText(photoList.get(position).getName());

        Picasso.with(mContext)
            .load(photoList.get(position).getImageUrl())
            .placeholder(R.mipmap.ic_launcher)// optional
            .error(R.mipmap.ic_launcher)// optional
            //.resize(400,400)// optional
            .into(viewHolder.mImageView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPhotoMetaDataDialog(position);
            }
        });

        return view;
    }

    private void displayPhotoMetaDataDialog(final int _position) {
        Dialog photoDetailDlg = new Dialog(mContext);
        photoDetailDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        photoDetailDlg.setContentView(R.layout.dialog_photo_detail_view);
        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.80);
        int height = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.90);
        photoDetailDlg.getWindow().setLayout(width, height);
        TextView name = photoDetailDlg.findViewById(R.id.selectedPhotoName);
        ImageView photo = photoDetailDlg.findViewById(R.id.selectedPhoto);
        TextView description = photoDetailDlg.findViewById(R.id.selectedPhotoDescription);
        TextView mapDetails = photoDetailDlg.findViewById(R.id.selectedPhotoMapDetails);
        ImageView favorite = photoDetailDlg.findViewById(R.id.selectedPhotoFavorite);

        name.setText(photoList.get(_position).getName());
        Picasso.with(mContext)
            .load(photoList.get(_position).getImageUrl())
            .placeholder(R.mipmap.ic_launcher)// optional
            .error(R.mipmap.ic_launcher)// optional
            //.resize(400,400)// optional
            .into(photo);

        description.setText((CharSequence) photoList.get(_position).getDescription());
        mapDetails.setText("Latitude : " + photoList.get(_position).getLatitude() + " , \n Longitude : " + photoList.get
            (_position).getLongitude());

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFavoriteOrUn(_position);
            }
        });

        photoDetailDlg.show();
    }

    private void doFavoriteOrUn(int position1) {
        LoaderUtil.getInstance().showProgress(mContext, "Photo favorite in process..!");
        Call<ResponseBody> photoFavorite = Zen3TaskApplication.getInstance().initRetrofit().photoFavorite(photoList.get
            (position1).getId(), mContext.getResources().getString(R.string.px_consumer_key));
        photoFavorite.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                LoaderUtil.getInstance().hideProgress();
                if (response.code() == Constants.OK) {
                    Toast.makeText(mContext, "Photos favorite success", Toast.LENGTH_SHORT).show();
                } else if (response.code() == Constants.FORBIDDEN) {
                    Toast.makeText(mContext, "The photo specified is already in your favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Photos favorite failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("PF", "PF-FAIL" + t.getMessage());
                LoaderUtil.getInstance().hideProgress();
            }
        });
    }

    public void refresh(List<Photo> photoList1) {
        Log.e("PSL", "PSL" + photoList1.size());
        photoList.clear();
        photoList.addAll(photoList1);
        notifyDataSetChanged();
    }

    public void addMoreItems(List<Photo> photoList1) {
        mCount += photoList1.size();
        photoList.addAll(photoList.size() - 1, photoList1);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView mImgName;
        ImageView mImageView;
    }
}
