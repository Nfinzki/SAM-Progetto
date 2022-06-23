package it.di.unipi.sam.noisyscanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class RecordingService extends Service {
    public static final int STOPPED = 0;
    public static final int RECORDING = 1;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private final IBinder binder = new RecordingBinder();
    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;

    private int state = STOPPED; //Not recording
    private Thread thread = null;
    private final List<OnStateChangedListener> listener = new ArrayList<>();
    private final List<OnNewDataListener> newDataListener = new ArrayList<>();

    private final int notificationId = 1;

    public class RecordingBinder extends Binder {

        void startRecording(Context context) {

            Notification notification = builder.build();

            startForeground(notificationId, notification);

            thread = new Thread(new RecordingJob(context, fusedLocationClient, geocoder, newDataListener));
            thread.start();
            state = RECORDING; //Recording

            for (OnStateChangedListener lst : listener)
                lst.onStateChanged(state);
        }

        void stopRecording() {
            stop();
        }

        RecordingBinder setOnStateChangedListner(OnStateChangedListener lst) {
            listener.add(lst);
            return this;
        }

        RecordingBinder setOnNewDataListener(OnNewDataListener dataListner) {
            newDataListener.add(dataListner);
            return this;
        }

        int getState() {
            return state;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), RecordingService.class);
        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);

        builder = new NotificationCompat.Builder(this, "MyChannel")
                .setSmallIcon(R.drawable.ic_microphone)
                .setContentTitle(getText(R.string.notification_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(this, 3, activityIntent, 0))
                .addAction(R.drawable.ic_stop, getText(R.string.stop), PendingIntent.getService(this, 2, intent, 0))
                .setOngoing(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        geocoder = Geocoder.isPresent() ? new Geocoder(this) : null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NOTIF_STOP", "Calling stop");
        stop();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("SERVICE", "onBind");
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("REC_SERVICE", "Inside onDestroy");

        if (thread != null) {
            thread.interrupt();
            state = STOPPED;

            for (OnStateChangedListener lst : listener)
                lst.onStateChanged(state);
        }

        notificationManager.cancel(notificationId);
    }

    public interface OnStateChangedListener {
        void onStateChanged(int state);
    }

    public interface OnNewDataListener {
        void onNewData(double decibel, String timestamp, String city);

        void onFail();
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }

        stopForeground(true);
        state = STOPPED; //Not recording

        for (OnStateChangedListener lst : listener)
            lst.onStateChanged(state);
    }
}