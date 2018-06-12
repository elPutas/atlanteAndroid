package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.adapters.MyPageAdapter;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ResumoPersonalizadoActivity extends AppCompatActivity
{

    FirebaseFirestore db;
    String idPersonalizado;
    String TAG = "GIO";
    TextView info;
    ImageView img1;
    ImageView img2;
    ImageView img3;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_personalizado);

        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();

        Manager.getInstance().actualContext = this;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.greenApp)));

        TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("RESUMEN SERVICIO");

        //get extra
        Intent myIntent = getIntent(); // gets the previously created intent
        String typeKey = myIntent.getStringExtra("idPersonalizado");
        final String clientKey = myIntent.getStringExtra("idClient");
        idPersonalizado = typeKey;

        info = (TextView) findViewById(R.id.info);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);


        TextView btnNext = (TextView) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Manager.getInstance().vehicleSelected != "")
                {
                    Intent i = new Intent(ResumoPersonalizadoActivity.this, OfertActivity.class);
                    i.putExtra("idClient", clientKey);
                    startActivity(i);
                    finish();
                }else{
                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(ResumoPersonalizadoActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setCornerRadius(20)
                            .setTextGravity(Gravity.CENTER)
                            .setTitle("¡Espera!")
                            .setMessage("Primero debes seleccionar un vehículo.")
                            .addButton("Selecciona un vehiculo", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent i = new Intent(ResumoPersonalizadoActivity.this, HomeActivity.class);
                                    i.putExtra("tab", "datos");
                                    startActivity(i);

                                }
                            })
                            .addButton("Cancelar", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            });

                    pDialog.show();
                }
            }
        });



        Manager.getInstance().arrPhotoThumb = new ArrayList();

        PicassoTools.clearCache(Picasso.with(getBaseContext()));

        DocumentReference docRef = db.collection("pedidos").document(typeKey);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {

                        Date dateTemp = (Date) task.getResult().getData().get("fecha_entrega");
                        //Log.i(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        String info_txt =
                                "DATOS DE ENVÍO \n\n" +
                                        "Origen: " +
                                        task.getResult().getData().get("origenStr") + "\n\n"+
                                        "Destino: " +
                                        task.getResult().getData().get("destinoStr") + "\n\n"+
                                        "Quien envía: " +
                                        task.getResult().getData().get("quien_entrega") + "\n\n"+
                                        "Quien recibe: " +
                                        task.getResult().getData().get("quien_recibe") + "\n\n"+
                                        "Que deseas enviar: " +
                                        task.getResult().getData().get("que_envia") + "\n\n"+
                                        "FECHA MÁXIMA DE ENTREGA: \n" +
                                        Manager.getInstance().dateFormatter.format(dateTemp.getTime()) +
                                        "";


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
                            Picasso.with(ResumoPersonalizadoActivity.this)
                                    .load(str_img1)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(img1);
                        }

                        if(str_img2.length() > 0)
                        {
                            Manager.getInstance().arrPhotoThumb.add(str_img2);
                            Picasso.with(ResumoPersonalizadoActivity.this)
                                    .load(str_img2)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(img2);
                        }

                        if(str_img3.length() > 0)
                        {
                            Manager.getInstance().arrPhotoThumb.add(str_img3);
                            Picasso.with(ResumoPersonalizadoActivity.this)
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
                final Dialog dialog = new Dialog(ResumoPersonalizadoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_viewpager);
                dialog.setCanceledOnTouchOutside(false);

                MyPageAdapter adapter = new MyPageAdapter(ResumoPersonalizadoActivity.this);
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
                final Dialog dialog = new Dialog(ResumoPersonalizadoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_viewpager);
                dialog.setCanceledOnTouchOutside(false);

                MyPageAdapter adapter = new MyPageAdapter(ResumoPersonalizadoActivity.this);
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
                final Dialog dialog = new Dialog(ResumoPersonalizadoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_viewpager);
                dialog.setCanceledOnTouchOutside(false);

                MyPageAdapter adapter = new MyPageAdapter(ResumoPersonalizadoActivity.this);
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
