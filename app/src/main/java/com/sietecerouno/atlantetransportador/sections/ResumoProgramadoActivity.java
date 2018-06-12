package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.adapters.MyPageAdapter;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.utils.ChargeCreditCard;
import com.sietecerouno.atlantetransportador.utils.SendNotification;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResumoProgramadoActivity extends AppCompatActivity
{

    FirebaseFirestore db;
    String idProgramado;
    String TAG = "GIO";
    TextView info;
    ImageView img1;
    ImageView img2;
    ImageView img3;

    String medioPagoID;
    String tokenPush;
    Integer price;
    Integer numCuotas =0;
    DocumentReference payRef;

    int ue_remove=0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_programado);

        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();

        Manager.getInstance().actualContext = this;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.yellowApp)));

        TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("DETALLES ENVÍO    ");

        //get extra
        Intent myIntent = getIntent(); // gets the previously created intent
        String typeKey = myIntent.getStringExtra("idProgramado");
        final String clientKey = myIntent.getStringExtra("idClient");
        final String ofertKey = myIntent.getStringExtra("idOferta");


        idProgramado = typeKey;

        info = (TextView) findViewById(R.id.info);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);



        TextView btnReject = (TextView) findViewById(R.id.btn_reject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(ResumoProgramadoActivity.this)
                        .setCornerRadius(20)
                        .setTextGravity(Gravity.CENTER)
                        .setTitle("¿Estas seguro que deseas rechazar el envío?")
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

                                Map<String, Object> data = new HashMap<>();
                                data.put("estado", 4);
                                db.collection("pedidos_programados").document(Manager.getInstance().idProgramadoDoc).collection("ofertas").document(ofertKey).set(data, SetOptions.merge());


                                // Update one field, creating the document if it does not already exist.
                                Map<String, Object> dataPedido = new HashMap<>();

                                Map<String, Date> estado = new HashMap<>();
                                estado.put("cancelado", new Date());

                                dataPedido.put("estado", estado);
                                dataPedido.put("estado_actual", 4);
                                db.collection("pedidos").document(Manager.getInstance().idProgramado).set(dataPedido, SetOptions.merge());


                                finish();
                            }
                        });

                pDialog.show();


            }
        });

        TextView btnAccept = (TextView) findViewById(R.id.btn_accept);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int new_ue = Manager.getInstance().eu_total_temp - Manager.getInstance().eu_remove_temp;
                if(new_ue > 0 )
                {
                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(ResumoProgramadoActivity.this)
                            .setCornerRadius(20)
                            .setTextGravity(Gravity.CENTER)
                            .setTitle("¿Estas seguro que deseas Aceptar el envío?")
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

                                    Manager.getInstance().actualId_to_charge = Manager.getInstance().idProgramado;
                                    Manager.getInstance().actualService = "programado";

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("estado", 2);
                                    db.collection("pedidos_programados").document(Manager.getInstance().idProgramadoDoc).collection("ofertas").document(ofertKey).set(data, SetOptions.merge());

                                    Map<String, Object> data2 = new HashMap<>();
                                    data2.put("ue_vehiculo", new_ue);
                                    db.collection("pedidos_programados").document(Manager.getInstance().idProgramadoDoc).set(data2, SetOptions.merge());


                                    // Update one field, creating the document if it does not already exist.
                                    Map<String, Object> dataPedido = new HashMap<>();

                                    Map<String, Date> estado = new HashMap<>();
                                    estado.put("en_curso", new Date());

                                    DocumentReference docRefTransportista = db.collection("usuarios").document(Manager.getInstance().myID);
                                    DocumentReference docRefV = db.collection("vehiculos").document(Manager.getInstance().vehicleSelected);
                                    dataPedido.put("transportista", docRefTransportista);
                                    dataPedido.put("vehiculo_obj", docRefV);
                                    dataPedido.put("estado", estado);
                                    dataPedido.put("estado_actual", 2);
                                    db.collection("pedidos").document(Manager.getInstance().idProgramado).set(dataPedido, SetOptions.merge());


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
                                                    if (document.exists())
                                                    {
                                                        if(document.getData().get("token") != null)
                                                        {
                                                            tokenPush = document.getData().get("token").toString();
                                                            Manager.getInstance().actualTokenPush = tokenPush;
                                                            SendNotification sendNotification = new SendNotification();
                                                            sendNotification._id = tokenPush;
                                                            sendNotification._body = "";
                                                            sendNotification._title = "Tu servicio fue aceptado";
                                                            sendNotification.execute();
                                                        }

                                                    } else {
                                                        Log.i(TAG, "No such document inside");
                                                    }
                                                } else {
                                                    Log.i(TAG, "get failed with inside", task.getException());
                                                }
                                            }
                                        });


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
                                                    chargeCreditCard.charge(price,medioPagoID,numCuotas);

                                                } else {
                                                    Log.i(TAG, "No such document inside");
                                                }
                                            } else {
                                                Log.i(TAG, "get failed with inside", task.getException());
                                            }
                                        }
                                    });



                                    finish();
                                }
                            });
                    pDialog.show();
                }else{
                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(ResumoProgramadoActivity.this)
                            .setCornerRadius(20)
                            .setTextGravity(Gravity.CENTER)
                            .setTitle("Ya no tienes mas espacio en tu vehículo")
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#FF0000"), CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.dismiss();

                                }
                            });
                    pDialog.show();
                }



            }
        });


        Manager.getInstance().idProgramado = idProgramado;
        Manager.getInstance().arrPhotoThumb = new ArrayList();

        PicassoTools.clearCache(Picasso.with(getBaseContext()));

        DocumentReference docRef = db.collection("pedidos").document(typeKey);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        //Log.i(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        Date dateTemp = (Date) task.getResult().getData().get("fecha_entrega");
                        String info_txt =
                                "DATOS DE ENVÍO \n\n" +
                                        "Origen: " +  task.getResult().getData().get("origen") + "\n"+
                                        task.getResult().getData().get("origenStr") + "\n\n"+
                                        "Destino: " + task.getResult().getData().get("destino") + "\n"+
                                        task.getResult().getData().get("destinoStr") + "\n\n"+
                                        "Quien envía: " +
                                        task.getResult().getData().get("quien_entrega") + "\n\n"+
                                        "Quien recibe: " +
                                        task.getResult().getData().get("quien_recibe") + "\n\n"+
                                        "Que deseas enviar: " +
                                        task.getResult().getData().get("que_envia") + "\n\n\n"+
                                        "FECHA MÁXIMA DE ENTREGA: \n" +
                                        Manager.getInstance().dateFormatter.format(dateTemp.getTime()) +
                                        "";


                        DocumentReference docRefPay = (DocumentReference) document.getData().get("mediosPago");
                        if(docRefPay!=null)
                        {
                            medioPagoID =  docRefPay.getId();
                            payRef = (DocumentReference) task.getResult().getData().get("mediosPago");
                        }
                        price = Integer.parseInt(document.getData().get("valor").toString());

                        ArrayList arrayList = (ArrayList) task.getResult().getData().get("imagenes");

                        String str_img1 = "";
                        String str_img2 = "";
                        String str_img3 = "";

                        if(arrayList.size() < 1)
                        {
                            img1.setVisibility(View.GONE);
                            img2.setVisibility(View.GONE);
                            img3.setVisibility(View.GONE);
                        }
                        else
                        {
                            str_img1 = (String) arrayList.get(0);
                        }

                        if(arrayList.size() < 2)
                        {
                            img2.setVisibility(View.GONE);
                            img3.setVisibility(View.GONE);
                        }
                        else
                        {
                            str_img2 = (String) arrayList.get(1);
                        }

                        if(arrayList.size() < 3)
                        {
                            img3.setVisibility(View.GONE);
                        }
                        else
                        {
                            str_img3 = (String) arrayList.get(2);
                        }



                        if(str_img1.length() > 0)
                        {
                            Manager.getInstance().arrPhotoThumb.add(str_img1);
                            Picasso.with(ResumoProgramadoActivity.this)
                                    .load(str_img1)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(img1);
                        }

                        if(str_img2.length() > 0)
                        {
                            Manager.getInstance().arrPhotoThumb.add(str_img2);
                            Picasso.with(ResumoProgramadoActivity.this)
                                    .load(str_img2)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(img2);
                        }

                        if(str_img3.length() > 0)
                        {
                            Manager.getInstance().arrPhotoThumb.add(str_img3);
                            Picasso.with(ResumoProgramadoActivity.this)
                                    .load(str_img3)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(img3);
                        }

                        info.setText(info_txt);
                    } else {
                        Log.i(TAG, "No such document");
                    }
                } else {
                    Log.i(TAG, "get failed with ", task.getException());
                }
            }
        });


        ImageView img1 = (ImageView) findViewById(R.id.img1);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final Dialog dialog = new Dialog(ResumoProgramadoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_viewpager);
                dialog.setCanceledOnTouchOutside(false);

                MyPageAdapter adapter = new MyPageAdapter(ResumoProgramadoActivity.this);
                ViewPager myPager = (ViewPager) dialog.findViewById(R.id.viewPagerDialog);
                myPager.setAdapter(adapter);
                dialog.show();

            }
        });


        ImageView img2 = (ImageView) findViewById(R.id.img2);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final Dialog dialog = new Dialog(ResumoProgramadoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_viewpager);
                dialog.setCanceledOnTouchOutside(false);

                MyPageAdapter adapter = new MyPageAdapter(ResumoProgramadoActivity.this);
                ViewPager myPager = (ViewPager) dialog.findViewById(R.id.viewPagerDialog);
                myPager.setAdapter(adapter);
                myPager.setCurrentItem(1);
                dialog.show();

            }
        });


        ImageView img3 = (ImageView) findViewById(R.id.img3);
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final Dialog dialog = new Dialog(ResumoProgramadoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_viewpager);
                dialog.setCanceledOnTouchOutside(false);

                MyPageAdapter adapter = new MyPageAdapter(ResumoProgramadoActivity.this);
                ViewPager myPager = (ViewPager) dialog.findViewById(R.id.viewPagerDialog);
                myPager.setAdapter(adapter);
                myPager.setCurrentItem(2);
                dialog.show();

            }

        });

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


    /*
            return ContainerImageFragment.newInstance(position, "https://firebasestorage.googleapis.com/v0/b/atlante-d7f0b.appspot.com/o/171108020801111.jpeg?alt=media&token=ff167ba3-1a9c-4bcb-9ede-68ea8b6c045f");
    */
}
