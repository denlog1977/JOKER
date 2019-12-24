package ru.it4u24.joker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //getPrefService1c();
        //setPrefService1c();
        Firebase firebase = new Firebase();
        firebase.setServise();
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
        } else if (id == R.id.action_firebase) {
            Intent intent = new Intent(MainActivity.this, QueueActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPrefService1c() {
        //sPref = getPreferences(MODE_PRIVATE);
        sPref = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        String service1cLog = sPref.getString("service1cLog", "");
        String service1cPas = sPref.getString("service1cPas", "");
        //Toast.makeText(this, "Текст установлен", Toast.LENGTH_SHORT).show();
        Log.d("myLogs", "Получено service1cLog=" + service1cLog);
    }

    private void setPrefService1c() {
        //sPref = getPreferences(MODE_PRIVATE);
        sPref = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("service1cLog", "1");
        ed.putString("service1cPas", "2");
        ed.commit();
        //Toast.makeText(this, "Текст сохранен", Toast.LENGTH_SHORT).show();
        Log.d("myLogs", "Сохранено service1cLog и service1cPas");
    }
}
