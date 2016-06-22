package com.gpslocation.friendzone.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import android.widget.Toolbar;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.helper.SessionManager;

/**
 * Created by mwathi on 5/19/2016.
 */
public class HangoutPage extends ActionBarActivity {

private Toolbar toolbar;
    private TextView name,email,location;
    private Button change_infor,change_location;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangout_page_layout);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        name=(TextView)findViewById(R.id.name);
        email=(TextView)findViewById(R.id.email);
        location=(TextView)findViewById(R.id.location);
        change_infor=(Button)findViewById(R.id.change_information);
        change_location=(Button)findViewById(R.id.change_location);
        session=new SessionManager(this);
        int id=session.getId();

        //fetchHangout(id);

        change_infor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        change_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }




}
