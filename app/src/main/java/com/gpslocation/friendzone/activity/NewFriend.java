package com.gpslocation.friendzone.activity;

//import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.gpslocation.friendzone.app.Result;
import com.gpslocation.friendzone.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mwathi on 3/14/2016.
 */
public class NewFriend extends ActionBarActivity {


    private static final String TAG = NewFriend.class.getSimpleName();
     private Toolbar toolbar;
     private SQLiteHandler db;

    private EditText email;
    private Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.new_friend);

toolbar=(Toolbar)findViewById(R.id.toolbar_newFriend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        email=(EditText)findViewById(R.id.new_email);
        add = (Button) findViewById(R.id.new_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String mail=email.getText().toString().trim();

if(!mail.isEmpty()){


    addNewFriend(mail);
}

else {

    Toast.makeText(getApplicationContext(),"fill the required fields",Toast.LENGTH_LONG).show();

}


    }
});




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(android.R.id.home==item.getItemId()){
Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }


// add friend
    private void addNewFriend(final String mail){


        db=new SQLiteHandler(getApplicationContext());
HashMap<String,String> user=db.getUserDetails();
        final String unique=user.get("uid");
        String tag_string_req = "add_friend_information";
        email.setText("");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADDFRIEND, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(!jsonObject.getBoolean("error")){



                       // Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                NewFriend.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Invitation");

                        // Setting Dialog Message
                        alertDialog.setMessage(jsonObject.getString("message"));

                        // Setting Icon to Dialog
                        alertDialog.setIcon(R.drawable.tick);

                        // Setting OK Button
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog closed
                               // Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();


                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();



                    }
                    else{

                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Add Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String,String>();
                params.put("email", mail);
                params.put("uid",unique);


                return params;
            }

        };

        // Adding request to request queue

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void showMyDialog(String name1,String name2){


String message=name1+" "+name2+" invited";

        AlertDialog alertDialog = new AlertDialog.Builder(
                NewFriend.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("New Friend");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed

            }
        });

        alertDialog.show();
    }


    }



