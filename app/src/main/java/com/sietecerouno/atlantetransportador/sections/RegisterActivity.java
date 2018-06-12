package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.manager.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity
{

    private String TAG = "GIO";
    private String userID;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    String cities[];
    String mail;
    String pass;
    String c_pass;
    String name;
    String lastname;
    String cell;
    String city;
    Boolean isAccept = false;

    CheckBox acceptTyC;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Manager.getInstance().isFirstV = true;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueApp)));

        TextView title = (TextView) findViewById(R.id.action_bar_title);
        title.setText("REGISTRO");

        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);

        //bg
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Glide.with(bg.getContext()).load(R.drawable.bg_solo).into(bg);

        final EditText mail_txt = (EditText) findViewById(R.id.mail_txt);
        final EditText pass_txt = (EditText) findViewById(R.id.pass_txt);
        final EditText confirm_pass_txt = (EditText) findViewById(R.id.confirm_pass_txt);
        final EditText name_txt = (EditText) findViewById(R.id.name_txt);
        final EditText lastname_txt = (EditText) findViewById(R.id.lastname_txt);
        final EditText cell_txt = (EditText) findViewById(R.id.cell_txt);
        final TextView city_txt = (TextView) findViewById(R.id.city_txt);
        final TextView back = (TextView) findViewById(R.id.back);
        final TextView tyc = (TextView) findViewById(R.id.readTyC);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, PoliciesActivity.class);
                startActivity(i);
            }
        });

        city_txt.setClickable(false);
        mail_txt.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        pass_txt.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        name_txt.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        lastname_txt.setHintTextColor(getResources().getColor(android.R.color.darker_gray));

        acceptTyC = (CheckBox) findViewById(R.id.acceptTyC);
        acceptTyC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isAccept = b;
            }
        });


        // set CITIES info from db user
        db.collection("ciudades").addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e)
            {
                cities = new String[documentSnapshots.size()];
                int num=0;
                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments())
                {
                    cities[num] = documentSnapshot.getData().get("ciudad").toString();
                    num++;
                }

                city_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(RegisterActivity.this);
                        builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
                        builder.setTitle("Selecciona un ciudad de origen");
                        builder.setItems(cities, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int index)
                            {
                                city_txt.setText(cities[index]);
                                city = city_txt.getText().toString();
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                });

            }
        });

        TextView register_btn = (TextView) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create Alert using Builder
                CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(RegisterActivity.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                //pDialog.setTitle("Espera");
                pDialog.setCornerRadius(20);
                pDialog.setTextGravity(Gravity.CENTER);
                /*
                SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.WARNING_TYPE);
                pDialog.getProgressHelper().setBarColor(R.color.blueApp);
                pDialog.setCancelable(false);
                */

                mail = mail_txt.getText().toString();
                pass = pass_txt.getText().toString();
                c_pass = confirm_pass_txt.getText().toString();
                name = name_txt.getText().toString();
                lastname = lastname_txt.getText().toString();
                cell = cell_txt.getText().toString();



                if(isEmailValid(mail))
                {
                    if(!Objects.equals(name, ""))
                    {
                        if(!Objects.equals(lastname, ""))
                        {
                            if(!Objects.equals(cell, "") && cell.length()==10)
                            {
                                if(!Objects.equals(pass, "")&& pass.length()>6)
                                {
                                    if(Objects.equals(c_pass, pass))
                                    {
                                        if(!Objects.equals(city, ""))
                                        {
                                            if(isAccept)
                                            {
                                                preRegister(mail, pass);
                                            }else{
                                                pDialog.setTitle("Acepta los términos y condiciones");
                                                pDialog.show();
                                            }

                                        }else{
                                            pDialog.setTitle("Falta tu ciudad");
                                            pDialog.show();
                                        }

                                    }else{
                                        pDialog.setTitle("Las contraseñas no coinciden");
                                        pDialog.show();
                                    }

                                }
                                else{
                                    pDialog.setTitle("Falta tu contraseña");
                                    pDialog.show();
                                }

                            }
                            else{
                                pDialog.setTitle("Falta tu celular");
                                pDialog.show();
                            }

                        }else{
                            pDialog.setTitle("Falta tu apellido:");
                            pDialog.show();
                        }

                    }
                    else{
                        pDialog.setTitle("Falta tu nombre");
                        pDialog.show();
                    }

                }else{
                    pDialog.setTitle("Escribe un formato válido de e-mail");
                    pDialog.show();
                }
            }
        });


    }

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    private void registerUser(String _name, String _lastName, String _cel, String _city )
    {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", _name);
        user.put("apellido", _lastName);
        user.put("celular", _cel);
        user.put("ciudad", _city);
        user.put("correo", mail);
        user.put("calificacion", 5);
        user.put("foto", "https://firebasestorage.googleapis.com/v0/b/atlante-d7f0b.appspot.com/o/user.jpg?alt=media&token=51a20419-f334-4432-9ebd-8f2b3d5de74c");
        user.put("estado", "activo");
        user.put("tipo", "transportista");

        // Access a Cloud Firestore instance from your Activity



        // Add a new document with a generated ID

        db.collection("usuarios").document(userID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void _void) {
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + _void.getId());


                        CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(RegisterActivity.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                .setTitle("¡Completaste tu registro exitosamente!")
                                .setCornerRadius(20)
                                .setTextGravity(Gravity.CENTER)
                                .setMessage("Vamos a confirmar la información y en los próximos días te enviaremos tu código de activaciòn.")
                                .addButton("CONTINUAR", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent i = new Intent(RegisterActivity.this, ChooseVActivity.class);
                                        startActivity(i);
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

    private void preRegister(String _email,String _pass)
    {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(_email, _pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        Log.i(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            // Name, email address, and profile photo Url


                            //name = user.getDisplayName().toString();
                            //mail = user.getEmail().toString();
                            //Uri photoUrl = user.getPhotoUrl();

                            // Check if user's email is verified
                            //boolean emailVerified = user.isEmailVerified();

                            // The user's ID, unique to the Firebase project. Do NOT use this value to
                            // authenticate with your backend server, if you have one. Use
                            // FirebaseUser.getToken() instead.
                            userID = user.getUid().toString();
                            Manager.getInstance().myID = userID;

                            registerUser(name, lastname, cell, city);
                        }else{
                            CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(RegisterActivity.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                    .setTitle("Algo salio  mal")
                                    .setCornerRadius(20)
                                    .setTextGravity(Gravity.CENTER)
                                    .setMessage("Comprueba que la contraseña tenga más de 6 carácteres y que no sea un e-mail ya registrado")
                                    .addButton("CONTINUAR", Color.parseColor("#FFFFFF"), Color.parseColor("#00518e"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
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
