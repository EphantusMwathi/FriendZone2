package com.gpslocation.friendzone.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gpslocation.friendzone.MyMarker;
import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.app.EndPoints;
import com.gpslocation.friendzone.gcm.NotificationUtils;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mwathi on 5/13/2016.
 */
public class Map extends ActionBarActivity {
    private Toolbar toolbar;
    private static final String TAG = Map.class.getSimpleName();
    // private GetLocation getLocation;
    private GoogleMap googleMap;
    private boolean blocked=false;

    private SQLiteHandler db;
    private Marker marker1,marker2;
private BroadcastReceiver broadcastReceiver;
    private String f_unique_id;
   private Handler handler = new Handler();
    private FloatingActionButton list,message,poking;
    private String name;
    private TextView mail,fname,m_distance,m_block;
   // private double latitude1,longitude1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);



        toolbar=(Toolbar)findViewById(R.id.toolbar_maps);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Maps");


        Intent intent=getIntent();
        String fuid=intent.getStringExtra("fuid");
        f_unique_id=fuid;

        fetchFriendInfor(fuid);

        list=(FloatingActionButton)findViewById(R.id.map_list);
        message=(FloatingActionButton)findViewById(R.id.map_message);
        poking=(FloatingActionButton)findViewById(R.id.map_poke);
        //block=(FloatingActionButton)findViewById(R.id.map_block);
        //unblock=(FloatingActionButton)findViewById(R.id.map_unblock);

        fname=(TextView)findViewById(R.id.name);
        mail=(TextView)findViewById(R.id.email);
        m_distance=(TextView)findViewById(R.id.map_distance);
        m_block=(TextView)findViewById(R.id.map_block);






list.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent mylist=new Intent(getApplicationContext(),ListOfCloseFriends.class);
        startActivity(mylist);
    }
});
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent message=new Intent(getApplicationContext(),ChatRoomActivity.class);


                message.putExtra("name", name);
                message.putExtra("friendID",f_unique_id);
                startActivity(message);

            }
        });

        poking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pokingfunction();

            }
        });


        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)){

                    handlePushNotification(intent);
                }
            }
        };

        try {
            // Loading map
            initilizeMap();
            //fetchMyCoordinates();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //fetchFriendCoordinates();


        handler.postDelayed(runnable,2000);

        /*
        fetchMyCoordinates();
        fetchFriendCoordinates();
        fetchDistance();
        */
    }


    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
if(marker1!=null){
    marker1.remove();

    //fetchMyCoordinates();
}
            if(marker2!=null){

                marker2.remove();
            }

            fetchMyCoordinates();
            fetchFriendCoordinates();
            fetchDistance();
            handler.postDelayed(this,3000);
        }
    };



private void handlePushNotification(Intent intent){

    int type = intent.getIntExtra("type", -1);
    String message2=intent.getStringExtra("message2");
    String friend_id=intent.getStringExtra("fid");


    if (type == AppConfig.PUSH_TYPE_POKE) {

        if(f_unique_id.equals(friend_id)){

            AlertDialog alertDialog = new AlertDialog.Builder(
                    Map.this).create();

            // Setting Dialog Title
            alertDialog.setTitle("Poked");

            // Setting Dialog Message
            alertDialog.setMessage(message2);

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.map_poke);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed

                }
            });

            alertDialog.show();
        }


    }


}

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map_view)).getMap();


            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            fetchMyCoordinates();
            fetchFriendCoordinates();


            // ROSE color icon

           // marker1= new MarkerOptions().position(new LatLng(latitude1, longitude1)).title("YOU ");

            //CameraPosition cameraPosition = new CameraPosition.Builder().target(
                   // new LatLng(latitude1, longitude1)).zoom(12).build();
         /*   marker1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            marker2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
*/
            //googleMap.addMarker(marker1);


            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }


        }
    }







    private void pokingfunction(){
        String tag_string_req = "req_friend_information";
        db=new SQLiteHandler(this);
        HashMap<String,String>user=db.getUserDetails();
        final String unique=user.get("uid");

        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_MAPPOKE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if(!object.getBoolean("error")){


                       Toast.makeText(getApplicationContext(),"poke sent",Toast.LENGTH_LONG).show();
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
            protected java.util.Map<String, String> getParams() {
                // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<String, String>();

                params.put("user_unique",unique);
                params.put("friend_unique",f_unique_id);



                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    /////fetch distance

    private void fetchDistance(){

        String tag_string_req = "req_friend_information";
        db=new SQLiteHandler(this);
        HashMap<String,String>user=db.getUserDetails();
        final String unique=user.get("uid");

        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_MAPDISTANCE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                try {
                    JSONObject object=new JSONObject(response);

                    if(!object.getBoolean("error")) {


                        double distance = object.getDouble("distance");

                        String sd = Double.toString(distance);

//Toast.makeText(getApplicationContext(),"now",Toast.LENGTH_LONG).show();

                        if(distance==1000000.0){
                            m_distance.setText("OFF");
                        }
                        else{

                            m_distance.setText(sd);

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
            protected java.util.Map<String, String> getParams() {
                // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<String, String>();

                params.put("user_unique",unique);
                params.put("friend_unique",f_unique_id);



                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }



    //fetch my coordinates
  private void fetchMyCoordinates(){
      String tag_string_req = "req_friend_information";
      db=new SQLiteHandler(this);
HashMap<String,String>user=db.getUserDetails();
      final String unique=user.get("uid");

      //final String  unique_id=user.get("uid");
      StringRequest strReq = new StringRequest(Request.Method.POST,
              EndPoints.MYCOORDINATES, new Response.Listener<String>() {

          @Override
          public void onResponse(String response) {
              Log.d(TAG, "Login Response: " + response.toString());
              try {
                  JSONObject object=new JSONObject(response);

                  if(!object.getBoolean("error")){

                      double latitude1=object.getDouble("latitude");
                      double longitude1=object.getDouble("longitude");

                      String lat=String.valueOf(latitude1);
                      String lon=String.valueOf(longitude1);

                      /*
                       latitude1=latitude;
                      longitude1=longitude;


*/                  LatLng mLatLng = new LatLng(latitude1,longitude1);





                         // marker1 = googleMap.addMarker(new MarkerOptions().position(mLatLng).title("you").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));



                      marker1 = googleMap.addMarker(new MarkerOptions().position(mLatLng).title("you").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));


                      // marker1= new MarkerOptions().position(new LatLng(latitude, longitude)).title("YOU ");
                     // googleMap.addMarker(marker1);



                      //marker1.visible(true);
                      //
                      //
                      //
                      // Toast.makeText(getApplicationContext(),lat+lon,Toast.LENGTH_LONG).show();


// adding marker
                     // googleMap.addMarker(marker1);

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

              params.put("unique",unique);


              return params;
          }

      };


      AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



  }


//fetch friends coordinates


    private void fetchFriendCoordinates(){

        String tag_string_req = "req_friend_information";
        HashMap<String,String>userDetails=db.getUserDetails();
        final String uid=userDetails.get("uid");

        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.FRIENDCOORDINATES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                try {
                    JSONObject object=new JSONObject(response);

                    if(!object.getBoolean("error")){

                        double latitude=object.getDouble("latitude");
                        double longitude=object.getDouble("longitude");


                        String lat=String.valueOf(latitude);
                        String lon=String.valueOf(longitude);




                      /*
                       latitude1=latitude;
                      longitude1=longitude;


*/                  LatLng mLatLng = new LatLng(latitude,longitude);




                        marker2 = googleMap.addMarker(new MarkerOptions().position(mLatLng).title("friend").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                                new LatLng(latitude, longitude)).zoom(19).build();

                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        // marker1= new MarkerOptions().position(new LatLng(latitude, longitude)).title("YOU ");
                        // googleMap.addMarker(marker1);



                        //marker1.visible(true);
                       // Toast.makeText(getApplicationContext(),lat+lon,Toast.LENGTH_LONG).show();

                        if(object.getBoolean("blocked")){
                            m_block.setText("blocked");
                        }
                        else{
                            m_block.setText("unblocked");

                        }


// adding marker
                        // googleMap.addMarker(marker1);

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

                params.put("unique_friend",f_unique_id);
                params.put("unique_user",uid);


                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);




    }





    public void fetchFriendInfor(final String unique){
        String tag_string_req = "req_friend_information";

        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.FRIENDINFOR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                try {
                    JSONObject object=new JSONObject(response);

                    if(!object.getBoolean("error")){

                        String firstname=object.getString("fname");
                        String sname=object.getString("sname");
                        String email=object.getString("email");
                        String full_name=firstname+" "+sname;
                        name=full_name;
                        fname.setText(full_name);
                        mail.setText(email);


//Toast.makeText(getApplicationContext(),"information",Toast.LENGTH_SHORT).show();

                       // double distance=object.getDouble("distance");



// adding marker
                        // googleMap.addMarker(marker1);

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
            protected java.util.Map<String, String> getParams() {
                // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<String, String>();

                params.put("unique",unique);


                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }




    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(android.R.id.home==item.getItemId()){

            Intent intent2=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent2);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConfig.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}
