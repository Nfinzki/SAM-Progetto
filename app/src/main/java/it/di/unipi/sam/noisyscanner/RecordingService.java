package it.di.unipi.sam.noisyscanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class RecordingService extends Service {
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(this, "MyChannel")
                .setSmallIcon(R.drawable.ic_microphone)
                .setContentTitle(getText(R.string.notification_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("SERVICE", "Inside onStartcommand");

        Notification notification = builder.build();
        Log.d("NOTIF", notification.toString());

        notificationManager.notify(1, notification);

        Thread thread = new Thread(new RecordingJob(this));
        thread.start();

        return START_NOT_STICKY; //TODO Modificare
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}