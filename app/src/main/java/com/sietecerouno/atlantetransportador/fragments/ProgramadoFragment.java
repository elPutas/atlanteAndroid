package com.sietecerouno.atlantetransportador.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.sietecerouno.atlantetransportador.sections.ChooseVActivity;
import com.sietecerouno.atlantetransportador.sections.HomeActivity;
import com.sietecerouno.atlantetransportador.sections.OfertActivity;
import com.sietecerouno.atlantetransportador.sections.PublishServiceActivity;
import com.sietecerouno.atlantetransportador.sections.ResumoPersonalizadoActivity;
import com.sietecerouno.atlantetransportador.sections.ResumoProgramadoActivity;
import com.sietecerouno.atlantetransportador.utils.CustomObj;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramadoFragment extends Fragment 
{
    FirebaseFirestore db;
    String TAG = "GIO";
    ListView listview;
    public static List<CustomObj> obj;

    public ProgramadoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        final View view = inflater.inflate(R.layout.fragment_programado, container, false);
        final TextView text_empty = (TextView) view.findViewById(R.id.text_empty);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRefMyUser = db.collection("usuarios").document(user.getUid());




        db.collection("pedidos_programados")
                .whereEqualTo("transportista", docRefMyUser)
                .whereGreaterThan("fecha", new Date())
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

                        for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                        {


                            if(document.get("estado_actual")==null)
                            {
                                //Integer t = Integer.parseInt(document.get("estado_actual").toString());
                                db.collection("pedidos_programados").document(document.getId()).collection("ofertas").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e)
                                    {
                                        if (e != null) {
                                            Log.w(TAG, "Listen failed.", e);
                                            return;
                                        }
                                        obj = new ArrayList<CustomObj>();
                                        for (final DocumentSnapshot documentI : documentSnapshots.getDocuments())
                                        {
                                            Integer val = Integer.parseInt(documentI.get("ue_solicitado").toString());
                                            Integer val_ue = Integer.parseInt(document.get("ue_vehiculo").toString());


                                            DocumentReference docRef = (DocumentReference) documentI.getData().get("pedido");
                                            DocumentReference docRefUser = (DocumentReference) documentI.getData().get("user");

                                            Integer status = Integer.parseInt(documentI.get("estado").toString());
                                            Boolean isClick = false;
                                            if(status < 2)
                                                isClick = true;


                                            obj.add(new CustomObj(document.getId(), docRef.getId().toString(), documentI.getId(), isClick, docRefUser.getId(), val, val_ue, status  ));
                                        }

                                        if(obj.size() > 0)
                                        {
                                            listview = (ListView) view.findViewById(R.id.s_listView);
                                            ListAdapter_programadoServices customAdapter = new ListAdapter_programadoServices(getActivity(), R.layout.choose_service_tab_custom, obj);
                                            listview.setAdapter(customAdapter);
                                        }else{

                                            text_empty.setText("Actualmente no se cuenta con servicios disponibles");

                                        }
                                    }
                                });

                            }



                        }


                    }
                });


        TextView btn_pulished = (TextView) view.findViewById(R.id.btn_pulished);
        btn_pulished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Manager.getInstance().vehicleSelected != "")
                {
                    Intent i = new Intent(getActivity(), PublishServiceActivity.class);
                    startActivity(i);
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

        if(obj == null)
            text_empty.setText("Actualmente no se cuenta con servicios disponibles");
        else
            text_empty.setText("");
        
        
        return view;
    }

}

class ListAdapter_programadoServices extends ArrayAdapter<CustomObj>
{
    List<CustomObj> objList;
    CustomObj p;
    String TAG = "GIO";

    public ListAdapter_programadoServices(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public ListAdapter_programadoServices(Context context, int resource, List<CustomObj> items)
    {
        super(context, resource, items);
        objList = items;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {


        ListAdapter_programadoServices.ViewHolder holder = null;
        if (convertView == null)
        {

            LayoutInflater vi = LayoutInflater.from(getContext());
            vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.choose_service_tab_custom, null);
            holder = new ListAdapter_programadoServices.ViewHolder();

            holder.backgroundImage = convertView.findViewById(R.id.selectedBG);
            holder.myCel = (RelativeLayout) convertView.findViewById(R.id.myCel);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            holder.myRate = (MaterialRatingBar) convertView.findViewById(R.id.myRate);

            Integer realStatus = objList.get(position).valueInt3;

            if(objList.get(position).selected)
            {
                holder.status.setText("Estado: Pendiente");
            }else{
                if(realStatus == 4)
                    holder.status.setText("Estado: Rechazado");
                else
                    holder.status.setText("Estado: Aceptado");
            }




            FirebaseFirestore db = FirebaseFirestore.getInstance();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            final ViewHolder finalHolder1 = holder;
            finalHolder1.idClient = objList.get(position).value2.toString();

            // set PEDIDO info from db pedido
            DocumentReference docRef = db.collection("pedidos").document(objList.get(position).value);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            String quien_entrega = document.getData().get("quien_entrega").toString();
                            String que_envia = document.getData().get("que_envia").toString();
                            String origenStr = document.getData().get("origenStr").toString();
                            String destinoStr = document.getData().get("destinoStr").toString();
                            String observaciones = document.getData().get("observaciones").toString();
                            Date dateTemp = (Date) task.getResult().getData().get("fecha_entrega");

                            if(quien_entrega != null && que_envia != null && origenStr !=null && destinoStr != null)
                            {
                                String info_txt = "FECHA MÁXIMO DE ENTREGA: \n"
                                        + Manager.getInstance().dateFormatter.format(dateTemp.getTime()) + "\n"
                                        +"Quien envía: " + quien_entrega + "\n"
                                        +"Envío: " + que_envia + "\n"
                                        +"Origen: " + origenStr + "\n"
                                        +"Destino: " + destinoStr + "\n"
                                        +"Observaciones: " + observaciones;

                                finalHolder1.info.setText(info_txt);
                            }

                        } else {
                            Log.i(TAG, "No such document inside");
                        }
                    } else {
                        Log.i(TAG, "get failed with inside", task.getException());
                    }
                }
            });



            // set USER info from db user
            DocumentReference docRefUser = db.collection("usuarios").document(objList.get(position).value2);
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
                            Long lg = (Long) document.getLong("calificacion");
                            CropCircleTransformation cropCircleTransformation = new CropCircleTransformation();
                            finalHolder1.myRate.setRating(lg.intValue());
                            Glide.with(finalHolder1.icon.getContext())
                                    .load(document.getData().get("foto").toString())
                                    .apply(RequestOptions.bitmapTransform(cropCircleTransformation))
                                    .into(finalHolder1.icon);

                        } else {
                            Log.i(TAG, "No such document inside");
                        }
                    } else {
                        Log.i(TAG, "get failed with inside", task.getException());
                    }
                }
            });



            holder.myCel.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {

                            if(objList.get(position).selected)
                            {
                                Manager.getInstance().eu_remove_temp = objList.get(position).valueInt;
                                Manager.getInstance().eu_total_temp = objList.get(position).valueInt2;
                                Manager.getInstance().idProgramado = objList.get(position).value;
                                Manager.getInstance().idProgramadoDoc = objList.get(position).name;
                                
                                String _idProgramado = String.valueOf(objList.get(position).value);
                                Intent i = new Intent(getContext(), ResumoProgramadoActivity.class);
                                i.putExtra("idProgramado", _idProgramado);
                                i.putExtra("idClient", finalHolder1.idClient);
                                i.putExtra("idOferta", objList.get(position).img);
                                getContext().startActivities(new Intent[]{i});
                            }else{
                                CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(getContext())
                                        .setCornerRadius(20)
                                        .setTextGravity(Gravity.CENTER)
                                        .setTitle("Ya aceptaste o rechazaste esta oferta")
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

            convertView.setTag(holder);
        }else {
            holder = (ListAdapter_programadoServices.ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public static class ViewHolder
    {
        public ImageView icon;
        public TextView status;
        public TextView info;
        public MaterialRatingBar myRate;
        View backgroundImage;
        public RelativeLayout myCel;
        public Boolean isNew = true;
        public String stateActual;
        public String idOferta;
        public String idClient;


    }

    public int getPositionById(String _tag)
    {
        Integer pos = 0;
        for (int i = 0; i < ChooseVActivity.obj.size(); i++)
        {
            if (Objects.equals(_tag, String.valueOf(ChooseVActivity.obj.get(i).selected)))
            {
                pos = i;
                return pos;
            }
        }
        return pos;
    }


    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
