package com.sietecerouno.atlantetransportador.service;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sietecerouno.atlantetransportador.manager.Manager;

/**
 * Created by Gio on 16/01/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
    String TAG = "GIO";
    FirebaseFirestore db;

    public MyFirebaseInstanceIDService()
    {
        super();
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }

    @Override
    public void onTokenRefresh()
    {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "token: " + refreshedToken);


        db = FirebaseFirestore.getInstance();
        if(Manager.getInstance().myID.length() > 0)
        {

            db.collection("usuarios")
                    .document(Manager.getInstance().myID)
                    .update("token", refreshedToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "save token!");
                }
            });
        }

        super.onTokenRefresh();
    }
}
