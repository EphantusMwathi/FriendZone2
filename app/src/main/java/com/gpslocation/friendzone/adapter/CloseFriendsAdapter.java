package com.gpslocation.friendzone.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.activity.ListOfCloseFriends;
import com.gpslocation.friendzone.model.Close;
import com.gpslocation.friendzone.model.Invite;

import java.util.List;

/**
 * Created by mwathi on 5/14/2016.
 */
public class CloseFriendsAdapter extends BaseAdapter {


    private TextView name,email,timestamp,distance;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Close> closeArrayList;
    private String[] bgColors;





    public CloseFriendsAdapter(Activity activity, List<Close> closeArrayList) {
        this.activity = activity;
        this.closeArrayList = closeArrayList;

    }



    @Override
    public int getCount() {
        return closeArrayList.size();
    }

    @Override
    public Object getItem(int location) {
        return closeArrayList.get(location);
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
            convertView = inflater.inflate(R.layout.list_close_row_layout, null);

        name= (TextView) convertView.findViewById(R.id.name);
        email= (TextView) convertView.findViewById(R.id.email);
        timestamp=(TextView)convertView.findViewById(R.id.timestamp);
        distance=(TextView)convertView.findViewById(R.id.distance);


        name.setText(closeArrayList.get(position).getName());
        timestamp.setText(String.valueOf(closeArrayList.get(position).getCreated_at()));
        email.setText(closeArrayList.get(position).getEmail());
        distance.setText(String.valueOf(closeArrayList.get(position).getDistance()));
        return convertView;
    }
}
