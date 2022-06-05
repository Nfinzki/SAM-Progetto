package it.di.unipi.sam.noisyscanner.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class City {
    @PrimaryKey(autoGenerate = true)
    public int cId;

    public String city;
}
