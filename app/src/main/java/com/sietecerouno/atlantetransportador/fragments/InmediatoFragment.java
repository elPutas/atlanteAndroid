package com.sietecerouno.atlantetransportador.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.sections.CloseServiceActivity;
import com.sietecerouno.atlantetransportador.sections.HomeActivity;
import com.sietecerouno.atlantetransportador.sections.RegisterActivity;
import com.sietecerouno.atlantetransportador.service.ServiceLocation;
import com.sietecerouno.atlantetransportador.utils.ChargeCreditCard;
import com.sietecerouno.atlantetransportador.utils.SendNotification;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class InmediatoFragment extends Fragment {
    private String TAG = "GIO";
    String id;
    String medioPagoID;
    Integer numCuotas;
    Integer price;
    String msj = "Servicio inmediato tomado";
    String action = "open_inmediato";
    OkHttpClient client = new OkHttpClient();

    OnChangeIconListener mCallback;

    SupportMapFragment fragment;
    GoogleMap map;
    View mapView;

    public int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap googleMap;
    GeoPoint geoPoint;

    GeoPoint geoPointOrigen;
    GeoPoint geoPointDestino;

    FirebaseFirestore db;
    FirebaseUser user;
    boolean isChecked;


    //screens
    ConstraintLayout resumeMap;
    ConstraintLayout infoReq;
    ConstraintLayout infoMap;
    ConstraintLayout containerCurrent;
    ConstraintLayout btnsResume;
    LinearLayout btnsCurrent;
    TextView btnClose;

    //btns current request
    TextView btnDetalle;
    TextView btnContacto;
    TextView btnFinalizar;

    TextView btn_viewMore;
    Switch switch1;

    String idSelected;

    TextView origen_text;
    TextView destino_text;
    TextView send_text;
    TextView get_text;
    TextView pack_text;
    TextView btnAccepted;
    TextView btnRejected;
    TextView current_origen;
    TextView current_destino;

    Boolean justOnce = false;
    DocumentReference docRefPay;
    ListenerRegistration checkDBListen;
    ListenerRegistration checkCancelListen;
    Query _query;
    DocumentReference docRefT;

    List<Marker> markerList = new ArrayList<>();
    ArrayList<String> idExcludes = new ArrayList<>();

    int REQUEST_PHONE_CALL = 111;

    //location
    private String provider;
    LocationManager locationManager;
    Location loc1 = new Location("");
    Location location;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (InmediatoFragment.OnChangeIconListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseFirestore.setLoggingEnabled(true);
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();





        View v = inflater.inflate(R.layout.fragment_inmediato, container, false);

        resumeMap = (ConstraintLayout) v.findViewById(R.id.resumePanel);
        infoReq = (ConstraintLayout) v.findViewById(R.id.infoReq);
        infoMap = (ConstraintLayout) v.findViewById(R.id.infoMap);
        containerCurrent = (ConstraintLayout) v.findViewById(R.id.containerCurrent);
        btnsResume = (ConstraintLayout) v.findViewById(R.id.btnsResume);
        btnsCurrent = (LinearLayout) v.findViewById(R.id.btnsCurrent);
        btnClose = (TextView) v.findViewById(R.id.btnClose);

        btnDetalle = (TextView) v.findViewById(R.id.btnDetalle);
        btnContacto = (TextView) v.findViewById(R.id.btnContacto);
        btnFinalizar = (TextView) v.findViewById(R.id.btnFinalizar);

        current_origen = (TextView) v.findViewById(R.id.current_origen);
        current_destino = (TextView) v.findViewById(R.id.current_destino);


        btn_viewMore = (TextView) v.findViewById(R.id.btn_viewMore);

        origen_text = (TextView) v.findViewById(R.id.origen);
        destino_text = (TextView) v.findViewById(R.id.destino);
        send_text = (TextView) v.findViewById(R.id.send);
        get_text = (TextView) v.findViewById(R.id.get);
        pack_text = (TextView) v.findViewById(R.id.pack);

        btnAccepted = (TextView) v.findViewById(R.id.btnAccepted);
        btnRejected = (TextView) v.findViewById(R.id.btnRejected);

        switch1 = (Switch) v.findViewById(R.id.switch1);


        Drawable background = infoMap.getBackground();
        background.setAlpha(98);

        //not block home
        Manager.getInstance().isBlock = false;
        mCallback.isBlock(false);


        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked = b;
                //Log.i(TAG, String.valueOf(b));

                if (isChecked) {
                    //do stuff when Switch is ON
                    checkDB();

                } else {
                    //myListener.remove();
                    //do stuff when Switch if OFF
                }
                Manager.getInstance().transporterActive = isChecked;
                mCallback.changeIcon(isChecked);

            }
        });

        clickCurrent();
        fisrtCheck();

        return v;
    }



    private void clickCurrent() {
        btnDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnsCurrent.setVisibility(View.GONE);
                resumeMap.setVisibility(View.VISIBLE);
                btnClose.setVisibility(View.VISIBLE);
                btnsResume.setVisibility(View.GONE);

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btnsCurrent.setVisibility(View.VISIBLE);
                        resumeMap.setVisibility(View.GONE);
                    }
                });
            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CloseServiceActivity.class);
                i.putExtra("idSelected", idSelected);
                i.putExtra("idClient", Manager.getInstance().actualClientCel_inmediato);

                startActivity(i);



            }
        });

        btnContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext());
                builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
                builder.setMessage("De que forma quieres contactarte");
                builder.setItems(new String[]{"LLAMADA", "MENSAJE"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        //Toast.makeText(context, "Selected:"+index, Toast.LENGTH_SHORT).show();
                        if (index == 0) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Manager.getInstance().actualClientCel_inmediato));
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                                return;
                            }
                            getContext().startActivity(intent);
                        }
                        else if(index == 1)
                        {
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            sendIntent.setData(Uri.parse("sms:"));
                            sendIntent.putExtra("address", Manager.getInstance().actualClientCel_inmediato);
                            sendIntent.putExtra("sms_body", "Buen día, ");
                            getContext().startActivity(sendIntent);

                            /*
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto",Manager.getInstance().actualClientMail_inmediato, null));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Atlante conductor");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "Buen día!...");
                            getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));
                            */
                        }
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }


    public void fisrtCheck()
    {

        /*
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle("Cargando");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        */

        db.collection("pedidos")
                .whereEqualTo("tipo", 1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {


                                //Log.i(TAG, document.getId() + " => " + document.getData().get("tipo"));
                                Integer actual_estado = Integer.parseInt(document.getLong("estado_actual").toString());
                                DocumentReference docRef = (DocumentReference) document.getData().get("transportista");
                                DocumentReference docRefClient = (DocumentReference) document.getData().get("user");
                                DocumentReference docRefPay = (DocumentReference) document.getData().get("mediosPago");
                                if (docRefPay != null) {
                                    medioPagoID = docRefPay.getId();


                                    price = Integer.parseInt(document.getLong("valor").toString());
                                    HashMap hashMap = (HashMap) document.getData().get("estado");


                                    if (hashMap != null) {

                                        if (actual_estado != 5 && document.getData().get("transportista") != null && hashMap.get("finalizado") == null && hashMap.get("cancelado") == null)
                                        {
                                            if (Objects.equals(user.getUid(), docRef.getId())) {
                                                //progress.dismiss();
                                                idSelected = document.getId().toString();
                                                Manager.getInstance().actualId_to_charge = idSelected;
                                                Manager.getInstance().idToTrack = idSelected;

                                                containerCurrent.setVisibility(View.VISIBLE);
                                                infoMap.setVisibility(View.GONE);

                                                Intent intentService = new Intent(getContext(), ServiceLocation.class);
                                                getContext().startService(intentService);
                                                listenChanges();

                                                DocumentReference _docRefUser = (DocumentReference) document.getData().get("user");
                                                Manager.getInstance().actualClientId_inmediato = docRefClient.getId();
                                                Manager.getInstance().actualClientId_finish = docRefClient.getId();

                                                // set USER info from db user
                                                DocumentReference docRefUser = db.collection("usuarios").document(Manager.getInstance().actualClientId_inmediato);
                                                docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                if (document.getData().get("token") != null) {
                                                                    Manager.getInstance().actualClientToken_inmediato = document.getData().get("token").toString();
                                                                    id = Manager.getInstance().actualClientToken_inmediato;

                                                                }

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


                                                GeoPoint tempD = (GeoPoint) document.getData().get("destino");
                                                GeoPoint tempO = (GeoPoint) document.getData().get("origen");

                                                openResume(document.getData().get("origenStr").toString(),
                                                        document.getData().get("destinoStr").toString(),
                                                        document.getData().get("quien_entrega").toString(),
                                                        document.getData().get("quien_recibe").toString(),
                                                        document.getData().get("que_envia").toString(),
                                                        tempD,
                                                        tempO
                                                );


                                                return;

                                            } else {

                                                containerCurrent.setVisibility(View.GONE);
                                            }

                                        } else {
                                            containerCurrent.setVisibility(View.GONE);
                                            infoMap.setVisibility(View.VISIBLE);
                                        }
                                    }

                                }
                                //progress.dismiss();
                                containerCurrent.setVisibility(View.GONE);
                                infoMap.setVisibility(View.VISIBLE);

                            }
                        }



        }
    });
    }

    public static boolean isParsable(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }


    public void checkDB()
    {

        Query query = db.collection("pedidos")
                .whereEqualTo("tipo", 1)
                .whereEqualTo("vehiculo", Manager.getInstance().vehicleSelectedType);

        checkDBListen = query
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e)
            {
                if(documentSnapshots!=null)
                {
                    for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                    {
                        final DocumentReference docRefClient = (DocumentReference) document.getData().get("user");

                        if(document.getData().get("mediosPago") !=null)
                        {
                            if (document.getData().get("transportista") == null)
                            {

                                docRefPay = (DocumentReference) document.getData().get("mediosPago");
                                medioPagoID = docRefPay.getId();

                                // set CUOTAS info from db user
                                docRefPay.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                String cuotas = document.getData().get("cuotas").toString();
                                                Log.i(TAG, cuotas);
                                                numCuotas = Integer.parseInt(cuotas);

                                            } else {
                                                Log.i(TAG, "No such document inside");
                                            }
                                        } else {
                                            Log.i(TAG, "get failed with inside", task.getException());
                                        }
                                    }
                                });


                                price = Integer.parseInt(document.getLong("valor").toString());

                                btnsResume.setVisibility(View.VISIBLE);

                                idSelected = document.getId().toString();
                                Manager.getInstance().actualId_to_charge = idSelected;
                                if (idExcludes.contains(idSelected)) {
                                    Log.i(TAG, "ya excluido");

                                } else {

                                    infoReq.setVisibility(View.VISIBLE);
                                    infoMap.setVisibility(View.GONE);

                                    // set USER info from db user
                                    if (idSelected.length() > 0) {

                                        DocumentReference docRefUser = db.collection("usuarios").document(docRefClient.getId());
                                        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        if (document.getData().get("token") != null) {
                                                            Manager.getInstance().actualClientToken_inmediato = document.getData().get("token").toString();
                                                            id = Manager.getInstance().actualClientToken_inmediato;
                                                            Manager.getInstance().actualTokenPush = id;
                                                            mCallback.changeIcon(true);
                                                        }

                                                        Manager.getInstance().actualClientId_inmediato = docRefClient.getId();
                                                        Manager.getInstance().actualClientId_finish = docRefClient.getId();
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


                                    }


                                    btn_viewMore.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            infoReq.setVisibility(View.GONE);
                                            infoMap.setVisibility(View.VISIBLE);
                                            resumeMap.setVisibility(View.VISIBLE);

                                            justOnce = false;

                                            DocumentReference docRefUser = (DocumentReference) document.getData().get("user");
                                            Manager.getInstance().actualClientId_inmediato = docRefUser.getId();
                                            Manager.getInstance().actualClientId_finish = docRefUser.getId();

                                            if(checkDBListen!=null)
                                                checkDBListen.remove();

                                            GeoPoint tempD = (GeoPoint) document.getData().get("destino");
                                            GeoPoint tempO = (GeoPoint) document.getData().get("origen");

                                            openResume(document.getData().get("origenStr").toString(),
                                                    document.getData().get("destinoStr").toString(),
                                                    document.getData().get("quien_entrega").toString(),
                                                    document.getData().get("quien_recibe").toString(),
                                                    document.getData().get("que_envia").toString(),
                                                    tempD,
                                                    tempO
                                            );
                                        }
                                    });

                                    //showResume(document.getId().toString());
                                    return;
                                }

                            }
                            else
                            {
                                docRefT = (DocumentReference) document.getData().get("transportista");
                                if(Objects.equals(docRefT.getId().toString() , Manager.getInstance().myID))
                                {
                                    if(document.getLong("estado_actual")==2)
                                    {
                                        idSelected = document.getId().toString();
                                        Manager.getInstance().actualId_to_charge = idSelected;
                                        Manager.getInstance().idToTrack = idSelected;

                                        containerCurrent.setVisibility(View.VISIBLE);
                                        infoMap.setVisibility(View.GONE);
                                        checkDBListen.remove();
                                        listenChanges();
                                        mCallback.changeIcon(true);
                                    }
                                    if(document.getLong("estado_actual")==3 || document.getLong("estado_actual")==4 || document.getLong("estado_actual")==5)
                                    {
                                        containerCurrent.setVisibility(View.GONE);
                                        infoMap.setVisibility(View.VISIBLE);

                                        Manager.getInstance().isBlock = false;
                                        mCallback.isBlock(false);
                                        googleMap.clear();
                                    }

                                }




                            }



                        }
                    }
                }

            }
        });

    }

    private void listenChanges()
    {
        DocumentReference docRef = db.collection("usuarios").document(user.getUid());
        _query = db.collection("pedidos")
                .whereEqualTo("tipo", 1)
                .whereEqualTo("transportista", docRef)
                .whereEqualTo("vehiculo", Manager.getInstance().vehicleSelectedType);

        checkCancelListen = _query
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                        {
                            if(Objects.equals(document.getId(),idSelected))
                            {

                                if(document.getLong("estado_actual") == 2)
                                {
                                    Manager.getInstance().isBlock = true;
                                    mCallback.isBlock(true);
                                }
                                if(document.getLong("estado_actual") == 5)
                                {
                                    infoReq.setVisibility(View.GONE);
                                    infoMap.setVisibility(View.VISIBLE);
                                    resumeMap.setVisibility(View.GONE);
                                    containerCurrent.setVisibility(View.GONE);


                                    checkCancelListen.remove();

                                    Manager.getInstance().isBlock = false;
                                    mCallback.isBlock(false);
                                    googleMap.clear();

                                    checkDB();

                                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(getContext())
                                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                            .setCornerRadius(20)
                                            .setTextGravity(Gravity.CENTER)
                                            .setTitle("Lo sentimos")
                                            .setMessage("Hubo un problema con la forma de pago del usuario")
                                            .addButton("Ok", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();


                                                }
                                            });

                                    pDialog.show();
                                    return;
                                };

                                if(document.getLong("estado_actual") == 4)
                                {
                                    infoReq.setVisibility(View.GONE);
                                    infoMap.setVisibility(View.VISIBLE);
                                    resumeMap.setVisibility(View.GONE);
                                    containerCurrent.setVisibility(View.GONE);


                                    checkCancelListen.remove();

                                    Manager.getInstance().isBlock = false;
                                    mCallback.isBlock(false);

                                    googleMap.clear();

                                    checkDB();

                                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(getContext())
                                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                            .setCornerRadius(20)
                                            .setTextGravity(Gravity.CENTER)
                                            .setTitle("Espera")
                                            .setMessage("El usuario cancelo su pedido")
                                            .addButton("Ok", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();


                                                }
                                            });

                                    pDialog.show();
                                    return;
                                }
                                if(document.getLong("estado_actual") == 3)
                                {
                                    googleMap.clear();
                                    checkCancelListen.remove();
                                    checkDB();
                                    containerCurrent.setVisibility(View.VISIBLE);
                                    infoMap.setVisibility(View.GONE);
                                    Manager.getInstance().isBlock = false;
                                    mCallback.isBlock(false);
                                    googleMap.clear();
                                }
                            }

                        }
                    }
                });
    }

    public void openResume(String _origen, String _destino, String _send, String _get, String _pack, GeoPoint _geoDestino, GeoPoint _geoOrigen)
    {

        geoPointOrigen = _geoOrigen;
        geoPointDestino = _geoDestino;

        origen_text.setText("Origen: " + _origen);
        destino_text.setText("Destino: " + _destino);
        send_text.setText("Quien envía: " + _send);
        get_text.setText("Quien recibe: " + _get);
        pack_text.setText("Que envía: " + _pack);

        current_origen.setText(_origen);
        current_destino.setText(_destino);

        btnRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resumeMap.setVisibility(View.INVISIBLE);
                if(idExcludes.contains(idSelected))
                    Log.i(TAG, "repetido");
                else
                    idExcludes.add(idSelected);

                checkDB();
                //Log.i(TAG, idExcludes.toString());
            }
        });


        // ::::::::::::::::::::     TOMAR PEDIDO        :::::::::::::::::::::

        btnAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(Manager.getInstance().vehicleSelected != "")
                {

                    db.collection("pedidos")
                            .document(idSelected).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful())
                            {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists())
                                {
                                    //CHECK IF REQUEST ITS ALLREADY TAKED
                                    //Log.i(TAG, document.getData().get("estado_actual").toString());
                                    if(document.getLong("estado_actual") == 1)
                                    {
                                        infoReq.setVisibility(View.GONE);
                                        infoMap.setVisibility(View.GONE);
                                        resumeMap.setVisibility(View.GONE);
                                        containerCurrent.setVisibility(View.VISIBLE);

                                        SendNotification sendNotification = new SendNotification();
                                        sendNotification._body = msj;
                                        sendNotification._id = id;
                                        sendNotification._title = "Aceptaron tu pedido";
                                        sendNotification.execute();

                                        Manager.getInstance().actualService = "inmediato";

                                        ChargeCreditCard chargeCreditCard = new ChargeCreditCard();
                                        chargeCreditCard.charge(price,medioPagoID,numCuotas);


                                        // Update one field, creating the document if it does not already exist.
                                        Map<String, Object> data = new HashMap<>();
                                        DocumentReference docRef = db.collection("usuarios").document(user.getUid());
                                        DocumentReference docRefV = db.collection("vehiculos").document(Manager.getInstance().vehicleSelected);  //CHANGE ID BY ADMIN

                                        Map<String, Date> estado = new HashMap<>();
                                        estado.put("en_curso", new Date());

                                        data.put("transportista", docRef);
                                        data.put("estado", estado);
                                        data.put("estado_actual", 2);
                                        data.put("vehiculo_obj", docRefV);
                                        db.collection("pedidos").document(idSelected).set(data, SetOptions.merge());

                                        listenChanges();

                                    }else{
                                        CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(getContext())
                                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                .setCornerRadius(20)
                                                .setTextGravity(Gravity.CENTER)
                                                .setTitle("Lo sentimos")
                                                .setMessage("El servicio ya fué tomado.")
                                                .addButton("Ok", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();

                                                    }
                                                });

                                        pDialog.show();

                                    }

                                } else {
                                    Log.i(TAG, "No such document inside");
                                }
                            } else {
                                Log.i(TAG, "get failed with inside", task.getException());
                            }
                        }
                    });



                    /*




                    */

                }else{
                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(getContext())
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setCornerRadius(20)
                            .setTextGravity(Gravity.CENTER)
                            .setTitle("¡Espera!")
                            .setMessage("Primero debes seleccionar un vehículo.")
                            .addButton("Selecciona un vehiculo", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent i = new Intent(getContext(), HomeActivity.class);
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
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.mapView);
        if (fragment == null)
        {
            fragment = SupportMapFragment.newInstance();
            mapView = fragment.getView();
            fm.beginTransaction().replace(R.id.container, fragment).commit();
        }


/***at this time google play services are not initialize so get map and add what ever you want to it in onResume() or onStart() **/
    }


    // Define the callback for what to do when data is received
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String latStr = intent.getStringExtra("lat");
            String lonStr = intent.getStringExtra("lon");

            Double lat = Double.parseDouble(latStr);
            Double lon = Double.parseDouble(lonStr);

            //Log.i(TAG, "strLAT: "+ latStr + "   num: " + lat.toString());
            //Log.i(TAG, "strLON: "+ lonStr + "   num: "+ lon.toString());

            GeoPoint _geoPoint = new GeoPoint(lat, lon);

            Map<String, GeoPoint> data = new HashMap<>();
            geoPoint = new GeoPoint(0, 0);

            data.put("pos_actual", _geoPoint);
            db.collection("pedidos").document(idSelected).update("pos_actual", _geoPoint).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "actualiza pos");
                }
            });


        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        IntentFilter filter = new IntentFilter(ServiceLocation.ACTION);
        getContext().registerReceiver(mReceiver, filter);

        if (map == null)
        {
            fragment.getMapAsync(new OnMapReadyCallback()
            {
                @Override
                public void onMapReady(GoogleMap mMap)
                {

                    googleMap = mMap;

                    // For showing a move to my location button
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


                    }else{
                        googleMap.setMyLocationEnabled(true);
                        googleMap.setPadding(0, 80, 0, 0);


                        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();

                        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                        if (location != null)
                        {
                            if(geoPointDestino!=null)
                            {

                                if(Manager.getInstance().isBlock)
                                {


                                    Marker markerD = googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(geoPointDestino.getLatitude(), geoPointDestino.getLongitude()))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    );
                                    Marker markerO = googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(geoPointOrigen.getLatitude(), geoPointOrigen.getLongitude()))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    );

                                    markerList.add(markerD);
                                    markerList.add(markerO);


                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    for (Marker marker : markerList) {
                                        builder.include(marker.getPosition());
                                    }
                                    LatLngBounds bounds = builder.build();

                                    int padding = 150; // offset from edges of the map in pixels


                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                                    googleMap.moveCamera(cu);
                                    googleMap.animateCamera(cu);
                                }else{
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                }
                            }else{
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                            //

                        }
                    }
                }
            });
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(mReceiver);
        super.onDestroy();
        //mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //mapView.onLowMemory();
    }

    public interface OnChangeIconListener
    {
        public void isBlock(Boolean _is);
        public void changeIcon(Boolean _is);
    }



}
