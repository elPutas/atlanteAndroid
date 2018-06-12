package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.firebase.firestore.Query;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.utils.UploadImg;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.EZPhotoPickStorage;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;

import static java.security.AccessController.getContext;

public class DocumentWhitPhotoActivity extends AppCompatActivity
{

    String actualSection;
    int numPhoto = 0;


    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;

    TextView copy;

    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_whit_photo);

        Manager.getInstance().actualContext = this;

        copy = (TextView) findViewById(R.id.copy);

        image1 = (ImageView) findViewById(R.id.img1);
        image2 = (ImageView) findViewById(R.id.img2);
        image3 = (ImageView) findViewById(R.id.img3);
        image4 = (ImageView) findViewById(R.id.img4);

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);


        //bg
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Glide.with(bg.getContext()).load(R.drawable.bg_solo).into(bg);


        //get extra
        Intent myIntent = getIntent(); // gets the previously created intent
        String typeKey = myIntent.getStringExtra("type");
        actualSection = typeKey;

        TextView title = (TextView) findViewById(R.id.action_bar_title);




        TextView btn_next = (TextView) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DocumentWhitPhotoActivity.this, DocumentsActivity.class);
                startActivity(i);
                finish();
            }
        });
        ConstraintLayout square1 = (ConstraintLayout) findViewById(R.id.square1);
        ConstraintLayout square2 = (ConstraintLayout) findViewById(R.id.square2);
        ConstraintLayout square3 = (ConstraintLayout) findViewById(R.id.square3);
        ConstraintLayout square4 = (ConstraintLayout) findViewById(R.id.square4);


        final CFAlertDialog.Builder alertDialog = new CFAlertDialog.Builder(DocumentWhitPhotoActivity.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setCornerRadius(20)
                .setTextGravity(Gravity.CENTER)
                .setMessage("Selecciona una opcion")
                .addButton("Galería", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        galleryPicture();
                    }
                })
                .addButton("Cámara", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        cameraPicture();
                    }

                });

        /*
        final AlertDialog alertDialog = new AlertDialog.Builder(DocumentWhitPhotoActivity.this).create();
        alertDialog.setTitle("Selecciona una opción");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cámara",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        cameraPicture();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Galería", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                galleryPicture();
            }
        });
        */



        square1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numPhoto = 1;
                alertDialog.show();
            }
        });

        square2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numPhoto = 2;
                alertDialog.show();
            }
        });

        square3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numPhoto = 3;
                alertDialog.show();
            }
        });

        square4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numPhoto = 4;
                alertDialog.show();
            }
        });


        ImageView icon = (ImageView) findViewById(R.id.icon);

        switch (typeKey)
        {
            case "photo":
                square2.setVisibility(View.GONE);
                square3.setVisibility(View.GONE);
                square4.setVisibility(View.GONE);
                title.setText("FOTOGRAFÍA");
                copy.setText("A continuación sube una fotografía del conductor de tu vehículo de manera legible.");

                if(Manager.getInstance().photoTemp_v_user_1.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_user_1)
                            .into(image1);
                break;

            case "cc":
                Glide.with(icon.getContext()).load(R.drawable.icon_cc).into(icon);
                square3.setVisibility(View.GONE);
                square4.setVisibility(View.GONE);
                title.setText("DOCUMENTO DE IDENTIDAD");
                copy.setText("A continuación sube la imagen de tu documento de identidad de manera legible, por ambas caras.");

                if(Manager.getInstance().photoTemp_v_cc_1.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_cc_1)
                            .into(image1);
                if(Manager.getInstance().photoTemp_v_cc_2.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_cc_2)
                            .into(image2);
                break;

            case "idCar":
                Glide.with(icon.getContext()).load(R.drawable.icon_cc).into(icon);
                square3.setVisibility(View.GONE);
                square4.setVisibility(View.GONE);
                title.setText("LICENCIA DE CONDUCCIÓN");
                copy.setText("A continuación sube la imagen de la Licencia de conducción de manera legible, por ambas caras.");


                if(Manager.getInstance().photoTemp_v_id_1.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_id_1)
                            .into(image1);
                if(Manager.getInstance().photoTemp_v_id_2.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_id_2)
                            .into(image2);
                break;

            case "rut":
                Glide.with(icon.getContext()).load(R.drawable.icon_rut).into(icon);
                title.setText("RUT");
                copy.setText("A continuación sube la imagen del RUT de manera legible.");

                if(Manager.getInstance().photoTemp_v_rut_1.length() > 0) {
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_rut_1)
                            .into(image1);
                }
                square2.setVisibility(View.GONE);
                square3.setVisibility(View.GONE);
                square4.setVisibility(View.GONE);

                break;

            case "tarjetaPropiedad":
                Glide.with(icon.getContext()).load(R.drawable.icon_cc).into(icon);
                Glide.with(icon.getContext()).load(R.drawable.icon_tarjeta).into(icon);
                title.setText("TARJETA DE PROPIEDAD");
                copy.setText("A continuación sube la imagen de la Tarjeta de propiedad de tu vehículo en regla y de manera legible, por ambas caras.");

                square3.setVisibility(View.GONE);
                square4.setVisibility(View.GONE);
                if(Manager.getInstance().photoTemp_v_tp_1.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_tp_1)
                            .into(image1);

                if(Manager.getInstance().photoTemp_v_tp_2.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_tp_2)
                            .into(image2);


                break;

            case "soat":
                Glide.with(icon.getContext()).load(R.drawable.icon_rut).into(icon);
                title.setText("SOAT");
                copy.setText("A continuación sube la imagen del SOAT de tu vehículo en regla y de manera legible, por ambas caras.");

                square2.setVisibility(View.GONE);
                square3.setVisibility(View.GONE);
                square4.setVisibility(View.GONE);
                if(Manager.getInstance().photoTemp_v_soat_1.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_soat_1)
                            .into(image1);


                break;

            case "tecnomecanica":
                Glide.with(icon.getContext()).load(R.drawable.icon_rut).into(icon);
                title.setText("TECNOMACÁNICA");
                copy.setText("A continuación sube la imagen de la tecnomecanica de tu vehículo en regla y de manera legible, por ambas caras.");

                square2.setVisibility(View.GONE);
                square3.setVisibility(View.GONE);
                square4.setVisibility(View.GONE);

                if(Manager.getInstance().photoTemp_v_tec_1.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_tec_1)
                            .into(image1);


                break;

            case "car":
                Glide.with(icon.getContext()).load(R.drawable.icon_vehiculo).into(icon);
                title.setText("VEHÍCULO");
                copy.setText("A continuación sube la fotografía con los 4 ángulos de tu vehículo: de frente, perfil derecho, perfil izquierdo, y parte trasera.");

                if(Manager.getInstance().photoTemp_v_img_1.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_img_1)
                            .into(image1);

                if(Manager.getInstance().photoTemp_v_img_2.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_img_2)
                            .into(image2);

                if(Manager.getInstance().photoTemp_v_img_3.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_img_3)
                            .into(image3);

                if(Manager.getInstance().photoTemp_v_img_4.length() > 0)
                    Picasso.with(DocumentWhitPhotoActivity.this)
                            .load(Manager.getInstance().photoTemp_v_img_4)
                            .into(image4);


                break;
        }
    }

    public void cameraPicture()
    {

        EZPhotoPickConfig config = new EZPhotoPickConfig();
        config.photoSource = PhotoSource.CAMERA;
        config.needToExportThumbnail = true;
        config.exportingThumbSize = 200;
        config.exportingSize = 1000;
        EZPhotoPick.startPhotoPickActivity(DocumentWhitPhotoActivity.this, config);

    }


    public void galleryPicture()
    {

        EZPhotoPickConfig config = new EZPhotoPickConfig();
        config.photoSource = PhotoSource.GALLERY;
        config.needToExportThumbnail = true;
        config.exportingThumbSize = 200;
        config.exportingSize = 1000;
        EZPhotoPick.startPhotoPickActivity(DocumentWhitPhotoActivity.this, config);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
        {
            return;
        }




        if(requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE)
        {
            try {
                Bitmap pickedPhoto = new EZPhotoPickStorage(DocumentWhitPhotoActivity.this).loadLatestStoredPhotoBitmapThumbnail();
                UploadImg uploadImg = new UploadImg();
                switch (numPhoto){
                    case 1:
                        image1.setImageBitmap(pickedPhoto);
                        uploadImg.uploadImageFirebase(pickedPhoto, actualSection, 1);
                        break;
                    case 2:
                        image2.setImageBitmap(pickedPhoto);
                        uploadImg.uploadImageFirebase(pickedPhoto, actualSection, 2);
                        break;
                    case 3:
                        image3.setImageBitmap(pickedPhoto);
                        uploadImg.uploadImageFirebase(pickedPhoto, actualSection, 3);
                        break;
                    case 4:
                        image4.setImageBitmap(pickedPhoto);
                        uploadImg.uploadImageFirebase(pickedPhoto, actualSection, 4);
                        break;
                }

                //uploadImageFirebase(pickedPhoto);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE){
            try {
                Bitmap pickedPhoto1 = new EZPhotoPickStorage(this).loadLatestStoredPhotoBitmapThumbnail();
                UploadImg uploadImg = new UploadImg();
                switch (numPhoto){
                    case 1:
                        image1.setImageBitmap(pickedPhoto1);
                        uploadImg.uploadImageFirebase(pickedPhoto1, actualSection, 1);
                        break;
                    case 2:
                        image2.setImageBitmap(pickedPhoto1);
                        uploadImg.uploadImageFirebase(pickedPhoto1, actualSection, 2);
                        break;
                    case 3:
                        image3.setImageBitmap(pickedPhoto1);
                        uploadImg.uploadImageFirebase(pickedPhoto1, actualSection, 3);
                        break;
                    case 4:
                        image4.setImageBitmap(pickedPhoto1);
                        uploadImg.uploadImageFirebase(pickedPhoto1, actualSection, 4);
                        break;
                }

                //uploadImageFirebase(pickedPhoto1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(DocumentWhitPhotoActivity.this, DocumentsActivity.class);
        startActivity(i);
        finish();
    }
/*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1)
        {
            ImageView imageView1 = (ImageView) findViewById(R.id.img1);
            ImageView imageView2 = (ImageView) findViewById(R.id.img2);
            ImageView imageView3 = (ImageView) findViewById(R.id.img3);

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                switch (numPhoto)
                {
                    case 1:
                        imageView1.setImageBitmap(bitmap);
                        break;
                    case 2:
                        imageView2.setImageBitmap(bitmap);
                        break;
                    case 3:
                        imageView3.setImageBitmap(bitmap);
                        break;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }*/
@Override
protected void onDestroy() {
    super.onDestroy();
    Manager.getInstance().actualContext = null;
}

}
