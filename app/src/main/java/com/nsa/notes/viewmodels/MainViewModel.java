package com.nsa.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nsa.notes.models.NoteModel;
import com.nsa.notes.repo.MainRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private MainRepository repo;
    public MainViewModel(@NonNull Application application) {
        super(application);
        repo=new MainRepository(application);
    }

    public void saveNote(NoteModel noteModel) {
        repo.saveNote(noteModel);
    }
    public LiveData<List<NoteModel>> getAllNotes(){
        return repo.getAllNotes();
    }

    public void update(NoteModel noteModel) {
        repo.update(noteModel);
    }

    public void delete(NoteModel noteModel) {
        repo.delete(noteModel);
    }
}
