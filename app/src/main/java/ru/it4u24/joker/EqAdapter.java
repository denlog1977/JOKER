package ru.it4u24.joker;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class EqAdapter extends BaseAdapter {

    ArrayList<ElectronicQueue> arrayList;
    LayoutInflater layoutInflater;
    Context context;

    EqAdapter(Context context, ArrayList<ElectronicQueue> list) {

        this.context = context;
        this.arrayList = list;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    public ElectronicQueue getPosition(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final String LOG_TAG = "myLogs";

        View view = convertView;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item, parent, false);
        }
        final ElectronicQueue eq = getPosition(position);
        String chassis = eq.getChassis();
        Date date = eq.getDate();
        Date currentDate = new Date();
        boolean before = date.before(currentDate);
        Log.d(LOG_TAG, "date=" + date.toString() + "=" + before);

        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvChassis = view.findViewById(R.id.tvChassis);
        EditText etChassis = view.findViewById(R.id.etChassis);
        ImageButton ibtnDel = view.findViewById(R.id.ibtnDel);
        CheckedTextView checkedTextView = view.findViewById(R.id.checkedTextView);
        tvTime.setText(eq.getTime());
        tvChassis.setText(chassis);
        ibtnDel.setVisibility(View.GONE);
        checkedTextView.setVisibility(View.GONE);
        if (chassis.isEmpty() && !before) {
            tvTime.setTextColor(Color.BLUE);
            etChassis.setVisibility(View.GONE);
            etChassis.setFocusable(true);
            eq.setEnabled(true);

            etChassis.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d(LOG_TAG, "beforeTextChanged: CharSequence=" + s + ", start=" + start + ", count=" + count + ", after=" + after);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d(LOG_TAG, "onTextChanged: CharSequence=" + s + ", start=" + start + ", count=" + count);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d(LOG_TAG, "afterTextChanged: CharSequence=" + s);
                    Log.d(LOG_TAG, "========================================================");
                }
            });
        } else {
            tvTime.setTextColor(Color.GRAY);
            etChassis.setVisibility(View.GONE);
            eq.setEnabled(false);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v;
        /*if (position == 0) {
            TextView tv = new TextView(context);
            tv.setVisibility(View.GONE);
            tv.setHeight(0);
            v = tv;
            v.setVisibility(View.GONE);
        }
        else*/
            v = super.getDropDownView(position, null, parent);
        return v;
    }
}