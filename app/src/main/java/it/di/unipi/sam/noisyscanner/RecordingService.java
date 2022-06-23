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

        RecordingBinder setOnStateChangedListener(OnStateChangedListener lst) {
            listener.add(lst);
            return this;
        }

        RecordingBinder setOnNewDataListener(OnNewDataListener dataListener) {
            newDataListener.add(dataListener);
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
        stop();
        return START_NOT_STICKY;
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
            thread = null;
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
            thread = null;
        }

        stopForeground(true);
        state = STOPPED; //Not recording

        for (OnStateChangedListener lst : listener)
            lst.onStateChanged(state);
    }
}