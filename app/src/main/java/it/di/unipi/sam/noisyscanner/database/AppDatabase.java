package it.di.unipi.sam.noisyscanner.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Recording.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecordingDAO recordingDAO();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabaseInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "Recording_DB")
                    .build();
        }

        return INSTANCE;
    }
}
