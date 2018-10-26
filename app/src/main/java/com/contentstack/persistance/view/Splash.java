package com.contentstack.persistance.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.contentstack.persistance.BaseApp;
import com.contentstack.persistance.R;
import com.contentstack.persistance.databinding.ActivitySplashBinding;
import com.contentstack.persistance.manager.RealmPersistenceHelper;
import com.contentstack.persistance.manager.SyncManager;
import com.contentstack.sdk.Stack;

import io.realm.Realm;

public class Splash extends AppCompatActivity implements SyncListener{

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        getSupportActionBar().hide();
        /* Initialise realm */


        initialiseTable();
    }


    private void initialiseTable(){

        // Get Realm instance
        Realm realm = BaseApp.getRealm();
        // Get Stack instance
        Stack stack = BaseApp.getStack();
        // get RealmPersistenceHelper instance
        RealmPersistenceHelper realmPersistenceHelper = new RealmPersistenceHelper(realm);
        // get SyncManager instance by passing realmPersistenceHelper and stack instance
        SyncManager syncManager = new SyncManager(realmPersistenceHelper, stack);
        syncManager.setViewListener(this);
        syncManager.stackRequest();
    }

    @Override
    public void onSuccessListener() {

        Intent intent = new Intent(getApplicationContext(), SessionActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMessage(String message) {
        Toast.makeText(this, "Server is not responding", Toast.LENGTH_SHORT).show();
    }
}
