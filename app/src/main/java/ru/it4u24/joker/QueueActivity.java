package ru.it4u24.joker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class QueueActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Spinner spinner;
    private ListView listViewData;
    private ArrayList<Rc> rcArrayList;
    private ArrayList<ElectronicQueue> eqArrayList;
//    private CalendarView mDateCalendar;
//    private long mDate;
    private Button mChooseDate;
    private String mDateTxt;
    private String mDateFormatText;
    private Integer mIDRc;
    private ProgressBar pbSpinner, pbListData;
    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue);

        pbSpinner = findViewById(R.id.pbSpinner);
        pbListData = findViewById(R.id.pbListData);

        new Thread(myThread).start();
        initViews();
    }

    private void initViews() {
        spinner = findViewById(R.id.spinner);
        listViewData = findViewById(R.id.lvData);
        //initSpinner();
        mChooseDate = findViewById(R.id.chooseDate);
        mChooseDate.setEnabled(false);
//        mDateCalendar = findViewById(R.id.dateCalendar);
//        mDateCalendar.setVisibility(View.GONE);

        mChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mDateCalendar.setVisibility(View.VISIBLE);

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

//        mDateCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
//
//            }
//        });
//
//        mDateCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
//                mDateTxt = i+"-"+i1+"-"+i2;
//                mChooseDate.setText("Дата: " + mDateTxt);
//                GregorianCalendar gregorianCalendar = new GregorianCalendar();
//                gregorianCalendar.set(i, i1, i2);
//                mDate = gregorianCalendar.getTimeInMillis();
//                calendarView.setVisibility(View.GONE);
//            }
//        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        mDateTxt = "Дата: " + day + "." + (month + 1) + "." + year;
        mDateFormatText = "" + year + "" + (month + 1) + "" + day;
        Toast.makeText(this, mDateTxt, Toast.LENGTH_LONG).show();
        mChooseDate.setText(mDateTxt);

        pbListData.setVisibility(View.VISIBLE);
        new MyRunnable();
    }

    private void initSpinner(String[][] args) {

        RcList rcList = new RcList(args);
        rcArrayList = rcList.getRcArrayList();

        RcAdapter rcAdapter = new RcAdapter(this, rcArrayList);

        spinner.setAdapter(rcAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i !=0 ) {
                    Rc rc = rcArrayList.get(i);
                    mChooseDate.setEnabled(true);
                    mIDRc = rc.getId();
                    Toast.makeText(QueueActivity.this, "Выбран элемент номер " + i + "\n"+ rc.getName(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initListData(String[][] args) {

        RcList list = new RcList(ElectronicQueue.class, args);
        eqArrayList = list.getEqArrayList();

        EqAdapter adapter = new EqAdapter(this, eqArrayList);

        listViewData.setAdapter(adapter);

        listViewData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                /*Rc rc = rcArrayList.get(i);
                mChooseDate.setEnabled(true);
                Toast.makeText(QueueActivity.this, "Выбран элемент номер " + i + "\n"+ rc.getName(), Toast.LENGTH_SHORT).show();*/

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private Runnable myThread = new Runnable() {

        String[][] result;
        long timeEnd;
        String ERROR;
        HttpClient httpClient;

        @Override
        public void run() {

            try {
                JsonParser jp = new JsonParser();
                jp.addObject("TipQuery", "Organization");
                //jp.addArrayObject("id", 0);
                //jp.addArrayObject("Наименование", "ТФК");
                //jp.addArray(new Date());
                //jp.addObjectArray("ConditionsQuery");
                String jpResult = jp.getResult();

                byte[] bytes = jpResult.getBytes();
                String encodequery = Base64.encodeToString(bytes, Base64.NO_WRAP);
                Log.d(LOG_TAG, "encodeString = " + encodequery);

                //byte[] bytess = Base64.decode(encodequery, Base64.NO_WRAP);
                //String decodestring = new String(bytess);
                //Log.d(LOG_TAG, "decodeString = " + decodestring);

                httpClient = new HttpClient();
                httpClient.execute(encodequery);//query
                Log.d(LOG_TAG, "Ожидание результата HttpClient");
                result = httpClient.get();
                Log.d(LOG_TAG, "Длина результата httpClient=" + result.length);
                timeEnd = httpClient.getTimeEnd();
            } catch (Exception e) {
                ERROR = "Exception error: " + e.getMessage();
                e.printStackTrace();
                handler.sendEmptyMessage(1);
                return;
            }

            ERROR = httpClient.getERROR();

            handler.sendEmptyMessage(1);
        }

        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Log.d(LOG_TAG, "handleMessage = " + msg);
                if (msg.what == 1) {
                    pbSpinner.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            "Время выполнения " + timeEnd, Toast.LENGTH_LONG).show();
                    initSpinner(result);
                }
            }
        };
    };

    private class MyRunnable implements Runnable {

        Thread thread;
        String[][] result;
        long timeEnd;
        String ERROR;
        HttpClient httpClient;

        // Конструктор
        MyRunnable() {
            // Создаём новый второй поток
            thread = new Thread(this, "Поток для примера");
            Log.d(LOG_TAG, "Создан второй поток " + thread);
            thread.start(); // Запускаем поток
        }

        // Обязательный метод для интерфейса Runnable
        public void run() {
            try {
                /*for (int i = 5; i > 0; i--) {
                    Log.i(LOG_TAG, "Второй поток: " + i);
                    Thread.sleep(500);
                }*/
                JsonParser jp = new JsonParser();
                jp.addObject("TipQuery", "ElectronicQueue");
                jp.addArrayObject("Период", mDateFormatText);
                jp.addArrayObject("ОрганизацияID", mIDRc);
                jp.addObjectArray("ConditionsQuery");
                String jpResult = jp.getResult();

                byte[] bytes = jpResult.getBytes();
                String encodequery = Base64.encodeToString(bytes, Base64.NO_WRAP);
                Log.d(LOG_TAG, "encodeString = " + encodequery);

                //byte[] bytess = Base64.decode(encodequery, Base64.NO_WRAP);
                //String decodestring = new String(bytess);
                //Log.d(LOG_TAG, "decodeString = " + decodestring);

                httpClient = new HttpClient();
                httpClient.execute(encodequery);//query
                Log.d(LOG_TAG, "Ожидание результата HttpClient");
                result = httpClient.get();
                Log.d(LOG_TAG, "Длина результата httpClient=" + result.length);
                timeEnd = httpClient.getTimeEnd();

            } catch (InterruptedException e) {
                Log.d(LOG_TAG, "Второй поток прерван");
                ERROR = "InterruptedException error: " + e.getMessage();
            } catch (Exception e) {
                ERROR = "Exception error: " + e.getMessage();
                e.printStackTrace();
             }

            ERROR = ERROR == null || ERROR.isEmpty() ? httpClient.getERROR() : ERROR;

            handler.sendEmptyMessage(1);
        }

        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Log.d(LOG_TAG, "handleMessage = " + msg);
                if (msg.what == 1) {
                    //pbSpinner.setVisibility(View.GONE);
                    pbListData.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            "Время выполнения " + timeEnd, Toast.LENGTH_LONG).show();
                    //initSpinner(result);
                    initListData(result);
                }
            }
        };
    }
}
