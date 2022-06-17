package it.di.unipi.sam.noisyscanner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.IOException;
import java.util.List;

import it.di.unipi.sam.noisyscanner.database.AppDatabase;

public class RecordingJob implements Runnable {
    private double decibelSum = 0;
    private int numSum = 0;
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final Geocoder geocoder;
    private final AppDatabase db;
    private final RecordingService.OnNewDataListener newDataListener;

    public RecordingJob(Context context, FusedLocationProviderClient fusedLocationClient, Geocoder geocoder, RecordingService.OnNewDataListener dataListener) {
        this.context = context;
        this.fusedLocationClient = fusedLocationClient;
        this.geocoder = geocoder;
        newDataListener = dataListener;

        db = AppDatabase.getDatabaseInstance(context);
    }

    @Override
    public void run() {
        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(context.getCacheDir().getAbsolutePath() + "/test.3gp");

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            int maxAmplitude = mediaRecorder.getMaxAmplitude();
            if (maxAmplitude != 0) {
                decibelSum += getDecibel(maxAmplitude);
                numSum++;

                Log.d("DB", decibelSum + "");
            }


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                maxAmplitude = mediaRecorder.getMaxAmplitude();
                if (maxAmplitude != 0) {
                    decibelSum += getDecibel(maxAmplitude);
                    numSum++;

                    Log.d("DB", decibelSum + "");
                }

                Log.d("REC", "Interrupted");
                break;
            }
        }

        mediaRecorder.stop();

        mediaRecorder.release();
        mediaRecorder = null;

        double avgDecibels = 0;
        if (decibelSum != 0) {
            avgDecibels = decibelSum / numSum;
            Log.d("Db AVG", avgDecibels + "");
            avgDecibels = (double) Math.round(avgDecibels * 100d) / 100d;
            Log.d("Db AVG rounded", avgDecibels + "");
        } else {
            //TODO Show Dialog che non è stato rilevato nulla
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            addRecWithNoLocation(avgDecibels);
        } else {
            double finalAvgDecibels = avgDecibels;

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                String registeredLocation = null;
                Log.d("LOC", location.toString());

                List<Address> addressList = null;
                if (geocoder != null) {
                    try {
                        addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        Log.d("GEO", address.getLocality());
                        registeredLocation = address.getLocality();
                    }
                } else {
                    registeredLocation = location.getLatitude() + " " + location.getLongitude();
                }

                String finalRegisteredLocation = registeredLocation;
                new Thread(() -> db.recordingDAO().insertRecording(finalAvgDecibels, finalRegisteredLocation)
                ).start();

                newDataListener.onNewData(finalAvgDecibels, "12-06-2022 12:20:22", finalRegisteredLocation); //TODO Aggiungere il timestamp
            });
        }
    }

    private double getDecibel(int maxAmplitude) {
        Log.d("AMPL", maxAmplitude + "");

        //Qui ci vorrebbe la calibrazione con uno strumento esterno
        return  20 * Math.log10((double) Math.abs(maxAmplitude));
    }

    private void addRecWithNoLocation(double avgDecibels) {
        db.recordingDAO().insertRecording(avgDecibels, (String)context.getText(R.string.location_not_available));
        newDataListener.onNewData(avgDecibels, "TimeStamp", (String) context.getText(R.string.location_not_available)); //TODO Aggiungere il timestamp
    }
}
