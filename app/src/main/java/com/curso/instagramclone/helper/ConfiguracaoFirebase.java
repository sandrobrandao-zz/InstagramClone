package com.curso.instagramclone.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    // retorna instancia do Firebase Database
    public static DatabaseReference getFirebaseDatabase() {
        if( database == null ) {
            database = FirebaseDatabase.getInstance().getReference();
        }

        return database;
    }

    // retorna instancia do Firebase Auth
    public static FirebaseAuth getFirebaseAutenticacao() {
        if( auth == null ) {
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

    public static StorageReference getFirebaseStorage(){
        if( storage == null ){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
