package it.di.unipi.sam.noisyscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton micButton = (FloatingActionButton) findViewById(R.id.micButton);
        micButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Snackbar.make(view, "Prova", Snackbar.LENGTH_SHORT).show();
    }
}