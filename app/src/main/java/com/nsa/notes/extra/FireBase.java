package com.nsa.notes.extra;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBase {
   public FirebaseDatabase database;
    public DatabaseReference appReference,userReference,userDataReference;


    public FireBase(){
        database = FirebaseDatabase.getInstance();
        appReference=database.getReference("NotesApp");
        userReference=appReference.child("users");
        userDataReference=appReference.child("data");
    }

}
