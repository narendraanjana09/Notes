package com.nsa.notes.repo;

import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.nsa.notes.models.NoteModel;

import java.util.List;

public class SyncToServer extends AsyncTask<List<NoteModel>,Void,Void> {

    DatabaseReference reference;

    public SyncToServer(DatabaseReference reference) {
        this.reference = reference;
    }

    @Override
    protected Void doInBackground(List<NoteModel>... lists) {
        for(NoteModel noteModel:lists[0]){
            if(noteModel.getTimeStamp()==null){
                continue;
            }
            reference.child(noteModel.getTimeStamp()).setValue(noteModel);
        }
        return null;
    }
}
