package com.sietecerouno.atlantetransportador.fragments;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.sections.ChooseVActivity;
import com.sietecerouno.atlantetransportador.sections.OfertActivity;
import com.sietecerouno.atlantetransportador.sections.ResumoPersonalizadoActivity;
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
public class PersonalizadoFragment extends Fragment
{
    ListView listview;
    public static List<CustomObj> obj;
    FirebaseFirestore db;
    String TAG = "GIO";
    TextView status;


    public PersonalizadoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        final View view = inflater.inflate(R.layout.fragment_personalizado, container, false);

        final TextView text_empty = (TextView) view.findViewById(R.id.text_empty);
        status = (TextView) view.findViewById(R.id.status);

        obj = new ArrayList<CustomObj>();

        db.collection("pedidos_personalizados")
                .whereEqualTo("estado_actual", 1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                        @Nullable FirebaseFirestoreException e)
                    {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        obj.clear();
                        for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                        {
                            DocumentReference docRef = (DocumentReference) document.getData().get("pedido");
                            DocumentReference docRefUser = (DocumentReference) document.getData().get("user");

                            obj.add(new CustomObj(document.getId(), docRef.getId().toString(), "", false, docRefUser.getId(), 0 ,0,0));

                        }

                        if(obj.size()>0)
                        {
                            listview = (ListView) view.findViewById(R.id.s_listView);
                            ListAdapter_personalizadoServices customAdapter = new ListAdapter_personalizadoServices(getActivity(), R.layout.choose_service_tab_custom, obj);
                            listview.setAdapter(customAdapter);
                            return;
                        }else{

                            text_empty.setText("Actualmente no se cuenta con servicios disponibles");

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

class ListAdapter_personalizadoServices extends ArrayAdapter<CustomObj>
{
    List<CustomObj> objList;
    CustomObj p;
    String TAG = "GIO";

    public ListAdapter_personalizadoServices(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public ListAdapter_personalizadoServices(Context context, int resource, List<CustomObj> items)
    {
        super(context, resource, items);
        objList = items;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {


        ListAdapter_personalizadoServices.ViewHolder holder = null;
        if (convertView == null)
        {

            LayoutInflater vi = LayoutInflater.from(getContext());
            vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.choose_service_tab_custom, null);
            holder = new ListAdapter_personalizadoServices.ViewHolder();

            holder.backgroundImage = convertView.findViewById(R.id.selectedBG);
            holder.myCel = (RelativeLayout) convertView.findViewById(R.id.myCel);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            holder.myRate = (MaterialRatingBar) convertView.findViewById(R.id.myRate);

            holder.status.setText(objList.get(position).img);


            String imgWithPath = "android.resource://com.sietecerouno.atlantetransportador/drawable/" + objList.get(position).img;
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);


            FirebaseFirestore db = FirebaseFirestore.getInstance();


            // set PEDIDO info from db pedido
            final ViewHolder finalHolder1 = holder;
            DocumentReference docRef = db.collection("pedidos").document(objList.get(position).value);
            finalHolder1.idClient = objList.get(position).value2.toString();
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
                                Date dateTemp = (Date) task.getResult().getData().get("fecha_entrega");
                                String observaciones = document.getData().get("observaciones").toString();

                                if(quien_entrega != null && que_envia != null && origenStr !=null && destinoStr != null)
                                {
                                    String info_txt = "FECHA MÁXIMO DE ENTREGA: \n"
                                            + Manager.getInstance().dateFormatter.format(dateTemp.getTime()) + "\n"
                                            +"Quien envía: " + quien_entrega + "\n"
                                            +"Envío: " + que_envia + "\n"
                                            +"Origen: " + origenStr + "\n"
                                            +"Destino: " + destinoStr+ "\n"
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
                        if (document.exists())
                        {
                            if(document.getData().get("calificacion")!=null)
                            {
                                Long lg = (Long) document.getData().get("calificacion");
                                finalHolder1.myRate.setRating(lg.intValue());
                                finalHolder1.myRate.setRating(lg.intValue());
                            }
                            CropCircleTransformation cropCircleTransformation = new CropCircleTransformation();
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

            // set OFERTAS info from db user
            final Boolean[] bEstado = new Boolean[1];
            final String[] msg = {""};
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DocumentReference docRefMyUser = db.collection("usuarios").document(user.getUid());

            db.collection("pedidos_personalizados")
                    .document(objList.get(position).name)
                    .collection("ofertas")
                    .whereEqualTo("transportista", docRefMyUser)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                            {
                                finalHolder1.idOferta = document.getId();
                                //Log.i(TAG, document.getId() + " => " + document.getData().get("estado"));
                                if(document.getData().get("estado") !=null)
                                {
                                    bEstado[0] = (Boolean) document.getData().get("estado");
                                }

                                finalHolder1.isNew = true;
                                msg[0] = "Sin ofertar";
                                if(bEstado[0] !=null)
                                {

                                    if(bEstado[0])
                                    {
                                        finalHolder1.isNew = false;
                                        if (document.getData().get("contraoferta") == null)
                                            msg[0] = "Pendiente";
                                        else
                                            msg[0] = "Contraoferta";

                                    }else{
                                        finalHolder1.isNew = false;
                                        msg[0] = "Rechazado";
                                    }
                                }
                                finalHolder1.status.setText("Estado: "+msg[0]);
                                finalHolder1.stateActual = msg[0];
                            }
                        }
                    });



            holder.myCel.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            if(finalHolder1.isNew)
                            {
                                Manager.getInstance().idPersonalizadoDoc = objList.get(position).name;
                                Manager.getInstance().idPersonalizado = objList.get(position).value;

                                String _idPersonalizado = String.valueOf(objList.get(position).value);
                                Intent i = new Intent(getContext(), ResumoPersonalizadoActivity.class);
                                i.putExtra("idPersonalizado", _idPersonalizado);
                                i.putExtra("idClient", finalHolder1.idClient);
                                getContext().startActivities(new Intent[]{i});
                            }else{
                                Manager.getInstance().idPersonalizadoDoc = objList.get(position).name;
                                Manager.getInstance().idPersonalizado = objList.get(position).value;

                                Intent i = new Intent(getContext(), OfertActivity.class);
                                i.putExtra("type", finalHolder1.stateActual);
                                i.putExtra("idOferta", finalHolder1.idOferta);
                                i.putExtra("idClient", finalHolder1.idClient);
                                getContext().startActivities(new Intent[]{i});
                            }


                        }
                    });

            convertView.setTag(holder);
        }else {
            holder = (ListAdapter_personalizadoServices.ViewHolder) convertView.getTag();
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
