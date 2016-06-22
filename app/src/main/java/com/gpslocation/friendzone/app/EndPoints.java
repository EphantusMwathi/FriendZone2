package com.gpslocation.friendzone.app;

/**
 * Created by mwathi on 3/23/2016.
 */
public class EndPoints {


    public static final String BASE_URL ="http://192.168.43.76/friendzone/android/";
    //public static final String LOGIN = BASE_URL + "/user/login";
    //public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS =BASE_URL+"getChats.php";
    public static final String CHAT_THREAD =BASE_URL+"getChatRoom.php";;
    public static final String CHAT_ROOM_MESSAGE =BASE_URL+"sendMessage.php";
    public static final String NOTIFICATION =BASE_URL+"getNotification.php";
    public static final String INVITES=BASE_URL+"getInvites.php";
    public static final String MYCOORDINATES=BASE_URL+"getMyCoordinates.php";
    public static final String FRIENDCOORDINATES=BASE_URL+"getFriendCoordinates.php";
    public static final String CLOSELIST=BASE_URL+"getCloseList.php";
    public static final String FRIENDINFOR=BASE_URL+"getFriendInfor.php";
    public static final String GCMREG=BASE_URL+"gcmRegistration.php";
  //  public static final String MAPBLOCK=BASE_URL+"mapBlock.php";
    public static final String MAPUNBLOCK=BASE_URL+"mapUnblock.php";
}
