package com.sietecerouno.atlantetransportador.utils;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.sections.DocumentWhitPhotoActivity;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContainerImageFragment extends Fragment
{

    private String title;
    private int page;


    public ContainerImageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container_image, container, false);


        ImageView img = (ImageView) view.findViewById(R.id.img);

        Picasso.with(getActivity())
                .load(title)
                .into(img);

        return view;
    }

    // newInstance constructor for creating fragment with arguments
    public static ContainerImageFragment newInstance(int page, String title) {
        ContainerImageFragment fragmentFirst = new ContainerImageFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

}
