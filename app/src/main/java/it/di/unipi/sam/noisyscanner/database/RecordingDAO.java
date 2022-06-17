package it.di.unipi.sam.noisyscanner.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordingDAO {
    @Query("SELECT * FROM Recording ORDER BY timestamp DESC LIMIT :limit")
    List<Recording> getRecentRecordings(int limit);

    @Query("SELECT city AS value, max(decibel) AS maxDecibel FROM Recording")
    Result getLoudestCity();

    @Query("SELECT strftime('%m', timestamp) AS value, max(decibel) AS maxDecibel FROM Recording")
    Result getLoudestMonth();

    @Query("SELECT strftime('%d-%m-%Y', timestamp) AS value, MAX(decibel) AS maxDecibel FROM Recording")
    Result getLoudestDay();

    @Query("SELECT strftime('%H', timestamp) AS value, MAX(decibel) AS maxDecibel FROM Recording")
    Result getLoudestHour();

    @Query("INSERT INTO Recording (decibel, city) VALUES (:decibel, :city)")
    void insertRecording(double decibel, String city);

    static class Result {
        public String value;
        public double maxDecibel;
    }
}