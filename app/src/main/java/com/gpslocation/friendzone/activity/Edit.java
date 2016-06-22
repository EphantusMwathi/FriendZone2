package com.gpslocation.friendzone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by mwathi on 5/17/2016.
 */
public class Edit extends ActionBarActivity {
    private String TAG ="EDIT ACTIVITY";
    private Toolbar toolbar;
    private EditText name1,name2,mail;
    private Button update;
    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        db=new SQLiteHandler(this);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        name1= (EditText) findViewById(R.id.edit_fname);
        name2=(EditText)findViewById(R.id.edit_sname);
        mail=(EditText)findViewById(R.id.edit_email);
        update=(Button)findViewById(R.id.edit_update);
        db=new SQLiteHandler(this);
        HashMap<String,String>edit_details=db.getUserDetails();
        String first=edit_details.get("name1");
        String second=edit_details.get("name2");
        String email=edit_details.get("email");

        name1.setText(first);
        name2.setText(second);
        mail.setText(email);



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String first_name=name1.getText().toString().trim();
                String second_name=name2.getText().toString().trim();
                String my_email=mail.getText().toString().trim();

                if(first_name.isEmpty() && second_name.isEmpty() && my_email.isEmpty()){

                    Toast.makeText(getApplicationContext(),"fill all the fields",Toast.LENGTH_LONG).show();
                }
                updateInfor(first_name,second_name,my_email);

                //Toast.makeText(getApplicationContext(),first_name,Toast.LENGTH_LONG).show();

            }
        });
    }

    public void updateInfor(final String first,final String second,final String email){

        String tag_string_req = "req_friend_information";
        db=new SQLiteHandler(getApplicationContext());
        HashMap<String,String>details=db.getUserDetails();
       final String uid=details.get("uid");



        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EDIT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject object=new JSONObject(response);

                    if(!object.getBoolean("error")){


                        String fname=object.getString("fname");
                        String sname=object.getString("sname");
                        String my_email=object.getString("email");
                        String created_at=object.getString("created_at");
                        String uni=object.getString("uid");
                        db.deleteUsers();
                        db.addUser(fname, sname, my_email, uni, created_at);

                       //showMyDialg();

                        AlertDialog alertDialog = new AlertDialog.Builder(
                                Edit.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Update");

                        // Setting Dialog Message
                        alertDialog.setMessage("update successfull");

                        // Setting Icon to Dialog
                        alertDialog.setIcon(R.drawable.tick);

                        // Setting OK Button
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                                // Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                                Intent update = new Intent(getApplicationContext(), Prof.class);
                                startActivity(update);

                            }
                        });

                        alertDialog.show();


                    }
                    else{

                        Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }



        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "display Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("uid",uid);
                params.put("first",first);
                params.put("second",second);
                params.put("email",email);


                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(android.R.id.home==item.getItemId()){

            Intent intent2=new Intent(getApplicationContext(),Prof.class);
            startActivity(intent2);

        }

        return super.onOptionsItemSelected(item);
    }


    private void showMyDialg(){


        AlertDialog alertDialog = new AlertDialog.Builder(
                Edit.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Update");

        // Setting Dialog Message
        alertDialog.setMessage("update success");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Intent update=new Intent(getApplicationContext(),Prof.class);
                startActivity(update);
            }
        });

        alertDialog.show();
    }

}
