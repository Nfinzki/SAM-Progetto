package it.di.unipi.sam.noisyscanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
        //recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new RecordingAdapter());
    }

    @Override
    public void onClick(View view) {
        FloatingActionButton button = (FloatingActionButton) view;
        Context context = view.getContext();

        if (button.getTag().equals("rec")) {
            button.setImageResource(R.drawable.ic_stop);
            button.setTag("stop");

            Intent i = new Intent(context, RecordingService.class);
            context.startService(i);
        } else {
            button.setImageResource(R.drawable.ic_microphone);
            button.setTag("rec");
        }
    }
}