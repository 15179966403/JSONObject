package com.hrc.administrator.jsonobject;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button send = (Button) findViewById(R.id.btnSend);
        send.setOnClickListener(this);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private void sendRequestWithHttpClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("aa","I'm in Thread running");
                    URL url=new URL("http://192.168.1.103/mdzz/get_data.json");
                    HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5*1000);
                    connection.setInstanceFollowRedirects(true);
                    connection.connect();
                    if(connection.getResponseCode()==200){
                        InputStream is=connection.getInputStream();
                        ByteArrayOutputStream byteos=new ByteArrayOutputStream();
                        byte[] bytes=new byte[1024];
                        int len;
                        while((len=is.read(bytes))>0){
                            byteos.write(bytes,0,len);
                        }
                        String response=new String(byteos.toByteArray());
                        byteos.flush();
                        byteos.close();
                        is.close();
                        connection.disconnect();
                        parseJSONWithJSONObject(response);
                    }else{
                        Log.d("aa","ResponseCode!=200  "+connection.getResponseCode());
                        Log.d("aa","Response Message:"+connection.getResponseMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData){
        try {
            JSONArray jsonArray=new JSONArray(jsonData);
            if(jsonArray.length()<0){
                Log.d("aa","len<0");
                return;
            }
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String id=jsonObject.getString("id");
                String name=jsonObject.getString("name");
                String version=jsonObject.getString("version");
                Log.d("aa","id is "+id);
                Log.d("aa","name is "+name);
                Log.d("aa","version is "+version);
            }
            Toast.makeText(MainActivity.this,"Success", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        Log.d("aa","Click");
        Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        sendRequestWithHttpClient();
    }
}
