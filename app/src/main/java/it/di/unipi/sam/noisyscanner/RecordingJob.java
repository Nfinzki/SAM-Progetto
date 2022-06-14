package it.di.unipi.sam.noisyscanner;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class RecordingJob implements Runnable {
    private double decibelSum = 0;
    private int numSum = 0;
    private final Context context;

    public RecordingJob(Context context) {
        this.context = context;
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

        while(!Thread.currentThread().isInterrupted()) {
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

        double avgDecibels = decibelSum / numSum;
        Log.d("Db AVG", avgDecibels + "");
        //TODO Insert into DB
    }

    private double getDecibel(int maxAmplitude) {
        Log.d("AMPL", maxAmplitude + "");

        //Qui ci vorrebbe la calibrazione con uno strumento esterno
        return  20 * Math.log10((double) Math.abs(maxAmplitude));
    }
}
