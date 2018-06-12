package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgetPassActivity extends AppCompatActivity
{
    String TAG="GIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);


        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

        TextView title = (TextView) findViewById(R.id.action_bar_title);
        final TextView mail_txt = (TextView) findViewById(R.id.mail_txt);
        title.setText("RESTAURAR CONTRASEÑA");

        //bg
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Glide.with(bg.getContext()).load(R.drawable.bg_solo).into(bg);

        TextView send_btn = (TextView) findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mail_txt.getText().length() > 0)
                {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(mail_txt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(ForgetPassActivity.this)
                                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                .setCornerRadius(20)
                                                .setTextGravity(Gravity.CENTER)
                                                .setTitle("Perfecto!")
                                                .setMessage("Te enviamos un enlace para reestablecer la contraseña.")
                                                .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        finish();

                                                    }
                                                });

                                        pDialog.show();

                                    }
                                }
                            });
                }
                else{

                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(ForgetPassActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setCornerRadius(20)
                            .setTextGravity(Gravity.CENTER)
                            .setMessage("Ingresa tu correo electrónico")
                            .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            });

                    pDialog.show();


                }




            }
        });
    }
}
