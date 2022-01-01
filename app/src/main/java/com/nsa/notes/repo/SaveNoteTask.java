package com.nsa.notes.repo;

import android.os.AsyncTask;

import com.nsa.notes.db.NoteDao;
import com.nsa.notes.models.NoteModel;

public class SaveNoteTask extends AsyncTask<NoteModel,Void,Void> {
    private NoteDao noteDao;
    public SaveNoteTask(NoteDao noteDao) {
        this.noteDao=noteDao;
    }

    @Override
    protected Void doInBackground(NoteModel... noteModels) {
        noteDao.insert(noteModels[0]);
        return null;
    }
}
