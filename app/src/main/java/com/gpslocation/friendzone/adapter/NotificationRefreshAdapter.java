package com.gpslocation.friendzone.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.activity.Friend;
import com.gpslocation.friendzone.model.Notification;

import java.util.List;

/**
 * Created by mwathi on 5/7/2016.
 */
public class NotificationRefreshAdapter extends BaseAdapter {


    private TextView name,message,timestamp,notification_id;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Notification> notificationList;
    private String[] bgColors;

    public NotificationRefreshAdapter(Activity activity, List<Notification> notificationList) {
        this.activity = activity;
        this.notificationList = notificationList;

    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int location) {
        return notificationList.get(location);
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
            convertView = inflater.inflate(R.layout.notification_list_row, null);

        name= (TextView) convertView.findViewById(R.id.title);
        message= (TextView) convertView.findViewById(R.id.message);
        timestamp=(TextView)convertView.findViewById(R.id.timestamp);
        notification_id=(TextView)convertView.findViewById(R.id.unique_id);

        notification_id.setText(String.valueOf(notificationList.get(position).getFriend_id()));
        name.setText(notificationList.get(position).getTitle());
        timestamp.setText(String.valueOf(notificationList.get(position).getCreated_at()));
        message.setText(notificationList.get(position).getNotification());


        //String color = bgColors[position % bgColors.length];
        //serial.setBackgroundColor(Color.parseColor(color));

        return convertView;
    }

}
