package com.srinivas.apps.sr500pxapplication.login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fivehundredpx.api.FiveHundredException;
import com.fivehundredpx.api.auth.AccessToken;
import com.fivehundredpx.api.tasks.XAuth500pxTask;
import com.srinivas.apps.sr500pxapplication.R;
import com.srinivas.apps.sr500pxapplication.Zen3TaskApplication;
import com.srinivas.apps.sr500pxapplication.photos.PhotosSearchActivity;
import com.srinivas.apps.sr500pxapplication.utils.LoaderUtil;
import com.srinivas.apps.sr500pxapplication.utils.RegExValidationUtil;
/**
 * Created by Srinivas Rupani on 12/7/2017.
 * Copyright (c) 2017 Promantra Inc, All rights reserved.
 */
public class MainActivity extends AppCompatActivity implements XAuth500pxTask.Delegate {
    private AccessToken mAccessToken;
    private EditText mUserName, mPasscode;
    private Button mSubmit;
    private XAuth500pxTask loginTask;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mUserName = findViewById(R.id.userName);
        mPasscode = findViewById(R.id.passcode);
        mSubmit = findViewById(R.id.doLogin);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    return;
                } else {
                    handleLoginProcess();
                }
            }
        });
    }

    private void handleLoginProcess() {
        LoaderUtil.getInstance().showProgress(this,"Logging in..!");
        loginTask = new XAuth500pxTask(this);

        loginTask.execute(getString(R.string.px_consumer_key), getString(R.string.px_consumer_secret), mUserName
                .getText().toString(),mPasscode.getText().toString());
    }

    private boolean validate() {
        boolean isValid = true;
        if (isEmpty(mUserName) && isEmpty(mPasscode)) {
            isValid = false;
            mUserName.setError("enter user name");
            mPasscode.setError("enter password");
            return isValid;
        }
        if (isEmpty(mUserName)) {
            isValid = false;
            mUserName.requestFocus();
            mUserName.setError("enter user name");
            return isValid;
        } else if (!(mUserName.getText().toString()).matches(RegExValidationUtil.EMAIL)) {
            isValid = false;
            mUserName.requestFocus();
            mUserName.setError(getString(R.string.error_invalid_emailid));
            return isValid;
        } else {
            mUserName.setError(null);
        }

        if (isEmpty(mPasscode)) {
            isValid = false;
            mPasscode.requestFocus();
            mPasscode.setError("enter password");
            return isValid;
        }
        return isValid;
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onSuccess(AccessToken result) {
        Log.e("PX", "L-OK");
        LoaderUtil.getInstance().hideProgress();
        mAccessToken = result;
        Zen3TaskApplication.getInstance().setLoginResponse(result);
        mIntent = new Intent(this, PhotosSearchActivity.class);
        startActivity(mIntent);
    }

    @Override
    public void onFail(FiveHundredException e) {
        Log.e("PX", "L-FAIL");
        LoaderUtil.getInstance().hideProgress();
    }
}
