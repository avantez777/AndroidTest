package com.avenuecode.androidtest;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ListActivity {

    Button btnSearch;
    EditText edtSearch;
    ListView listSearch;
    Context context = this;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Edit Search

        edtSearch = (EditText) findViewById(R.id.edtSearch);

        // Button Search

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // Receive the user's text and encode to url format

                String url = "";
                String text = "";
                try {
                    text = edtSearch.getText().toString();
                    url = "http://maps.googleapis.com/maps/api/geocode/json?address="
                          + URLEncoder.encode(text, "UTF-8")
                          + "&sensor=false";
                }catch(Exception e){
                    e.printStackTrace();
                }

                // Sets task and loads json from url

                JsonWebserviceTask task = new JsonWebserviceTask(context,url) {
                    @Override
                    public void delegate(JSONObject jsonObject) {

                        // Parses jsonObject from AsyncTask
                        try{

                            JSONArray results = jsonObject.getJSONArray("results");
                            String status = jsonObject.getString("status");

                            // If its everything OK populate listview

                            if(status.equals("OK"))
                                populateList(results);

                        }catch(Exception e){
                            e.printStackTrace();
                        }


                    }
                };
                task.execute();

            }
        });
    }

    // Populate items of the listview

    private void populateList(JSONArray results){

        final ArrayList<String> locations = new ArrayList<String>();
        final ArrayList<String> coordinates = new ArrayList<String>();

        // Iterate through places inside results and add to lists

        try{
            for(int i=0;i<results.length();i++){

                JSONObject obj = results.getJSONObject(i);
                String address = obj.getString("formatted_address");
                String coords = obj.getJSONObject("geometry").getJSONObject("location").toString();

                locations.add(address);
                coordinates.add(coords);

            }
        }catch(Exception e){
            e.printStackTrace();
        }

        // Updates Listview

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                locations);
        listSearch = getListView();
        listSearch.setAdapter(adapter);
        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Create map load dialog

                dialog = new ProgressDialog(context);
                dialog.setTitle("Wait");
                dialog.setMessage("Loading map ...");
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();

                // Call another activity passing values

                Bundle bnd = new Bundle();
                bnd.putInt("position", position);
                bnd.putStringArrayList("locations", locations);
                bnd.putStringArrayList("coordinates", coordinates);

                Intent map = new Intent(context, MapActivity.class);
                map.putExtras(bnd);
                startActivityForResult(map, 0);
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(dialog.isShowing())
                dialog.cancel();
        }
    }
}
