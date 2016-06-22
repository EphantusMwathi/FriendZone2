package com.gpslocation.friendzone.activity;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;



//import com.gpslocation.friendzone.Maps;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.gcm.NotificationUtils;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.helper.SessionManager;

/**
 * Created by mwathi on 2/11/2016.
 */

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;


public class MainActivity extends ActionBarActivity {

    private String TAG="MainActivity";
private SessionManager sessionManager;
    private Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SQLiteHandler db;
    private int[] tabIcons ={R.drawable.friends, R.drawable.messages2,R.drawable.notice,R.drawable.menu3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

      //  setupTabIcons();


broadcastReceiver=new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)){

            handlePushNotification(intent);
        }

    }
};


    }


private void handlePushNotification(Intent intent){

NotificationUtils notificationUtils=new NotificationUtils(this);
    notificationUtils.playNotificationSound();
    int type=intent.getIntExtra("type",-1);
    if(type==AppConfig.PUSH_TYPE_USER){

        AlertDialog alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Message");

        // Setting Dialog Message
        alertDialog.setMessage("New Message");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.map_message);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

            }
        });

        alertDialog.show();

    }
    if(type==AppConfig.PUSH_TYPE_NOTIFICATION){
        AlertDialog alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Notification");

        // Setting Dialog Message
        alertDialog.setMessage("You have a new notification");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.notification);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

            }
        });

        alertDialog.show();
    }
    if(type==AppConfig.PUSH_TYPE_INVITE){

        AlertDialog alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Invite");

        // Setting Dialog Message
        alertDialog.setMessage("You have a new invite");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.invite);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

            }
        });

        alertDialog.show();
    }
    if(type==AppConfig.PUSH_TYPE_POKE){

String message=intent.getStringExtra("message1");
        AlertDialog alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Poke");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.invite);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

            }
        });

        alertDialog.show();
    }





}

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

    }

    private void setupViewPager(ViewPager viewPager) {


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Contacts(), "Contacts");
        adapter.addFragment(new MessagesPage(), "Chats");
        adapter.addFragment(new NotificationPage(),"Notification");
        adapter.addFragment(new Invitation(),"Invitation");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if(id==R.id.profile){
            Intent myprof=new Intent(this,Prof.class);
            startActivity(myprof);

            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            db=new SQLiteHandler(this);
            sessionManager=new SessionManager(this);

            Intent location=new Intent(getApplicationContext(),GetLocation.class);
            stopService(location);

            updateDatabase();




            return true;
        }
          if(id==R.id.setting){

              Intent callservice;
              callservice = new Intent(MainActivity.this,ServiceSetting.class);
              startActivity(callservice);
              return true;
          }


        return super.onOptionsItemSelected(item);
    }



    private void updateDatabase(){
        SQLiteHandler db=new SQLiteHandler(this);

        HashMap<String,String>user_details=db.getUserDetails();

        final String uid=user_details.get("uid");


        String tag_string_req = "req_friend_information";


        //final String  unique_id=user.get("uid");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATEDATABASE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject object=new JSONObject(response);

                    if(!object.getBoolean("error")){
                        SQLiteHandler db=new SQLiteHandler(getApplicationContext());
                        sessionManager.setLogin(false);
                        sessionManager.setService(false);
                        db.deleteUsers();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);

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
                params.put("id",uid);



                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



    }


    @Override
    protected void onResume() {
        super.onResume();

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