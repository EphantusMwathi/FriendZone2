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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwathi on 5/13/2016.
 */
public class InvitePage extends ActionBarActivity {

    private String TAG="Invite Page";
    private Toolbar toolbar;
    private TextView name,email;
    private Button deny,accept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=getIntent();
       final String invite_id=intent.getStringExtra("invite_id");
        String myname=intent.getStringExtra("name");
        String myemail=intent.getStringExtra("email");

        setContentView(R.layout.invite_page_layout);

        name=(TextView)findViewById(R.id.name);
        email=(TextView)findViewById(R.id.email);
        deny=(Button)findViewById(R.id.deny);
        accept=(Button)findViewById(R.id.accept);

        name.setText(myname);
        email.setText(myemail);
        toolbar=(Toolbar)findViewById(R.id.toolbar_invite);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Invite Me");

        //Toast.makeText(getApplicationContext(),invite_id,Toast.LENGTH_LONG).show();

        //fetchInviteDetails(invite_id);



        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                acceptInvite(invite_id);
            }
        });
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                denyInvite(invite_id);
            }
        });

    }


    //deny Invite
    private  void denyInvite(final String invite_id){
        String tag_string_req = "req_friend_information";


        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DENYINVITE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject object=new JSONObject(response);

                    if(!object.getBoolean("error")){


                        AlertDialog alertDialog = new AlertDialog.Builder(
                                InvitePage.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Friend denied");

                        // Setting Dialog Message
                        alertDialog.setMessage(object.getString("message"));

                        // Setting Icon to Dialog
                        alertDialog.setIcon(R.drawable.block);

                        // Setting OK Button
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                Intent denyintent=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(denyintent);
                            }
                        });

                        alertDialog.show();


                    }
                    else{



                        Toast.makeText(getApplicationContext(),object.getString("message"),Toast.LENGTH_LONG).show();
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
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("invite_id",invite_id);



                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    //accept invite

    private void acceptInvite(final String invite_id){

        String tag_string_req = "req_friend_information";
Toast.makeText(getApplicationContext(),invite_id,Toast.LENGTH_LONG).show();

        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ACCEPTINVITE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject object=new JSONObject(response);

                    if(!object.getBoolean("error")){


                        AlertDialog alertDialog = new AlertDialog.Builder(
                                InvitePage.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Accepted");

                        // Setting Dialog Message
                        alertDialog.setMessage(object.getString("message"));

                        // Setting Icon to Dialog
                        alertDialog.setIcon(R.drawable.tick);

                        // Setting OK Button
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent accepted=new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(accepted);

                            }
                        });

                        alertDialog.show();


                    }
                    else{



                        Toast.makeText(getApplicationContext(),object.getString("message"),Toast.LENGTH_LONG).show();
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
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("invite_id",invite_id);



                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }



//fetch Invite details
    private void fetchInviteDetails(final String invite_id){
        String tag_string_req = "req_friend_information";


        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INVITEDETAILS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject object=new JSONObject(response);

                    if(!object.getBoolean("error")){

                        String fname=object.getString("fname");
                        String sname=object.getString("sname");
                        String mail=object.getString("email");
                        String full_name=fname+" "+sname;

                        name.setText(full_name);
                        email.setText(mail);

                    }
                    else{



                        Toast.makeText(getApplicationContext(),object.getString("message"),Toast.LENGTH_LONG).show();
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
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("invite_id",invite_id);



                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(android.R.id.home==item.getItemId()){

           Intent intent2=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent2);

        }

        return super.onOptionsItemSelected(item);
    }


}
