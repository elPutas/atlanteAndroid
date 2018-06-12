package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity
{
    private String TAG = "GIO";
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();


        TextView register_btn = (TextView) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        TextView login_btn = (TextView) findViewById(R.id.login_btn);
        TextView forget_btn = (TextView) findViewById(R.id.forget_btn);
        forget_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgetPassActivity.class);
                startActivity(i);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mail_txt = (EditText) findViewById(R.id.mail_txt);
                EditText pass_txt = (EditText) findViewById(R.id.pass_txt);

                if(mail_txt.getText().length() > 0 && mail_txt.getText().length() > 0)
                {
                    String email = mail_txt.getText().toString();
                    String password = pass_txt.getText().toString();

                    logMe(email, password);
                }
            }

        });
    }
    private void logMe(final String _user, String _pass)
    {
        mAuth.signInWithEmailAndPassword(_user, _pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        //Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if(task.isSuccessful())
                        {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            Manager.getInstance().user_email = _user;
                            Manager.getInstance().myID = user.getUid();

                            // set USER info from db user
                            DocumentReference docRefClient = db.collection("usuarios").document(Manager.getInstance().myID);
                            docRefClient.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null)
                                        {
                                            Log.i(TAG, document.getData().get("tipo").toString());

                                            if(Objects.equals(document.getData().get("tipo").toString() , "cliente"))
                                            {
                                                FirebaseAuth.getInstance().signOut();
                                                CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(LoginActivity.this)
                                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                        .setCornerRadius(20)
                                                        .setTextGravity(Gravity.CENTER)
                                                        .setMessage("Este usuario no es un conductor")
                                                        .setTitle("Error")
                                                        .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();

                                                            }
                                                        });

                                                pDialog.show();
                                            }else{
                                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(i);
                                            }

                                        } else {
                                            FirebaseAuth.getInstance().signOut();
                                            CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(LoginActivity.this)
                                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                    .setCornerRadius(20)
                                                    .setTextGravity(Gravity.CENTER)
                                                    .setMessage("El usuario no existe")
                                                    .setTitle("Error")
                                                    .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();

                                                        }
                                                    });

                                            pDialog.show();
                                            Log.i(TAG, "No such document inside");
                                        }
                                    } else {
                                        Log.i(TAG, "get failed with inside", task.getException());

                                    }
                                }
                            });



                        }else{
                            if(task.getException().getMessage() == "The password is invalid or the user does not have a password.")
                            {
                                CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(LoginActivity.this)
                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                        .setCornerRadius(20)
                                        .setTextGravity(Gravity.CENTER)
                                        .setMessage("Parece que no escribiste correctamente tu contraseña")
                                        .setTitle("Confirma tus datos")
                                        .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        });

                                pDialog.show();
                            }
                            else if(task.getException().getMessage() == "There is no user record corresponding to this identifier. The user may have been deleted.")
                            {
                                CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(LoginActivity.this)
                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                        .setCornerRadius(20)
                                        .setTextGravity(Gravity.CENTER)
                                        .setMessage("Parece que este usuario fue eliminado o no existe")
                                        .setTitle("Confirma tus datos")
                                        .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        });

                                pDialog.show();
                            }

                            else
                            {
                                CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(LoginActivity.this)
                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                        .setCornerRadius(20)
                                        .setTextGravity(Gravity.CENTER)
                                        .setMessage("Parece que este usuario no existe")
                                        .setTitle("Confirma tus datos")
                                        .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        });

                                pDialog.show();
                            }
                        }
                    }
                });

    }
}
