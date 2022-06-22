package it.di.unipi.sam.noisyscanner.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity
public class Recording {
    @PrimaryKey(autoGenerate = true)
    public int rId;

    public String timestamp;
    public double decibel;
    public String city;

}
