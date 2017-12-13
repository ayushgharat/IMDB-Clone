package com.example.ayush.imdbdummyapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 12/8/2017.
 */

public class DataProcess extends AsyncTask<Void,Void,String> {

    private Context context;
    private String url;
    private ProgressDialog pDialog;
    private Datalistener dataListener;
    View view;

    //parameterized constructor
    public DataProcess(Context context, String url, Datalistener dataListener, View view) {
        this.context = context;
        this.url = url;
        this.view = view;
        this.dataListener = dataListener;
    }

    @Override
    protected void onPreExecute() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        //internetavailable = isOnline();
        //TODO: check  connection here also, app crashes
        //creating httpclient
        OkHttpClient okHttpClient = new OkHttpClient();
        //setting timeout time limit
        okHttpClient.setReadTimeout(120, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(120, TimeUnit.SECONDS);

        //Requesting url builder
        Request request = new Request.Builder().url(url).build();
        String responsedata = null;
        try {
            //response of the httpclient
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                //setting response data if response is successful
                responsedata = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //returning response data
        return responsedata;
    }


    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        if (pDialog.isShowing())
            pDialog.dismiss();
        //calling update list method
        dataListener.updatelist(aVoid);

    }
}