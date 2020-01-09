package ru.it4u24.joker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputTextList extends AppCompatActivity {

    EditText editText;
    CheckedTextView checkedTextView;
    SimpleAdapter adapter;
    ListView listView;
    ArrayList<Map<String, Object>> arrayList;
    Map<String, Object> map;

    final String ATTRIBUTE_NAME_TEXT = "time";
    final String ATTRIBUTE_CHASSIS_TEXT = "chassis";
    final String ATTRIBUTE_DELETE_TEXT = "delete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_text_item);

        editText = findViewById(R.id.editText);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        onClickAdd(v);
                        return true;
                    }
                return false;
            }
        });
        checkedTextView = findViewById(R.id.checkedTextView);

        String[] from = { ATTRIBUTE_NAME_TEXT, ATTRIBUTE_CHASSIS_TEXT, ATTRIBUTE_DELETE_TEXT };
        int[] to = { R.id.tvTime, R.id.tvChassis, R.id.checkedTextView };

        arrayList = new ArrayList<>();
        adapter = new MySimpleAdapter(this, arrayList, R.layout.item, from, to);

        listView = findViewById(R.id.lvInputText);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                boolean isChosen = false;
                Log.d("myLogs", "Выбранная позиция: " + position);

                SparseBooleanArray chosen = ((ListView) parent).getCheckedItemPositions();
                for (int i = 0; i < chosen.size(); i++) {
                    // если пользователь выбрал пункт списка, то устанавливаем его в arrayList.
                    if (chosen.valueAt(i)) {
                        Log.d("myLogs", "Выбранные значения: " + chosen.keyAt(i));
                        if (chosen.keyAt(i) == position) {
                            isChosen = true;
                            break;
                        }
                    }
                }

                arrayList.get(position).put(ATTRIBUTE_DELETE_TEXT, isChosen);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void onClickAdd(View view) {

        if (editText.getText().toString().isEmpty()) return;

        map = new HashMap<>();
        map.put(ATTRIBUTE_NAME_TEXT, editText.getText().toString());
        map.put(ATTRIBUTE_DELETE_TEXT, false);
        arrayList.add(map);

        adapter.notifyDataSetChanged();

        editText.setText("");

        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void onClickDel(View view) {

        if (arrayList.isEmpty()) return;

        SparseBooleanArray chosen = listView.getCheckedItemPositions();
        for (int i = 0; i < chosen.size(); i++) {
            // удаляем из списка выбранные значения arrayList
            if (chosen.valueAt(i)) {
                Log.d("myLogs", "Удаляется выбранное значение по позиции: " + chosen.keyAt(i));
                arrayList.remove(chosen.keyAt(i));
            }
        }

        adapter.notifyDataSetChanged();

        editText.setText("");

        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void onClickОК(View view) {

        //if (arrayList.isEmpty()) return;
        String[] result = new String[arrayList.size()];

        for (int i = 0; i < arrayList.size() - 1; i++) {
            result[i] = (String) arrayList.get(i).get(ATTRIBUTE_CHASSIS_TEXT);
            Log.d("myLogs", "Выбранное значение по позиции: " + i);
        }

        Intent intent = new Intent();
        intent.putExtra(ATTRIBUTE_CHASSIS_TEXT, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class MySimpleAdapter extends SimpleAdapter {

        private MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
                               String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public void setViewText(TextView v, String text) {
            super.setViewText(v, text);

            if (v.getId() == R.id.tvTime) {
                //if (i == true) v.setTextColor();
                //else v.setTextColor(v.getTextColors());
            } else if (v.getId() == R.id.tvChassis) {
                //v.setVisibility(View.GONE);
            }
        }
    }
}
