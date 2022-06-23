package it.di.unipi.sam.noisyscanner.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Recording {
    @PrimaryKey(autoGenerate = true)
    public int rId;

    public String timestamp;
    public double decibel;
    public String city;

}
