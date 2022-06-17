package it.di.unipi.sam.noisyscanner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import it.di.unipi.sam.noisyscanner.database.AppDatabase;
import it.di.unipi.sam.noisyscanner.database.Recording;

public class RecordingFragment extends Fragment implements View.OnClickListener,
        RecordingService.OnStateChangedListener, RecordingService.OnNewDataListener {

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
    private final int maxLastRecordings = 20;

    public static Fragment newInstance() {
        RecordingFragment rf = new RecordingFragment();
        return rf;
    }

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
        RecordingAdapter adapter = new RecordingAdapter();
        recyclerView.setAdapter(adapter);
        new Thread(() -> {
            List<Recording> recordings = AppDatabase.getDatabaseInstance(context).recordingDAO().getRecentRecordings(maxLastRecordings);
            adapter.setRecordings(recordings);
            recyclerView.post(() -> adapter.notifyDataSetChanged());
        }).start();
    }

    @Override
    public void onClick(View view) {
        if (recordingService != null) {
            switch (recordingService.getState()) {
                case RecordingService.STOPPED:
                    recordingService.startRecording(view, this, this);
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

    @Override
    public void onNewData(double decibel, String timestamp, String city) {
        FragmentManager fm = requireActivity().getSupportFragmentManager();

        if (((MainActivity)getActivity()).getVisibility()) {
            ResultDialog resultDialog = new ResultDialog(decibel, timestamp, city);
            resultDialog.show(fm, "Result Dialog");
        }

        RecyclerView rv = requireView().findViewById(R.id.recent_recordings);
        RecordingAdapter ra = (RecordingAdapter) rv.getAdapter();
        new Thread(() -> {
            List<Recording> recordings = AppDatabase.getDatabaseInstance(getContext()).recordingDAO().getRecentRecordings(maxLastRecordings);

            rv.post(() -> {
                if (ra != null) {
                    ra.setRecordings(recordings);
                    ra.notifyDataSetChanged();
                }
            });
        }).start();

    }
}