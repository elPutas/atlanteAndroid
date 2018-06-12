package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sietecerouno.atlantetransportador.MainActivity;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.utils.NumberTextWatcherForThousand;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class PublishServiceActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{

    SimpleDateFormat simpleDateFormat;
    TextView dateTextView;
    EditText origen_str;
    EditText destino_str;
    TextView ues;
    String TAG = "GIO";
    String cities[];
    Calendar calendar;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_service);

        Manager.getInstance().actualContext = this;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.yellowApp)));


        //bg
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Glide.with(bg.getContext()).load(R.drawable.bg_solo).into(bg);

        db = FirebaseFirestore.getInstance();

        final TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        title.setText("PUBLICAR OFERTA");

        final TextView origen = (TextView) findViewById(R.id.origen);
        final TextView destino = (TextView) findViewById(R.id.destino);

        FrameLayout calendario = (FrameLayout) findViewById(R.id.calendario);
        final TextView horario_txt = (TextView) findViewById(R.id.horario_txt);
        FrameLayout horario = (FrameLayout) findViewById(R.id.horario);

        TextView btn_pulished = (TextView) findViewById(R.id.btn_pulished);
        final EditText precio_ue = (EditText) findViewById(R.id.price);
        final TextView time = (TextView) findViewById(R.id.time);

        final TextView width_txt = (TextView) findViewById(R.id.width_txt);
        final TextView height_txt = (TextView) findViewById(R.id.height_txt);
        final TextView depth_txt = (TextView) findViewById(R.id.depth_txt);
        ues = (TextView) findViewById(R.id.ues);
        origen_str = (EditText) findViewById(R.id.origen_str);
        destino_str = (EditText) findViewById(R.id.destino_str);

        precio_ue.addTextChangedListener(new NumberTextWatcherForThousand(precio_ue));


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                Integer num1 =0;
                Integer num2 =0;
                Integer num3 =0;
                if(width_txt.getText().toString().length() > 0)
                num1 = Integer.parseInt(width_txt.getText().toString());

                if(height_txt.getText().toString().length() > 0)
                num2 = Integer.parseInt(height_txt.getText().toString());

                if(depth_txt.getText().toString().length() > 0)
                num3 = Integer.parseInt(depth_txt.getText().toString());

                if(num1!= null && num2!= null && num3 != null)
                    calculateUes(num1, num2, num3);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        width_txt.addTextChangedListener(textWatcher);
        height_txt.addTextChangedListener(textWatcher);
        depth_txt.addTextChangedListener(textWatcher);



        ImageView ue = (ImageView) findViewById(R.id.ue);
        ue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(PublishServiceActivity.this)
                        .setTitle("UEs")
                        .setMessage("Una UE es una unidad de volumen estimada a una caja de zapatos, sus medidas correspondientes son: 15cm de Alto, 20cm de Ancho y 35cm de Profundo")
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                pDialog.show();

            }
        });

        // set CITIES info from db user
        db.collection("ciudades").addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e)
            {
                cities = new String[documentSnapshots.size()];
                int num=0;
                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments())
                {
                    cities[num] = documentSnapshot.getData().get("ciudad").toString();
                    num++;
                }

                origen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(PublishServiceActivity.this);
                        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
                        builder.setTitle("Selecciona un ciudad de origen");
                        builder.setItems(cities, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int index)
                            {
                                origen.setText(cities[index]);
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                });


                destino.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(PublishServiceActivity.this);
                        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
                        builder.setTitle("Selecciona un ciudad de destino");
                        builder.setItems(cities, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int index)
                            {
                                destino.setText(cities[index]);
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            }
        });


        calendario.setOnClickListener(new View.OnClickListener()
        {


            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                new SpinnerDatePickerDialogBuilder()
                        .context(PublishServiceActivity.this)
                        .callback(PublishServiceActivity.this)
                        .defaultDate(year, month, day)
                        .spinnerTheme(R.style.DatePickerSpinner)
                        .defaultDate(2017, 0, 1)
                        .maxDate(2020, 0, 1)
                        .minDate(year, month, day)
                        .build()
                        .show();



            }
        });

        horario.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String hr[] = new String[]{"Mañana", "Tarde", "Noche"};
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(PublishServiceActivity.this);
                builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
                builder.setTitle("Selecciona un horario");
                builder.setItems(hr, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index)
                    {
                        horario_txt.setText(hr[index]);
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });



        btn_pulished.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(PublishServiceActivity.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                if(!Objects.equals(origen.getText().toString(), ""))
                {
                    if(!Objects.equals(destino.getText().toString(), ""))
                    {
                        if(!Objects.equals(dateTextView.getText().toString(), ""))
                        {
                            if(!Objects.equals(horario_txt.getText().toString(), ""))
                            {
                                if(!Objects.equals(time.getText().toString(), ""))
                                {
                                    if(!Objects.equals(precio_ue.getText().toString(), ""))
                                    {
                                        if(!Objects.equals(ues.getText().toString(), "0"))
                                        {
                                            if(!Objects.equals(origen_str.getText().toString(), ""))
                                            {
                                                if(!Objects.equals(destino_str.getText().toString(), ""))
                                                {
                                                    Integer _num = Integer.parseInt(NumberTextWatcherForThousand.trimCommaOfString(precio_ue.getText().toString()));

                                                    saveService(
                                                            destino.getText().toString(),
                                                            origen.getText().toString(),
                                                            horario_txt.getText().toString(),
                                                            _num,
                                                            Integer.parseInt(time.getText().toString()),
                                                            Integer.parseInt(ues.getText().toString()));
                                                }else{
                                                    pDialog.setTitle("Escribe una dirreción de destino");
                                                    pDialog.show();
                                                }
                                            }else{
                                                pDialog.setTitle("Escribe una dirreción de origen");
                                                pDialog.show();
                                            }
                                        }else{
                                            pDialog.setTitle("Selecciona un alto, ancho y largo para poder calcular tus UEs");
                                            pDialog.show();
                                        }

                                    }else{
                                        pDialog.setTitle("Selecciona una precio por tu espacio");
                                        pDialog.show();
                                    }
                                }else{
                                    pDialog.setTitle("Selecciona el tiempo del reocrrido");
                                    pDialog.show();
                                }
                            }else{
                                pDialog.setTitle("Selecciona un horario");
                                pDialog.show();
                            }

                        }else{
                            pDialog.setTitle("Selecciona una fecha");
                            pDialog.show();
                        }

                    }else{
                        pDialog.setTitle("Falta ciudad de destino");
                        pDialog.show();
                    }

                }else{
                    pDialog.setTitle("Falta ciudad de origen");
                    pDialog.show();
                }
            }
        });

    }

    private void calculateUes(int _n1, int _n2, int _n3)
    {

        //ues.setText(((_n1*_n2*_n3)/10500)+"");
        Double temp = Math.ceil((_n1*_n2*_n3)/10500.0);

        ues.setText(temp.toString().substring(0,temp.toString().indexOf(".")));
    }


    public void saveService(String _ciudad_destino, String _ciudad_origen, String _horario, int _precio_ue, int _tiempo, int _ue)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRefUser = db.collection("usuarios").document(user.getUid());
        DocumentReference docRefV = db.collection("vehiculos").document(Manager.getInstance().vehicleSelected);

        Map<String, Object> ruta = new HashMap<>();
        ruta.put("ciudad_destino", _ciudad_destino);
        ruta.put("ciudad_origen", _ciudad_origen);
        ruta.put("origenStr", origen_str.getText().toString());
        ruta.put("destinoStr", destino_str.getText().toString());
        ruta.put("fecha", calendar.getTime());
        ruta.put("horario", _horario);
        ruta.put("precio_ue", Math.round(_precio_ue/_ue));
        ruta.put("tiempo_recorrido", _tiempo);
        ruta.put("ue_vehiculo", _ue);
        ruta.put("transportista", docRefUser);
        ruta.put("vehiculo", docRefV);

        // Add a new document with a generated ID

        db.collection("pedidos_programados").document()
                .set(ruta)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void _void) {
                        CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(PublishServiceActivity.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                .setTitle("¡Tu Servicio fue publicado!")
                                .setCornerRadius(20)
                                .setTextGravity(Gravity.CENTER)
                                .addButton("CONTINUAR", Color.parseColor("#FFFFFF"), getResources().getColor(R.color.yellowApp), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });

                        pDialog.show();
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateTextView.setText(simpleDateFormat.format(calendar.getTime()));
    }


}
