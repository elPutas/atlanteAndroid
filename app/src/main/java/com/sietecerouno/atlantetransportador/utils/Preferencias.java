package com.sietecerouno.atlantetransportador.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by Gio on 1/12/17.
 */

public class Preferencias
{
    public static final String empleado = "pref_empleado";
    public static final String NIT = "pref_NIT";
    public static final String usuario = "Pref_usuario";
    public static final String  idUsuario="00";

    public final SharedPreferences misPreferencias;
    public boolean estaLogueado = false;

    private static Preferencias INSTANCIA;


    public static Preferencias get(Context context) {
        if (INSTANCIA == null) {
            INSTANCIA = new Preferencias(context);
        }
        return INSTANCIA;
    }
    public    Preferencias(Context context) {
        misPreferencias = context.getSharedPreferences(empleado, Context.MODE_PRIVATE);
        estaLogueado = !TextUtils.isEmpty(misPreferencias.getString(NIT, null));
    }


    public boolean isLogueado(){
        return  estaLogueado;
    }
}
