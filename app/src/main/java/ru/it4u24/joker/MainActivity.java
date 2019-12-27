package ru.it4u24.joker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Base64DataException;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

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
                Toast toast3 = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
                toast3.setGravity(Gravity.FILL, 0, 0);//FILL_HORIZONTAL
                ImageView imageToast = new ImageView(getApplicationContext());
                imageToast.setImageResource(R.drawable.joker);
                imageToast.setBackgroundColor(Color.parseColor("#008577"));//rgb(0, 133, 119)//BLUE
                LinearLayout toastContainer = (LinearLayout) toast3.getView();
                toastContainer.setBackgroundColor(Color.GREEN); //Color.TRANSPARENT - прозрачный
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

            try {
                JsonParser jp = new JsonParser();
                jp.addObject("TipQuery", "Organization");
                jp.addArrayObject("id", 1);
                jp.addArrayObject("Наименование", "ТФК");
                jp.addArray(new Date());
                jp.addObjectArray("ConditionsQuery");
                String jpResult = jp.getResult();
                Log.d(LOG_TAG, "jpResult = " + jpResult);

                byte[] bytes = jpResult.getBytes();
                String encodequery = Base64.encodeToString(bytes, Base64.NO_WRAP);
                Log.d(LOG_TAG, "encodeString = " + encodequery);

                byte[] bytess = Base64.decode(encodequery, Base64.NO_WRAP);
                String decodestring = new String(bytess);
                Log.d(LOG_TAG, "decodeString = " + decodestring);

                hc = new HttpClient();
                hc.execute(encodequery);//query
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
