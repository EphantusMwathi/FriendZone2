package com.gpslocation.friendzone.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
//import android.widget.Toolbar;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.activity.MainActivity;
import com.gpslocation.friendzone.activity.NewFriend;

/**
 * Created by mwathi on 3/16/2016.
 */
public class Result extends ActionBarActivity {
private TextView first,second,mail;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_added_layout);
        Intent intent=getIntent();
        String name1 = intent.getStringExtra("firstname");
        String name2 = intent.getStringExtra("secondname");
        String email = intent.getStringExtra("email");
toolbar=(Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        first=(TextView) findViewById(R.id.first_result);
        second=(TextView) findViewById(R.id.second_result);
        mail=(TextView)findViewById(R.id.email_result);

        first.setText(name1);
        second.setText(name2);
        mail.setText(email);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(android.R.id.home==item.getItemId()){
            Intent intent=new Intent(getApplicationContext(),NewFriend.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
}
