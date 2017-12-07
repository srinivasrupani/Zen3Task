package com.srinivas.apps.sr500pxapplication.network.io;

import com.srinivas.apps.sr500pxapplication.login.dao.HomeResponse;
import com.srinivas.apps.sr500pxapplication.network.utils.ZConstants;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Srinivas Rupani on 12/7/2017.
 * Copyright (c) 2017 Promantra Inc, All rights reserved.
 */
/*This interface contains all network related REST API calls*/
public interface Zen3RestService {
    //    term,tag,page,rpp,tags
    @Headers({ZConstants.CONTENT_TYPE + "" + ZConstants.CM_APPLICATION_JSON})
    @GET(ZConstants.PHOTO_SEARCH)
    Call<HomeResponse> doPhotosSearch(@Query("term") String searchQuery, @Query("consumer_key") String
                                          consumerKey);

    @Headers({ZConstants.CONTENT_TYPE + "" + ZConstants.CM_APPLICATION_JSON})
    @GET(ZConstants.PHOTO_FAVORITE)
    Call<ResponseBody> photoFavorite(@Path("id") Integer photoId, @Query("consumer_key") String consumerKey);
}
