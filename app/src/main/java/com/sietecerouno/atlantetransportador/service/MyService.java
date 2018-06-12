package com.sietecerouno.atlantetransportador.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Gio on 30/11/17.
 */

public class MyService extends IntentService
{
    public static final String ACTION = "com.sietecerouno.atlantetransportador.service.ServiceLocation";

    public MyService() {
        super("my-service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {

    }
}
