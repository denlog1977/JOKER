package ru.it4u24.joker;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Firebase {

    private final String LOG_TAG = "myLogs";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    Boolean result;

    public void signIn (String email, String password) {

        //boolean result = false;

        /*SignInRun signInRun = new SignInRun();
        signInRun.execute(email, password);

        try {
            result = signInRun.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Log.d(LOG_TAG, "Старт авторизации");
        signInRun(email, password);
        //List<>

        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; result == null && i <= 10; i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                        Log.d(LOG_TAG, "for signIn=" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();

        //FirebaseUser user = mAuth.getCurrentUser();
        //result = user != null;

        Log.d(LOG_TAG, "Firebase:result=" + result);

        //return result;
    }

    public String[] getServise() {

        final String[] resultat = {"0", "0"};
        Log.d(LOG_TAG, "mDatabase начало");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Boolean bol = dataSnapshot.hasChild("1c");
                DataSnapshot master1c = dataSnapshot.child("master1c");
                String valueLog = master1c.child("log").getValue(String.class);
                String valuePas = master1c.child("pas").getValue(String.class);
                Log.d(LOG_TAG, valueLog);
                resultat[0] = valueLog;
                resultat[1] = valuePas;
                Log.d(LOG_TAG, "mDatabase результат");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG_TAG, "Failed to read value.", databaseError.toException());
            }
        });

        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; resultat[0] == "0" && i <= 10; i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                        Log.d(LOG_TAG, "for service=" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();

        return resultat;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Boolean getResult() {
        return result;
    }

    private void signInRun(final String email, final String password) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "Авторизация пройдена");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //result = true;
                            setResult(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(LOG_TAG, "Авторизация не пройдена", task.getException());
                            //result = false;
                            setResult(false);
                        }
                    }
                });
    }
}
