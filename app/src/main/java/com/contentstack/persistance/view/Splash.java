package com.contentstack.persistance.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.contentstack.persistance.BaseApp;
import com.contentstack.persistance.R;
import com.contentstack.persistance.databinding.ActivitySplashBinding;
import com.contentstack.persistance.syncwrapper.RealmStore;
import com.contentstack.persistance.syncwrapper.SyncManager;
import com.contentstack.persistance.syncwrapper.SyncStore;
import com.contentstack.sdk.Stack;

import io.realm.Realm;

public class Splash extends AppCompatActivity implements SyncListener{

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        getSupportActionBar().hide();
        initialiseTable();
    }


    private void initialiseTable(){

        Realm realm = BaseApp.getRealm();
        Stack stack = BaseApp.getStack();
        RealmStore realmStore = new RealmStore(realm);
        SyncManager syncManager = new SyncManager(realmStore, stack);
        syncManager.setViewListener(this);
        syncManager.stackRequest();
    }

    @Override
    public void onSuccessListener() {
        Log.e("Success:", "Response received");
        Intent intent = new Intent(getApplicationContext(), SessionActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMessage(String message) {
        Log.e("Error:", "Server is not responding");
        Toast.makeText(this, "Server is not responding", Toast.LENGTH_SHORT).show();
    }
}
