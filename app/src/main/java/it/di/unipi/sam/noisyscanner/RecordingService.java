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
    private OnStateChangedListener listener;
    private View view;

    private final int notificationId = 1;

    public class RecordingBinder extends Binder {

        void startRecording(View v, OnStateChangedListener lst, OnNewDataListener dataListener) {
            view = v;
            listener = lst;

            Context context = view.getContext();
            Notification notification = builder.build();

            startForeground(notificationId, notification);

            thread = new Thread(new RecordingJob(context, fusedLocationClient, geocoder, dataListener));
            thread.start();
            state = RECORDING; //Recording
            listener.onStateChanged(view, state);
        }

        void stopRecording() {
            stop();
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

        builder = new NotificationCompat.Builder(this, "MyChannel") //TODO Aggiungere l'azione e il fatto che se si clicca parte la main activity
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
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("REC_SERVICE", "Inside onDestroy");

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

    public interface OnNewDataListener {
        void onNewData(double decibel, String timestamp, String city);
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }

        stopForeground(true);
        state = STOPPED; //Not recording
        listener.onStateChanged(view, state);
    }
}