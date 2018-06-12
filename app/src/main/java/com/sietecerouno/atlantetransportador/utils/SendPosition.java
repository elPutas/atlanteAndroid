package com.sietecerouno.atlantetransportador.utils;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.sietecerouno.atlantetransportador.manager.Manager;

import java.util.HashMap;

/**
 * Created by Gio on 30/11/17.
 */

public class SendPosition
{
     void sendData(GeoPoint geoPoint)
    {
        Log.i("GIO", geoPoint.toString());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //db.collection("pedidos").document(Manager.getInstance().idToTrack).update("pos_actual", geoPoint);
    }
}
