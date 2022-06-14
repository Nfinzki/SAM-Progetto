package it.di.unipi.sam.noisyscanner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecordingFragment extends Fragment implements View.OnClickListener, RecordingService.OnStateChangedListener {
    private RecordingService.RecordingBinder recordingService = null;
    private final ServiceConnection recordingConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            recordingService = (RecordingService.RecordingBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            recordingService = null;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Context context = view.getContext();
        context.bindService(new Intent(context, RecordingService.class), recordingConnection, Context.BIND_AUTO_CREATE);

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

        if (recordingService != null) {
            switch (recordingService.getState()) {
                case RecordingService.STOPPED:
                    recordingService.startRecording(view, this);
                    break;
                case RecordingService.RECORDING:
                    recordingService.stopRecording();
            }
        }
    }

    @Override
    public void onStateChanged(View view, int state) {
        FloatingActionButton button = (FloatingActionButton)view.findViewById(R.id.micButton);

        switch (state) {
            case RecordingService.STOPPED:
                button.setImageResource(R.drawable.ic_microphone);
                break;
            case RecordingService.RECORDING:
                button.setImageResource(R.drawable.ic_stop);
        }
    }
}