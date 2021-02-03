package com.contentstack.persistance.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.contentstack.persistance.BaseApp;
import com.contentstack.persistance.R;
import com.contentstack.persistance.databinding.ActivitySplashBinding;
import com.contentstack.persistance.syncwrapper.RealmStore;
import com.contentstack.persistance.syncwrapper.SyncManager;
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
