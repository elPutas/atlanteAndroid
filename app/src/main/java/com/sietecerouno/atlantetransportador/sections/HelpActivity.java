package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.utils.Chat;
import com.sietecerouno.atlantetransportador.utils.CustomObj;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelpActivity extends AppCompatActivity
{

    String TAG = "GIO";
    ListView listview;
    public static List<CustomObj> obj = new ArrayList<CustomObj>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueApp)));


        final TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("AYUDA");

        obj.clear();
        listview = (ListView) findViewById(R.id.s_listView);
        obj.add(new CustomObj("", "¿Algo fallo?, háznoslo saber para que nuestro equipo profesional de soporte centre toda su atención en tus necesidades", "", false, "chat", R.drawable.soporte_img, 0,0));
        obj.add(new CustomObj("", "Cuéntanos los aciertos y desacierto de Atlant.e desde tu experiencia, denuncia cualquier situación de la cual estas inconforme.", "", false, "comments", R.drawable.comentarios_img, 0,0));
        obj.add(new CustomObj("", "Desde esta sección puedes actualizar tus datos personales o de contacto en caso de que estos hayan cambiado. Ingresa al aplicativo la información que deseas cambiar, nuestro soporte se pondrá en contacto contigo para que nos suministres el soporte de este cambio", "", false, "update", R.drawable.brn_update, 0,0));

        ListAdapter_help customAdapter = new ListAdapter_help(HelpActivity.this, R.layout.choose_service_tab_custom, obj);
        listview.setAdapter(customAdapter);
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



class ListAdapter_help extends ArrayAdapter<CustomObj>
{
    List<CustomObj> objList;
    CustomObj p;
    String TAG = "GIO";

    public ListAdapter_help(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public ListAdapter_help(Context context, int resource, List<CustomObj> items)
    {
        super(context, resource, items);
        objList = items;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {


        ListAdapter_help.ViewHolder holder = null;
        if (convertView == null)
        {

            LayoutInflater vi = LayoutInflater.from(getContext());
            vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.customtab_help, null);
            holder = new ListAdapter_help.ViewHolder();

            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.copy = (TextView) convertView.findViewById(R.id.copy);

            holder.title.setText(objList.get(position).name);
            holder.copy.setText(objList.get(position).value);

            holder.icon.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent chat = new Intent(getContext(), ChatSoporteActivity.class);
                    Intent commets = new Intent(getContext(), CommentsActivity.class);
                    Intent update = new Intent(getContext(), UpdateDataActivity.class);


                    if(objList.get(position).value2 == "chat")
                    {
                        getContext().startActivity(chat);
                    }
                    else if(objList.get(position).value2 == "comments")
                    {
                        getContext().startActivity(commets);
                    }
                    else if(objList.get(position).value2 == "update")
                    {
                        getContext().startActivity(update);
                    }
                    Log.i(TAG, objList.get(position).value2);
                }
            });
            Glide.with(holder.icon.getContext())
                    .load(objList.get(position).valueInt)
                    .into(holder.icon);

            convertView.setTag(holder);
        }else {
            holder = (ListAdapter_help.ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    public static class ViewHolder
    {
        public ImageView icon;
        public TextView title;
        public TextView copy;
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
