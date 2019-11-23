package com.pro.firebasepro.googlemap;

import android.app.IntentService;
import android.content.Intent;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.pro.firebasepro.R;


import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class GeofencingPendingIntentService extends IntentService {

    private final String channelID = "my_channel_id";
    public GeofencingPendingIntentService() {

        super("GeofencingPendingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        int transitionType = event.getGeofenceTransition();
        String transitionString = "";
        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                transitionString = "entered";
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                transitionString = "exited";
                break;
        }

        List<Geofence>geofenceList = event.getTriggeringGeofences();
        List<String> triggeringGeofenceIds = new ArrayList<>();

        for(Geofence g: geofenceList){
            triggeringGeofenceIds.add(g.getRequestId());
        }

        String notificationString = "You have "+transitionString+" "+
                TextUtils.join(", ",triggeringGeofenceIds);
        sendNotifications(notificationString);
    }

    private void sendNotifications(String notificationString) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelID);
        builder.setContentTitle("Geofence Detected");
        builder.setContentText(notificationString);
        builder.setSmallIcon(R.drawable.ic_notifications_active_black_24dp);
        builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "geofencing";
            String description = "something";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            manager.createNotificationChannel(channel);
        }

        manager.notify(55, builder.build());
    }

}

