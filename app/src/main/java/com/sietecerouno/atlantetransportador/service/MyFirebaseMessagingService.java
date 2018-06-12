package com.sietecerouno.atlantetransportador.service;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.sections.LoginActivity;

/**
 * Created by Gio on 16/01/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        //Log.i("GIO", "From: " + remoteMessage.getFrom());

        if (Manager.getInstance().actualContext != null)
        {
            //Let this be the code in your n'th level thread from main UI thread
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run()
                {
                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(Manager.getInstance().actualContext)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                            .setTextGravity(Gravity.CENTER)
                            .setMessage(remoteMessage.getNotification().getBody())
                            .setTitle(remoteMessage.getNotification().getTitle());

                    pDialog.show();

                }
            });
        }

        super.onMessageReceived(remoteMessage);
    }



    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }
}