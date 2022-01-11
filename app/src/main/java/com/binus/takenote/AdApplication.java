package com.binus.takenote;

import android.app.Application;

import com.huawei.hms.ads.HwAds;

public class AdApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Ads SDK.
        HwAds.init(this);
    }
}
