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

        //mediaRecorder.getMaxAmplitude();

        while(!Thread.currentThread().isInterrupted()) {
            int maxAmplitude = mediaRecorder.getMaxAmplitude();

            if (maxAmplitude != 0) {
                Log.d("AMPL", maxAmplitude + "");

                decibelSum += 20 * Math.log10((double)Math.abs(maxAmplitude));
                numSum++;

                Log.d("DB", decibelSum + "");

                if (numSum >= 5) break;
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                //TODO Ultima lettura
                double avgDecibels = decibelSum / numSum;
                //TODO Insert into DB

                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

            }
        }

        double avgDecibels = decibelSum / numSum;
        Log.d("Db AVG", avgDecibels + "");

        mediaRecorder.stop();

        mediaRecorder.release();
        mediaRecorder = null;
    }
}
