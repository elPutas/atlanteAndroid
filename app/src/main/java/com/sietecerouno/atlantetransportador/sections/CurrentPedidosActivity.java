package com.sietecerouno.atlantetransportador.sections;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.sietecerouno.atlantetransportador.utils.CustomObj;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class CurrentPedidosActivity extends AppCompatActivity
{
    FirebaseFirestore db;
    String TAG = "GIO";
    ListView listview;

    TextView text_empty;

    DocumentReference docRefMyCar;
    DocumentReference docRefMyUser;

    TextView v_1;
    TextView v_2;
    TextView v_3;
    TextView v_4;

    ArrayList listCars;
    ArrayList listIDCars;
    ArrayList listTypeCars;

    int iNum;
    public static List<CustomObj> obj = new ArrayList<CustomObj>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_pedidos);

        Manager.getInstance().actualContext = this;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //bg
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Glide.with(bg.getContext()).load(R.drawable.bg_solo).into(bg);

        text_empty = (TextView) findViewById(R.id.text_empty);
        TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("ENVÍOS EN CURSO");

        v_1 = (TextView) findViewById(R.id.v_1);
        v_2 = (TextView) findViewById(R.id.v_2);
        v_3 = (TextView) findViewById(R.id.v_3);
        v_4 = (TextView) findViewById(R.id.v_4);

        listCars = new ArrayList();
        listCars.add(v_1);
        listCars.add(v_2);
        listCars.add(v_3);
        listCars.add(v_4);

        db = FirebaseFirestore.getInstance();

        text_empty.setText("Actualmente no se cuenta con servicios disponibles");
        listview = (ListView) findViewById(R.id.s_listView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        docRefMyUser = db.collection("usuarios").document(user.getUid());
        if(Manager.getInstance().vehicleSelected != "")
            docRefMyCar = db.collection("vehiculos").document(Manager.getInstance().vehicleSelected);
        checkdb();

        db.collection("vehiculos")
                .whereEqualTo("user", docRefMyUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful()) {
                            listIDCars = new ArrayList();
                            listTypeCars = new ArrayList();
                            iNum = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                listIDCars.add(document.getId());

                                Long myType = (Long) document.getData().get("tipo");
                                int realType = myType.intValue();
                                listTypeCars.add(realType);

                                TextView textViewTemp = (TextView) listCars.get(iNum);
                                textViewTemp.setVisibility(View.VISIBLE);
                                textViewTemp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        allCarsGray();
                                        if (view.getId() == R.id.v_1)
                                        {
                                            v_1.setTextColor(getResources().getColor(R.color.blueApp));
                                            docRefMyCar = db.collection("vehiculos").document(listIDCars.get(0).toString());
                                        }
                                        if (view.getId() == R.id.v_2) {
                                            v_2.setTextColor(getResources().getColor(R.color.blueApp));
                                            docRefMyCar = db.collection("vehiculos").document(listIDCars.get(1).toString());
                                        }
                                        if (view.getId() == R.id.v_3) {
                                            v_3.setTextColor(getResources().getColor(R.color.blueApp));
                                            docRefMyCar = db.collection("vehiculos").document(listIDCars.get(2).toString());
                                        }
                                        if (view.getId() == R.id.v_4) {
                                            v_4.setTextColor(getResources().getColor(R.color.blueApp));
                                            docRefMyCar = db.collection("vehiculos").document(listIDCars.get(3).toString());
                                        }

                                        obj.clear();
                                        listview.setAdapter(null);
                                        checkdb();

                                    }
                                });


                                if (Objects.equals(document.getId(), Manager.getInstance().vehicleSelected)) {
                                    textViewTemp.setTextColor(getResources().getColor(R.color.blueApp));
                                }
                                iNum++;

                            }
                        }


                    }

                });



    }

    void allCarsGray()
    {
        v_1.setTextColor(getResources().getColor(R.color.gray));
        v_2.setTextColor(getResources().getColor(R.color.gray));
        v_3.setTextColor(getResources().getColor(R.color.gray));
        v_4.setTextColor(getResources().getColor(R.color.gray));
    }



    void checkdb()
    {
        db.collection("pedidos")
                .whereEqualTo("transportista", docRefMyUser)
                .whereEqualTo("vehiculo_obj", docRefMyCar)
                .whereEqualTo("estado_actual", 2)
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
                        obj.clear();
                        for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                        {
                            Integer type = Integer.parseInt(document.getLong("tipo").toString());

                            String quien_entrega = document.getData().get("quien_entrega").toString();
                            String que_envia = document.getData().get("que_envia").toString();
                            String origenStr = document.getData().get("origenStr").toString();
                            String destinoStr = document.getData().get("destinoStr").toString();
                            String observaciones = "";
                            DocumentReference user = (DocumentReference) document.getData().get("user");
                            String userId = user.getId().toString();
                            Date fechaEntrega = (Date) document.getData().get("fecha_entrega");
                            if(document.getData().get("observaciones")!=null)
                            {
                                observaciones = document.getData().get("observaciones").toString();
                            }

                            String info_txt = "FECHA MÁXIMO DE ENTREGA: " + Manager.getInstance().dateFormatter.format(fechaEntrega.getTime()) + "\n"
                                    +"Quien envía: " + quien_entrega + "\n"
                                    +"Envío: " + que_envia + "\n"
                                    +"Origen: " + origenStr + "\n"
                                    +"Destino: " + destinoStr+ "\n"
                                    +"Observaciones: " + observaciones;



                            obj.add(new CustomObj(document.getId(), info_txt, document.getId(), false, userId, type, 0,0));

                        }



                        if(obj.size() > 0)
                        {

                            ListAdapter_currentServices customAdapter = new ListAdapter_currentServices(CurrentPedidosActivity.this, R.layout.choose_service_tab_custom, obj);
                            listview.setAdapter(customAdapter);
                        }else{
                            text_empty.setText("Actualmente no se cuenta con servicios disponibles");
                        }
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
}

class ListAdapter_currentServices extends ArrayAdapter<CustomObj>
{
    List<CustomObj> objList;
    CustomObj p;
    String TAG = "GIO";

    public ListAdapter_currentServices(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public ListAdapter_currentServices(Context context, int resource, List<CustomObj> items)
    {
        super(context, resource, items);
        objList = items;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {


        ListAdapter_currentServices.ViewHolder holder = null;
        if (convertView == null)
        {

            LayoutInflater vi = LayoutInflater.from(getContext());
            vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.current_service_tab_custom, null);
            holder = new ListAdapter_currentServices.ViewHolder();

            holder.backgroundImage = convertView.findViewById(R.id.selectedBG);
            holder.myCel = (RelativeLayout) convertView.findViewById(R.id.myCel);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.idR = (TextView) convertView.findViewById(R.id.idR);


            holder.info.setText(objList.get(position).value);
            holder.idR.setText("ID: #"+objList.get(position).img);


            final ListAdapter_currentServices.ViewHolder finalHolder1 = holder;
            finalHolder1.idClient = objList.get(position).value2.toString();
            
            int colorToSend = 0;

            if(objList.get(position).valueInt == 1)
            {
                holder.type.setText("SERVICIO INMEDIATO");
                holder.type.setTextColor(ContextCompat.getColor(getContext(), R.color.blueApp));
                colorToSend = R.color.blueApp;
            }
            else if(objList.get(position).valueInt == 2)
            {
                holder.type.setText("SERVICIO PERSONALIZADO");
                holder.type.setTextColor(ContextCompat.getColor(getContext(), R.color.greenApp));
                colorToSend = R.color.greenApp;
            }
            else if(objList.get(position).valueInt == 3)
            {
                holder.type.setText("SERVICIO PROGRAMADO");
                holder.type.setTextColor(ContextCompat.getColor(getContext(), R.color.yellowApp));
                colorToSend = R.color.yellowApp;
            }

            final int finalcolorToSend = colorToSend;
            holder.myCel.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {

                            String idUser = String.valueOf(objList.get(position).value2);
                            Manager.getInstance().actualClientId_finish = idUser;

                            Intent i = new Intent(getContext(), ResumoFinishAllActivity.class);
                            i.putExtra("type", true);
                            i.putExtra("idClient", idUser);
                            i.putExtra("idPersonalizado", objList.get(position).name);
                            i.putExtra("color", finalcolorToSend);

                            getContext().startActivities(new Intent[]{i});
                            ((Activity)getContext()).finish();
                        }
                    });

            convertView.setTag(holder);
        }else {
            holder = (ListAdapter_currentServices.ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public static class ViewHolder
    {
        public ImageView icon;
        public TextView status;
        public TextView info;
        public TextView type;
        public TextView idR;
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
