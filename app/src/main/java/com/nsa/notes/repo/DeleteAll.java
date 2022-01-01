package com.nsa.notes.repo;

import android.os.AsyncTask;

import com.nsa.notes.db.NoteDao;
import com.nsa.notes.models.NoteModel;

public class DeleteAll extends AsyncTask<Void,Void,Void> {
    private NoteDao noteDao;
    public DeleteAll(NoteDao noteDao) {
        this.noteDao=noteDao;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        noteDao.deleteAllnotes();
        return null;
    }
}
