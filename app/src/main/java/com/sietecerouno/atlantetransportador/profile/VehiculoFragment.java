package com.sietecerouno.atlantetransportador.profile;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.sections.ChooseVActivity;
import com.sietecerouno.atlantetransportador.sections.DocumentWhitPhotoActivity;
import com.sietecerouno.atlantetransportador.sections.DocumentsActivity;
import com.sietecerouno.atlantetransportador.sections.RegisterActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class VehiculoFragment extends Fragment implements View.OnClickListener
{

    String TAG = "GIO";
    int numberOfVehicles = 0;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    int actualV;

    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;

    Fragment ref;

    String myCars[] = new String[4];

    public VehiculoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vehiculo, container, false);

        FrameLayout v1 = (FrameLayout) view.findViewById(R.id.v1);
        FrameLayout v2 = (FrameLayout) view.findViewById(R.id.v2);
        FrameLayout v3 = (FrameLayout) view.findViewById(R.id.v3);
        FrameLayout v4 = (FrameLayout) view.findViewById(R.id.v4);

        img1 = (ImageView) view.findViewById(R.id.img1);
        img2 = (ImageView) view.findViewById(R.id.img2);
        img3 = (ImageView) view.findViewById(R.id.img3);
        img4 = (ImageView) view.findViewById(R.id.img4);

        ref = this;

        v1.setOnClickListener(this);
        v2.setOnClickListener(this);
        v3.setOnClickListener(this);
        v4.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final String[] listCar = {
                "bici_small",
                "moto_small",
                "carro_small",
                "camioneta_small",
                "camion_small"
        };

        final int[] listCarId = {
                R.drawable.bici_small,
                R.drawable.moto_small,
                R.drawable.carro_small,
                R.drawable.camioneta_small,
                R.drawable.van_small,
                R.drawable.camion_small,
        };

        final ImageView [] listImg ={
                img1,img2, img3, img4
        };

        actualV = 0;

        DocumentReference docRefV = db.collection("usuarios").document(user.getUid());
        db.collection("vehiculos")
                .whereEqualTo("user", docRefV)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                //Log.i(TAG, document.getId() + " => " + document.getData().get("tipo"));

                                Long myType = (Long) document.getData().get("tipo");
                                int realType = myType.intValue()-1;
                                myCars[actualV] = document.getId();


                                Picasso.with(getContext())
                                        .load(listCarId[realType])
                                        .into(listImg[actualV]);

                                if(Objects.equals(document.getId(), Manager.getInstance().vehicleSelected))
                                {
                                    allEquals();

                                    listImg[actualV].setPadding(5, 5, 5, 5);
                                    listImg[actualV].setBackground(getResources().getDrawable(R.drawable.bg_circle_blue));
                                }

                                listImg[actualV].setOnClickListener((View.OnClickListener) ref);
                                actualV++;
                            }
                        } else {
                            Log.i(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        return view;

    }


    //SET TRANSPORTER CAR'S
    private void registerV()
    {
        DocumentReference docRefV = db.collection("vehiculos").document(Manager.getInstance().vehicleSelected);

        docRefV.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document != null)
                    {
                        Log.i(TAG, document.getData().get("tipo").toString());
                        Manager.getInstance().vehicleSelectedType = Integer.parseInt( document.getData().get("tipo").toString());

                    } else {
                        Log.i(TAG, "No such document inside");
                    }
                } else {
                    Log.i(TAG, "get failed with inside", task.getException());
                }
            }
        });

        Map<String, Object> current = new HashMap<>();
        current.put("vehiculo_seleccionado", docRefV);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("usuarios").document(user.getUid())
                .set(current, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void _void) {
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + _void.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    @Override
    public void onClick(View view)
    {
        Log.d(TAG, String.valueOf(view));
        allEquals();
        if(view.getId() == R.id.img1)
        {
            Manager.getInstance().vehicleSelected = myCars[0];
            img1.setPadding(5, 5, 5, 5);
            img1.setBackground(getResources().getDrawable(R.drawable.bg_circle_blue));
            registerV();
        }
        else if(view.getId() == R.id.img2)
        {
            if(myCars[1]!=null)
                Manager.getInstance().vehicleSelected = myCars[1];
            img2.setPadding(5, 5, 5, 5);
            img2.setBackground(getResources().getDrawable(R.drawable.bg_circle_blue));
            registerV();
        }
        else if(view.getId() == R.id.img3)
        {
            if(myCars[2]!=null)
                Manager.getInstance().vehicleSelected = myCars[2];
            img3.setPadding(5, 5, 5, 5);
            img3.setBackground(getResources().getDrawable(R.drawable.bg_circle_blue));
            registerV();
        }
        else if(view.getId() == R.id.img4)
        {
            if(myCars[3]!=null)
                Manager.getInstance().vehicleSelected = myCars[3];
            img4.setPadding(5, 5, 5, 5);
            img4.setBackground(getResources().getDrawable(R.drawable.bg_circle_blue));
            registerV();
        }
        else{
            Manager.getInstance().isFirstV = false;
            Intent i = new Intent(getActivity(), ChooseVActivity.class);
            startActivity(i);
        }


    }

    void allEquals()
    {
        img1.setPadding(0,0,0,0);
        img1.setBackground(null);

        img2.setPadding(0,0,0,0);
        img2.setBackground(null);

        img3.setPadding(0,0,0,0);
        img4.setBackground(null);

        img4.setPadding(0,0,0,0);
        img4.setBackground(null);
    }
}
