package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.utils.CustomObj;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class ChooseVActivity extends AppCompatActivity
{
    String TAG = "GIO";
    ListView listview;
    public static List<CustomObj> obj = new ArrayList<CustomObj>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_v);

        Manager.getInstance().actualContext = this;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("INFORMACIÓN VEHÍCULO");

        //bg
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Glide.with(bg.getContext()).load(R.drawable.bg_solo).into(bg);

        TextView btn_next = (TextView) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Manager.getInstance().isFirstV)
                {
                    Intent i = new Intent(ChooseVActivity.this, DocumentsActivity.class);
                    i.putExtra("show", "all");
                    startActivity(i);
                }else{
                    Intent i = new Intent(ChooseVActivity.this, DocumentsActivity.class);
                    i.putExtra("show", "infoCar");
                    startActivity(i);
                }
            }
        });


        String[] listCar = {
                "bici_small",
                "moto_small",
                "carro_small",
                "camioneta_small",
                "van_small",
                "camion_small"
        };

        String[] listTitle = {
                "BICICLETA ( Sin licencia )",
                "MOTO ( Categoría A1/A2 )",
                "AUTOMOVIL ( Categoría B1 )",
                "CAMIONETA ( Categoría B1 )",
                "VAN / PICKUP / CAMIÓN PARTICULAR\n" +
                        "( Categoría B2 / B3 )",
                "VAN / PICKUP / CAMIÓN PÚBLICO\n" +
                        "( Categoría C1 / C2 )"
        };

        String[] listDesc = {
                "No necesitas licencia de conducción, podrás trabajar con tu bici, todo el tiempo sin preocuparte por el pico y placa, o el trafico",
                "Necesitas licencia de conducción A1 para cilindraje menor o igual a 125 c.c. o A2 para cilindraje superior a 125 c.c., podrás trabajar con tu vehículo de dos ruedas todo el tiempo sin preocuparte por pico y placa",
                "Necesitas licencia de conducción B1 o superior para poder trabajar con tu vehículo particular de pequeñas dimensiones (automóvil), o el de alguien más que te permita hacer uso del mismo. Y, así podrás sacar provecho de tu vehículo sin asumir los riesgos de llevar pasajeros",
                "Necesitas licencia de conducción B1 o superior para poder trabajar con tu vehículo particular de medianas dimensiones (camioneta), o el de alguien más que te permita hacer uso del mismo. Y, así podrás sacar provecho de tu vehículo sin asumir los riesgos de llevar pasajeros",
                "Necesitas licencia de conducción B2 o superior para poder trabajar con tu vehículo particular de grandes dimensiones (Van/Pickup/Camión), o el de alguien más que te permita hacer uso del mismo. Si el vehículo que deseas usar es articulado necesitas una licencia de conducción B3. Y, así podrás sacar provecho de tu vehículo sin asumir los riesgos de llevar pasajeros",
                "Si cuentas con un vehículo de servicio público necesitas contar con una licencia de conducción C1 o superior. Sin embargo, si el vehículo que deseas usar es un camión rígido debes contar con una licencia de conducción C2 o superior. Y, así podrás sacar provecho de tu vehículo en aquellos momentos que no tienes pasajeros, no tienes un servicio en el día o deseas hacer uso de tu espacio libre"
        };

        obj.clear();
        for (Integer i =0 ; i < listCar.length ; i++)
        {
            obj.add(new CustomObj(listTitle[i], listDesc[i], listCar[i], false,"", 0, 0 ,0));
        }



        listview = (ListView) findViewById(R.id.s_listView);
        ListAdapter_detailProfile customAdapter = new ListAdapter_detailProfile(this, R.layout.choose_v_tab_custom, obj);
        listview.setAdapter(customAdapter);
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





class ListAdapter_detailProfile extends ArrayAdapter<CustomObj>
{
    List<CustomObj> objList;
    CustomObj p;
    String TAG = "GIO";

    public ListAdapter_detailProfile(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public ListAdapter_detailProfile(Context context, int resource, List<CustomObj> items)
    {
        super(context, resource, items);
        objList = items;
    }

    Drawable background;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {


        ViewHolder holder = null;
        if (convertView == null)
        {


            LayoutInflater vi = LayoutInflater.from(getContext());
            vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.choose_v_tab_custom, null);
            holder = new ViewHolder();



            holder.backgroundImage = convertView.findViewById(R.id.selectedBG);
            holder.bg = convertView.findViewById(R.id.bg);
            holder.myCel = (RelativeLayout) convertView.findViewById(R.id.myCel);




            float num = position;
            if((num/2)-(position/2) == 0 ) {
                holder.backgroundImage.setVisibility(View.INVISIBLE);
            }else{
                holder.backgroundImage.setVisibility(View.VISIBLE);
            }



            String imgWithPath = "android.resource://com.sietecerouno.atlantetransportador/drawable/" + objList.get(position).img;
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.id);
            holder.desc = (TextView) convertView.findViewById(R.id.description);
            Glide.with(holder.icon.getContext()).load(imgWithPath).into(holder.icon);

            holder.title.setText(objList.get(position).name);
            holder.desc.setText(objList.get(position).value);

            final ViewHolder finalHolder = holder;
            final ViewHolder[] finalHolder2 = {null};

            holder.myCel.setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            Manager.getInstance().idTempVSelected = position;

                            for(int i = 0; i<objList.size(); i++)
                            {
                                objList.get(i).selected = false;
                            }
                            objList.get(position).selected = true;

                            finalHolder.bg.setVisibility(View.VISIBLE);
                            notifyDataSetChanged();

                        }
                    });

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }




        //Drawable background = holder.backgroundImage.getBackground();

        if(objList.get(position).selected)
            holder.bg.setVisibility(View.VISIBLE);
        else
            holder.bg.setVisibility(View.INVISIBLE);


        return convertView;
    }

    public static class ViewHolder
    {
        public ImageView icon;
        View backgroundImage;
        View bg;
        public RelativeLayout myCel;
        public TextView title;
        public TextView desc;

    }

    public boolean isSelected(String _tag)
    {
        boolean myBol = false;
        for (int i = 0; i < ChooseVActivity.obj.size(); i++)
        {
            if (Objects.equals(_tag, String.valueOf(ChooseVActivity.obj.get(i).selected)))
            {
                myBol = true;
                return myBol;
            }else{
                myBol = false;
            }
        }
        return myBol;
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