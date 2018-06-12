package com.sietecerouno.atlantetransportador.profile;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.sections.PublishServiceActivity;
import com.sietecerouno.atlantetransportador.utils.CustomObj;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalificacionFragment extends Fragment
{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference docRefMyUser;
    String TAG = "GIO";
    String listDateStart[];
    ArrayList<String> stratStr;
    SimpleDateFormat dateFormatter;

    PieView pieViewPeriod;
    PieView pieView;
    ArrayList<Integer> arrTotal;

    public CalificacionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calificacion, container, false);

        pieView = (PieView) view.findViewById(R.id.pieView);
        pieViewPeriod = (PieView) view.findViewById(R.id.pieViewPeriod);


        pieView.setPercentageBackgroundColor(getResources().getColor(R.color.blueApp));
        pieView.setMainBackgroundColor(getResources().getColor(R.color.black));
        pieView.setPieInnerPadding(10);
        pieView.setMaxPercentage(5f);




        final TextView de = (TextView) view.findViewById(R.id.de);
        final TextView hasta = (TextView) view.findViewById(R.id.hasta);
        final TextView totalTripis_txt = (TextView) view.findViewById(R.id.totalTripis_txt);
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy  -   HH:mm");


        docRefMyUser = db.collection("usuarios").document(user.getUid());

        final ArrayList<Date> startDate = new ArrayList<>();

        // set USER info from db user
        DocumentReference docRefUser = db.collection("usuarios").document(Manager.getInstance().myID);
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
                        int tempNum=0;
                        Long lg = (Long) document.getData().get("calificacion");
                        if(lg == null)
                        {
                            tempNum = 5;
                            pieView.setPercentage(5);
                        }else{
                            tempNum = lg.intValue();
                            pieView.setPercentage(lg.intValue());
                        }

                        PieAngleAnimation animation = new PieAngleAnimation(pieView);
                        animation.setDuration(2000);
                        pieView.setInnerText(tempNum+"");
                        pieView.startAnimation(animation);

                    } else {
                        Log.i(TAG, "No such document inside");
                    }
                } else {
                    Log.i(TAG, "get failed with inside", task.getException());
                }
            }
        });



        db.collection("pedidos")
                .whereEqualTo("transportista", docRefMyUser)
                .whereEqualTo("estado_actual", 3)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                        @Nullable FirebaseFirestoreException e)
                    {
                        if (e != null)
                        {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        int _trips = 0;
                        stratStr = new ArrayList<>();
                        listDateStart = new String[documentSnapshots.size()];
                        for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                        {
                            HashMap hashMap = (HashMap) document.getData().get("estado");
                            Date dateTemp = (Date) hashMap.get("finalizado");
                            startDate.add( dateTemp);
                            _trips++;
                        }

                        totalTripis_txt.setText(_trips+"");


                        Collections.sort(startDate, Collections.reverseOrder());
                        for (int j = 0; j < startDate.size(); j++)
                        {

                            listDateStart[j] = dateFormatter.format(startDate.get(j).getTime());
                        }
                        //Log.i(TAG, String.valueOf(startDate));
                    }
                });


        de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext());
                builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
                builder.setTitle("FECHA DE INICIO");
                builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
                builder.setItems(listDateStart, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index)
                    {
                        de.setText( listDateStart[index]);
                        dialogInterface.dismiss();
                        filterRanking(hasta.getText(), de.getText());
                    }
                });
                builder.show();
            }
        });

        hasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext());
                builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
                builder.setTitle("FECHA FINAL");
                builder.setTextGravity(Gravity.CENTER_HORIZONTAL);
                builder.setItems(listDateStart, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index)
                    {
                        hasta.setText( listDateStart[index]);
                        dialogInterface.dismiss();

                        filterRanking(hasta.getText(), de.getText());
                    }
                });
                builder.show();
            }
        });



        return view;
    }



    public void filterRanking(final CharSequence _initDate, final CharSequence _finishDate)
    {
        if(_initDate.length() >0 && _finishDate.length() > 0)
        {
            db.collection("pedidos")
                    .whereEqualTo("transportista", docRefMyUser)
                    .whereEqualTo("estado_actual", 3)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                            @Nullable FirebaseFirestoreException e)
                        {
                            if (e != null)
                            {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }
                            int i =0;
                            arrTotal = new ArrayList<>();

                            for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                            {
                                HashMap hashMap = (HashMap) document.getData().get("estado");
                                Date dateInit = (Date) hashMap.get("finalizado");
                                Date _init = null;
                                Date _finish = null;
                                try {
                                    _init = dateFormatter.parse(_initDate.toString());
                                    _finish = dateFormatter.parse(_finishDate.toString());
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }


                                if (dateInit.after(_init) && dateInit.before(_finish))
                                {
                                    if(document.getData().get("calificacion_user")!=null)
                                    {
                                        Double dnum = (Double) document.getData().get("calificacion_user");
                                        int rank = dnum.intValue();
                                        arrTotal.add(rank);
                                        i++;
                                    }
                                }
                            }

                            int total = arrTotal.size();
                            int totalRank = 0;
                            for (int j = 0; j < total; j++)
                            {
                                totalRank += arrTotal.get(j);
                            }
                            if(totalRank == 0)
                                totalRank = 1;
                            if(total == 0)
                                total = 1;
                            totalRank=(totalRank/total);
                            pieViewPeriod.setPercentageBackgroundColor(getResources().getColor(R.color.yellowApp));
                            pieViewPeriod.setMainBackgroundColor(getResources().getColor(R.color.gray));
                            pieViewPeriod.setMaxPercentage(5f);
                            pieViewPeriod.setPercentage(totalRank);
                            pieViewPeriod.setInnerText(totalRank+"");

                            PieAngleAnimation animation = new PieAngleAnimation(pieViewPeriod);
                            animation.setDuration(4000);
                            pieViewPeriod.startAnimation(animation);

                        }
                    });
        }
    }


}
