package com.gpslocation.friendzone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwathi on 3/7/2016.
 */
public class Profile extends ActionBarActivity {
private String TAG="Profile";
    private Intent intent,intent2;
    private Toolbar toolbar;
    private TextView name,email,distance;
    private boolean block=false;
    private String message;
    private  String unique;
    private Handler handler=new Handler();


private Button unFriend,Block;

    int counter;
    SQLiteHandler db;
    private String pageID,full_name;

   // Intent intent = getIntent();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_layout);
        Block=(Button)findViewById(R.id.block);
        unFriend=(Button)findViewById(R.id.un_friend);
        name=(TextView)findViewById(R.id.profile_name);
        email=(TextView)findViewById(R.id.profile_email);
        distance=(TextView)findViewById(R.id.profile_distance);
        FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.profile_message);


        db=new SQLiteHandler(this);
        intent = getIntent();
         message = intent.getStringExtra("message");



        toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        HashMap<String,String>userdetails=db.getUserDetails();
         unique=userdetails.get("uid");
        getprofile();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat=new Intent(getApplicationContext(),ChatRoomActivity.class);
                chat.putExtra("friendID",pageID);
                chat.putExtra("name",full_name);
                startActivity(chat);

            }
        });

Block.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        if(block){


            unblockFriend();

        }
        else{

            blockFriend();
        }

    }
});
        unFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfriend();
            }
        });

       // Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();


        handler.postDelayed(runnable,10000);
    }



    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            fetchDistance();
            handler.postDelayed(this,4000);
        }
    };


    //fetch distance
   private void fetchDistance(){


       String tag_string_req = "req_friend_information";


       //final String  unique_id=user.get("uid");
       StringRequest strReq = new StringRequest(Request.Method.POST,
               AppConfig.URL_PROFILEDISTANCE, new Response.Listener<String>() {

           @Override
           public void onResponse(String response) {
               Log.d(TAG, "Login Response: " + response.toString());

               try {
                   JSONObject object=new JSONObject(response);

                   if(!object.getBoolean("error")){

                       double j_distance=object.getDouble("distance");

                       if(j_distance==1000000.0){
                           distance.setText("OFF");
                           //Toast.makeText(getApplicationContext(),"OFF",Toast.LENGTH_LONG).show();

                       }
                       else{

                           String aString = Double.toString(j_distance);

                           distance.setText(aString);
                           //Toast.makeText(getApplicationContext(),aString,Toast.LENGTH_LONG).show();

                       }
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
               params.put("id",message);
               params.put("unique",unique);


               return params;
           }

       };


       AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



   }

    //get profile information

    private void getprofile(){

        String tag_string_req = "req_friend_information";


        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PROFILE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject object=new JSONObject(response);

                    if(!object.getBoolean("error")){
                        String fname=object.getString("fname");
                        String sname=object.getString("sname");
                        String mail=object.getString("email");
                        String fuid=object.getString("fuid");

                        pageID=fuid;
                        double j_distance=object.getDouble("distance");
                        String aString = Double.toString(j_distance);
                        String fullname=fname + " " + sname;
                        full_name=fullname;
                        boolean blocked=object.getBoolean("block");
                        name.setText(fullname);
                        email.setText(mail);

                       // Toast.makeText(getApplicationContext(),fullname,Toast.LENGTH_LONG).show();
                        if(blocked){
                            block=true;
                           Block.setText("unblock");
                            Block.setBackgroundColor(getResources().getColor(R.color.black));
                            distance.setText("OFF");
                        }
                        else{
                            block=false;
                            Block.setText("block");
                            Block.setBackgroundColor(getResources().getColor(R.color.wallet_holo_blue_light));
                            distance.setText(aString);
                        }

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
                params.put("id",message);
                params.put("unique",unique);


                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }




//unblock friend

    private void unblockFriend(){
        String tag_string_req = "req_friend_information";


        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UNBLOCK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject object=new JSONObject(response);
                    if(!object.getBoolean("error")){

                        if(object.getBoolean("unblock")){
                            block=false;
                            Block.setText("block");
                            Block.setBackgroundColor(getResources().getColor(R.color.wallet_holo_blue_light));

                            Toast.makeText(Profile.this,object.getString("message"), Toast.LENGTH_SHORT).show();

                        }
                        else{

                            block=true;
                            Block.setBackgroundColor(getResources().getColor(R.color.black));
                            Block.setText("unblock");

                            Toast.makeText(Profile.this,"friend still blocked", Toast.LENGTH_SHORT).show();
                        }

                    }else {


                        Toast.makeText(Profile.this,object.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("message",message);
                params.put("unique",unique);


                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }




//block friend

    private void blockFriend(){
        String tag_string_req = "req_friend_information";


        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_BLOCK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());


                try {
                    JSONObject object=new JSONObject(response);
                    if(!object.getBoolean("error")){

                        if(object.getBoolean("block")){
                            block=true;
                            Block.setText("unblock");
                            Block.setBackgroundColor(getResources().getColor(R.color.black));

                            Toast.makeText(Profile.this,object.getString("message"), Toast.LENGTH_SHORT).show();

                        }
                        else{

                            block=false;
                            Block.setText("block");
                            Block.setBackgroundColor(getResources().getColor(R.color.wallet_holo_blue_light));
                            Toast.makeText(Profile.this,"friend still blocked", Toast.LENGTH_SHORT).show();
                        }

                    }else {


                        Toast.makeText(Profile.this,object.getString("message"), Toast.LENGTH_SHORT).show();
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
                params.put("message",message);
                params.put("unique",unique);


                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }


//unfriend friend


    private void unfriend(){
        String tag_string_req = "req_friend_information";


        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UNFRIEND, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject object=new JSONObject(response);

                    if(!object.getBoolean("error")){


                       // Toast.makeText(getApplicationContext(),object.getString("message"),Toast.LENGTH_LONG).show();

                        AlertDialog alertDialog = new AlertDialog.Builder(
                                Profile.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Unfriend");

                        // Setting Dialog Message
                        alertDialog.setMessage(object.getString("message"));

                        // Setting Icon to Dialog
                        alertDialog.setIcon(R.drawable.tick);

                        // Setting OK Button
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                                // Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(main);
                                finish();

                            }
                        });

                        // Showing Alert Message
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
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("message",message);
                params.put("unique",unique);


                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(android.R.id.home==item.getItemId()){


            handler.removeCallbacks(runnable);


intent2=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent2);
finish();

        }

        return super.onOptionsItemSelected(item);
    }




}
