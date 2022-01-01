package com.nsa.notes.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.nsa.notes.db.Constants;

import java.io.Serializable;

@Entity(tableName = Constants.TABLE_NAME,indices = {@Index(value = {"timeStamp"},unique = true)})
public class NoteModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;


    @ColumnInfo(name = "timeStamp")
    private String timeStamp;

    public NoteModel() {
    }

    public NoteModel(int id, String title, String description, String timeStamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
