package ru.it4u24.joker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

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



        KeystoreFirebase keystoreFirebaseData = App.getKeystoreDatabaseReference();
        keystoreFirebaseData.runService();
        KeystoreFirebase keystoreFirebaseUser = App.getKeystoreFirebaseAuth();

        if (!keystoreFirebaseUser.isSignInUser()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
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

}
