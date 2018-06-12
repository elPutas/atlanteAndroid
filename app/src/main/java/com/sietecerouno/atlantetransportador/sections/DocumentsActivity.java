package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sietecerouno.atlantetransportador.MainActivity;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DocumentsActivity extends AppCompatActivity
{
    String TAG = "GIO";

    TextView photo;
    TextView cc;
    TextView idCar;
    TextView rut;
    TextView tarjetaPropiedad;
    TextView soat;
    TextView tecnomecanica;
    TextView car;

    ImageView ok_photo;
    ImageView ok_cc;
    ImageView ok_idCar;
    ImageView ok_rut;
    ImageView ok_tarjetaPropiedad;
    ImageView ok_soat;
    ImageView ok_tecnomecanica;
    ImageView ok_car;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        db = FirebaseFirestore.getInstance();

        Manager.getInstance().actualContext = this;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("DOCUMENTOS");

        //bg
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Glide.with(bg.getContext()).load(R.drawable.bg_solo).into(bg);

        //fields
        photo = (TextView) findViewById(R.id.photo);
        ok_photo = (ImageView) findViewById(R.id.ok_photo);

        cc = (TextView) findViewById(R.id.cc);
        ok_cc = (ImageView) findViewById(R.id.ok_cc);

        idCar = (TextView) findViewById(R.id.idCar);
        ok_idCar = (ImageView) findViewById(R.id.ok_idCar);

        rut = (TextView) findViewById(R.id.rut);
        ok_rut = (ImageView) findViewById(R.id.ok_rut);

        tarjetaPropiedad = (TextView) findViewById(R.id.tarjetaPropiedad);
        ok_tarjetaPropiedad = (ImageView) findViewById(R.id.ok_tarjetaPropiedad);

        soat = (TextView) findViewById(R.id.soat);
        ok_soat = (ImageView) findViewById(R.id.ok_soat);

        tecnomecanica = (TextView) findViewById(R.id.tecnomecanica);
        ok_tecnomecanica = (ImageView) findViewById(R.id.ok_tecnomecanica);

        car = (TextView) findViewById(R.id.car);
        ok_car = (ImageView) findViewById(R.id.ok_car);

        if(!Manager.getInstance().isFirstV)
        {
            photo.setVisibility(View.GONE);
            ok_photo.setVisibility(View.GONE);
            cc.setVisibility(View.GONE);
            ok_cc.setVisibility(View.GONE);
            idCar.setVisibility(View.GONE);
            ok_idCar.setVisibility(View.GONE);
            rut.setVisibility(View.GONE);
            ok_rut.setVisibility(View.GONE);
        }


        setInvisibleChecks();
        checkFields();

        TextView btn_next = (TextView) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Manager.getInstance().isFirstV)
                {
                    registerVehicle();
                    registerUserDocuments();
                    Intent i = new Intent(DocumentsActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    registerVehicle();
                    Intent i = new Intent(DocumentsActivity.this, HomeActivity.class);
                    i.putExtra("tab", "datos");
                    startActivity(i);
                    finish();
                }

            }
        });






        TextView photo = (TextView) findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DocumentsActivity.this, DocumentWhitPhotoActivity.class);
                i.putExtra("type", "photo");
                startActivity(i);
                finish();
            }
        });

        TextView cc = (TextView) findViewById(R.id.cc);
        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DocumentsActivity.this, DocumentWhitPhotoActivity.class);
                i.putExtra("type", "cc");
                startActivity(i);
                finish();
            }
        });

        TextView idcar = (TextView) findViewById(R.id.idCar);
        idcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DocumentsActivity.this, DocumentWhitPhotoActivity.class);
                i.putExtra("type", "idCar");
                startActivity(i);
                finish();
            }
        });


        TextView rut = (TextView) findViewById(R.id.rut);
        rut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DocumentsActivity.this, DocumentWhitPhotoActivity.class);
                i.putExtra("type", "rut");
                startActivity(i);
                finish();
            }
        });


        TextView tarjetaPropiedad = (TextView) findViewById(R.id.tarjetaPropiedad);
        tarjetaPropiedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DocumentsActivity.this, DocumentWhitPhotoActivity.class);
                i.putExtra("type", "tarjetaPropiedad");
                startActivity(i);
                finish();
            }
        });


        TextView soat = (TextView) findViewById(R.id.soat);
        soat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DocumentsActivity.this, DocumentWhitPhotoActivity.class);
                i.putExtra("type", "soat");
                startActivity(i);
                finish();
            }
        });


        TextView tecnomecanica = (TextView) findViewById(R.id.tecnomecanica);
        tecnomecanica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DocumentsActivity.this, DocumentWhitPhotoActivity.class);
                i.putExtra("type", "tecnomecanica");
                startActivity(i);
                finish();
            }
        });


        TextView car = (TextView) findViewById(R.id.car);
        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DocumentsActivity.this, DocumentWhitPhotoActivity.class);
                i.putExtra("type", "car");
                startActivity(i);
                finish();
            }
        });
    }

    private void setInvisibleChecks()
    {
        ok_tecnomecanica.setVisibility(View.INVISIBLE);
        ok_soat.setVisibility(View.INVISIBLE);
        ok_car.setVisibility(View.INVISIBLE);
        ok_rut.setVisibility(View.INVISIBLE);
        ok_idCar.setVisibility(View.INVISIBLE);
        ok_photo.setVisibility(View.INVISIBLE);
        ok_cc.setVisibility(View.INVISIBLE);
        ok_tarjetaPropiedad.setVisibility(View.INVISIBLE);
    }

    private void checkFields()
    {

        if(Manager.getInstance().photoTemp_v_user_1.length() > 0)
            ok_photo.setVisibility(View.VISIBLE);



        if(Manager.getInstance().photoTemp_v_cc_1.length() > 0
                && Manager.getInstance().photoTemp_v_cc_2.length() > 0)
            ok_cc.setVisibility(View.VISIBLE);


        if(Manager.getInstance().photoTemp_v_id_1.length() > 0
                && Manager.getInstance().photoTemp_v_id_2.length() > 0)
            ok_idCar.setVisibility(View.VISIBLE);



        if(Manager.getInstance().photoTemp_v_rut_1.length() > 0)
            ok_rut.setVisibility(View.VISIBLE);


        if(Manager.getInstance().photoTemp_v_tp_1.length() > 0
                && Manager.getInstance().photoTemp_v_tp_2.length() > 0)
            ok_tarjetaPropiedad.setVisibility(View.VISIBLE);


        if(Manager.getInstance().photoTemp_v_soat_1.length() > 0)
            ok_soat.setVisibility(View.VISIBLE);

        if(Manager.getInstance().photoTemp_v_tec_1.length() > 0)
            ok_tecnomecanica.setVisibility(View.VISIBLE);

        if(Manager.getInstance().photoTemp_v_img_1.length() > 0
                && Manager.getInstance().photoTemp_v_img_2.length() > 0
                && Manager.getInstance().photoTemp_v_img_3.length() > 0
                && Manager.getInstance().photoTemp_v_img_4.length() > 0)
            ok_car.setVisibility(View.VISIBLE);

    }


    private void registerUserDocuments()
    {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("usuarios").document(fbUser.getUid());

        Map<String, Object> estado = new HashMap<>();
        estado.put("pendiente", new Date());

        Map<String, String> cc = new HashMap<>();
        cc.put("f1", Manager.getInstance().photoTemp_v_cc_1);
        cc.put("f2", Manager.getInstance().photoTemp_v_cc_2);


        Map<String, String> id = new HashMap<>();
        id.put("f1", Manager.getInstance().photoTemp_v_id_1);
        id.put("f2", Manager.getInstance().photoTemp_v_id_2);


        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("cedula", cc);
        user.put("estado", estado);
        user.put("foto", Manager.getInstance().photoTemp_v_user_1);
        user.put("licencia", id);
        user.put("user", docRef);
        user.put("rut", Manager.getInstance().photoTemp_v_rut_1);
        // Access a Cloud Firestore instance from your Activity



        // Add a new document with a generated ID

        db.collection("documentos").document()
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void _void) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + _void);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });


        db.collection("usuarios")
                .document(fbUser.getUid())
                .update("foto", Manager.getInstance().photoTemp_v_user_1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "save foto!");
            }
        });
    }

    private void registerVehicle()
    {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("usuarios").document(fbUser.getUid());

        Map<String, Object> estado = new HashMap<>();
        estado.put("pendiente", new Date());

        Map<String, String> tarjeta_propiedad = new HashMap<>();
        tarjeta_propiedad.put("f1", Manager.getInstance().photoTemp_v_tp_1);
        tarjeta_propiedad.put("f2", Manager.getInstance().photoTemp_v_tp_2);

        Map<String, String> car = new HashMap<>();
        car.put("f1", Manager.getInstance().photoTemp_v_img_1);
        car.put("f2", Manager.getInstance().photoTemp_v_img_2);
        car.put("f3", Manager.getInstance().photoTemp_v_img_3);
        car.put("f4", Manager.getInstance().photoTemp_v_img_4);


        Map<String, Object> user = new HashMap<>();
        user.put("color", "");
        //user.put("idUser", fbUser.getUid());
        user.put("estado", estado);
        user.put("modelo", "");
        user.put("placa", "");
        user.put("tipo", Manager.getInstance().idTempVSelected+1);
        user.put("user", docRef);
        user.put("soat", Manager.getInstance().photoTemp_v_soat_1);
        user.put("tecnomecanica", Manager.getInstance().photoTemp_v_tec_1);
        user.put("tarjeta_propiedad", tarjeta_propiedad);
        user.put("fotos", car);

        // Access a Cloud Firestore instance from your Activity



        // Add a new document with a generated ID

        db.collection("vehiculos").document()
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void _void) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + _void);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Manager.getInstance().actualContext = null;
    }

}
