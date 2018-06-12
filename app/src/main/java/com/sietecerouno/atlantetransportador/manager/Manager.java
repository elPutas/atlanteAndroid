package com.sietecerouno.atlantetransportador.manager;


import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by gio on 14/9/16.
 */
public class Manager
{

    public static String myID = "";
    public static String idToTrack = "";
    public static String vehicleSelected = "";
    public static int vehicleSelectedType = 0;
    //public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static String user_email = "";
    public static String user_photo = "";
    public static String user_name = "";
    public static String user_last_name = "";
    public static String user_cel = "";

    public static String goto_profile = "";
    public static String photoTemp = "https://firebasestorage.googleapis.com/v0/b/atlante-d7f0b.appspot.com/o/171107101501111.jpeg?alt=media&token=5d1a320c-27a2-4bcf-bccd-ba8e61a78935";

    public static String photoTemp_v_user_1 = "";

    public static String photoTemp_v_cc_1 = "";
    public static String photoTemp_v_cc_2 = "";

    public static String photoTemp_v_id_1 = "";
    public static String photoTemp_v_id_2 = "";

    public static String photoTemp_v_rut_1 = "";

    public static String photoTemp_v_tp_1 = "";
    public static String photoTemp_v_tp_2 = "";

    public static String photoTemp_v_soat_1 = "";

    public static String photoTemp_v_tec_1 = "";

    public static String photoTemp_v_img_1 = "";
    public static String photoTemp_v_img_2 = "";
    public static String photoTemp_v_img_3 = "";
    public static String photoTemp_v_img_4 = "";

    public static String id_client_temp = "";

    public static Context actualContext = null;

    public static String actualService = "";
    public static Boolean isBlock = false;

    public static String actualTokenPush = "";
    public static String actualId_to_charge = "";
    public static String actualId_to_chat = "";
    public static String actualClientId_inmediato = "";
    public static String actualClientCel_inmediato = "";
    public static String actualClientToken_inmediato = "";
    public static String actualClientToken = "";
    public static String actualClientMail_inmediato = "";
    public static String actualClientId_finish = "";
    public static String idPersonalizado = "";
    public static String idProgramadoDoc = "";
    public static String idProgramado = "";
    public static String idPersonalizadoDoc = "";


    public static Boolean transporterActive = false;



    public static SimpleDateFormat dateFormatter  = new SimpleDateFormat("dd/MM/yyyy");

    public static int eu_remove_temp = 0;
    public static int eu_total_temp = 0;

    public static ArrayList arrPhotoThumb = new ArrayList(){};

    public static Boolean isFirstV = true;
    public static Boolean isFinishInmediato = false;

    public static int idTempVSelected;

    public String URL_TPAGA_DEV =  "https://sandbox.tpaga.co/api";
    public String PRIVATE_BASIC_AUTH_DEV =  "Basic dThsdmVnc2dhbGJtcHRuNjcwb3NubzZhaTJwY2g2OGE6";
    public String PUBLIC_BASIC_AUTH_DEV =  "Basic ZnA2cmwzbmxkcnQzcGRlbGozZzV0ZmI0Y2dycGswNjg6";


    public String URL_TPAGA_PROD = "https://api.tpaga.co/api";
    public String PRIVATE_BASIC_AUTH_PROD =  "";
    public String PUBLIC_BASIC_AUTH_PROD =  "";

    private static Manager   _instance;
    public synchronized static Manager getInstance()
    {
        if (_instance == null)
        {
            _instance = new Manager();
        }
        return _instance;
    }
}
