package it.di.unipi.sam.noisyscanner.database;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordingDAO {
    @Query("SELECT rId, strftime('%d-%m-%Y %H:%M', timestamp) AS timestamp, decibel, city " +
            "FROM Recording ORDER BY Recording.timestamp DESC LIMIT :limit")
    List<Recording> getRecentRecordings(int limit);

    @Query("SELECT strftime('%d-%m-%Y %H:%M', timestamp) AS dayhour, city, MAX(decibel) AS decibel FROM Recording")
    LoudestCity getLoudestDay();

    @Query("INSERT INTO Recording (decibel, city, timestamp) VALUES (:decibel, :city, :timestamp)")
    void insertRecording(double decibel, String city, String timestamp);

    @Query("SELECT strftime('%m', timestamp) AS value, AVG(decibel) AS decibel FROM Recording " +
            "WHERE strftime('%Y', timestamp) = :year GROUP BY value")
    List<Result> getAvgPerMonth(String year);

    @Query("SELECT strftime('%Y', timestamp) AS _id FROM Recording GROUP BY _id ORDER BY _id DESC")
    Cursor getAllYears();

    static class Result {
        public String value;
        public double decibel;
    }

    static class LoudestCity {
        public String dayhour;
        public String city;
        public double decibel;
    }
}