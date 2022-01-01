package com.nsa.notes.repo;

import android.os.AsyncTask;

import com.nsa.notes.db.NoteDao;
import com.nsa.notes.models.NoteModel;

public class UpdateNote extends AsyncTask<NoteModel,Void,Void> {
    private NoteDao noteDao;
    public UpdateNote(NoteDao noteDao) {
        this.noteDao=noteDao;
    }

    @Override
    protected Void doInBackground(NoteModel... noteModels) {
        noteDao.update(noteModels[0]);
        return null;
    }
}
