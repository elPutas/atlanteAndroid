package com.sietecerouno.atlantetransportador;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

//import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.sections.ChooseVActivity;
import com.sietecerouno.atlantetransportador.sections.DocumentsActivity;
import com.sietecerouno.atlantetransportador.sections.HomeActivity;
import com.sietecerouno.atlantetransportador.sections.LoginActivity;
import com.sietecerouno.atlantetransportador.sections.RegisterActivity;


import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
{
    private static String TAG = "GIO";
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        //
        db = FirebaseFirestore.getInstance();


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            //Log.i(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            db.collection("usuarios").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    if (task.isSuccessful())
                    {

                        Manager.getInstance().user_photo = task.getResult().getData().get("foto").toString();
                        Manager.getInstance().user_name = task.getResult().getData().get("nombre").toString();
                        Manager.getInstance().user_last_name = task.getResult().getData().get("apellido").toString();
                        Manager.getInstance().user_cel = task.getResult().getData().get("celular").toString();

                        if(task.getResult().getData().get("vehiculo_seleccionado") != null)
                        {
                            DocumentReference vs = (DocumentReference) task.getResult().getData().get("vehiculo_seleccionado");
                            Manager.getInstance().vehicleSelected = vs.getId();


                            DocumentReference docRefV = db.collection("vehiculos").document(Manager.getInstance().vehicleSelected);

                            docRefV.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null)
                                        {
                                            Log.i(TAG, document.getData().get("tipo").toString());
                                            Manager.getInstance().vehicleSelectedType = Integer.parseInt( document.getLong("tipo").toString());

                                        } else {
                                            Log.i(TAG, "No such document inside");
                                        }
                                    } else {
                                        Log.i(TAG, "get failed with inside", task.getException());
                                    }
                                }
                            });

                        }
                        Manager.getInstance().user_email = user.getEmail();
                        Manager.getInstance().myID = user.getUid();
                        Intent i = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(i);

                        FirebaseMessaging.getInstance().subscribeToTopic("conductor");


                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                        Log.i(TAG, "token: " + refreshedToken);



                        db.collection("usuarios")
                                .document(Manager.getInstance().myID)
                                .update("token", refreshedToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "save token!");
                            }
                        });



                    }

                }
            });

        } else {
            // No user is signed in
            //Log.i(TAG, "onAuthStateChanged:signed_out");

            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }





        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.sietecerouno.atlantetransportador",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }






}
