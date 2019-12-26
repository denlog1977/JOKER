package ru.it4u24.joker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class QueueActivity extends AppCompatActivity {

    private Spinner spinner;
    private ArrayList<Rc> rcArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        initViews();

    }

    private void initViews() {
        spinner = findViewById(R.id.spinner);
        initSpinner();
    }

    private void initSpinner() {

        RcList rcList = new RcList();
        rcArrayList = rcList.getRcArrayList();

        RcAdapter rcAdapter = new RcAdapter(this, rcArrayList);

        spinner.setAdapter(rcAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i !=0 ) {
                    Rc rc = rcArrayList.get(i);
                    Toast.makeText(QueueActivity.this, "Выбран элемент номер " + i + "\n"+ rc.getName(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


}
