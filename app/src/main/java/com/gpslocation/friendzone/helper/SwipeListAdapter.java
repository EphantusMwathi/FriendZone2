package com.gpslocation.friendzone.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gpslocation.friendzone.R;
import com.gpslocation.friendzone.activity.Friend;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by mwathi on 3/7/2016.
 */
public class SwipeListAdapter extends BaseAdapter {
private TextView name,email,id,distance;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Friend> friendList;
    private String[] bgColors;

    public SwipeListAdapter(Activity activity, List<Friend> friendList) {
        this.activity = activity;
        this.friendList = friendList;

    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int location) {
        return friendList.get(location);
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
            convertView = inflater.inflate(R.layout.contactrowlayout, null);

         name= (TextView) convertView.findViewById(R.id.json_name);
         email= (TextView) convertView.findViewById(R.id.json_email);
        distance=(TextView)convertView.findViewById(R.id.json_distance);
        id=(TextView)convertView.findViewById(R.id.json_id);

        id.setText(String.valueOf(friendList.get(position).id));
        name.setText(friendList.get(position).name);
      distance.setText(friendList.get(position).distance);
        email.setText(friendList.get(position).email);


        //String color = bgColors[position % bgColors.length];
        //serial.setBackgroundColor(Color.parseColor(color));

        return convertView;
    }

}
