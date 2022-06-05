package it.di.unipi.sam.noisyscanner.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface RecordingDAO {
    @Query("SELECT Recording.decibel AS decibel, Recording.timestamp AS timestamp, City.city AS city " +
            "FROM Recording INNER JOIN City ON Recording.cityId = City.cId LIMIT :limit")
    List<PositionRecording> getRecentRecordings(int limit);

    @Query("SELECT City.city FROM City INNER JOIN Recording ON City.cId = Recording.cityId " +
            "GROUP BY City.city HAVING MAX(Recording.decibel)")
    String getLoudestCity();

    @Query("SELECT strftime('%m', timestamp) AS month FROM Recording GROUP BY month HAVING MAX(Recording.decibel)")
    String getLoudestMonth();

    @Query("SELECT strftime('%Y', timestamp) as day FROM Recording GROUP BY day HAVING MAX(Recording.decibel)")
    String getLoudestDay();

    @Query("SELECT strftime('%M', timestamp) as hour FROM Recording GROUP BY hour HAVING MAX(Recording.decibel)")
    String getLoudestHour();

    static class PositionRecording {
        public String city;
        public float decibel;
        public LocalDateTime timestamp;
    }
}
