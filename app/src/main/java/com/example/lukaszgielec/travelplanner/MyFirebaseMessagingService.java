package com.example.lukaszgielec.travelplanner;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        //TYPES
        final int TRIP_ADD_NOTIFY = 1;
        final int TRIP_REMOVE_NOTIFY = 2;
        final int PLACE_ADD_NOTIFY = 4;//
        final int PLACE_REMOVE_NOTIFY = 5;//

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        Map<String,String> data = remoteMessage.getData();

        String channelId = data.get("trip_id");

        Intent intent = new Intent(this, TripActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("trip_id",Integer.parseInt(data.get("trip_id")));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Integer.parseInt(data.get("trip_id")) /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);



        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_card_travel_black_48dp)
                .setContentTitle(data.get("trip_name") )
                .setContentText(data.get("msg"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);



        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        StatusBarNotification[] statusBarNotifications = notificationManager.getActiveNotifications();
        String bigTextMsg = data.get("msg");

        if (statusBarNotifications != null){

            for (int i = 0; i< statusBarNotifications.length; i++){

                Log.i("ids",""+statusBarNotifications[i].getId());

                if (statusBarNotifications[i].getId() == Integer.parseInt(data.get("trip_id"))){

                    Log.i("NOTIFY","powiadomienie o tym tripie było, dodany tekst");
                    Log.i("NOTIFY","ids "+statusBarNotifications[i].getId());
                    bigTextMsg += "\n" +statusBarNotifications[i].getTag();

                    notificationBuilder
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(bigTextMsg));


                    notificationManager.cancel(statusBarNotifications[i].getTag(),statusBarNotifications[i].getId());

                    break;
                }

            }

        }

        notificationManager.notify(bigTextMsg,Integer.parseInt(data.get("trip_id")), notificationBuilder.build());

    }

}