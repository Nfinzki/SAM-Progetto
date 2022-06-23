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

            recordingService.setOnNewDataListener(RecordingFragment.this)
                    .setOnStateChangedListener(RecordingFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            recordingService = null;
        }
    };

    private static RecordingFragment INSTANCE = null;

    private final int maxLastRecordings = 20;
    private FloatingActionButton button = null;

    public static Fragment getInstance() {
        if (INSTANCE == null)
            INSTANCE = new RecordingFragment();

        return INSTANCE;
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

        button = (FloatingActionButton) view.findViewById(R.id.micButton);
        if (recordingService != null) {
            if (recordingService.getState() == RecordingService.RECORDING)
                button.setImageResource(R.drawable.ic_stop);
            else
                button.setImageResource(R.drawable.ic_microphone);
        }
        button.setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recent_recordings);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        RecordingAdapter adapter = new RecordingAdapter();
        recyclerView.setAdapter(adapter);

        refreshData(context, recyclerView, adapter);
    }

    @Override
    public void onClick(View view) {
        if (recordingService == null) return;

        switch (recordingService.getState()) {
            case RecordingService.STOPPED:
                recordingService.startRecording(view.getContext());
                break;
            case RecordingService.RECORDING:
                recordingService.stopRecording();
        }
    }

    @Override
    public void onStateChanged(int state) {
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
        if (getActivity() == null) return;

        if (((MainActivity)getActivity()).getVisibility()) {
            ResultDialog resultDialog = new ResultDialog(decibel, timestamp, city);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            resultDialog.show(fm, "Result Dialog");
        }

        RecyclerView rv = requireView().findViewById(R.id.recent_recordings);
        RecordingAdapter ra = (RecordingAdapter) rv.getAdapter();

        refreshData(getContext(), rv, ra);
    }

    @Override
    public void onFail() {
        if (getActivity() == null) return;

        if (((MainActivity)getActivity()).getVisibility()) {
            NoAudioDialog noAudioDialog = new NoAudioDialog();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            noAudioDialog.show(fm, "No Audio");
        }
    }

    private void refreshData(Context context, RecyclerView recyclerView, RecordingAdapter adapter) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabaseInstance(context);
            List<Recording> recordings = db.recordingDAO().getRecentRecordings(maxLastRecordings);

            recyclerView.post(() -> {
                if (adapter == null) return;

                adapter.setRecordings(recordings);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }
}