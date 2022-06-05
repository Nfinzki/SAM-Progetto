package it.di.unipi.sam.noisyscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity implements ChipNavigationBar.OnItemSelectedListener {
    private ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chipNavigationBar = findViewById(R.id.navigation_bar);

        if (savedInstanceState == null) {
            Log.d("ON_CREATE", "savedInstanceState = null");

            chipNavigationBar.setItemSelected(R.id.recording, true);

            getSupportFragmentManager().beginTransaction()
                .replace(R.id.placeholder, new RecordingFragment())
                .commit();
        } else {
            Log.d("ON_CREATE", "savedInstanceState = " + savedInstanceState.get("ChipNavBar"));

            chipNavigationBar.setItemSelected(savedInstanceState.getInt("ChipNavBar"), true);

            Fragment fragment = null;
            switch (savedInstanceState.getInt("ChipNavBar")) {
                case R.id.recording:
                    fragment = new RecordingFragment();
                    break;
                case R.id.statistics:
                    fragment = new StatisticFragment();
                    break;
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.placeholder, fragment)
                        .commit();
            }
        }

        chipNavigationBar.setOnItemSelectedListener(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("ChipNavBar", chipNavigationBar.getSelectedItemId());
        Log.d("SAVE_STATE", "Saved: " + chipNavigationBar.getSelectedItemId());
    }

    @Override
    public void onItemSelected(int i) {
        Fragment fragment = null;

        switch (i) {
            case R.id.recording:
                fragment = new RecordingFragment();
                break;

            case R.id.statistics:
                fragment = new StatisticFragment();
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.placeholder, fragment)
                    .commit();
        }
    }
}