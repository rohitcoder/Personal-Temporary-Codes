package com.infooby.edvoid;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

public class Splash_Activity extends AppCompatActivity {
    DatabaseHelper mydb;
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mydb = new DatabaseHelper(this);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                checkUpdate();
                boolean is_loggedin = mydb.is_loggedin(mydb.getAuthToken(getApplicationContext()));
                if (is_loggedin == true) {
                    Toast.makeText(Splash_Activity.this,"Something Wrong Happened Please Contact us",Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(Splash_Activity.this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Splash_Activity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
            }
        }, SPLASH_TIME_OUT);
    }
    private void checkUpdate(){
        StringRequest checkUpdates = new StringRequest(Request.Method.GET, DatabaseHelper.ROOT_API+"/?request=version_check&v="+BuildConfig.VERSION_NAME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    String status = data.getString("status");
                    if(status.equals("417")){
                        String msg = data.getString("msg");
                        String title = data.getString("title");
                        Toast.makeText(Splash_Activity.this, "Updates available.", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder popup =  new AlertDialog.Builder(Splash_Activity.this);
                        popup.setTitle(title);
                        popup.setMessage(msg);
                        popup .setCancelable(false);
                        popup.setNegativeButton("Update Now",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+BuildConfig.APPLICATION_ID));
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        popup.create();
                        popup.show();
                    }
                } catch (Exception e) {
                    Toast.makeText(Splash_Activity.this, "Updates Error-"+e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volerror) {
                Toast.makeText(Splash_Activity.this, volerror.toString() , Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String,String>();
                String version;
                version = BuildConfig.VERSION_NAME;
                params.put("v",version);
                return params;
            }
        };
        RequestQueue processdata = Volley.newRequestQueue(this);
        processdata.add(checkUpdates);

    }

}
