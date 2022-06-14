package it.di.unipi.sam.noisyscanner.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordingDAO {
    @Query("SELECT * FROM Recording ORDER BY timestamp DESC LIMIT :limit")
    List<Recording> getRecentRecordings(int limit);

    @Query("SELECT city FROM Recording " +
            "GROUP BY city HAVING MAX(decibel)")
    String getLoudestCity();

    @Query("SELECT strftime('%m', timestamp) AS month FROM Recording GROUP BY month HAVING MAX(Recording.decibel)")
    String getLoudestMonth();

    @Query("SELECT strftime('%d-%m-%Y', timestamp) as day FROM Recording GROUP BY day HAVING MAX(Recording.decibel)")
    String getLoudestDay();

    @Query("SELECT strftime('%H', timestamp) as hour FROM Recording GROUP BY hour HAVING MAX(Recording.decibel)")
    String getLoudestHour();

    @Query("INSERT INTO Recording (decibel, city) VALUES (:decibel, :city)")
    void insertRecording(double decibel, String city);
}
