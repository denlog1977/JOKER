package ru.it4u24.joker;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RcAdapter extends BaseAdapter {

    ArrayList<Rc> rcArrayList;
    LayoutInflater layoutInflater;
    Context ctx;

    RcAdapter(Context context, ArrayList<Rc> rcList) {
        ctx = context;
        rcArrayList = rcList;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return rcArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return rcArrayList.get(position);
    }

    public Rc getRc(int position) {
        return rcArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.rc_item, parent, false);
        }
        final Rc rc = getRc(position);
        ((TextView) (view.findViewById(R.id.textViewName))).setText(rc.getName());
        ((TextView) (view.findViewById(R.id.textViewId))).setText(Integer.toString(rc.getId()));
        ((TextView) (view.findViewById(R.id.textViewWorktime))).setText(Integer.toString(rc.getWorktime()));
        ((TextView) (view.findViewById(R.id.textViewMinut))).setText(Integer.toString(rc.getMinut()));
        return view;
    }


}