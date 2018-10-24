package com.contentstack.persistance;

import android.app.Application;
import com.contentstack.sdk.Config;
import com.contentstack.sdk.Contentstack;
import com.contentstack.sdk.Stack;

import io.realm.Realm;

public class BaseApp extends Application {

    private static Stack stack;

    @Override
    public void onCreate() {
        super.onCreate();
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
