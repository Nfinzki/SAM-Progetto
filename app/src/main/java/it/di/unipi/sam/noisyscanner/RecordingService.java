package it.di.unipi.sam.noisyscanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;

import androidx.core.app.NotificationCompat;

public class RecordingService extends Service {
    public static final int STOPPED = 0;
    public static final int RECORDING = 1;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private final IBinder binder = new RecordingBinder();
    private int state = STOPPED; //Not recording
    private Thread thread = null;
    private OnStateChangedListener listener;
    private View view;

    private final int notificationId = 1;

    public class RecordingBinder extends Binder {

        void startRecording(View v, OnStateChangedListener lst) {
            view = v;
            listener = lst;

            Context context = view.getContext();
            Notification notification = builder.build();

            notificationManager.notify(notificationId, notification);

            thread = new Thread(new RecordingJob(context));
            thread.start();
            state = RECORDING; //Recording
            listener.onStateChanged(view, state);
        }

        void stopRecording() {
            thread.interrupt();
            notificationManager.cancel(notificationId);
            state = STOPPED; //Not recording
            listener.onStateChanged(view, state);
        }

        int getState() {
            return state;
        }
    }

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
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (thread != null) {
            thread.interrupt();
            state = STOPPED;
            listener.onStateChanged(view, state);
        }

        notificationManager.cancel(notificationId);
    }

    public interface OnStateChangedListener {
        void onStateChanged(View view, int state);
    }
}