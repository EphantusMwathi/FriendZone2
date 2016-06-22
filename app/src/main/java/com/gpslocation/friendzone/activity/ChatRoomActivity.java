package com.gpslocation.friendzone.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.adapter.ChatRoomThreadAdapter;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.app.EndPoints;
import com.gpslocation.friendzone.gcm.NotificationUtils;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.model.Message;
import com.gpslocation.friendzone.model.User;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity {


    private String TAG = ChatRoomActivity.class.getSimpleName();

    private String friend;
    //private String fuid;
    private RecyclerView recyclerView;
    private ChatRoomThreadAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private EditText inputMessage;
    private Button btnSend;
    private NotificationUtils notificationUtils;
    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_room);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
       // chatRoomId = intent.getStringExtra("chat_room_id");
        String title = intent.getStringExtra("name");
        final String fuid=intent.getStringExtra("friendID");
        friend=fuid;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        if (fuid == null) {
            Toast.makeText(getApplicationContext(), "Chat room not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (Button) findViewById(R.id.btn_send);
      recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        messageArrayList=new ArrayList<>();
        db=new SQLiteHandler(this);
        HashMap<String,String>userId=db.getUserDetails();
        final String myID=userId.get("uid");

        mAdapter = new ChatRoomThreadAdapter(this, messageArrayList, myID);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                    // new push message is received
                    handlePushNotification(intent);
                }
            }
        };

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(myID,fuid);
            }
        });

        fetchChatThread(myID, fuid);
//Toast.makeText(getApplicationContext(),friend,Toast.LENGTH_LONG).show();

    }

private void handlePushNotification(Intent intent){
   // int chat_one=0;
    int type = intent.getIntExtra("type", -1);

    if(type==AppConfig.PUSH_TYPE_USER) {
        Message message = (Message) intent.getSerializableExtra("message");
        //String chat = intent.getStringExtra("chat_room_id");
        String friend_id = intent.getStringExtra("friend_id");
        // int chat_one=0;
        //chat_one=Integer.parseInt(chat);


        if (friend_id.equals(friend)) {
            NotificationUtils notificationUtils=new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

            //Toast.makeText(getApplicationContext(), "new message", Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), friend, Toast.LENGTH_LONG).show();
            if (message != null) {
                messageArrayList.add(message);
                mAdapter.notifyDataSetChanged();

                if (mAdapter.getItemCount() > 1) {
                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                   // NotificationUtils notificationUtils=new NotificationUtils(getApplicationContext());
                    //notificationUtils.playNotificationSound();
                }
            }
        }
    }

   }

    private void fetchChatThread(final String uid ,final String fuid){


            StringRequest strReq = new StringRequest(Request.Method.POST,
                    EndPoints.CHAT_THREAD, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.e(TAG, "response: " + response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        // check for error
                        if (obj.getBoolean("error") == false) {
                            JSONArray commentsObj = obj.getJSONArray("data");

                            for (int i = 0; i < commentsObj.length(); i++) {
                                JSONObject commentObj = (JSONObject) commentsObj.get(i);

                                String commentId = commentObj.getString("message_id");
                                int chat_id=commentObj.getInt("chat_room_id");
                                String commentText = commentObj.getString("message");
                                String createdAt = commentObj.getString("created_at");
                                String fname=commentObj.getString("fname");
                                String sname=commentObj.getString("sname");
                                String user_id=commentObj.getString("user_id");
                                String name=fname+" "+sname;
                               // chatRoomId=chat_id;

                                User user = new User(user_id, name, null);

                                Message message = new Message();
                                message.setId(commentId);
                                message.setMessage(commentText);
                                message.setCreatedAt(createdAt);
                                message.setUser(user);

                                messageArrayList.add(message);
                                NotificationUtils notificationUtils=new NotificationUtils(getApplicationContext());
                                notificationUtils.playNotificationSound();
                            }

                            mAdapter.notifyDataSetChanged();
                            if (mAdapter.getItemCount() > 1) {
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "json parsing error: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                    Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("uid", uid);
                    params.put("fuid", fuid);


                    return params;
                }

            };

            //Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq);
        }




    private void sendMessage(final String uid,final String fuid){


        final String message=inputMessage.getText().toString().trim();
       // Toast.makeText(getApplicationContext(), fuid+"    "+message, Toast.LENGTH_SHORT).show();

inputMessage.setText("");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.CHAT_ROOM_MESSAGE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj=new JSONObject(response);
                    if(!obj.getBoolean("error")) {
                        JSONObject messageObj = obj.getJSONObject("message");

                        String commentId = messageObj.getString("message_id");
                        String commentText = messageObj.getString("message");
                        String createdAt = messageObj.getString("created_at");

                        db= new SQLiteHandler(getApplicationContext());
                        HashMap<String,String>user_details=db.getUserDetails();
                        String fname=user_details.get("name1");
                        String sname=user_details.get("name2");
                        String user_id=user_details.get("uid");
                        String email=user_details.get("email");
                        String name=fname+" "+sname;
                        User user=new User(user_id,name,email);
                        Message message = new Message();
                        message.setId(commentId);
                        message.setMessage(commentText);
                        message.setCreatedAt(createdAt);
                        message.setUser(user);

                        messageArrayList.add(message);

                        NotificationUtils notificationUtils=new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();

                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.getItemCount() > 1) {
                            // scrolling to bottom of the recycler view
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                        }

                    }



                    else{


                        Toast.makeText(ChatRoomActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("friend", fuid);
                params.put("message",message);


                return params;
            }

        };

        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        strReq.setRetryPolicy(policy);

        //Adding request to request queue

    }

    @Override
    protected void onResume() {
        super.onResume();

        // registering the receiver for new notification
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConfig.PUSH_NOTIFICATION));

        NotificationUtils.clearNotifications();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(android.R.id.home==item.getItemId()){

           Intent chatRoom=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(chatRoom);
        }

        return super.onOptionsItemSelected(item);
    }

}
