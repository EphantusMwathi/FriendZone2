package com.gpslocation.friendzone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.helper.SessionManager;

/**
 * Created by mwathi on 2/19/2016.
 */
public class MenuPage extends Fragment {


private SessionManager session;
    private Button sql_btn;
private SQLiteHandler db;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.menu_layout,container,false);


sql_btn=(Button)v.findViewById(R.id.clear_sql);
        sql_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db=new SQLiteHandler(getActivity());
                db.deleteFriend();
            }
        });
        return v;
    }




}
