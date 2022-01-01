package com.nsa.notes.db;



import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nsa.notes.models.NoteModel;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NoteModel note);

    @Update
    void update(NoteModel note);

    @Delete
    void delete(NoteModel note);

    @Query("DELETE FROM notes_table")
    void deleteAllnotes();

    @Query("DELETE FROM notes_table where id = :id")
    int deleteFromnote(int id);

    @Query("SELECT * FROM notes_table order by timeStamp desc,id desc")
    LiveData<List<NoteModel>> getAllnotes();

    @Query("SELECT * FROM notes_table where id = :id")
    LiveData<NoteModel> getnoteByID(int id);




}