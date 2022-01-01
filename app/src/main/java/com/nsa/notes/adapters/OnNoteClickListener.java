package com.nsa.notes.adapters;

import com.nsa.notes.models.NoteModel;

public interface OnNoteClickListener {
    void onClick(NoteModel note);
    void onLongClick();

    void selectedCounter(int size);
}
