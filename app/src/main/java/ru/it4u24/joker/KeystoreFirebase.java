package ru.it4u24.joker;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    public void signIn(final Context context, final String email, final String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        boolean successful = task.isSuccessful();
                        if (successful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "Авторизация пройдена");
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(LOG_TAG, "Авторизация не пройдена", task.getException());
                        }

                        LoginActivity loginActivity = (LoginActivity) context;
                        loginActivity.updateSignIn(successful);
                    }
                });
    }

    public void registration (final Context context, final String email, final String password,
                                 final String name, final String phone) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        boolean successful = task.isSuccessful();
                        if (successful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "Пользователь успешно зарегистрирован");
                            User user = new User(mAuth.getUid(), name, email, phone);
                            if (mDatabase == null) {
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                            }
                            mDatabase.child("users").child(mAuth.getUid()).setValue(user);
                            KeystoreSharedPreferences myPref = App.getKeystoreSharedPreferens();
                            myPref.setString(myPref.KEY_USER_NAME, name);
                            myPref.setString(myPref.KEY_USER_EMAIL, email);
                            myPref.setString(myPref.KEY_USER_PHONE, phone);
                            myPref.setString(myPref.KEY_STATUS_EMAIL, "Новый");
                            myPref.setString(myPref.KEY_STATUS_PHONE, "Новый");

                            sendVerificationEmail(null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "Не прошла регистрация", task.getException());
                        }

                        LoginActivity loginActivity = (LoginActivity) context;
                        loginActivity.updateSignIn(successful);
                    }
                });
    }

    public void sendVerificationEmail(final Context context) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            KeystoreSharedPreferences myPref = App.getKeystoreSharedPreferens();
                            String status_email;
                            boolean successful = task.isSuccessful();

                            if (successful) {
                                status_email = "Отправлено";
                                Log.w(LOG_TAG, "Письмо с подтверждением отправлено");
                            } else {
                                status_email = "Не удалось отправить";
                                Log.w(LOG_TAG, "Не удалось отправить письмо", task.getException());
                            }

                            mDatabase.child("users").child(mAuth.getUid()).child("statusEmail")
                                    .setValue(status_email);
                            myPref.setString(myPref.KEY_STATUS_EMAIL, status_email);

                            if (context != null) {
                                LoginActivity loginActivity = (LoginActivity) context;
                                loginActivity.updateSignIn(successful);
                            }
                        }
                    });
        }
    }

    public void signOut() {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mAuth.signOut();
        }
    }

    public void reauthenticate(final String email, final String password) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && (email.isEmpty() || password.isEmpty())) {
            mAuth.signOut();
            return;
        } else if (email.isEmpty() || password.isEmpty()) return;

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(MainActivity.this,
                            //        "Переавторизация прошла успешно", Toast.LENGTH_SHORT).show();
                            Log.d(LOG_TAG, "Переавторизация прошла успешно");
                        } else {
                            Log.d(LOG_TAG, "Переавторизация не пройдена", task.getException());
                        }

                    }
                });
    }

    public boolean isSignInUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null ) {
            firebaseUser.reload();
            //String status_email_base =
            //String stаtus_email = firebaseUser.isEmailVerified() ? "Подтвержден" : status_email_base;
            String token = firebaseUser.getIdToken(false).toString();
            Log.d(LOG_TAG,
                    "Пользователь вошел в систему как " + firebaseUser.getEmail() +
                            (firebaseUser.isEmailVerified() ?
                                    ", и адрес электронной почты подтвержден." :
                                    ". Адрес электронной почты не подтвержден!") + " TokenID=" + token);
        } else {
            Log.d(LOG_TAG, "Пользователь не авторизован");
        }
        return firebaseUser != null;
    }

    private static boolean equally(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    private class User {

        public String id;
        public String name;
        public String email;
        public String phone;
        public String statusEmail;
        public String statusPhone;

        public User() {
            // Default constructor
        }

        public User(String id, String name, String email, String phone) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.statusEmail = "Новый";
            this.statusPhone = "Новый";
        }

    }
}
