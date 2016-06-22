package com.gpslocation.friendzone.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.gcm.GcmIntentService;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by mwathi on 5/19/2016.
 */
public class HangoutLogin extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangout_login_layout);

        inputEmail = (EditText) findViewById(R.id.mail);
        inputPassword = (EditText) findViewById(R.id.passi);
        btnLogin = (Button) findViewById(R.id.login);
        btnLinkToRegister = (Button) findViewById(R.id.register);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form

                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        HangoutRegister.class);
                startActivity(i);
                finish();
            }
        });


    }



    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        session=new SessionManager(this);
        String tag_string_req = "req_login";

        db=new SQLiteHandler(this);

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_HANGOUTLOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();


                try {


                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error && jObj!=null) {
                        // user successfully logged in
                        // Create login session
                       if(session.isLoggedIn()){

                           Toast.makeText(getApplicationContext(),"logout from the main account to proceed",Toast.LENGTH_LONG).show();
                       }
                        else{


                           String business_name=jObj.getString("business_name");
                           String myEmail=jObj.getString("email");
                           int id=jObj.getInt("id");
                           String details=jObj.getString("details");

                           session.setHangout(true);
                           session.setID(id);


                           Intent hangoutpage=new Intent(getApplicationContext(),HangoutPage.class);
                           startActivity(hangoutpage);
                       }

                        // Now store the user in SQLite
                        //String uid = jObj.getString("uid");

                       // Toast.makeText(getApplication(),name1,Toast.LENGTH_LONG).show();


                        // Inserting row in users table
                       // db.addUser(name1,name2,email,uid,created_at);


                        //Toast.makeText(getApplicationContext(),fname, Toast.LENGTH_SHORT).show();
                        // Launch main activity







                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
