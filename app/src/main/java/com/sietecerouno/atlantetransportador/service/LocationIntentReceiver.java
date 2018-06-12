package com.sietecerouno.atlantetransportador.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.sietecerouno.atlantetransportador.fragments.InmediatoFragment;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.sections.HomeActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gio on 30/11/17.
 */

public class LocationIntentReceiver extends BroadcastReceiver
{
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    String TAG = "GIO";
    @Override
    public void onReceive(Context context, Intent intent)
    {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        String latStr = intent.getStringExtra("lat");
        String lonStr = intent.getStringExtra("lon");

        Double lat = Double.parseDouble(latStr);
        Double lon = Double.parseDouble(lonStr);

        Log.i(TAG, "strLAT: "+ latStr + "   num: " + lat.toString());
        Log.i(TAG, "strLON: "+ lonStr + "   num: "+ lon.toString());

        GeoPoint geoPoint = new GeoPoint(lat, lon);

        Map<String, GeoPoint> data = new HashMap<>();
        data.put("pos_actual", geoPoint);

        FirebaseFirestore.setLoggingEnabled(true);
        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //FirebaseFirestore db = new FirebaseFirestore("https://firestore.googleapis.com/v1beta1/projects/atlante-d7f0b");

        //Manager.getInstance().db.collection("pedidos").document(Manager.getInstance().idToTrack).update("pos_actual", geoPoint);
        //SendPosition sendPosition = new SendPosition();
        //sendPosition.sendData(geoPoint);

        intent.putExtra("lat", latStr);
        intent.putExtra("lon", lonStr);

        //toSend(context);

    }

    // Display lock screen
    private void toSend(Context context)
    {
        //mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //context.startActivity(mIntent);
        Intent mIntent = new Intent(context, HomeActivity.class);
        context.sendBroadcast(mIntent);
        Log.i(TAG, "BCR " );

    }
}
