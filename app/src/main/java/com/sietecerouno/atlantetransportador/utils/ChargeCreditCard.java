package com.sietecerouno.atlantetransportador.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.sietecerouno.atlantetransportador.assets.CallTpaga;
import com.sietecerouno.atlantetransportador.assets.ListenerTpaga;
import com.sietecerouno.atlantetransportador.manager.Manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gio on 24/01/18.
 */

public class ChargeCreditCard implements ListenerTpaga {
    String TAG = "GIO";
    String tokenFinalTc;
    Context context;
    CallTpaga callTpaga = new CallTpaga();
    String typeCall = "";

    FirebaseFirestore db;

    public void ChargeCreditCard()
    {




    }


    public void charge(final Integer _amount, final String _idPay, final Integer cuotas)
    {

        db = FirebaseFirestore.getInstance();
        callTpaga.listenerTpaga = this;


        DocumentReference docRefPay = db.collection("mediosPago").document(_idPay);

        docRefPay.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {

                if(documentSnapshot.exists())
                {

                    Log.i(TAG, documentSnapshot.getData().get("ultimosCuatro").toString());
                    tokenFinalTc = documentSnapshot.getData().get("tokenFinalTc").toString();

                    Log.i(TAG, "_idPay: "+_idPay);
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date now = new Date();
                    String strDate = sdfDate.format(now);

                    Integer amount = _amount; // VALOR POR EL CUAL SE HACE LA COMPRA
                    String creditCard = tokenFinalTc; // TOKEN FINAL DE TC
                    String currency = "COP" ; // TIPO DE MONEDA
                    String description = "Compra AtlanteApp_Android"; // DESCRIPCION COMPRA
                    Integer installments = cuotas; // NUMERO DE CUOTAS
                    String orderId = Manager.getInstance().myID.substring(0, 5) + strDate;; // SON LOS 5 PRIMEROS DIGITOS DEL ID DEL USUARIO EN FIREBASE + YYYY + MM + DD + HH + mm + ss , DEL MOMENTO EN QUE SE HACE LA ORDEN, EL ORDER ID NO PUDE SUPERAR LOS 20 CARACTERES
                    Integer taxAmount = 0; // COBRO DE IMPUESTOS - enviamos en 0
                    Integer tipAmount = 0; // PROPINAS (?) - enviamos en 0
                    typeCall = "charge";

                    chargeCreditCard(amount,creditCard,currency,description,installments,orderId,taxAmount,tipAmount );
                }
            }
        });
    }



    private void chargeCreditCard(Integer amount,String creditCard,String currency, String description, Integer installments, String orderId, Integer taxAmount, Integer tipAmount ){

        try {
            Log.i(TAG, "entro");

            JSONObject params = new JSONObject();
            params.put("amount", amount);
            params.put("creditCard", creditCard);
            params.put("currency", currency);
            params.put("description", description);
            params.put("installments", installments);
            params.put("orderId", orderId);
            params.put("taxAmount", taxAmount);
            params.put("tipAmount", tipAmount);


            callTpaga.callPostWebServiceTpagaPrivate(Manager.getInstance().URL_TPAGA_DEV + "/charge/credit_card", params, context);

        }catch (JSONException e) {

            Log.i(TAG, "A ocurrido un error, intentalo de nuevo :(");

        }

    }




    public void dataPhpReturn(JSONObject output){

        try {

            JSONObject dataObj = (JSONObject) output;
            Log.i(" ::::::: ", " :::::::::: " + dataObj);

            switch (typeCall)
            {

                case "charge":

                    JSONObject transactionInfo = dataObj.getJSONObject("transactionInfo");
                    String authorizationCode =  transactionInfo.getString("authorizationCode");

                    String tokenCobro = dataObj.getString("id");



                    Map<String, Object> data = new HashMap<>();

                    data.put("tokenCobro", tokenCobro);
                    data.put("authorizationCode", authorizationCode);
                    db.collection("pedidos").document(Manager.getInstance().actualId_to_charge).set(data, SetOptions.merge());

                    //Toast.makeText(context, "COBRO OK : token cobro" + tokenCobro + " :: authCode ::  " + authorizationCode, Toast.LENGTH_LONG).show();

                    // LOS DATOS QUE DEBES GUARDAR EN FIREBASE EN LA TABLA DE PEDIDOS (VER TABLA DE PEDIDOS) JUNTO CON LA DEMAS INFORMACION DEL PEDIDO SON authorizationCode, fecha Y tokenCobro
                    // PARA FUTUROS RECLAMOS, DEBES VERIFICAR TAMBIEN EL errorCode SI NO ES 00 ES QUE PASO ALGO Y HAY QUE INFORMAR EN UNA ALERTA QUE LA TRANSACCION NO PUDO SER REALIZADA
                    // TAMBIEN SE DEBE VERIFICAR EL TIEMPO DE RESPUESTA QUE NO SOBREPASE LOS 2 MINUTOS. ESO ES SRRRRRRRR..

                    break;

                default:
                    break;


            }



        }catch (JSONException e){

            Log.i(TAG, "A ocurrido un error, intentalo de nuevo :(");

        }
    }

    public void dataPhpError(String output){

        Log.i("ERROR", " :::::: " + output);

        /*
        CFAlertDialog.Builder pDialog = new CFAlertDialog.Builder(Manager.getInstance().actualContext)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setCornerRadius(20)
                .setTextGravity(Gravity.CENTER)
                .setTitle("¡Oops!")
                .setMessage("Ocurrió un error con la forma de pago del usuario.")
                .addButton("Aceptar", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });


        pDialog.show();
*/

        Map<String, Object> data = new HashMap<>();
        Map<String, Date> estado = new HashMap<>();
        estado.put("cancelado_tpaga", new Date());

        data.put("estado_actual", 5);
        data.put("estado", estado);

        SendNotification sendNotification = new SendNotification();
        sendNotification._body = "Tu método de pago no es válido";
        sendNotification._id = Manager.getInstance().actualTokenPush;
        sendNotification._title = "Error";
        sendNotification.execute();

        db.collection("pedidos").document(Manager.getInstance().actualId_to_charge).set(data, SetOptions.merge());

        if(Manager.getInstance().actualService == "personalizado")
        {
            Map<String, Object> dataPer = new HashMap<>();
            dataPer.put("estado_actual", 5);
            db.collection("pedidos_personalizados").document(Manager.getInstance().idPersonalizadoDoc).set(dataPer, SetOptions.merge());
        }
        if(Manager.getInstance().actualService == "programado")
        {
            Map<String, Object> dataPer = new HashMap<>();
            dataPer.put("estado_actual", 5);
            db.collection("pedidos_programados").document(Manager.getInstance().idProgramadoDoc).set(dataPer, SetOptions.merge());
        }
    }
}
