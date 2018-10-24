package com.contentstack.persistance.view;

public interface SyncListener {

    void onSuccessListener();
    void onMessage(String message);
}
