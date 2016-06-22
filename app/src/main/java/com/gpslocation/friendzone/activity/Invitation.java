package com.gpslocation.friendzone.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
//import com.gpslocation.friendzone.Maps;
import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.adapter.ChatRoomsAdapter;
import com.gpslocation.friendzone.adapter.InviteAdapter;
import com.gpslocation.friendzone.adapter.NotificationRefreshAdapter;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.app.EndPoints;
import com.gpslocation.friendzone.gcm.NotificationUtils;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.helper.SimpleDividerItemDecoration;
import com.gpslocation.friendzone.model.ChatRoom;
import com.gpslocation.friendzone.model.Invite;
import com.gpslocation.friendzone.model.Notification;
import com.gpslocation.friendzone.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwathi on 4/22/2016.
 */
public class Invitation extends Fragment implements SwipeRefreshLayout.OnRefreshListener {



    private String TAG = Invitation.class.getSimpleName();

    private List<Invite> inviteArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
   private  InviteAdapter inviteAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.invite_layout,container,false);


        ListView listView=(ListView)v.findViewById(R.id.listview);
        swipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_layout);

        inviteArrayList=new ArrayList<>();

        inviteAdapter = new InviteAdapter(getActivity(),inviteArrayList);
        listView.setAdapter(inviteAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        if (inviteArrayList != null) {

                                            inviteArrayList.clear();
                                            getInvites();

                                        }


                                    }
                                }
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView sid = (TextView) view.findViewById(R.id.invite_id);
                TextView myname = (TextView) view.findViewById(R.id.name);
                TextView myemail = (TextView) view.findViewById(R.id.email);

                String xid = sid.getText().toString();
                String n = myname.getText().toString();
                String e = myemail.getText().toString();
                Intent intent = new Intent(getActivity(), InvitePage.class);
                intent.putExtra("invite_id", xid);
                intent.putExtra("name", n);
                intent.putExtra("email", e);
                //intent.putExtra("friend_id",)


                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                return false;
            }
        });


       broadcastReceiver=new BroadcastReceiver() {
           @Override
           public void onReceive(Context context, Intent intent) {

               if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                   // new push notification is received
                   handlePushNotification(intent);

                  // Toast.makeText(getActivity(),"new Invite",Toast.LENGTH_LONG).show();
               }
           }
       };



        return v;
    }

    private void handlePushNotification(Intent intent){

        int type = intent.getIntExtra("type", -1);




        if(AppConfig.PUSH_TYPE_INVITE==type){


           Invite invite= (Invite) intent.getSerializableExtra("invite");
            inviteArrayList.add(invite);
            inviteAdapter.notifyDataSetChanged();

       //Toast.makeText(getActivity(),"new invite",Toast.LENGTH_LONG).show();

        }

    }

    //private void addFriend(final int invite_id){}

    //private void denyFriend(final int invite_id){}

    private void getInvites(){
        swipeRefreshLayout.setRefreshing(true);
        SQLiteHandler db=new SQLiteHandler(getActivity());
        HashMap<String,String> userDetails=db.getUserDetails();
        final String unique_id=userDetails.get("uid");

        //Toast.makeText(getActivity(),unique_id, Toast.LENGTH_LONG).show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.INVITES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        JSONArray notificationArray = obj.getJSONArray("data");
                        for (int i = 0; i < notificationArray.length(); i++) {
                            JSONObject inviteObj = (JSONObject) notificationArray.get(i);
                            Invite invite=new Invite();
                           String fname=inviteObj.getString("fname");
                            String sname=inviteObj.getString("sname");
                            String name=fname+" "+sname;
                            String created_at=inviteObj.getString("created_at");
                            int invite_id=inviteObj.getInt("invite_id");
                            String email=inviteObj.getString("email");
                            //String unique_id=inviteObj.getString("unique_id");
                           invite.setCreated_at(created_at);
                            invite.setName(name);
                            invite.setEmail(email);
                            invite.setInvite_id(invite_id);

                            //Toast.makeText(getActivity(),created_at,Toast.LENGTH_LONG).show();

                             inviteArrayList.add(invite);



                        }

                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(getActivity(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                inviteAdapter.notifyDataSetChanged();
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
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", unique_id);


                return params;
            }
        };

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);




    }

    @Override
    public void onRefresh() {


        if(inviteArrayList!=null){

            inviteArrayList.clear();



            getInvites();
        }

    }
    @Override
    public void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConfig.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConfig.PUSH_NOTIFICATION));

        // clearing the notification tray
        NotificationUtils.clearNotifications();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

}
