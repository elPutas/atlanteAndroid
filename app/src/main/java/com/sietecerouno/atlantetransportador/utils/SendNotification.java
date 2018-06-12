package com.sietecerouno.atlantetransportador.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by mauriciovaldes on 17/01/18.
 */

public class SendNotification extends AsyncTask<String, Void, String> {

    public String _id;
    public String _title;
    public String _body;
    public String _icon;

    @Override
    protected String doInBackground(String... params)
    {
        try
        {
            return sendData();
        }
        catch (IOException e)
        {
            return "bad request token";
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        Log.i("GIO", "result: "+result);

    }

    private String sendData() throws IOException
    {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    " +
                "\"notification\": {\n   " +
                "\t\"title\": \""+_title+"\",\n   " +
                "\t\"body\": \""+_body+"\",\n " +
                "\t\"sound\": \"default\",\n " +
                "\t\"click_action\": \"\"\n\t},\n " +
                "\"to\": \""+_id+"\"\n}\n\n\t");

        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .addHeader("cache-control", "no-cache")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key=AAAAazmm74U:APA91bHUL_3QTxFp4kRV_vMAN6jNbiB2WXp6NQWurF5NeJWBr8q6P1eIrndaGcQbFWVMBsJxC0bXpzoozy3Tyf1zpBxBd7InPH47ZM8pfiIOql-kJD2WGHhUTIWoHjHDxwYJtOawvNuI")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return (response.body().string());

    }
}