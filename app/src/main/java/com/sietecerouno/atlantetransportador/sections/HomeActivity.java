package com.sietecerouno.atlantetransportador.sections;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sietecerouno.atlantetransportador.MainActivity;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.adapters.AdapterMenu;
import com.sietecerouno.atlantetransportador.adapters.DrawerListAdapter;
import com.sietecerouno.atlantetransportador.fragments.InmediatoFragment;
import com.sietecerouno.atlantetransportador.fragments.PersonalizadoFragment;
import com.sietecerouno.atlantetransportador.fragments.ProfileFragment;
import com.sietecerouno.atlantetransportador.fragments.ProgramadoFragment;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.profile.CalificacionFragment;
import com.sietecerouno.atlantetransportador.profile.DatosFragment;
import com.sietecerouno.atlantetransportador.profile.GananciasFragment;
import com.sietecerouno.atlantetransportador.profile.VehiculoFragment;
import com.sietecerouno.atlantetransportador.utils.CustomObj;
import com.sietecerouno.atlantetransportador.utils.DrawerItem;
import com.sietecerouno.atlantetransportador.utils.MyViewPager;
import com.sietecerouno.atlantetransportador.utils.ViewPagerAdaptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class HomeActivity extends AppCompatActivity implements ProfileFragment.OnHeadlineSelectedListener, InmediatoFragment.OnChangeIconListener
{

    AdapterMenu adapterMenu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navMenuTitles;

    Toolbar             toolbar;
    TabLayout           tabLayout;
    MyViewPager         viewPager;
    ViewPagerAdaptor    viewPagerAdaptor;
    DrawerLayout        drawerLayout;

    TextView title;
    String              currentPage;
    String              TAG = "GIO";

    FirebaseFirestore db;
    FirebaseUser user;

    //profile fragmet's
    GananciasFragment gananciasFragment = new GananciasFragment();
    DatosFragment datosFragment = new DatosFragment();
    VehiculoFragment vehiculoFragment = new VehiculoFragment();
    CalificacionFragment calificacionFragment = new CalificacionFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    int[] btnsSelected = new int[4];
    int[] btnsUnselected = new int[4];

    @Override
    protected void onResume() {
        super.onResume();
        Manager.getInstance().actualContext = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();



        setupNavigationView();
        addSlindingMenu();

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //bg
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Glide.with(bg.getContext()).load(R.drawable.bg_solo).into(bg);


        title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        title.setText("SERVICIO INMEDIATO");

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerVisible(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (MyViewPager) findViewById(R.id.viewPager);
        viewPager.setPagingEnabled(true);

        //setupTabsIcon();



        viewPagerAdaptor = new ViewPagerAdaptor(getSupportFragmentManager(), getBaseContext());

        viewPagerAdaptor.addFragmentes(new InmediatoFragment(), "000000", R.drawable.check);
        viewPagerAdaptor.addFragmentes(new PersonalizadoFragment(), "000000", R.drawable.check);
        viewPagerAdaptor.addFragmentes(new ProgramadoFragment(), "000000", R.drawable.check);
        viewPagerAdaptor.addFragmentes(new ProfileFragment(), "000000", R.drawable.check);

        viewPager.setAdapter(viewPagerAdaptor);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00518e"));


        int[] icons = new int[]{R.drawable.tab_inmediato, R.drawable.tab_personalizado, R.drawable.tab_programado, R.drawable.tab_perfil};
        String[] titltes = new String[]{"inmediato", "personalizado", "programado", "perfil"};


        for (int i = 0; i < icons.length; i++)
        {
            //tabLayout.getTabAt(i).setIcon(icons[i]);
            tabLayout.getTabAt(i).setText(titltes[i]);


            View view = getLayoutInflater().inflate(R.layout.customtab, null);
            view.findViewById(R.id.icon).setBackgroundResource(icons[i]);
            tabLayout.getTabAt(i).setCustomView(view);
        }


        //get extra
        Intent myIntent = getIntent(); // gets the previously created intent
        String tabKey = myIntent.getStringExtra("tab");
        if(tabKey!=null)
        {
            if(Objects.equals(tabKey, "datos"))
            {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                TabLayout.Tab tab = tabLayout.getTabAt(3);
                                tab.select();
                                title.setText("VEHÍCULOS");
                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                changeFr("vehiculo");
                                            }
                                        },
                                        500);
                            }
                        },
                        100);

            }
        }
    }


    // tabs are allowed from inmediatoFragment class
    private void addTabListenerAllow()
    {
        tabLayout.clearOnTabSelectedListeners();

        tabLayout.setupWithViewPager(viewPager);
        int[] icons = new int[]{R.drawable.tab_inmediato, R.drawable.tab_personalizado, R.drawable.tab_programado, R.drawable.tab_perfil};
        String[] titltes = new String[]{"inmediato", "personalizado", "programado", "perfil"};

        if(Manager.getInstance().transporterActive)
            icons[0] = R.drawable.tab_inmediato_ok;

        for (int i = 0; i < icons.length; i++)
        {
            //tabLayout.getTabAt(i).setIcon(icons[i]);
            tabLayout.getTabAt(i).setText(titltes[i]);


            View view = getLayoutInflater().inflate(R.layout.customtab, null);
            view.findViewById(R.id.icon).setBackgroundResource(icons[i]);
            tabLayout.getTabAt(i).setCustomView(view);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {

                if(!Manager.getInstance().isBlock)
                {
                    currentPage = tab.getText().toString();

                    TextView title = (TextView) findViewById(R.id.action_bar_title);

                    if (Objects.equals(currentPage, "perfil")) {
                        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blueApp));
                        title.setText("PERFIL");
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueApp)));
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, profileFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    if (Objects.equals(currentPage, "inmediato")) {
                        title.setText("SERVICIO INMEDIATO");
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blueApp)));
                        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blueApp));
                    }
                    if (Objects.equals(currentPage, "personalizado")) {
                        title.setText("SERVICIO PERSONALIZADO");
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.greenApp)));
                        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.greenApp));
                    }
                    if (Objects.equals(currentPage, "programado")) {
                        title.setText("SERVICIO PROGRAMADO");
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.yellowApp)));
                        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.yellowApp));
                    }



                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    // tabs are allowed from inmediatoFragment class
    private void addTabListenerNotAllow()
    {
        final Boolean[] _jO = {true};
        tabLayout.clearOnTabSelectedListeners();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(_jO[0])
                {
                    _jO[0] = false;

                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(HomeActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setCornerRadius(20)
                            .setTextGravity(Gravity.CENTER)
                            .setTitle("Espera")
                            .setMessage("Primero termina el servicio")
                            .addButton("Ok", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                                    tab.select();

                                    _jO[0] = true;
                                }
                            });

                    pDialog.show();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private void setupNavigationView()
    {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_container);

    }


    public void changeFr(String _goto)
    {
        ConstraintLayout fr = (ConstraintLayout) findViewById(R.id.fragmentProfile);
        fr.setVisibility(View.INVISIBLE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
        switch (_goto)
        {
            case "ganancias":
                title.setText("GANANCIAS");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                transaction.replace(R.id.container, gananciasFragment);
                break;

            case "datos":
                title.setText("DATOS PERSONALES");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                transaction.replace(R.id.container, datosFragment);
                break;

            case "vehiculo":
                title.setText("VEHÍCULO");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                transaction.replace(R.id.container, vehiculoFragment);
                break;

            case "calificacion":
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                title.setText("CALIFICACIÓN");
                transaction.replace(R.id.container, calificacionFragment);
                break;
        }

        transaction.addToBackStack(null);
        transaction.commit();
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
                if(Objects.equals(currentPage, "perfil"))
                {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    title.setText("PERFIL");
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
                    transaction.replace(R.id.container, profileFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {

        if(Objects.equals(currentPage, "perfil"))
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right);
            transaction.replace(R.id.container, profileFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }



    @Override
    public void changeIcon(Boolean _is)
    {
        View view = getLayoutInflater().inflate(R.layout.customtab, null);
        tabLayout.getTabAt(0).setCustomView(null);
        if(_is)
            view.findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_inmediato_ok);
        else
            view.findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_inmediato);


        tabLayout.getTabAt(0).setCustomView(view);

    }

    @Override
    public void isBlock(Boolean _is) {
        if(_is)
            addTabListenerNotAllow();
        else
            addTabListenerAllow();
    }

    private void addSlindingMenu()
    {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_container);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        TextView btnLogout = (TextView) findViewById(R.id.btnLogout);
        ImageView photo = (ImageView) findViewById(R.id.photo);
        TextView name = (TextView) findViewById(R.id.name);
        TextView mail = (TextView) findViewById(R.id.mail_txt);
        RelativeLayout drawerll = (RelativeLayout) findViewById(R.id.drawerll);

        drawerll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        String completeName = Manager.getInstance().user_name + " " + Manager.getInstance().user_last_name;
        name.setText(completeName);

        mail.setText(Manager.getInstance().user_email);

        CropCircleTransformation cropCircleTransformation = new CropCircleTransformation();
        Glide.with(photo.getContext())
                .load(Manager.getInstance().user_photo)
                .apply(RequestOptions.bitmapTransform(cropCircleTransformation))
                .into(photo);


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(HomeActivity.this)
                        .setTitle(" ¿Estas seguro que deseas cerrar sesión? ")
                        .setTextGravity(Gravity.CENTER)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                        .addButton("ACEPTAR", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                //logout
                                db.collection("usuarios")
                                        .document(Manager.getInstance().myID)
                                        .update("token", "").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i(TAG, "delete token!");

                                        FirebaseAuth.getInstance().signOut();
                                        Intent i = new Intent(HomeActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });


                            }
                        })
                        .addButton("CANCELAR", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                pDialog.show();



            }
        });


        ArrayList<CustomObj> titlesMenu = new ArrayList<CustomObj>();
        titlesMenu.add(new CustomObj("Perfil","","",false, "" ,R.drawable.mini_ico_perfil,0,0));
        titlesMenu.add(new CustomObj("Vehículos","","",false, "" ,R.drawable.mini_ico_vehi,0,0));
        titlesMenu.add(new CustomObj("Envíos en curso","","",false, "" ,R.drawable.mini_ico_current,0,0));
        titlesMenu.add(new CustomObj("Historial","","",false, "" ,R.drawable.mini_ico_history,0,0));
        titlesMenu.add(new CustomObj("Ganancias","","",false, "" ,R.drawable.mini_ico_ganancias,0,0));
        titlesMenu.add(new CustomObj("Calificación","","",false, "" ,R.drawable.mini_ico_calificacion,0,0));
        titlesMenu.add(new CustomObj("Para ti","","",false, "" ,R.drawable.mini_ico_parati,0,0));
        titlesMenu.add(new CustomObj("¿Necesitas Ayuda?","","",false, "" ,R.drawable.mini_ico_ayuda,0,0));

        adapterMenu = new AdapterMenu(this, titlesMenu);
        mDrawerList.setAdapter(adapterMenu);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Intent intentProductos = new Intent(activity, Productos.class);

                if(!Manager.getInstance().isBlock)
                {
                    switch (position) {

                        case 0:
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            TabLayout.Tab tab = tabLayout.getTabAt(3);
                                            tab.select();
                                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                                            new android.os.Handler().postDelayed(
                                                    new Runnable() {
                                                        public void run() {
                                                            changeFr("datos");
                                                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                                                        }
                                                    },
                                                    500);
                                        }
                                    },
                                    100);


                            break;
                        case 1:
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            TabLayout.Tab tab = tabLayout.getTabAt(3);
                                            tab.select();
                                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                            new android.os.Handler().postDelayed(
                                                    new Runnable() {
                                                        public void run() {
                                                            changeFr("vehiculo");
                                                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                                                        }
                                                    },
                                                    500);
                                        }
                                    },
                                    100);
                            break;
                        case 2:
                            Intent i = new Intent(HomeActivity.this, CurrentPedidosActivity.class);
                            startActivity(i);
                            break;
                        case 3:
                            Intent j = new Intent(HomeActivity.this, HistoryActivity.class);
                            startActivity(j);
                            break;

                        case 4:
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            TabLayout.Tab tab = tabLayout.getTabAt(3);
                                            tab.select();
                                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                            new android.os.Handler().postDelayed(
                                                    new Runnable() {
                                                        public void run() {
                                                            changeFr("ganancias");
                                                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                                                        }
                                                    },
                                                    500);
                                        }
                                    },
                                    100);
                            break;
                        case 5:
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            TabLayout.Tab tab = tabLayout.getTabAt(3);
                                            tab.select();
                                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                            new android.os.Handler().postDelayed(
                                                    new Runnable() {
                                                        public void run() {
                                                            changeFr("calificacion");
                                                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                                                        }
                                                    },
                                                    500);
                                        }
                                    },
                                    100);
                            break;
                        case 6:
                            Intent h = new Intent(HomeActivity.this, ParatiActivity.class);
                            startActivity(h);
                            break;
                        case 7:
                            Intent k = new Intent(HomeActivity.this, HelpActivity.class);
                            startActivity(k);
                            break;
                        default:
                            break;


                    }
                }else{
                    CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(HomeActivity.this)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setCornerRadius(20)
                            .setTextGravity(Gravity.CENTER)
                            .setTitle("Espera")
                            .setMessage("Primero termina el servicio")
                            .addButton("Ok", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
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
