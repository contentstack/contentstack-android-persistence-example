package com.contentstack.persistance;
import android.app.Application;
import com.contentstack.sdk.Config;
import com.contentstack.sdk.Contentstack;
import com.contentstack.sdk.Error;
import com.contentstack.sdk.Stack;
import com.contentstack.sdk.SyncResultCallBack;
import com.contentstack.sdk.SyncStack;

import java.util.Date;

import io.realm.Realm;

public class BaseApp extends Application {
    private static Stack stack;

    @Override
    public void onCreate() {
        super.onCreate();
        //Multidex.install();
        Realm.init(getApplicationContext());
        Config config = new Config();
        config.setHost(BuildConfig.BASE_URL);
        try {
            stack = Contentstack.stack(this, BuildConfig.API_KEY, BuildConfig.DELIVERY_TOKEN, BuildConfig.ENVIRONMENT, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Stack getStack() { return stack; }

    public static Realm getRealm(){ return Realm.getDefaultInstance(); }
}
