package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity
{
    String TAG = "GIO";
    FirebaseFirestore db;
    EditText editText;
    TextView btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Manager.getInstance().actualContext = this;
        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueApp)));

        db = FirebaseFirestore.getInstance();
        editText = (EditText) findViewById(R.id.myText);
        btnNext = (TextView) findViewById(R.id.btn_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().length()<=0)
                {
                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(CommentsActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Escribe tu comentario")
                            .setCornerRadius(20)
                            .setTextGravity(Gravity.CENTER)
                            .addButton("CONTINUAR", Color.parseColor("#FFFFFF"), getResources().getColor(R.color.blueApp), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    pDialog.show();
                }else{
                    sendComment();
                }
            }
        });

        final TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("COMENTARIOS Y SUGERENCIAS");
    }

    private void sendComment()
    {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("usuarios").document(fbUser.getUid());

        Map<String, Object> objSend = new HashMap<>();
        objSend.put("user", docRef);
        objSend.put("sugerencia", editText.getText().toString());
        objSend.put("fecha", new Date());

        // Add a new document with a generated ID

        db.collection("sugerencias").document()
                .set(objSend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void _void) {
                        CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(CommentsActivity.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                .setTitle("¡Tu comentario fue enviado!")
                                .setCornerRadius(20)
                                .setTextGravity(Gravity.CENTER)
                                .addButton("CONTINUAR", Color.parseColor("#FFFFFF"), getResources().getColor(R.color.blueApp), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });

                        pDialog.show();
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
