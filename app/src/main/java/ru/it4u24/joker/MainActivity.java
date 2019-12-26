package ru.it4u24.joker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //private SharedPreferences sPref;
    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast3 = Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG);
                toast3.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
                ImageView imageToast = new ImageView(MainActivity.this);
                imageToast.setImageResource(R.drawable.joker4);
                imageToast.setBackgroundColor(R.color.colorPrimary);
                LinearLayout toastContainer = (LinearLayout) toast3.getView();
                toastContainer.addView(imageToast, 0);
                toast3.show();
            }
        });



        KeystoreFirebase keystoreFirebase = App.getKeystoreDatabaseReference();
        keystoreFirebase.runService();

        new Thread(myThread).start();
        //Keys.service1cLog.getTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, 1);
//            return true;
        } else if (id == R.id.action_firebase) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
//            return true;
        } else if (id == R.id.action_queue) {
            Intent intent = new Intent(MainActivity.this, QueueActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private Runnable myThread = new Runnable() {

        String[][] result;
        long timeEnd;
        String ERROR;
        HttpClient hc;

        @Override
        public void run() {

            hc = new HttpClient();
            hc.execute("Organization");//query

            try {
                Log.d(LOG_TAG, "Try to get result");
                result = hc.get();
                Log.d(LOG_TAG, "get returns " + result.length);
                timeEnd = hc.getTimeEnd();
            } catch (Exception e) {
                ERROR = "Exception error: " + e.getMessage();
                e.printStackTrace();
                handler.sendEmptyMessage(1);
                return;
            }

            ERROR = hc.getERROR();

            //showResult();
            handler.sendEmptyMessage(1);
        }

        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Log.d(LOG_TAG, "handleMessage = " + msg);
                if (msg.what == 1) {
                    //showResult(result, ERROR);
                    //pbHor.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            "Время выполнения " + timeEnd, Toast.LENGTH_LONG).show();
                    //handler.removeCallbacks(myThread);
                }
            }
        };
    };



}
