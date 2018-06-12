package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.utils.ChargeCreditCard;
import com.sietecerouno.atlantetransportador.utils.NumberTextWatcherForThousand;
import com.sietecerouno.atlantetransportador.utils.SendNotification;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class OfertActivity extends AppCompatActivity
{

    String TAG = "GIO";
    FirebaseFirestore db;
    FirebaseUser user;

    String clientToken = "";
    String medioPagoID = "";

    TextView myComments;
    EditText value_txt;
    Integer value = 0;
    Integer numCuotas = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofert);
        Manager.getInstance().actualContext = this;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.greenApp)));

        //bg
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Glide.with(bg.getContext()).load(R.drawable.bg_solo).into(bg);

        TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("OFERTA SERVICIO PERSONALIZADO   ");

        //get extra
        Intent myIntent = getIntent(); // gets the previously created intent
        final String typeKey = myIntent.getStringExtra("type");
        final String idKey = myIntent.getStringExtra("idOferta");
        String clientKey = myIntent.getStringExtra("idClient");

        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        final TextView btnNext = (TextView) findViewById(R.id.btn_next);
        final TextView btn_aceptar = (TextView) findViewById(R.id.btn_aceptar);
        final TextView btn_rechazar = (TextView) findViewById(R.id.btn_rechazar);
        final EditText contraoferta_txt = (EditText) findViewById(R.id.contraoferta_txt);
        final TextView contraoferta = (TextView) findViewById(R.id.contraoferta);


        contraoferta.setVisibility(View.INVISIBLE);

        final ConstraintLayout btns_contraoferta = (ConstraintLayout) findViewById(R.id.btns_contraoferta);
        myComments = (TextView) findViewById(R.id.comment);
        value_txt = (EditText) findViewById(R.id.value);

        value_txt.addTextChangedListener(new NumberTextWatcherForThousand(value_txt));

        if(Objects.equals("Pendiente", typeKey)  || Objects.equals("Contraoferta", typeKey) || Objects.equals("Rechazado", typeKey))
        {
            btnNext.setVisibility(View.INVISIBLE);
            db.collection("pedidos_personalizados").document(Manager.getInstance().idPersonalizadoDoc)
                    .collection("ofertas")
                    .document(idKey)
                    .get()
                    .addOnCompleteListener(
                            new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        myComments.setText(task.getResult().getData().get("comentario").toString());
                                        value_txt.setText(task.getResult().getData().get("oferta").toString());
                                        value_txt.setEnabled(false);
                                        myComments.setEnabled(false);



                                        if(Objects.equals("Contraoferta", typeKey))
                                        {

                                            contraoferta_txt.addTextChangedListener(new NumberTextWatcherForThousand(contraoferta_txt));
                                            contraoferta_txt.setText(task.getResult().getLong("contraoferta").toString());
                                            value = Integer.parseInt(task.getResult().getLong("contraoferta").toString());

                                            contraoferta.setVisibility(View.VISIBLE);
                                            btns_contraoferta.setVisibility(View.VISIBLE);
                                            btn_aceptar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view)
                                                {
                                                    // Create Alert using Builder
                                                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(OfertActivity.this)
                                                            .setCornerRadius(20)
                                                            .setTextGravity(Gravity.CENTER)
                                                            .setTitle("¿ Estas seguro que deseas aceptar la contraoferta?")
                                                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                            .addButton("NO", Color.parseColor("#FFFFFF"), Color.parseColor("#FF0000"), CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int i) {
                                                                    dialog.dismiss();

                                                                }
                                                            })
                                                            .addButton("SI", Color.parseColor("#FFFFFF"), Color.parseColor("#578936"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which)
                                                                {
                                                                    DocumentReference docRefT = db.collection("usuarios").document(user.getUid());
                                                                    DocumentReference docRefV = db.collection("vehiculos").document(Manager.getInstance().vehicleSelected);  //CHANGE ID BY ADMIN

                                                                    Map<String, Object> data = new HashMap<>();
                                                                    data.put("estado_actual", 2);
                                                                    data.put("transportista", docRefT);
                                                                    data.put("vehiculo_obj", docRefV);
                                                                    db.collection("pedidos_personalizados").document(Manager.getInstance().idPersonalizadoDoc).set(data, SetOptions.merge());



                                                                    // Update one field, creating the document if it does not already exist.
                                                                    Map<String, Object> dataPedido = new HashMap<>();

                                                                    Map<String, Date> estado = new HashMap<>();
                                                                    estado.put("en_curso", new Date());

                                                                    dataPedido.put("valor", value);
                                                                    dataPedido.put("transportista", docRefT);
                                                                    dataPedido.put("estado", estado);
                                                                    dataPedido.put("estado_actual", 2);
                                                                    dataPedido.put("vehiculo_obj", docRefV);
                                                                    db.collection("pedidos").document(Manager.getInstance().idPersonalizado).set(dataPedido, SetOptions.merge());

                                                                    sendPush("Tu servicio fue aceptado","aceptaron tu contra oferta",clientToken);

                                                                    checkPay();
                                                                    finish();



                                                                }
                                                            });

                                                    pDialog.show();

                                                }
                                            });

                                            btn_rechazar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view)
                                                {

                                                    // Create Alert using Builder
                                                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(OfertActivity.this)
                                                            .setCornerRadius(20)
                                                            .setTextGravity(Gravity.CENTER)
                                                            .setTitle("¿ Estas seguro que deseas rechazar la contraoferta?")
                                                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                            .addButton("NO", Color.parseColor("#FFFFFF"), Color.parseColor("#FF0000"), CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int i) {
                                                                    dialog.dismiss();

                                                                }
                                                            })
                                                            .addButton("SI", Color.parseColor("#FFFFFF"), Color.parseColor("#578936"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Map<String, Object> data = new HashMap<>();
                                                                    data.put("estado", false);
                                                                    db.collection("pedidos_personalizados").document(Manager.getInstance().idPersonalizadoDoc).collection("ofertas").document(idKey).set(data, SetOptions.merge());
                                                                    sendPush("Tu oferta fue rechazada"," no aceptaron tu oferta",clientToken);
                                                                    finish();
                                                                }
                                                            });

                                                    pDialog.show();



                                                }
                                            });

                                        }else{
                                            btns_contraoferta.setVisibility(View.GONE);
                                        }

                                    } else {
                                        Log.i(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
        }else{
            btnNext.setVisibility(View.VISIBLE);
        }


        final ImageView icon = (ImageView) findViewById(R.id.photoUser);
        final MaterialRatingBar myRate = (MaterialRatingBar) findViewById(R.id.myRate);
        final TextView name = (TextView) findViewById(R.id.name);

        // set USER info from db user
        DocumentReference docRefUser = db.collection("usuarios").document(clientKey);
        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document != null)
                    {
                        CropCircleTransformation cropCircleTransformation = new CropCircleTransformation();
                        String completeName = document.getData().get("nombre") + " " + document.getData().get("apellido");
                        Long lg = (Long) document.getData().get("calificacion");
                        myRate.setRating(lg.intValue());
                        name.setText(completeName);

                        if(document.getData().get("token") != null)
                            clientToken = (String) document.getData().get("token");
                        else
                            clientToken = "";
                        Manager.getInstance().actualTokenPush = clientToken;

                        Glide.with(icon.getContext())
                                .load(document.getData().get("foto").toString())
                                .apply(RequestOptions.bitmapTransform(cropCircleTransformation))
                                .into(icon);

                    } else {
                        Log.i(TAG, "No such document inside");
                    }
                } else {
                    Log.i(TAG, "get failed with inside", task.getException());
                }
            }
        });




        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update one field, creating the document if it does not already exist.

                Map<String, Object> data = new HashMap<>();
                DocumentReference docRefUser = db.collection("usuarios").document(user.getUid());
                DocumentReference docRefV = db.collection("vehiculos").document(Manager.getInstance().vehicleSelected);

                //Integer _num = Integer.parseInt(value_txt.getText().toString());
                Integer _num = Integer.parseInt(NumberTextWatcherForThousand.trimCommaOfString(value_txt.getText().toString()));

                data.put("transportista", docRefUser);
                data.put("estado", true);
                data.put("comentario", myComments.getText().toString());
                data.put("oferta", _num);
                data.put("vehiculo", docRefV);
                db.collection("pedidos_personalizados").document(Manager.getInstance().idPersonalizadoDoc).collection("ofertas").document().set(data);

                sendPush("Nueva oferta","Tu servicio tiene una nueva oferta",clientToken);
                finish();
            }
        });
    }

    private void checkPay()
    {
        DocumentReference docRef = db.collection("pedidos").document(Manager.getInstance().idPersonalizado);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {

                        DocumentReference payRef = (DocumentReference) task.getResult().getData().get("mediosPago");
                        medioPagoID = payRef.getId();

                        Manager.getInstance().actualId_to_charge = Manager.getInstance().idPersonalizado;
                        Manager.getInstance().actualService = "personalizado";

                        // set CUOTAS info from db user
                        payRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task)
                            {
                                if (task.isSuccessful())
                                {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists())
                                    {
                                        String cuotas = document.getData().get("cuotas").toString();
                                        Log.i(TAG, cuotas);
                                        numCuotas = Integer.parseInt(cuotas);

                                        ChargeCreditCard chargeCreditCard = new ChargeCreditCard();
                                        chargeCreditCard.charge(value,medioPagoID,numCuotas);

                                    } else {
                                        Log.i(TAG, "No such document inside");
                                    }
                                } else {
                                    Log.i(TAG, "get failed with inside", task.getException());
                                }
                            }
                        });





                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }



    private void sendPush(String _title, String _body, String _to)
    {
        SendNotification sendNotification = new SendNotification();
        sendNotification._id = _to;
        sendNotification._body = _body;
        sendNotification._title = _title;
        sendNotification.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Manager.getInstance().actualContext = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
