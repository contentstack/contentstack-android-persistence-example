package com.contentstack.persistance.view;

import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;
import com.contentstack.persistance.BaseApp;
import com.contentstack.persistance.R;
import com.contentstack.persistance.databinding.ActivitySessionBinding;
import com.contentstack.persistance.syncwrapper.RealmPersistenceHelper;
import com.contentstack.persistance.syncwrapper.SyncManager;
import com.contentstack.persistance.table.Session;
import com.contentstack.sdk.Stack;
import io.realm.Realm;
import io.realm.RealmResults;


public class SessionActivity extends AppCompatActivity implements SyncListener {

    private ActivitySessionBinding binding;
    private final String TAG = SessionActivity.class.getSimpleName();
    private Realm realmInstance;
    private RealmResults<Session> sessionList;
    private SessionAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Session");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session);

        /* get realm instance */
        realmInstance = BaseApp.getRealm();

        /* set up recyclerView*/
        setupRecyclerView();

        /*Swipe Refresh View*/
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                // after refresh is done, remember to call the following code
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    fetchUpdatedContent();
                    adapter.notifyDataSetChanged();
                    binding.swipeRefreshLayout.setRefreshing(false);  // This hides the spinner
                }
            }
        });

    }



    private void setupRecyclerView() {

        // Setting up recyclerView and attached with adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(), linearLayoutManager.getOrientation());
        binding.recyclerView.addItemDecoration(dividerItemDecoration);

        sessionList = realmInstance.where(Session.class).sort("start_time").findAll();
        adapter = new SessionAdapter(sessionList);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    /* This is the code used for implementing Offline persistence*/
    /* Make  stack request for check for updated content
     * SyncManager takes RealmPersistenceHelper instance and stack in the parameter
     * RealmPersistenceHelper can be initialise like below:
     * RealmPersistenceHelper helperInstance = new RealmPersistenceHelper("realm-instance")
     * Pass to SyncManager; */

    private void fetchUpdatedContent() {

        RealmPersistenceHelper realmPersistenceHelper = new RealmPersistenceHelper(realmInstance);
        Stack stack = BaseApp.getStack();
        SyncManager syncManager = new SyncManager(realmPersistenceHelper, stack);
        syncManager.setViewListener(this);
        syncManager.stackRequest();
    }


    @Override
    public void onSuccessListener() {
        Toast.makeText(SessionActivity.this, "completed...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessage(String message) {
        Toast.makeText(SessionActivity.this, "completed...", Toast.LENGTH_SHORT).show();
    }
}
