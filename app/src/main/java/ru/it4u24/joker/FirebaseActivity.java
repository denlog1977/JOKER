package ru.it4u24.joker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class FirebaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        Firebase firebase = new Firebase();
        String[] getServise = firebase.getServise();


    }
}
