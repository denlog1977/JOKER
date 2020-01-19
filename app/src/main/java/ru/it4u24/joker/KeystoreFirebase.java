package ru.it4u24.joker;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class KeystoreFirebase implements Keystore {

    private String login;
    private String password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final String LOG_TAG = "myLogs";

    public KeystoreFirebase(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public KeystoreFirebase(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    @Override
    public String getLogin(String key) {
        return null;
    }

    @Override
    public String getPassword(String key) {
        return null;
    }

    @Override
    public void setLogin(String key, String login) {

    }

    @Override
    public void setPassword(String key, String password) {

    }

    public void runService() {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Boolean bol = dataSnapshot.hasChild("1c");
                DataSnapshot master1c = dataSnapshot.child("master1c");
                login = master1c.child("log").getValue(String.class);
                password = master1c.child("pas").getValue(String.class);
                Log.d(LOG_TAG, "Firebase login master1c=" + login);

                KeystoreSharedPreferences myPref = App.getKeystoreSharedPreferens();
                String prefLog = myPref.getLogin(myPref.KEY_LOG_SERVICE1C);
                String prefPas = myPref.getPassword(myPref.KEY_PAS_SERVICE1C);

                Log.d(LOG_TAG, "login=" + login + "=" + prefLog + " && password=" + password + "=" + prefPas);
                Log.d(LOG_TAG, "login==prefLog=" + login.equals(prefLog) + " && password==prefPas=" + password.equals(prefPas));

                if (login.equals(prefLog) && password.equals(prefPas)) return;

                myPref.setLoginPasswordService1c(login, password);
                Log.d(LOG_TAG, "Установлен логин и пароль в SharedPreferens из Firebase");
             }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG_TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void runSignIn(final Context context, final String email, final String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "Авторизация пройдена");
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(LOG_TAG, "Авторизация не пройдена", task.getException());
                        }

                        LoginActivity loginActivity = (LoginActivity) context;
                        loginActivity.updateSignIn(task.isSuccessful());
                    }
                });
    }

    public boolean isSignInUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null;
    }

    private static boolean equally(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }
}
