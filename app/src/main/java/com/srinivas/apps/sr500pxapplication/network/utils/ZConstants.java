package com.srinivas.apps.sr500pxapplication.network.utils;
/**
 * Created by Srinivas Rupani on 12/7/2017.
 * Copyright (c) 2017 Promantra Inc, All rights reserved.
 */

/**
 * Constants or end points of 500px
 */
public interface ZConstants {
    String SAMPLE = "/documenttype/{Id}";
    String GET_LOGIN_ROLES = "user/getloginroles";
    String LOGIN = "user/login";
    String LOGOUT = "user/Logout";
    String PHOTO_SEARCH = "photos/search";
    String PHOTO_FAVORITE = "photos/{id}/favorite";


    String CONTENT_TYPE = "Content-Type: ";
    String AUTHORIZATION = "Authorization";
    String CM_APPLICATION_JSON = "application/json";
    String CM_H_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";//Content/Media Type,Header
    String H_X_MS_BLOB_KEY = "x-ms-blob-type";
    String H_X_MS_BLOB_VALUE = "BlockBlob";
    String C_H_MULTIPART_FORM_DATA = "multipart/form-data";
    String GSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    String BEARER_HEAD = "Bearer ";
}
