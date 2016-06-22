package com.gpslocation.friendzone.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by mwathi on 5/19/2016.
 */
public class HangoutRegister extends Activity {
    private static final String TAG = HangoutRegister.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText hangout_name,hangout_email,hangout_detail;
    private EditText inputPassword;

    private EditText inputPasswordconfirm;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hangout_register_layout);

        hangout_name = (EditText) findViewById(R.id.hangout_name);
        hangout_email=(EditText)findViewById(R.id.hangout_email);
        hangout_detail = (EditText) findViewById(R.id.hangout_detail);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPasswordconfirm=(EditText)findViewById(R.id.confirm_password);
        btnRegister = (Button) findViewById(R.id.sign_up);
        btnLinkToLogin = (Button) findViewById(R.id.login_in);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String hangname = hangout_name.getText().toString().trim();
                String hangemail=hangout_email.getText().toString().trim();
                String hangdetail = hangout_detail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirmpassword = inputPasswordconfirm.getText().toString().trim();

                if (hangname.isEmpty()&& hangemail.isEmpty() && hangdetail.isEmpty() && password.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
                if(!password.equals(confirmpassword)){
                    Toast.makeText(getApplicationContext(),
                            "passwords don't match!", Toast.LENGTH_LONG)
                            .show();

                }
                if(!validateEmail(hangemail)){

                    Toast.makeText(getApplicationContext(),"The email is not valid.....try again",Toast.LENGTH_LONG).show();


                }else {

                    registerUser(hangname, hangemail, hangdetail, password);
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        HangoutLogin.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void registerUser(final String name,final String mail,final String detail,final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_HANGOUTREGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite





                        // if(nameOne==null || nameTwo==null ||email==null ||created_at==null ||uid==null){

                        // Toast.makeText(getApplicationContext(),"no varied inputs returned",Toast.LENGTH_LONG).show();
                        // }


                        // Inserting row in users table
                        //db.addUser(name1,name2,email, uid, created_at);

                       // Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        AlertDialog alertDialog = new AlertDialog.Builder(
                                HangoutRegister.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Register Success");

                        // Setting Dialog Message
                        alertDialog.setMessage(jObj.getString("message"));

                        // Setting Icon to Dialog
                        alertDialog.setIcon(R.drawable.tick);

                        // Setting OK Button
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                                Intent intent = new Intent(
                                        HangoutRegister.this,
                                        HangoutLogin.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                        alertDialog.show();
                        // Launch l
                        // ogin activity

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected java.util.Map<String,String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<String,String>();
                params.put("name",name);
                params.put("detail",detail);
                params.put("email",mail);
                params.put("password",password);

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

    private boolean validateEmail(String email) {


        if (email.isEmpty() || !isValidEmail(email)) {


            return false;
        } else {

        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
