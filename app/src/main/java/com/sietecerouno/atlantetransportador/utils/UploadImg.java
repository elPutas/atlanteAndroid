package com.sietecerouno.atlantetransportador.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sietecerouno.atlantetransportador.manager.Manager;


import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Created by Gio on 7/11/17.
 */

public class UploadImg
{
    public void uploadImageFirebase(Bitmap bitmap, final String saveIn, final int pos)
    {
        //pDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
        String typeImage = ".jpeg";
        String datetime = ft.format(dNow);
        String pathStorage = "gs://atlante-d7f0b.appspot.com/";
        String reference = pathStorage + datetime + typeImage;
        StorageReference storageRef = storage.getReferenceFromUrl(reference);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //pDialog.hide();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();


                switch (saveIn)
                {
                    case "photo":
                        Manager.getInstance().photoTemp_v_user_1 = String.valueOf(downloadUrl);
                        break;

                    case "cc":
                        if(pos ==1)
                            Manager.getInstance().photoTemp_v_cc_1 = String.valueOf(downloadUrl);
                        if(pos ==2)
                            Manager.getInstance().photoTemp_v_cc_2 = String.valueOf(downloadUrl);
                        break;

                    case "idCar":
                        if(pos ==1)
                            Manager.getInstance().photoTemp_v_id_1 = String.valueOf(downloadUrl);
                        if(pos ==2)
                            Manager.getInstance().photoTemp_v_id_2 = String.valueOf(downloadUrl);
                        break;

                    case "rut":
                        Manager.getInstance().photoTemp_v_rut_1 = String.valueOf(downloadUrl);
                        break;

                    case "tarjetaPropiedad":
                        if(pos ==1)
                            Manager.getInstance().photoTemp_v_tp_1 = String.valueOf(downloadUrl);
                        if(pos ==2)
                            Manager.getInstance().photoTemp_v_tp_2 = String.valueOf(downloadUrl);
                        break;

                    case "soat":
                        Manager.getInstance().photoTemp_v_soat_1 = String.valueOf(downloadUrl);
                        break;

                    case "tecnomecanica":
                            Manager.getInstance().photoTemp_v_tec_1 = String.valueOf(downloadUrl);
                        break;

                    case "car":
                        if(pos ==1)
                            Manager.getInstance().photoTemp_v_img_1 = String.valueOf(downloadUrl);
                        if(pos ==2)
                            Manager.getInstance().photoTemp_v_img_2 = String.valueOf(downloadUrl);
                        if(pos ==3)
                            Manager.getInstance().photoTemp_v_img_3 = String.valueOf(downloadUrl);
                        if(pos ==4)
                            Manager.getInstance().photoTemp_v_img_4 = String.valueOf(downloadUrl);
                        break;
                }
                Log.i("GIO", String.valueOf(downloadUrl));
            }
        });

    }

    public void realUpdatePhotos()
    {

    }
}



