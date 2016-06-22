package com.gpslocation.friendzone.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.adapter.ChatRoomsAdapter;
import com.gpslocation.friendzone.adapter.NotificationAdapter;
import com.gpslocation.friendzone.adapter.NotificationRefreshAdapter;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.app.EndPoints;
import com.gpslocation.friendzone.gcm.GcmIntentService;
import com.gpslocation.friendzone.gcm.NotificationUtils;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.helper.SimpleDividerItemDecoration;
import com.gpslocation.friendzone.helper.SwipeListAdapter;
import com.gpslocation.friendzone.model.ChatRoom;
import com.gpslocation.friendzone.model.Message;
import com.gpslocation.friendzone.model.Notification;
import com.gpslocation.friendzone.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationPage extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    private String TAG = NotificationPage.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
   // private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BroadcastReceiver broadCastReceiver;
    private ArrayList<Notification> notificationArrayList;
    private NotificationRefreshAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
       // super.onCreate(savedInstanceState);
       // setContentView(R.layout.notification_page);
        View v=inflater.inflate(R.layout.notification_page, container, false);

       // toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Notification");

        ListView listView=(ListView)v.findViewById(R.id.listviewnotification);
swipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_layout);

        notificationArrayList=new ArrayList<>();
        mAdapter = new NotificationRefreshAdapter(getActivity(),notificationArrayList);
        listView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        if (notificationArrayList != null) {

                                            notificationArrayList.clear();


                                        }

                                        fetchNotification();
                                    }
                                }
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView sid = (TextView) view.findViewById(R.id.unique_id);

                String fuid = sid.getText().toString();
                Intent map = new Intent(getActivity(), Map.class);
                map.putExtra("fuid", fuid);
                startActivity(map);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });


        broadCastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)){


                    handlePushNotification(intent);
                }
            }
        };

return v;

    }


    private void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra("type", -1);

        // if the push is of chat room message
        // simply update the UI unread messages count
        if (type == AppConfig.PUSH_TYPE_NOTIFICATION) {
          // notificationArrayList.clear();

            //fetchNotification();
            //Toast.makeText(getActivity(),"new notification",Toast.LENGTH_LONG).show();

            Notification notification= (Notification) intent.getSerializableExtra("notification");
            notificationArrayList.add(notification);

            mAdapter.notifyDataSetChanged();
            //
            // Toast.makeText(getActivity(), "new notification", Toast.LENGTH_SHORT).show();



        }

    }


    private void fetchNotification(){


        swipeRefreshLayout.setRefreshing(true);
        SQLiteHandler db=new SQLiteHandler(getActivity());
        HashMap<String,String> userDetails=db.getUserDetails();
        final String unique_id=userDetails.get("uid");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.NOTIFICATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        JSONArray notificationArray = obj.getJSONArray("data");
                        for (int i = 0; i < notificationArray.length(); i++) {
                            JSONObject notificationObj = (JSONObject) notificationArray.get(i);
                            Notification notification=new Notification();
                           // User user=new User();
                            notification.setNotification_id(notificationObj.getInt("notification_id"));

                            notification.setTitle(notificationObj.getString("title"));

                            String friend_id=notificationObj.getString("friend_id");
                            String message=notificationObj.getString("message");
                            String fname=notificationObj.getString("fname");
                            String sname=notificationObj.getString("sname");
                            String created_at=notificationObj.getString("created_at");
                            String name=fname+" "+sname;
                            double distance=notificationObj.getDouble("distance");
                            String cha_distance=Double.toString(distance);
                            notification.setFriend_id(friend_id);
                           String mess=name+" is "+cha_distance+" close to you";
                            //String message=name1+" "+name2+" "+mess;

                            notification.setNotification(mess);
                            notification.setCreated_at(created_at);




                            notificationArrayList.add(notification);
                        }

                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(getActivity(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                mAdapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);

                // subscribing to all chat room topics

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getActivity(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting parameters to login url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("uid", unique_id);


                return params;
            }
        };

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);




    }

    @Override
    public void onRefresh() {

        if(notificationArrayList!=null){

            notificationArrayList.clear();




        }
        fetchNotification();
    }

    @Override
    public void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadCastReceiver,
                new IntentFilter(AppConfig.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadCastReceiver,
                new IntentFilter(AppConfig.PUSH_NOTIFICATION));

        // clearing the notification tray
        NotificationUtils.clearNotifications();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadCastReceiver);
        super.onPause();
    }
}
