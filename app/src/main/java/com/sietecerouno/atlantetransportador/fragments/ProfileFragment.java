package com.sietecerouno.atlantetransportador.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.profile.DatosFragment;
import com.sietecerouno.atlantetransportador.profile.GananciasFragment;
import com.sietecerouno.atlantetransportador.sections.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment
{
    OnHeadlineSelectedListener mCallback;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);



        ImageView btnDatos = (ImageView) view.findViewById(R.id.datos_btn);
        ImageView btnGanancias = (ImageView) view.findViewById(R.id.ganancias_btn);
        ImageView btnVehiculo = (ImageView) view.findViewById(R.id.vehiculo_btn);
        ImageView btnCalificacion = (ImageView) view.findViewById(R.id.calificacion_btn);

        //if(Manager.getInstance().goto_profile == "vehiculo")
        //{
            //Manager.getInstance().goto_profile = "";
            //mCallback.changeFr("vehiculo");
        //}


        btnGanancias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.changeFr("ganancias");

            }
        });

        btnDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.changeFr("datos");
            }
        });
        btnVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.changeFr("vehiculo");

            }
        });
        btnCalificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.changeFr("calificacion");

            }
        });

        return view;
    }


    public interface OnHeadlineSelectedListener
    {
        public void changeFr(String _to);
    }


}
