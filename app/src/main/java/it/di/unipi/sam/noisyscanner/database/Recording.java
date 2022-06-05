package it.di.unipi.sam.noisyscanner.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(foreignKeys = @ForeignKey(entity=City.class, parentColumns = "cId", childColumns = "cityId"))
public class Recording {
    @PrimaryKey(autoGenerate = true)
    public int rId;

    public LocalDateTime timestamp;
    public float decibel;
    public int cityId;

}
