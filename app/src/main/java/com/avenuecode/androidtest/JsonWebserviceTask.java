package com.avenuecode.androidtest;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by Wally7 on 17-04-2015.
 */
public abstract class JsonWebserviceTask extends AsyncTask<Void, Void, JSONObject> implements JsonWebserviceInterface{

    ProgressDialog dialog;
    String url;
    Context context;

    // Constructor

    JsonWebserviceTask(Context context, String url){
        this.context = context;
        this.url = url;
    }


    // AsyncTasks default methods

    @Override
    protected void onPreExecute(){
        super.onPreExecute();

        // Create ProgressDialog

        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading ...");
        dialog.setCancelable(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
    }

    @Override
    protected JSONObject doInBackground(Void... params){

        JSONObject json = null;

        try{
            // Connect to url location

            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);

            // Get content from response, convert to stream and pass to JsonObject

            InputStream input = response.getEntity().getContent();
            if (input != null)
                json = new JSONObject(readInput(input));

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("There was an error at connection");
        }

        return json;
    }

    @Override
    protected void onPostExecute(JSONObject result){

        delegate(result);

        // Cancel ProgressDialog
        if(dialog.isShowing())
            dialog.cancel();
    }


    // Read Inputstream and pass it as string

    public String readInput(InputStream input){

        String result = "";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            StringBuilder sb = new StringBuilder();
            String line = "";

            // Append every buffer line to string builder

            while((line = br.readLine()) != null){
                sb.append(line);
            }

            // Converts string builder to string
            result = sb.toString();


        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    // Delegate method

    public abstract void delegate(JSONObject jsonObject);

}
