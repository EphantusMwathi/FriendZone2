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
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.app.EndPoints;
import com.gpslocation.friendzone.gcm.GcmIntentService;
import com.gpslocation.friendzone.gcm.NotificationUtils;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.helper.SimpleDividerItemDecoration;
import com.gpslocation.friendzone.model.ChatRoom;
import com.gpslocation.friendzone.model.Message;
import com.gpslocation.friendzone.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesPage extends Fragment {

    private Toolbar toolbar;
    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ArrayList<ChatRoom> chatRoomArrayList;
    private ChatRoomsAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.messages_page, container, false);
       //setContentView(R.layout.messages_page);





        SQLiteHandler db=new SQLiteHandler(getActivity());

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                if (intent.getAction().equals(AppConfig.REGISTRATION_COMPLETE)) {


                } else if (intent.getAction().equals(AppConfig.SENT_TOKEN_TO_SERVER)) {
                    Log.e(TAG, "GCM registration id is sent to our server");
                } else if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                }

            }
        };


        chatRoomArrayList = new ArrayList<>();
        mAdapter = new ChatRoomsAdapter(getActivity(), chatRoomArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ChatRoomsAdapter.RecyclerTouchListener(getActivity(), recyclerView, new ChatRoomsAdapter.ClickListener() {


            @Override
            public void onClick(View view, int position) {

               ChatRoom chatRoom = chatRoomArrayList.get(position);
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);

                intent.putExtra("chat_room_id", chatRoom.getId());
                intent.putExtra("name", chatRoom.getName());
                intent.putExtra("friendID",chatRoom.getfriendID());

                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (checkPlayServices()) {
            registerGCM();
            HashMap<String,String>userD=db.getUserDetails();
            String uid=userD.get("uid");

            fetchChatRooms(uid);
        }
return v;
    }
    private void handlePushNotification(Intent intent) {
        int type = intent.getIntExtra("type", -1);

        // if the push is of chat room message
        // simply update the UI unread messages count
        if (type == AppConfig.PUSH_TYPE_CHATROOM) {
            Message message = (Message) intent.getSerializableExtra("message");
            int chatRoomId = intent.getIntExtra("chat_room_id",-1);

            if (message != null ) {
                updateRow(chatRoomId, message);
            }
        }

    }




    private void updateRow(int chatRoomId, Message message) {
        for (ChatRoom cr : chatRoomArrayList) {
            if (cr.getId()==chatRoomId) {
                int index = chatRoomArrayList.indexOf(cr);
                cr.setLastMessage(message.getMessage());
                cr.setUnreadCount(cr.getUnreadCount() + 1);
                chatRoomArrayList.remove(index);
                chatRoomArrayList.add(index, cr);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    private void fetchChatRooms(final String uid) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.CHAT_ROOMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        JSONArray chatRoomsArray = obj.getJSONArray("data");
                        for (int i = 0; i < chatRoomsArray.length(); i++) {
                            JSONObject chatRoomsObj = chatRoomsArray.getJSONObject(i);
                            ChatRoom cr = new ChatRoom();
                            cr.setId(chatRoomsObj.getInt("chat_room_id"));
                            String fname=chatRoomsObj.getString("fname");
                            String sname=chatRoomsObj.getString("sname");
                           // String email=chatRoomsObj.getString("email");
                            String friend_id=chatRoomsObj.getString("friend_id");
                            String created_at=chatRoomsObj.getString("created_at");
                            String name=fname+" "+sname;

                            cr.setfriendID(friend_id);
                            cr.setName(name);
                            cr.setLastMessage("");
                            cr.setUnreadCount(0);
                            cr.setTimestamp(created_at);

                            chatRoomArrayList.add(cr);
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

                // subscribing to all chat room topics

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getActivity(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);


                return params;
            }

        };

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }



    @Override
    public void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConfig.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConfig.PUSH_NOTIFICATION));

        // clearing the notification tray
        NotificationUtils.clearNotifications();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(getActivity(), GcmIntentService.class);
        intent.putExtra("key", "register");
        //startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getActivity(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
               // finish();
            }
            return false;
        }
        return true;
    }
}