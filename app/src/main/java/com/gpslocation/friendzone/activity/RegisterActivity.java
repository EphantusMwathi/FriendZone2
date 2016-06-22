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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwathi on 2/11/2016.
 */
public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText firstname,inputPasswordconfirm;
    private EditText secondname;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private AppConfig appConfig=new AppConfig();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        firstname = (EditText) findViewById(R.id.f_name);
        secondname=(EditText)findViewById(R.id.s_name);
        inputEmail = (EditText) findViewById(R.id.email);
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

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String fname = firstname.getText().toString().trim();
                String sname=secondname.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirmpassword = inputPasswordconfirm.getText().toString().trim();

                if (fname.isEmpty()&& sname.isEmpty() && email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
                if(!password.equals(confirmpassword)){
                    Toast.makeText(getApplicationContext(),
                            "passwords don't match!", Toast.LENGTH_LONG)
                            .show();

                }
                if(!validateEmail(email)){

                    Toast.makeText(getApplicationContext(),"The email is not valid.....try again",Toast.LENGTH_LONG).show();


                }else {




                    registerUser(fname, sname, email, password);
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String fname,final String sname,final String email,final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        Toast.makeText(getApplicationContext(),
                fname, Toast.LENGTH_LONG)
                .show();

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

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


                        JSONObject user = jObj.getJSONObject("user");

                        String uid = jObj.getString("uid");
                        String nameOne = user.getString("fname");
                        String nameTwo=user.getString("sname");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");
                        String updated_at = user.getString("updated_at");


                       // if(nameOne==null || nameTwo==null ||email==null ||created_at==null ||uid==null){

                           // Toast.makeText(getApplicationContext(),"no varied inputs returned",Toast.LENGTH_LONG).show();
                       // }


                        // Inserting row in users table
                        //db.addUser(name1,name2,email, uid, created_at);

                        //Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                RegisterActivity.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Registered");

                        // Setting Dialog Message
                        alertDialog.setMessage("Registered Successfully Welcome to FriendZone");

                        // Setting Icon to Dialog
                        alertDialog.setIcon(R.drawable.tick);

                        // Setting OK Button
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                                //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();

                                Intent d=new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(d);
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();
                        // Launch l
                        // ogin activity

                        //finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
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
            protected Map<String,String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String,String>();
                params.put("fname",fname);
                params.put("sname",sname);
                params.put("email",email);
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
