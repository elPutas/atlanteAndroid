package com.sietecerouno.atlantetransportador.profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class DatosFragment extends Fragment {


    public DatosFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View v = inflater.inflate(R.layout.fragment_datos, container, false);
        ImageView photo = (ImageView) v.findViewById(R.id.photo);
        TextView name = (TextView) v.findViewById(R.id.name_txt);
        TextView cel = (TextView) v.findViewById(R.id.cell_txt);

        String completeName = Manager.getInstance().user_name + " " + Manager.getInstance().user_last_name;
        name.setText(completeName);
        cel.setText(Manager.getInstance().user_cel);
        CropCircleTransformation cropCircleTransformation = new CropCircleTransformation();
        Glide.with(photo.getContext())
                .load(Manager.getInstance().user_photo)
                .apply(RequestOptions.bitmapTransform(cropCircleTransformation))
                .into(photo);


        return v;
    }

}
