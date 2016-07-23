package com.amphenol;

import android.app.Application;

import com.pgyersdk.crash.PgyCrashManager;

/**
 * Created by Carl on 2016-07-22 022.
 */
public class PgyApplication extends Application {
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        PgyCrashManager.register(this);
    }
}
