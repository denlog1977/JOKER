package ru.it4u24.joker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class QueueActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Spinner spinner;
    private ListView listViewData;
    private EqAdapter eqAdapter;
    private Button mChooseDate;
    private String mDateFormatText;
    private Integer mIDRc;
    private Integer mPositionArrayList;
    private ProgressBar pbSpinner, pbListData;
    private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue);

        pbSpinner = findViewById(R.id.pbSpinner);
        pbListData = findViewById(R.id.pbListData);

        new StartRunnable(this, "Organization");
        initViews();
    }

    private void initViews() {
        spinner = findViewById(R.id.spinner);
        listViewData = findViewById(R.id.lvData);
        mChooseDate = findViewById(R.id.chooseDate);
        mChooseDate.setEnabled(false);

        mChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        String mDateTxt = "Дата: " + day + "." + (month + 1) + "." + year;
        String mon = (month < 9 ? "0" : "") + (month + 1);
        String dy = (day < 10 ? "0" : "") + day;
        mDateFormatText = "" + year + "-" + mon + "-" + dy;
        Toast.makeText(this, mDateTxt, Toast.LENGTH_LONG).show();
        mChooseDate.setText(mDateTxt);

        pbListData.setVisibility(View.VISIBLE);
        new StartRunnable(this, "ElectronicQueue");
    }

    private void initSpinner(String[][] args) {

        InitList rcList = new InitList(args);
        final ArrayList<Rc> rcArrayList = rcList.getRcArrayList();

        RcAdapter rcAdapter = new RcAdapter(this, rcArrayList);

        spinner.setAdapter(rcAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i !=0 ) {
                    Rc rc = rcArrayList.get(i);
                    mChooseDate.setEnabled(true);
                    mIDRc = rc.getId();
                    //mUIDRc = rc.getUID();
                    Toast.makeText(QueueActivity.this,
                            "Выбран элемент номер " + i + "\n"+ rc.getName(), Toast.LENGTH_SHORT).show();
                    if (mDateFormatText != null && !mDateFormatText.isEmpty()){
                        pbListData.setVisibility(View.VISIBLE);
                        new StartRunnable(getApplicationContext(), "ElectronicQueue");
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initListData(String[][] args) {

        InitList list = new InitList(args);
        final ArrayList<ElectronicQueue> eqArrayList = list.getEqArrayList();
        eqAdapter = new EqAdapter(this, eqArrayList);

        listViewData.setAdapter(eqAdapter);

        listViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ElectronicQueue eq = eqArrayList.get(position);
                Toast.makeText(QueueActivity.this,
                        "Выбран элемент номер " + position + "\n"+ eq.getTime(), Toast.LENGTH_SHORT).show();

                if (eq.isEnabled()) {
                    mPositionArrayList = position;
                    Intent intent = new Intent(getApplicationContext(), InputTextList.class);
                    startActivityForResult(intent, 1);
                } else {
                    mPositionArrayList = null;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1 && data != null && mPositionArrayList != null) {

                String[] chassis = data.getStringArrayExtra("chassis");

                if (chassis == null || chassis.length == 0) return;

                int position = mPositionArrayList;

                for (String name : chassis) {
                    Log.d(LOG_TAG, "Выбрана шасси: " + name);
                    while (position < eqAdapter.getCount()) {
                        ElectronicQueue eq = eqAdapter.getItemEQ(position);
                        if (eq.isEnabled()) {
                            eq.setChassis(name);
                            position++;
                            break;
                        } else
                            position++;
                    }

                }
                eqAdapter.notifyDataSetChanged();
            }
        }
    }

    private class StartRunnable implements Runnable {

        Thread thread;
        String[][] result;
        long timeEnd;
        String ERROR;
        HttpClient httpClient;
        Context context;
        String name;

        // Конструктор
        StartRunnable(Context context, String name) {
            // Создаём новый поток
            this.context = context;
            this.name = name;
            thread = new Thread(this, name);
            Log.d(LOG_TAG, "Создан новый поток " + thread);
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
                jp.addObject("TipQuery", name);
                if (name.equals("ElectronicQueue")) {
                    jp.addArrayObject("Период", mDateFormatText);
                    jp.addArrayObject("ОрганизацияID", mIDRc);
                    jp.addObjectArray("ConditionsQuery");

                } //else if (name.equals("Organization"))

                String jpResult = jp.getResult();

                String encodequery = jpResult;
                /*byte[] bytes = jpResult.getBytes();
                String encodequery = Base64.encodeToString(bytes, Base64.NO_WRAP);*/
                Log.d(LOG_TAG, "encodeString = " + encodequery);

                //byte[] bytess = Base64.decode(encodequery, Base64.NO_WRAP);
                //String decodestring = new String(bytess);
                //Log.d(LOG_TAG, "decodeString = " + decodestring);

                httpClient = new HttpClient();
                httpClient.setURL("http://yourport.ru/xxx");//"https://kamaz.ddns.net:10100/testut/hs/ExchangeTFK/query"
                httpClient.execute("GetList"+name, encodequery);//query
                Log.d(LOG_TAG, "Ожидание результата HttpClient");
                result = httpClient.get();
                Log.d(LOG_TAG, "Длина результата httpClient=" + result.length);
                timeEnd = httpClient.getTimeEnd();

            } catch (InterruptedException e) {
                Log.d(LOG_TAG, "Поток прерван");
                ERROR = "InterruptedException error: " + e.getMessage();
            } catch (Exception e) {
                ERROR = "Exception error: " + e.getMessage();
                e.printStackTrace();
             }

            ERROR = ERROR == null || ERROR.isEmpty() ? httpClient.getERROR() : ERROR;
            Log.d(LOG_TAG, "ERROR=" + ERROR);

            handler.sendEmptyMessage(1);
        }

        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                Log.d(LOG_TAG, "handleMessage = " + msg);
                if (msg.what == 1) {

                    Toast.makeText(context,
                            "Время выполнения " + timeEnd, Toast.LENGTH_LONG).show();

                    if (name.equals("Organization")) {
                        pbSpinner.setVisibility(View.GONE);
                        initSpinner(result);
                    } else if (name.equals("ElectronicQueue")) {
                        pbListData.setVisibility(View.GONE);
                        initListData(result); // переделать выходить ошибка на младших версиях
                    }

                }
            }
        };
    }

}
