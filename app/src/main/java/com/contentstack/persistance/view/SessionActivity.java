package com.contentstack.persistance.view;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.contentstack.persistance.BaseApp;
import com.contentstack.persistance.R;
import com.contentstack.persistance.databinding.ActivitySessionBinding;
import com.contentstack.persistance.syncwrapper.RealmStore;
import com.contentstack.persistance.syncwrapper.SyncManager;
import com.contentstack.persistance.table.Session;
import com.contentstack.sdk.Stack;
import java.util.Objects;
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
        Objects.requireNonNull(getSupportActionBar()).setTitle("Session");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session);
        realmInstance = BaseApp.getRealm();
        setupRecyclerView();
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (binding.swipeRefreshLayout.isRefreshing()) {
                fetchUpdatedContent();
                adapter.notifyDataSetChanged();
                binding.swipeRefreshLayout.setRefreshing(false);  // This hides the spinner
            }
        });

    }



    private void setupRecyclerView() {
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
        RealmStore realmPersistenceHelper = new RealmStore(realmInstance);
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
