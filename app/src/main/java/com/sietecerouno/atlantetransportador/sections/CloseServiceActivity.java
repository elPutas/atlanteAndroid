package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.fragments.InmediatoFragment;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.service.ServiceLocation;
import com.sietecerouno.atlantetransportador.utils.NumberTextWatcherForThousand;
import com.sietecerouno.atlantetransportador.utils.SendNotification;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloseServiceActivity extends AppCompatActivity
{
    String TAG = "GIO";
    String id;
    OkHttpClient client = new OkHttpClient();

    FirebaseFirestore db;
    FirebaseUser user;

    String idSelected;
    RatingBar rateBar;
    Number myRate;

    EditText myComments;
    EditText price;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_service);

        Manager.getInstance().actualContext = this;

        id=Manager.getInstance().actualClientToken_inmediato;
        //get extra
        Intent myIntent = getIntent(); // gets the previously created intent
        idSelected = myIntent.getStringExtra("idSelected");
        final int colorToUse = myIntent.getIntExtra("color", R.color.blueApp);

        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        rateBar = (RatingBar) findViewById(R.id.myRate);
        rateBar.setStepSize(1);
        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser)
            {
                myRate = rating;
                //Toast.makeText(getApplicationContext(),"Your Selected Ratings  : " + String.valueOf(rating),Toast.LENGTH_LONG).show();
            }
        });

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(colorToUse)));

        myComments  = (EditText) findViewById(R.id.myComments);
        price  = (EditText) findViewById(R.id.price);
        price.addTextChangedListener(new NumberTextWatcherForThousand(price));

        TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("SERVICIO FINALIZADO");

        TextView btnClose = (TextView) findViewById(R.id.btnClose);

        if(R.color.blueApp == colorToUse)
        {
            btnClose.setBackground(getResources().getDrawable(R.drawable.bg_button_blue));
        }
        if(R.color.greenApp == colorToUse)
        {
            btnClose.setBackground(getResources().getDrawable(R.drawable.bg_button_green));
        }
        if(R.color.yellowApp == colorToUse)
        {
            btnClose.setBackground(getResources().getDrawable(R.drawable.bg_button_yellow));
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update one field, creating the document if it does not already exist.
                closeService(colorToUse);

            }
        });

        // set USER info from db user
        DocumentReference _docRefUser = db.collection("usuarios").document(Manager.getInstance().actualClientId_finish);
        _docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        if(document.getData().get("token")!=null)
                        {

                            Manager.getInstance().actualClientToken = document.getData().get("token").toString();

                        }

                    } else {
                        Log.i(TAG, "No such document inside");
                    }
                } else {
                    Log.i(TAG, "get failed with inside", task.getException());
                }
            }
        });


        // set PEDIDO info from db pedidos
        DocumentReference _docRefPedido = db.collection("pedidos").document(idSelected);
        _docRefPedido.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        if(document.getData().get("valor")!=null)
                        {

                            price.setText(document.getLong("valor").toString());

                        }

                    } else {
                        Log.i(TAG, "No such document inside");
                    }
                } else {
                    Log.i(TAG, "get failed with inside", task.getException());
                }
            }
        });


        //bg
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Glide.with(bg.getContext()).load(R.drawable.bg_solo).into(bg);
    }

    public void closeService(int _colorToUse)
    {
        Map<String, Object> data = new HashMap<>();

        Map<String, Date> estado = new HashMap<>();
        estado.put("finalizado", new Date());

        data.put("fecha_entrega", new Date());
        data.put("estado", estado);
        data.put("calificacion_user", myRate);
        data.put("comentario_user", myComments.getText().toString());
        data.put("estado_actual", 3);
        db.collection("pedidos").document(idSelected).set(data, SetOptions.merge());


        Map<String, Object> dataRate = new HashMap<>();
        DocumentReference docRefMe = db.collection("usuarios").document(user.getUid());
        DocumentReference docRefPedido = db.collection("pedidos").document(idSelected);
        DocumentReference docRefUser = db.collection("usuarios").document(Manager.getInstance().actualClientId_finish);

        dataRate.put("fecha", new Date());
        dataRate.put("calificacion", myRate);
        dataRate.put("comentario", myComments.getText().toString());
        dataRate.put("pedido", docRefPedido);
        dataRate.put("califico", docRefMe);
        dataRate.put("usuario", docRefUser);
        db.collection("calificaciones").document().set(dataRate);


        sendPush();

        if(R.color.blueApp == _colorToUse)
        {

            CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(CloseServiceActivity.this)
                    .setHeaderView(R.layout.image_layout)
                    .setMessage("Nos gusta contar con usuarios como tú. Regresa pronto.")
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setCornerRadius(20)
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .addButton("OK", Color.parseColor("#FFFFFF"), getResources().getColor(_colorToUse), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Manager.getInstance().isFinishInmediato = true;
                            //ServiceLocation.serviceLocation.stopSelf();
                            stopService(new Intent(CloseServiceActivity.this, ServiceLocation.class));
                            finish();
                        }
                    });

            pDialog.show();
        }else{
            CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(CloseServiceActivity.this)
                    .setHeaderView(R.layout.image_layout)
                    .setMessage("Nos gusta contar con usuarios como tú. Regresa pronto.")
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setCornerRadius(20)
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .addButton("OK", Color.parseColor("#FFFFFF"), getResources().getColor(_colorToUse), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent i = new Intent(CloseServiceActivity.this, CurrentPedidosActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });

            pDialog.show();


        }


    }

    private void sendPush()
    {
        SendNotification sendNotification = new SendNotification();
        sendNotification._id = Manager.getInstance().actualClientToken;
        sendNotification._body = "";
        sendNotification._title = "Tu servicio a finalizado";
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
