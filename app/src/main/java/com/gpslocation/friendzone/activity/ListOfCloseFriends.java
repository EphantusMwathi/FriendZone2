package com.gpslocation.friendzone.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
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
//import android.widget.Toolbar;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.adapter.CloseFriendsAdapter;
import com.gpslocation.friendzone.adapter.InviteAdapter;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.app.EndPoints;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.model.Close;
import com.gpslocation.friendzone.model.Invite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by mwathi on 5/14/2016.
 */
public class ListOfCloseFriends extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String TAG = Invitation.class.getSimpleName();

    private List<Close> closeArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private Toolbar toolbar;
    //private BroadcastReceiver mRegistrationBroadcastReceiver;
    private CloseFriendsAdapter closeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_close_layout);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Close Friends");



        ListView listView=(ListView)findViewById(R.id.listview);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        closeArrayList=new ArrayList<>();

        closeAdapter = new CloseFriendsAdapter(this,closeArrayList);
        listView.setAdapter(closeAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        if (closeArrayList != null) {

                                            closeArrayList.clear();
                                            getList();

                                        }


                                    }
                                }
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friend_id=closeArrayList.get(position).getFriend_id();

                Intent intent = new Intent(getApplicationContext(), Map.class);
                intent.putExtra("fuid", friend_id);
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


    }








    //private void addFriend(final int invite_id){}

    //private void denyFriend(final int invite_id){}

    private void getList(){
        swipeRefreshLayout.setRefreshing(true);
        SQLiteHandler db=new SQLiteHandler(getApplicationContext());
        HashMap<String,String> userDetails=db.getUserDetails();
        final String unique_id=userDetails.get("uid");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.CLOSELIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        JSONArray notificationArray = obj.getJSONArray("data");
                        for (int i = 0; i < notificationArray.length(); i++) {
                            JSONObject inviteObj = notificationArray.getJSONObject(i);
                            Close close=new Close();
                            String fname=inviteObj.getString("fname");
                            String sname=inviteObj.getString("sname");
                            String name=fname+" "+sname;
                            String updated_at=inviteObj.getString("updated_at");
                            String friend_id=inviteObj.getString("friend_id");
                            String email=inviteObj.getString("email");
                            double distance=inviteObj.getDouble("distance");
                            //String unique_id=inviteObj.getString("unique_id");
                            close.setCreated_at(updated_at);
                            close.setName(name);
                            close.setEmail(email);
                            close.setFriend_id(friend_id);
                            close.setDistance(distance);

                           // Toast.makeText(getApplicationContext(), updated_at, Toast.LENGTH_LONG).show();

                            closeArrayList.add(close);



                        }

                    } else {
                        // error in fetching chat rooms
                        Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                closeAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

                // subscribing to all chat room topics

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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


        if (closeArrayList != null) {

            closeArrayList.clear();


        }

        getList();

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
