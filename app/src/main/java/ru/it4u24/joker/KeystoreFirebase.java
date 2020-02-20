package ru.it4u24.joker;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;


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

    private void setDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }
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

        final LoginActivity loginActivity = (LoginActivity) context;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        final boolean successful = task.isSuccessful();
                        if (successful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "Авторизация пройдена");
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(LOG_TAG, "Авторизация не пройдена", task.getException());
                        }

                        setDatabase();
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                new User(dataSnapshot);
                                loginActivity.updateSignIn(successful);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                loginActivity.updateSignIn(successful);
                            }
                        });
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
                            setDatabase();
                            mDatabase.child("users").child(mAuth.getUid()).setValue(user);
                            sendVerificationEmail(null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "Не прошла регистрация", task.getException());
                        }
                        if (context instanceof LoginActivity) {
                            LoginActivity loginActivity = (LoginActivity) context;
                            loginActivity.updateSignIn(successful);
                        }
                     }
                });
    }

    public void sendVerificationEmail(final Context context) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            KeystoreSharedPreferences myPref = App.getKeystoreSharedPreferens();
                            String status_email;
                            boolean successful = task.isSuccessful();

                            if (successful) {
                                status_email = "Ожидается подтверждение";
                                Log.w(LOG_TAG, "Письмо с подтверждением отправлено");
                            } else {
                                status_email = "Необходимо подтверждение";
                                Log.w(LOG_TAG, "Не удалось отправить письмо", task.getException());
                            }

                            setStatusEmail(status_email);
                            myPref.setString(myPref.KEY_STATUS_EMAIL, status_email);

                            if (context != null) {
                                LoginActivity loginActivity = (LoginActivity) context;
                                loginActivity.updateSignIn(successful);
                            }
                        }
                    });
        }
    }

    public void setStatusEmail(String status) {
        setDatabase();
        mDatabase.child("users").child(mAuth.getUid()).child("statusEmail").setValue(status);
    }

    public void sendVerificationPhone(final Context context, String phoneNumber) {

        if (isSignInUser()) {
            PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            Log.d(LOG_TAG, "onVerificationCompleted:" + phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            Log.d(LOG_TAG, "onVerificationFailed", e);

                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                // Неверный запрос
                                // ...
                                Log.d(LOG_TAG, getClass() + ":onVerificationFailed=Неверный запрос", e);
                            } else if (e instanceof FirebaseTooManyRequestsException) {
                                // Превышена квота SMS для проекта
                                // ...
                                Log.d(LOG_TAG, getClass() + ":onVerificationFailed=Превышена квота SMS для проекта", e);
                            }

                        }

                        @Override
                        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                            //super.onCodeSent(s, token);
                            // Код подтверждения SMS был отправлен на указанный номер телефона,
                            // теперь нужно попросить пользователя ввести код и затем создать учетные данные
                            // путем объединения кода с идентификатором проверки.
                            Log.d(LOG_TAG, "onCodeSent:" + verificationId);

                            // Сохраняем идентификатор подтверждения и повторно отправляем токен, чтобы мы могли использовать их позже
                            //mVerificationId = verificationId;
                            //mResendToken = token;
                        }
                    };

            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS,
                    (Activity) context, mCallbacks);
        }
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                            ////updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                        } else {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                ////mVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            ////updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }

    public void signOut() {

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            mAuth.signOut();
            new User();
        }
    }

    public void reauthenticate(final String email, final String password) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null) return;
        if (email.isEmpty() || password.isEmpty()) {
            mAuth.signOut();
            return;
        }
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        firebaseUser.reauthenticate(credential)
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

    public boolean isEmailVerified() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.reload();
            return firebaseUser.isEmailVerified();
        } else
            return false;
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
            saveUserSharedPreferences();
        }

        public User(String id, String name, String email, String phone) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.statusEmail = "Необходимо подтверждение";
            this.statusPhone = "Необходимо подтверждение";

            saveUserSharedPreferences();
        }

        public User(@NonNull DataSnapshot dataSnapshot) {

            FirebaseUser firebaseUser = mAuth.getCurrentUser();

            if (firebaseUser == null) return;
            if (!dataSnapshot.hasChild("users")) return;

            DataSnapshot dataUsers = dataSnapshot.child("users");

            this.id = firebaseUser.getUid();

            if (!dataUsers.hasChild(id)) return;

            DataSnapshot dataUser = dataUsers.child(id);

            this.email = firebaseUser.getEmail();
            this.name = dataUser.hasChild("name") ?
                    dataUser.child("name").getValue(String.class) : "";
            this.phone = dataUser.hasChild("phone") ?
                    dataUser.child("phone").getValue(String.class) : "";
            this.statusEmail = dataUser.hasChild("statusEmail") ?
                    dataUser.child("statusEmail").getValue(String.class) : "Необходимо подтверждение";
            this.statusPhone = dataUser.hasChild("statusPhone") ?
                    dataUser.child("statusPhone").getValue(String.class) : "Необходимо подтверждение";

            saveUserSharedPreferences();
        }

        private void saveUserSharedPreferences() {

            //if (id == null || email == null || id.isEmpty() || email.isEmpty()) return;

            KeystoreSharedPreferences sPref = App.getKeystoreSharedPreferens();

            sPref.setString(sPref.KEY_USER_NAME, name);
            sPref.setString(sPref.KEY_USER_EMAIL, email);
            sPref.setString(sPref.KEY_USER_PHONE, phone);
            sPref.setString(sPref.KEY_STATUS_EMAIL, statusEmail);
            sPref.setString(sPref.KEY_STATUS_PHONE, statusPhone);
        }

    }
}
