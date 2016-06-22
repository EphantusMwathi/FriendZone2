package com.gpslocation.friendzone.gcm;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;
import com.gpslocation.friendzone.activity.ChatRoomActivity;
import com.gpslocation.friendzone.activity.Invitation;
import com.gpslocation.friendzone.activity.MainActivity;
import com.gpslocation.friendzone.activity.Map;
import com.gpslocation.friendzone.activity.MessagesPage;
import com.gpslocation.friendzone.activity.NotificationPage;
import com.gpslocation.friendzone.app.AppConfig;
import com.gpslocation.friendzone.app.AppController;
import com.gpslocation.friendzone.helper.SQLiteHandler;
import com.gpslocation.friendzone.model.Invite;
import com.gpslocation.friendzone.model.Message;
import com.gpslocation.friendzone.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by mwathi on 3/23/2016.
 */
public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private String logo="FriendZone2016";

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        String title = bundle.getString("title");
       // String message = bundle.getString("message");
        //String image = bundle.getString("image");


        String flag = bundle.getString("flag");
        String data = bundle.getString("data");
        String timestamp = bundle.getString("created_at");

        Boolean isBackground=Boolean.valueOf(bundle.getString("is_background"));

        Log.e(TAG, "From: " + from);
        Log.e(TAG, "Title: " + title);
        //Log.e(TAG, "message: " + message);
       // Log.e(TAG, "image: " + image);
        Log.e(TAG, "timestamp: " + timestamp);


        if (flag == null)
            return;
        SQLiteHandler db=new SQLiteHandler(this);

        HashMap<String,String>user=db.getUserDetails();
        if(user== null){
            // user is not logged in, skipping push notification
            Log.e(TAG, "user is not logged in, skipping push notification");
            return;
        }



       /* HashMap<String,String>friend_details=db.getFriendDetails(title);
        String name1=friend_details.get("name1");
        String name2=friend_details.get("name2");
        String secondname=name1+" "+name2;*/

        switch (Integer.parseInt(flag)) {
            case AppConfig.PUSH_TYPE_USER:
                // push notification is specific to user
                processUserMessage(logo, isBackground, data);
                break;
            case AppConfig.PUSH_TYPE_INVITE:
                processUserinvite(logo, isBackground, data);
                break;
            case AppConfig.PUSH_TYPE_NOTIFICATION:
                processUserNotification(logo, isBackground, data);
                break;

            case AppConfig.PUSH_TYPE_POKE:
                processUserPoke(logo, isBackground, data);
                break;
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }


    private void processUserMessage(String title, boolean isBackground, String data) {
        if (!isBackground) {

            try {
                JSONObject datObj = new JSONObject(data);

                String imageUrl = datObj.getString("image");

                JSONObject mObj = datObj.getJSONObject("message");
                Message message = new Message();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getString("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));
                int chatRoomId= mObj.getInt("chat_room_id");


                //String chat=String.valueOf(chatRoomId);

                JSONObject uObj = datObj.getJSONObject("user");
                User user = new User();
                String fid=uObj.getString("user_id");
                String mail=uObj.getString("email");
                String fname=uObj.getString("fname");
                String sname=uObj.getString("sname");
                String name=fname+" "+sname;



                user.setId(fid);
                user.setEmail(mail);
                user.setName(name);
                message.setUser(user);

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", AppConfig.PUSH_TYPE_USER);
                    pushNotification.putExtra("message", message);
                   // pushNotification.putExtra("chat_room_id", chat);
                    pushNotification.putExtra("friend_id",fid);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);



                    Intent pushNotification2 = new Intent(AppConfig.PUSH_NOTIFICATION);
                     pushNotification2.putExtra("type", AppConfig.PUSH_TYPE_CHATROOM);
                    pushNotification2.putExtra("message", message);
                    // pushNotification.putExtra("chat_room_id", chat);
                    pushNotification2.putExtra("chat_room_id",chatRoomId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification2);
                    // play notification sound
                    //NotificationUtils notificationUtils = new NotificationUtils(this);
                    //notificationUtils.playNotificationSound();




                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    resultIntent.putExtra("name",name);
                    resultIntent.putExtra("friendID", fid);

                    // check for push notification image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(),title , "Message: "+user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                    } else {
                        // push notification contains image
                        // show it with the image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message.getMessage(), message.getCreatedAt(), resultIntent, imageUrl);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }


    private void processUserinvite(String title,boolean isBackground,String data){

        if(!isBackground){

//            Toast.makeText(getApplicationContext(),"New Invite",Toast.LENGTH_LONG).show();
            try{


                //Toast.makeText(getApplicationContext(),"New Invite",Toast.LENGTH_LONG).show();
                JSONObject obj=new JSONObject(data);

                JSONObject userObj=obj.getJSONObject("user");
                String created_at=obj.getString("created_at");
                int invite_id=obj.getInt("invite_id");

                String fname=userObj.getString("fname");
                String sname=userObj.getString("sname");
                String name=fname+" "+sname;
                String email=userObj.getString("email");
                String fid= userObj.getString("user_id");

String mess="Invite: "+name;
                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {


                    Invite invite=new Invite();
                    invite.setName(name);
                    invite.setEmail(email);
                    invite.setInvite_id(invite_id);
                    invite.setCreated_at(created_at);
                    //Toast.makeText(getApplicationContext(),"New Invite",Toast.LENGTH_LONG).show();

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", AppConfig.PUSH_TYPE_INVITE);
                    pushNotification.putExtra("invite",invite);
                    // pushNotification.putExtra("chat_room_id", chatRoomId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    //NotificationUtils notificationUtils = new NotificationUtils();
                    //notificationUtils.playNotificationSound();
                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(),MainActivity.class);

                    showNotificationMessage(getApplicationContext(), title, mess, created_at, resultIntent);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{



        }


    }


    private void processUserNotification(String title,boolean isBackground,String data){

        if(!isBackground){

            try{

                JSONObject obj=new JSONObject(data);
               // String mess=obj.getString("message");
                JSONObject userObject=obj.getJSONObject("user");
                String fname=userObject.getString("fname");
                String sname=userObject.getString("sname");
                String name=fname+" "+sname;
                int notification_id=obj.getInt("notification_id");
                String fid=userObject.getString("user_id");
                double distance=obj.getDouble("distance");
                String Dstring=Double.toString(distance);
                String created_at=obj.getString("created_at");
                //String message_id=obj.getString("message_id");

                //String message=fname+" "+sname+" "+mess;
                String mess="Notification: "+name+" is "+Dstring+" close to you";


                com.gpslocation.friendzone.model.Notification notification=new com.gpslocation.friendzone.model.Notification();
                notification.setTitle(logo);
                notification.setNotification(mess);
                notification.setFriend_id(fid);
                notification.setCreated_at(created_at);
                notification.setNotification_id(notification_id);



                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", AppConfig.PUSH_TYPE_NOTIFICATION);
                    pushNotification.putExtra("notification",notification);
                    //pushNotification.putExtra("message", mess);
                   // pushNotification.putExtra("chat_room_id", chatRoomId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                   // NotificationUtils notificationUtils = new NotificationUtils();
                    //notificationUtils.playNotificationSound();
                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(),MainActivity.class);

                    showNotificationMessage(getApplicationContext(), title, mess, created_at, resultIntent);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{



        }


    }

    private  void processUserPoke(String title,boolean isBackground,String data) {

        if (!isBackground) {
            try {
                JSONObject jObject = new JSONObject(data);
                String message1 = jObject.getString("message");
                JSONObject uObject = jObject.getJSONObject("user");
                String fname = uObject.getString("fname");
                String sname = uObject.getString("sname");
                String fid=uObject.getString("user_id");
                String name = fname + " " + sname;

                String message2 = name + " " + message1;

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(AppConfig.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", AppConfig.PUSH_TYPE_POKE);
                    pushNotification.putExtra("message1", message2);
                    pushNotification.putExtra("fid",fid);

                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    //NotificationUtils notificationUtils = new NotificationUtils();
                    //notificationUtils.playNotificationSound();
                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(),Map.class);

                    showNotificationMessage(getApplicationContext(), title, message2, null, resultIntent);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}
