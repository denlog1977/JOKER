package ru.it4u24.joker;


import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {

    private static KeystoreSharedPreferences keystoreSharedPreferences;
    private static KeystoreFirebase keystoreFirebaseAuth;
    private static KeystoreFirebase keystoreDatabaseReference;

    @Override
    public void onCreate() {
        super.onCreate();

        keystoreSharedPreferences = new KeystoreSharedPreferences(
                getSharedPreferences("MyPrefs", MODE_PRIVATE));
        keystoreFirebaseAuth = new KeystoreFirebase(FirebaseAuth.getInstance());
        keystoreDatabaseReference = new KeystoreFirebase(FirebaseDatabase.getInstance().getReference());
    }

    public static KeystoreSharedPreferences getKeystoreSharedPreferens() {
        return keystoreSharedPreferences;
    }

    public static KeystoreFirebase getKeystoreFirebaseAuth() {
        return keystoreFirebaseAuth;
    }

    public static KeystoreFirebase getKeystoreDatabaseReference() {
        return keystoreDatabaseReference;
    }
}
