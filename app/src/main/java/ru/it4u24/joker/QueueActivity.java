package ru.it4u24.joker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class QueueActivity extends AppCompatActivity {

    private Spinner spinner;
    private ArrayList<Rc> rcArrayList;
    private CalendarView mDateCalendar;
    private long mDate;
    private Button mChooseDate;
    private String mDateTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        initViews();


    }

    private void initViews() {
        spinner = findViewById(R.id.spinner);
        initSpinner();
        mChooseDate = findViewById(R.id.chooseDate);
        mDateCalendar = findViewById(R.id.dateCalendar);
        mDateCalendar.setVisibility(View.GONE);

        mChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDateCalendar.setVisibility(View.VISIBLE);
            }
        });

        mDateCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {

            }
        });

        mDateCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                mDateTxt = i+"-"+i1+"-"+i2;
                mChooseDate.setText("Дата: " + mDateTxt);
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                gregorianCalendar.set(i, i1, i2);
                mDate = gregorianCalendar.getTimeInMillis();
                calendarView.setVisibility(View.GONE);
            }
        });


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
