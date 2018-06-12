package com.sietecerouno.atlantetransportador.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.utils.CustomObj;

import java.util.ArrayList;


/**
 * Created by MAURICIO on 6/04/17.
 */

public class AdapterMenu extends BaseAdapter {

    private Context activity;
    private LayoutInflater inflater;
    private ArrayList<CustomObj> objects;

    public AdapterMenu(Context activity, ArrayList<CustomObj> retoObjects) {
        this.activity = activity;
        this.objects = retoObjects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int location) {
        return objects.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.row_menu, null);

        TextView titulo = (TextView) convertView.findViewById(R.id.text_menu);
        titulo.setText(objects.get(position).name);

        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        Glide.with(icon.getContext())
                .load(objects.get(position).valueInt)
                .into(icon);


        return convertView;
    }

}
