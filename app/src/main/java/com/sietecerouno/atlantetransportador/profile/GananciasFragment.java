package com.sietecerouno.atlantetransportador.profile;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.utils.NumberTextWatcherForThousand;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import az.plainpie.animation.PieAngleAnimation;
import me.ithebk.barchart.BarChart;
import me.ithebk.barchart.BarChartModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class GananciasFragment extends Fragment
{

    String TAG = "GIO";
    String listDateStart[];
    ArrayList<String> stratStr;
    SimpleDateFormat dateFormatter;

    ArrayList<Integer> arrTotal;
    BarChart barChart;

    EditText total_txt;
    Integer total_earn;
    DocumentReference docRefMyUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public GananciasFragment() {
        // Required empty public constructor
    }

    int iNum =0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ganancias, container, false);
        docRefMyUser = db.collection("usuarios").document(Manager.getInstance().myID);

        barChart = (BarChart) view.findViewById(R.id.bar_chart_vertical);
        barChart.setBarMaxValue(100000);

        final TextView de = (TextView) view.findViewById(R.id.de);
        final TextView hasta = (TextView) view.findViewById(R.id.hasta);

        total_txt = (EditText) view.findViewById(R.id.total_txt);
        total_earn = 0;

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy  -   HH:mm");
        final ArrayList<Date> startDate = new ArrayList<>();

        db.collection("usuarios").document(Manager.getInstance().myID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {


                db.collection("pedidos")
                        .whereEqualTo("transportista", docRefMyUser)
                        .whereEqualTo("estado_actual", 3)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                                @Nullable FirebaseFirestoreException e)
                            {
                                iNum=0;
                                for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                                {
                                    Integer valor = 0;
                                    if(document.getData().get("tipo") != null && document.getData().get("valor") != null)
                                    {

                                        try{
                                            valor = Integer.parseInt(document.getLong("valor").toString());
                                        }catch (Exception e1)
                                        {
                                            Double strTemp = (Double) document.getData().get("valor");
                                            valor = strTemp.intValue();
                                        }

                                        BarChartModel barChartModel = new BarChartModel();
                                        barChartModel.setBarValue(valor);

                                        total_earn += valor;

                                        if((iNum%2) == 0)
                                            barChartModel.setBarColor(Color.parseColor("#009900"));
                                        else
                                            barChartModel.setBarColor(Color.parseColor("#006600"));

                                        iNum++;


                                        barChart.addBar(barChartModel);
                                    }
                                }

                                total_txt.addTextChangedListener(new NumberTextWatcherForThousand(total_txt));
                                total_txt.setText("$" +total_earn);
                            }
                        });

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
                            barChart.clearAll();
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
                                    if(document.getData().get("valor")!=null)
                                    {
                                        Integer valor = Integer.parseInt(document.getLong("valor").toString());


                                        BarChartModel barChartModel = new BarChartModel();
                                        barChartModel.setBarValue(valor);

                                        total_earn += valor;

                                        if((iNum%2) == 0)
                                            barChartModel.setBarColor(Color.parseColor("#009900"));
                                        else
                                            barChartModel.setBarColor(Color.parseColor("#006600"));

                                        iNum++;


                                        barChart.addBar(barChartModel);


                                        arrTotal.add(valor);
                                        i++;
                                    }
                                }
                            }


                        }
                    });
        }
    }

}
