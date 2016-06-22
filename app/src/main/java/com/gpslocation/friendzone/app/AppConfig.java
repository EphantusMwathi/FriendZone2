package com.gpslocation.friendzone.app;

/**
 * Created by mwathi on 2/11/2016.
 */
public class AppConfig {
    public static  String BASE="http://192.168.43.76/friendzone/android/";
    public static String URL_LOGIN =BASE+"Login.php";

    // Server user register url

    public static String URL_HANGOUTLOGIN=BASE+"hangoutLogin.php";

    public static String URL_UPDATEDATABASE=BASE+"updateDatabase.php";

    public static String URL_UNBLOCK=BASE+"unblock.php";
    public static String URL_BLOCK=BASE+"block.php";
    public static String URL_PROFILE=BASE+"profile.php";
    public static String URL_REGISTER=BASE+"Register.php";
    public static String URL_LOCATION=BASE+"coordinates.php";
    public static String URL_DISPLAY=BASE+"androidDisplayInformation.php";
    public static String URL_ADDFRIEND=BASE+"addfriend.php";
    public static String URL_MAP=BASE+"getfriendLocation.php";
    public static String URL_EDIT=BASE+"edit.php";
    public static String URL_INVITEDETAILS=BASE+"inviteDetails.php";
    public static String URL_ACCEPTINVITE=BASE+"acceptInvite.php";
    public static String URL_DENYINVITE=BASE+"denyInvite.php";
    public static String URL_PROFILEDISTANCE=BASE+"profileDistance.php";
    public static String URL_MAPDISTANCE=BASE+"mapDistance.php";
    public static String URL_MAPPOKE=BASE+"mapPoke.php";
    public static String URL_HANGOUTREGISTER=BASE+"hangoutRegister.php";
    public static String URL_UNFRIEND=BASE+"unfriend.php";

    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;
    public static  final int PUSH_TYPE_INVITE=3;
    public static  final int PUSH_TYPE_NOTIFICATION=4;
    public static  final int PUSH_TYPE_POKE=5;

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

}

