package com.gpslocation.friendzone.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.model.ChatRoom;
import com.gpslocation.friendzone.model.Invite;
import com.gpslocation.friendzone.model.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by mwathi on 4/26/2016.
 */
public class InviteAdapter extends BaseAdapter {
    private TextView name,email,timestamp,invite_id;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Invite> inviteArrayList;
    private String[] bgColors;





    public InviteAdapter(Activity activity, List<Invite> inviteArrayList) {
        this.activity = activity;
        this.inviteArrayList = inviteArrayList;

    }

    @Override
    public int getCount() {
        return inviteArrayList.size();
    }

    @Override
    public Object getItem(int location) {
        return inviteArrayList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {




        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.invite_row_list, null);

        name= (TextView) convertView.findViewById(R.id.name);
        email= (TextView) convertView.findViewById(R.id.email);
        timestamp=(TextView)convertView.findViewById(R.id.timestamp);
        invite_id=(TextView)convertView.findViewById(R.id.invite_id);

        invite_id.setText(String.valueOf(inviteArrayList.get(position).getInvite_id()));
        name.setText(inviteArrayList.get(position).getName());
        timestamp.setText(String.valueOf(inviteArrayList.get(position).getCreated_at()));
        email.setText(inviteArrayList.get(position).getEmail());
        return convertView;
    }
}
