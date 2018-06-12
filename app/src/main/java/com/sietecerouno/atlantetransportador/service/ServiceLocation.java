package com.sietecerouno.atlantetransportador.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.sections.HomeActivity;
import com.sietecerouno.atlantetransportador.utils.Preferencias;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gio on 30/11/17.
 */

public class ServiceLocation extends Service
{
    private BroadcastReceiver mReceiver = new LocationIntentReceiver();

    private static final String TAG = "GIO";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 20000;
    private static final float LOCATION_DISTANCE = 10;
    public static Location loc1 = new Location("");
    public static ServiceLocation serviceLocation;

    Location mLastLocation;
    String ubicacion="";
    private Context mContext;
    Preferencias preff;
    private int id_usuario;

    //
    //SendPosition sendPosition = new SendPosition();
    ValueEventListener handler;
    FirebaseApp db;
    public static final String ACTION = "com.sietecerouno.atlantetransportador.service.ServiceLocation.location_change";

    public ServiceLocation()
    {
        //super("service-location");

    }

    @Override
    public void onCreate()
    {
        //Log.i(TAG, "onCreate");
        serviceLocation = this;
        //db = FirebaseFirestore.getInstance().getApp();
        //("https://firestore.googleapis.com/v1beta1/projects/atlante-d7f0b");

        preff = new Preferencias(getApplicationContext());
        id_usuario = preff.misPreferencias.getInt("idUsuario",666);
        //Log.i(TAG, String.valueOf(id_usuario));

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            //Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            //Log.i(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            //Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            //Log.i(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    private void initializeLocationManager() {
        //Log.i(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //Log.i(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        IntentFilter filter = new IntentFilter(ACTION);
        intent.setType("text/plain");
        registerReceiver(mReceiver, filter);
        startForeground();

        return START_STICKY;
    }

    // Run service in foreground so it is less likely to be killed by system
    private void startForeground()
    {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText("Servicio en curso")
                .setContentIntent(null)
                .setOngoing(true)
                .build();
        startForeground(1000,notification);
    }


    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;



        public LocationListener(String provider)
        {
            //Log.i(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            //Log.i(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            //Log.i("GIO", "posr: "+String.valueOf(location));
            //Log.i("GIO", "myID: : "+Manager.getInstance().myID);
            Double _lat =  (location.getLatitude());
            Double _lng =  (location.getLongitude());


            loc1.setLatitude(_lat);
            loc1.setLongitude(_lng);

            GeoPoint geoPoint = new GeoPoint(_lat, _lng);

            if(!Manager.getInstance().isFinishInmediato)
            {
                // Update one field, creating the document if it does not already exist.
                Map<String, GeoPoint> data = new HashMap<>();
                data.put("pos_actual", geoPoint);

                //sendPosition.SendData(geoPoint);
            }
            Intent intent = new Intent();
            intent.putExtra("lat", _lat.toString());
            intent.putExtra("lon", _lng.toString());
            intent.setAction(ACTION);
            sendBroadcast(intent);

            Intent mIntent = new Intent(getBaseContext(), HomeActivity.class);
            sendBroadcast(mIntent);
            Log.i(TAG, "BCR " );

        }

        @Override
        public void onProviderDisabled(String provider)
        {
            //Log.i(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            //Log.i(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            //Log.i(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };




    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    //Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }


}
