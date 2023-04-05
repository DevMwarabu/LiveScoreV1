package com.laxco.livescorev1.Utils;

import android.app.Application;

import com.applovin.sdk.AppLovinSdk;
import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.laxco.livescorev1.R;
import com.onesignal.OneSignal;

/**
 * This project file is owned by DevMwarabu, johnmwarabuchone@gmail.com.
 * Created on 10/8/21. Copyright (c) 2021 DevMwarabu
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;
    private static Head_Of_App appOpenManager;
    public void onCreate() {
        super.onCreate();
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(this.getResources().getString(R.string.one_signal_app_id));
        Stetho.initializeWithDefaults(this);

        //applovin
        AppLovinSdk.initializeSdk(this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        appOpenManager = new Head_Of_App(this);
    }
}
