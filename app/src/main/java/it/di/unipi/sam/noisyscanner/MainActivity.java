package it.di.unipi.sam.noisyscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity implements ChipNavigationBar.OnItemSelectedListener {
    private ChipNavigationBar chipNavigationBar;
    private boolean visible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chipNavigationBar = findViewById(R.id.navigation_bar);
        chipNavigationBar.setOnItemSelectedListener(this);

        if (savedInstanceState == null)
            chipNavigationBar.setItemSelected(R.id.recording, true);
        else
            chipNavigationBar.setItemSelected(savedInstanceState.getInt("ChipNavBar"), true);

        createNotificationChannel();
        checkAndRequestPermissions();
    }

    @Override
    protected void onStop() {
        super.onStop();

        visible = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        visible = true;
    }

    public boolean getVisibility() {
        return visible;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("ChipNavBar", chipNavigationBar.getSelectedItemId());
        Log.d("SAVE_STATE", "Saved: " + chipNavigationBar.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if ((int) savedInstanceState.get("ChipNavBar") == R.id.recording)
            chipNavigationBar.setItemSelected(R.id.recording, true);
        else
            chipNavigationBar.setItemSelected(R.id.statistics, true);
    }

    @Override
    public void onItemSelected(int i) {
        Fragment fragment = null;

        switch (i) {
            case R.id.recording:
                fragment = RecordingFragment.getInstance();
                break;

            case R.id.statistics:
                fragment = StatisticFragment.getInstance();
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.placeholder, fragment)
                    .commit();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = getString(R.string.app_name);

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("MyChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }
}