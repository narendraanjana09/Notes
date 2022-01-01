package com.nsa.notes.repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nsa.notes.db.NoteDao;
import com.nsa.notes.db.NoteDatabase;
import com.nsa.notes.models.NoteModel;

import java.util.List;

public class MainRepository {
    private Application application;
    private NoteDao noteDao;

    public MainRepository(Application application) {
        this.application = application;
        noteDao= NoteDatabase.getInstance(application).noteDao();
    }

    public void saveNote(NoteModel noteModel) {
        new SaveNoteTask(noteDao).execute(noteModel);
    }
    public LiveData<List<NoteModel>> getAllNotes(){
        return noteDao.getAllnotes();
    }

    public void update(NoteModel noteModel) {
        new UpdateNote(noteDao).execute(noteModel);
    }

    public void delete(NoteModel noteModel) {
        new DeleteNote(noteDao).execute(noteModel);
    }
}
