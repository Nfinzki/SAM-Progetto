package it.di.unipi.sam.noisyscanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ResultDialog extends DialogFragment {
    private final String decibel;
    private final String timestamp;
    private final String city;

    public ResultDialog(double decibel, String timestamp, String city) {
        this.decibel = decibel + "";
        this.timestamp = timestamp;
        this.city = city;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.result_dialog, container, false);
        getDialog().setTitle("Dialog di prova");

        TextView decibelText = (TextView) v.findViewById(R.id.registeredDecibel);
        TextView cityText = (TextView) v.findViewById(R.id.registeredCity);
        TextView timestampText = (TextView) v.findViewById(R.id.registeredTimestamp);

        decibelText.setText(decibel);
        cityText.setText(city);
        timestampText.setText(timestamp);
        return v;
    }
}
