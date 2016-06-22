package com.gpslocation.friendzone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import android.widget.Toolbar;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.helper.SessionManager;

import java.util.HashMap;

/**
 * Created by mwathi on 2/25/2016.
 */
public class ServiceSetting extends ActionBarActivity {

private Button button_one,button_two,button_three;
    private TextView mail;
    private SQLiteHandler db;
    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_setting);
       button_one=(Button)findViewById(R.id.button_1);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        db = new SQLiteHandler(getApplicationContext());

sessionManager=new SessionManager(this);
        if(sessionManager.serviceStarted()){


            button_one.setText("service started");
            button_one.setBackgroundColor(getResources().getColor(R.color.wallet_holo_blue_light));

        }
        else{

            button_one.setText("service stopped");
            button_one.setBackgroundColor(getResources().getColor(R.color.black));
        }

        // session manager


        button_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sessionManager.serviceStarted()){


                    Intent stop=new Intent(getApplicationContext(),GetLocation.class);
                    stopService(stop);
                    sessionManager.setService(false);

                    button_one.setText("service stopped");
                    button_one.setBackgroundColor(getResources().getColor(R.color.black));
                    finish();


                }
                else
                {

                    Intent start=new Intent(getApplicationContext(),GetLocation.class);
                    startService(start);
                    sessionManager.setService(true);
                    button_one.setText("service started");
                    button_one.setBackgroundColor(getResources().getColor(R.color.wallet_holo_blue_light));
                    finish();
                }
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(android.R.id.home==item.getItemId()){

            Intent backhome=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(backhome);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }
}
