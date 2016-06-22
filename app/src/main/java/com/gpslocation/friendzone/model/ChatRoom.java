package com.gpslocation.friendzone.model;

import java.io.Serializable;

/**
 * Created by mwathi on 3/23/2016.
 */
public class ChatRoom  implements Serializable {


    String  name, lastMessage, timestamp,friendID;
    int unreadCount;
    int id;

    public ChatRoom() {
    }

    public ChatRoom(int id, String name, String lastMessage, String timestamp, int unreadCount) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public String getfriendID() {
        return friendID;
    }

    public void setfriendID(String friendID) {
        this.friendID = friendID;
    }
}

