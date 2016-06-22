package com.gpslocation.friendzone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.helper.SQLiteHandler;

import java.util.HashMap;

/**
 * Created by mwathi on 3/3/2016.
 */
public class Prof extends ActionBarActivity {

    private TextView name,email;
    private Button edit;
    //private SQLiteHandler db;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page_main);


        name=(TextView)findViewById(R.id.profile_name);
        email=(TextView)findViewById(R.id.profile_email);
        edit=(Button)findViewById(R.id.edit);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

       SQLiteHandler db=new SQLiteHandler(getApplicationContext());

        HashMap<String,String>profile_details=db.getUserDetails();

        String name1=profile_details.get("name1");
        String name2=profile_details.get("name2");
        String mail=profile_details.get("email");

        String f_name=name1+" "+name2;
       // Toast.makeText(getApplicationContext(),f_name,Toast.LENGTH_LONG).show();

        name.setText(f_name);
        email.setText(mail);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),Edit.class);
                startActivity(intent);
            }
        });


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
