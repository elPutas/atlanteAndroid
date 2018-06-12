package com.sietecerouno.atlantetransportador.assets;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sietecerouno.atlantetransportador.manager.Manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by MAURICIO on 21/07/17.
 */

public class CallTpaga {

    public  ListenerTpaga listenerTpaga = null ;


    public void callPostWebServiceTpagaPrivate(String url, JSONObject params, final Context context){

        Log.i("HOLA  :: ", "send :::" +  url +" :: "+ params);

        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", Manager.getInstance().PRIVATE_BASIC_AUTH_DEV);

            StringEntity entity = new StringEntity(params.toString());

            client.post(context, url, entity, "application/json",  new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    listenerTpaga.dataPhpReturn(response);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.i("POST :: ", "Login failed : error code " + errorResponse);
                    String mensaje;
                    mensaje = "Lo siento ha ocurrido un error , intentalo de nuevo :(";

                    listenerTpaga.dataPhpError(mensaje);

                }
            });

        }catch (UnsupportedEncodingException e){

            String mensaje;
            mensaje = "Lo siento ha ocurrido un error , intentalo de nuevo :(";

            listenerTpaga.dataPhpError(mensaje);

        }


    }


    public void callPostWebServiceTpagaPublic(String url, JSONObject params, final Context context){

        Log.d("HOLA  :: ", "send :::" +  url +" :: "+ params);

        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", Manager.getInstance().PUBLIC_BASIC_AUTH_DEV);

            StringEntity entity = new StringEntity(params.toString());

            client.post(context, url, entity, "application/json",  new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    listenerTpaga.dataPhpReturn(response);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("POST :: ", "Login failed : error code " + statusCode);
                    String mensaje;
                    mensaje = "Lo siento ha ocurrido un error , intentalo de nuevo :(";

                    listenerTpaga.dataPhpError(mensaje);

                }
            });

        }catch (UnsupportedEncodingException e){

            String mensaje;
            mensaje = "Lo siento ha ocurrido un error , intentalo de nuevo :(";

            listenerTpaga.dataPhpError(mensaje);

        }
    }

}
