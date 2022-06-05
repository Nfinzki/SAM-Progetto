package it.di.unipi.sam.noisyscanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.RecordViewHolder> {
    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_recording_item, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        //TODO Implementare
        holder.city.setText("Pisa");
        holder.timestamp.setText("13:20");
        holder.decibel.setText("82 Db");
    }

    @Override
    public int getItemCount() {
        return 20; //TODO Modificare
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView city;
        TextView timestamp;
        TextView decibel;

        RecordViewHolder(View itemView) {
            super(itemView);

            card = (MaterialCardView) itemView.findViewById(R.id.recording_item);
            city = (TextView) itemView.findViewById(R.id.city);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            decibel = (TextView) itemView.findViewById(R.id.decibel);
        }
    }
}
