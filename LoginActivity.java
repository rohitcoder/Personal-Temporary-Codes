package com.infooby.edvoid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    DatabaseHelper mydb;
    Button btnSubmitLoginForm;
    EditText userid, pass;
    ProgressBar login_loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mydb = new DatabaseHelper(this);
        userid = (EditText)findViewById(R.id.user_id);
        pass = (EditText)findViewById(R.id.password);
        btnSubmitLoginForm = (Button)findViewById(R.id.submit);
        login_loader = (ProgressBar)findViewById(R.id.login_loader);
        btnSubmitLoginForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userid.getText().toString().trim().length() == 0){
                    userid.requestFocus();
                    Toast.makeText(LoginActivity.this,"Please Enter Username or Login ID", Toast.LENGTH_SHORT).show();
                }else if(pass.getText().toString().trim().length() == 0){
                    pass.requestFocus();
                    Toast.makeText(LoginActivity.this,"Please Enter Your Password.", Toast.LENGTH_SHORT).show();
                }else{
                    letslogin();
                }
            }
        });
    }
    private void letslogin(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) { }
        else {
            Toast.makeText(LoginActivity.this,"No Internet Available", Toast.LENGTH_SHORT).show();
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        login_loader.setVisibility(View.VISIBLE);
        StringRequest reqforLogin = new StringRequest(Request.Method.POST, DatabaseHelper.ROOT_API+"/?request=login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    String status = data.getString("status");
                    if(status.equals("417")){
                        login_loader.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        String msg = data.getString("msg");
                        Toast.makeText(LoginActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
                    }else if(status.equals("200")){
                        String auth_token = data.getString("auth_token");
                        String user_id = data.getString("user_id");
                        String school_id = data.getString("school_id");
                        String user_type = data.getString("user_type");
                        login_loader.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("edvoid_data", 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("login_session", auth_token);
                        editor.putString("user_id", user_id);
                        editor.putString("school_id", school_id);
                        editor.putString("user_type", user_type);
                        editor.commit();
                        String db_keys[] = {"login_session","user_id","school_id","user_type"};
                        String db_vals[] = {auth_token,user_id,school_id,user_type};
                        boolean isInserted = mydb.insertData(mydb.T_SETTINGS,db_keys,db_vals);
                        if (isInserted==true){
                            Intent i = new Intent(LoginActivity.this,DashboardActivity.class);
                            startActivity(i);
                        }else{
                            Toast.makeText(LoginActivity.this,"Something Wrong Happened Please Contact us",Toast.LENGTH_SHORT).show();;
                        }
                   }
                } catch (Exception e) {
                    login_loader.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volerror) {
                login_loader.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if(volerror.networkResponse==null) {
                    if (volerror.getClass().equals(TimeoutError.class)) {
                        Toast.makeText(LoginActivity.this, "Requeset Timed Out - Please Check your Network", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, volerror.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String,String>();
                String user_id,password;
                user_id = userid.getText().toString();
                password = pass.getText().toString();
                params.put("login_id",user_id);
                params.put("password",password);
                params.put("platform","2");
                return params;
            }
        };
        RequestQueue processdata = Volley.newRequestQueue(this);
        processdata.add(reqforLogin);
    }
 }
