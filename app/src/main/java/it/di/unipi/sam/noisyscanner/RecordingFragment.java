package it.di.unipi.sam.noisyscanner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecordingFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.micButton);
        button.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recent_recordings);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new RecordingAdapter());
    }

    @Override
    public void onClick(View view) {
        FloatingActionButton button = (FloatingActionButton) view;

        if (button.getTag().equals("rec")) {
            button.setImageResource(R.drawable.ic_stop);
            button.setTag("stop");
        } else {
            button.setImageResource(R.drawable.ic_microphone);
            button.setTag("rec");
        }
    }
}