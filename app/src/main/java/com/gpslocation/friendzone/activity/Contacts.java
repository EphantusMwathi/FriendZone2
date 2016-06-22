package com.gpslocation.friendzone.activity;



import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
//import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ListView;


import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;

import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;

//import com.gpslocation.friendzone.helper.OpenDataBase;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.helper.SwipeListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Response.*;
//import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Contacts extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = Contacts.class.getSimpleName();



    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeListAdapter adapter;
    private List<Friend> friendList;
    private SQLiteHandler db;







       @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.main_page, container, false);


           db=new SQLiteHandler(getActivity());
           HashMap<String,String> user=db.getUserDetails();

           final String unique_id = user.get("uid");


           ListView listView = (ListView) v.findViewById(R.id.listviewcontact);
           swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_layout);

           friendList = new ArrayList<>();
           adapter = new SwipeListAdapter(getActivity(),friendList);
           listView.setAdapter(adapter);

           swipeRefreshLayout.setOnRefreshListener(this);

           FloatingActionButton floatingActionButton=(FloatingActionButton)v.findViewById(R.id.fab);
           FloatingActionButton floatingActionButton1=(FloatingActionButton)v.findViewById(R.id.list);



           floatingActionButton1.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent list=new Intent(getActivity(),ListOfCloseFriends.class);
                   startActivity(list);
               }
           });


           floatingActionButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                  Intent intent=new Intent(getActivity(),NewFriend.class);
                   startActivity(intent);
               }
           });

           /**
            * Showing Swipe Refresh animation on activity create
            * As animation won't start on onCreate, post runnable is used
            */
           swipeRefreshLayout.post(new Runnable() {
                                       @Override
                                       public void run() {
                                           swipeRefreshLayout.setRefreshing(true);
                                           if (friendList != null) {

                                               friendList.clear();
                                               displayContent(unique_id);

                                           }


                                       }
                                   }
           );

           listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   TextView sid = (TextView) view.findViewById(R.id.json_id);

                   String xid = sid.getText().toString();
                   Intent intent = new Intent(getActivity(), Profile.class);
                   intent.putExtra("message", xid);


                   startActivity(intent);
               }
           });
           listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
               @Override
               public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                   return false;
               }
           });

           return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onRefresh() {

        if(friendList!=null){

            friendList.clear();
db=new SQLiteHandler(getActivity());
            HashMap<String,String> user=db.getUserDetails();
            String unique_id=user.get("uid");



            displayContent(unique_id);
        }

    }



    public void displayContent(final String unique_id) {


        String tag_string_req = "req_friend_information";
        db=new SQLiteHandler(getActivity());
        HashMap<String,String> user=db.getUserDetails();

        String name1=user.get("fname");


        //final String  unique_id=user.get("uid");



        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DISPLAY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject object=new JSONObject(response);
                    if(!object.getBoolean("error")){
                        JSONArray jsonArray=object.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++){

                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            String fname=jsonObject.getString("fname");
                            String sname=jsonObject.getString("sname");
                            String name=fname+" "+sname;
                            int id=jsonObject.getInt("id");
                            String email=jsonObject.getString("email");
                            double distance=jsonObject.getDouble("distance");
                            String mydistance=String.valueOf(distance);
                            Friend m;
                            if(distance==1000000){

                                String nulldistance="OFF";
                             //   double doubleOFF=Double.valueOf(nulldistance);
                                m=new Friend(id,name,nulldistance,email);
                                friendList.add(0,m);
                            }
                            else{

                                m=new Friend(id,name,mydistance,email);
                                friendList.add(0,m);
                            }



                        }




                    }
                    else{
                        Toast.makeText(getActivity(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }



        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "display Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("unique", unique_id);


                return params;
            }

        };


        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
