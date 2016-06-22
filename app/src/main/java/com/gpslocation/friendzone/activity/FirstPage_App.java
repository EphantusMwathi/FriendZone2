package com.gpslocation.friendzone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.helper.SessionManager;

/**
 * Created by mwathi on 3/23/2016.
 */



public class FirstPage_App extends AppCompatActivity {
    private SessionManager session;
    private Button login_button,register_button,hangout_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.first_page_layout);

        login_button=(Button)findViewById(R.id.bg_login);
        register_button=(Button)findViewById(R.id.bg_register);



        session=new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }




        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });



    }
}
