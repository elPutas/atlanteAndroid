package com.sietecerouno.atlantetransportador.sections;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ResumoFinishAllActivity extends AppCompatActivity 
{

    FirebaseFirestore db;
    String idPersonalizado;
    String TAG = "GIO";
    TextView info;
    ImageView img1;
    ImageView img2;
    ImageView img3;

    ImageView mail;
    ImageView phone;

    int finalcolorToSend =0;
    int REQUEST_PHONE_CALL = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumo_finish_all);

        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();

        Manager.getInstance().actualContext = this;

        //get extra
        Intent myIntent = getIntent(); // gets the previously created intent
        String typeKey = myIntent.getStringExtra("idPersonalizado");
        int colorToUse = myIntent.getIntExtra("color", R.color.blueApp);
        final String clientKey = myIntent.getStringExtra("idClient");
        idPersonalizado = typeKey;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(colorToUse)));

        TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("RESUMEN SERVICIO    ");

        finalcolorToSend = colorToUse;

        info = (TextView) findViewById(R.id.info);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);

        mail = (ImageView) findViewById(R.id.mail);
        phone = (ImageView) findViewById(R.id.phone);

        // set USER info from db user
        DocumentReference docRefClient = db.collection("usuarios").document(clientKey);
        docRefClient.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document != null)
                    {
                        Manager.getInstance().actualClientCel_inmediato = document.getData().get("celular").toString();
                        Manager.getInstance().actualClientMail_inmediato = document.getData().get("correo").toString();

                    } else {
                        Log.i(TAG, "No such document inside");
                    }
                } else {
                    Log.i(TAG, "get failed with inside", task.getException());
                }
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));
                sendIntent.putExtra("address", Manager.getInstance().actualClientCel_inmediato);
                sendIntent.putExtra("sms_body", "Buen día, ");
                ResumoFinishAllActivity.this.startActivity(sendIntent);

                /*
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",Manager.getInstance().actualClientMail_inmediato, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Atlante conductor");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Buen día!...");
                ResumoFinishAllActivity.this.startActivity(Intent.createChooser(emailIntent, "Send email..."));
                */
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Manager.getInstance().actualClientCel_inmediato));
                if (ActivityCompat.checkSelfPermission(ResumoFinishAllActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((ResumoFinishAllActivity.this), new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    return;
                }
                ResumoFinishAllActivity.this.startActivity(intent);
            }
        });

        TextView btnNext = (TextView) findViewById(R.id.btn_finish);
        TextView btnChat = (TextView) findViewById(R.id.btn_chat);

        if(R.color.blueApp == colorToUse)
        {
            btnNext.setBackground(getResources().getDrawable(R.drawable.bg_button_blue));
            btnChat.setBackground(getResources().getDrawable(R.drawable.bg_button_blue));
        }
        if(R.color.greenApp == colorToUse)
        {
            btnNext.setBackground(getResources().getDrawable(R.drawable.bg_button_green));
            btnChat.setBackground(getResources().getDrawable(R.drawable.bg_button_green));
        }
        if(R.color.yellowApp == colorToUse)
        {
            btnNext.setBackground(getResources().getDrawable(R.drawable.bg_button_yellow));
            btnChat.setBackground(getResources().getDrawable(R.drawable.bg_button_yellow));
        }


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ResumoFinishAllActivity.this, CloseServiceActivity.class);
                i.putExtra("idClient", clientKey);
                i.putExtra("color", finalcolorToSend);
                i.putExtra("idSelected", idPersonalizado);
                startActivity(i);
                finish();
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ResumoFinishAllActivity.this, ChatActivity.class);
                startActivity(i);
                finish();
            }
        });


        Manager.getInstance().id_client_temp = clientKey;
        Manager.getInstance().idPersonalizado = idPersonalizado;
        Manager.getInstance().arrPhotoThumb = new ArrayList();


        // set USER info from db user
        final MaterialRatingBar myRate = (MaterialRatingBar) findViewById(R.id.myRate);
        final ImageView icon = (ImageView) findViewById(R.id.photo);
        final TextView name = (TextView) findViewById(R.id.name);
        final TextView textPrice = (TextView) findViewById(R.id.textSafe);

        DocumentReference docRefUser = db.collection("usuarios").document(Manager.getInstance().actualClientId_finish);
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
                        name.setText(document.getData().get("nombre").toString() +" "+ document.getData().get("apellido").toString());
                        Long lg = (Long) document.getData().get("calificacion");
                        myRate.setRating(lg.intValue());
                        CropCircleTransformation cropCircleTransformation = new CropCircleTransformation();
                        myRate.setRating(lg.intValue());
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

        PicassoTools.clearCache(Picasso.with(getBaseContext()));

        DocumentReference docRef = db.collection("pedidos").document(typeKey);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        //Log.i(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        textPrice.setText("Valor: $"+document.getData().get("valor").toString());

                        Date dateTemp = (Date) task.getResult().getData().get("fecha_entrega");

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
                                        task.getResult().getData().get("que_envia") + "\n\n\n"+
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
                            Picasso.with(ResumoFinishAllActivity.this)
                                    .load(str_img1)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(img1);
                        }

                        if(str_img2.length() > 0)
                        {
                            Manager.getInstance().arrPhotoThumb.add(str_img2);
                            Picasso.with(ResumoFinishAllActivity.this)
                                    .load(str_img2)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(img2);
                        }

                        if(str_img3.length() > 0)
                        {
                            Manager.getInstance().arrPhotoThumb.add(str_img3);
                            Picasso.with(ResumoFinishAllActivity.this)
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
                final Dialog dialog = new Dialog(ResumoFinishAllActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_viewpager);
                dialog.setCanceledOnTouchOutside(false);

                MyPageAdapter adapter = new MyPageAdapter(ResumoFinishAllActivity.this);
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
                final Dialog dialog = new Dialog(ResumoFinishAllActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_viewpager);
                dialog.setCanceledOnTouchOutside(false);

                MyPageAdapter adapter = new MyPageAdapter(ResumoFinishAllActivity.this);
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
                final Dialog dialog = new Dialog(ResumoFinishAllActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_viewpager);
                dialog.setCanceledOnTouchOutside(false);

                MyPageAdapter adapter = new MyPageAdapter(ResumoFinishAllActivity.this);
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
                Intent i = new Intent(ResumoFinishAllActivity.this, CurrentPedidosActivity.class);
                startActivity(i);
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
