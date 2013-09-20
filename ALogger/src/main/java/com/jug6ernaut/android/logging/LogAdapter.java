
package com.jug6ernaut.android.logging;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;

public class LogAdapter extends BaseAdapter {

    private Context mContext;
    public ArrayList<LogEntry> mEntries;
    private LayoutInflater mInflater;
    private DateFormat format = new SimpleDateFormat("'('yyyy-MM-dd')_('HH:mm:ss.SSSZ')'");

    public LogAdapter(Context context, ArrayList<LogEntry> entries) {
        this.mContext = context;
        this.mEntries = entries;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public LogEntry getItem(int index) {
        return mEntries.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LogEntry entry = mEntries.get(position); // get data object from array

        ViewHolder holder = null;
        TextView when = null;
        TextView what = null;
        View divider = null;

        View v = convertView;

        if (v == null) { // if convertView is null we need to load it initially
            v = mInflater.inflate(R.layout.log_row, parent, false);
            holder = new ViewHolder();

            when = (TextView)v.findViewById(R.id.tvWhen);
            what = (TextView)v.findViewById(R.id.tvWhat);
            divider = v.findViewById(R.id.divider);

            holder.when = when;
            holder.what = what;
            holder.divider = divider;

            v.setTag(holder);
        } else { // View has been initalized, use it.
            holder = (ViewHolder)convertView.getTag();
            when = holder.when;
            what = holder.what;
            divider = holder.divider;
        }

        when.setText("When: " + format.format(entry.getWhen()));
        what.setText(entry.getMessage());

        int color = 0;

        Level l = entry.getLevel();

        if(l.equals(Level.FINER)){
            color = Color.BLACK;
        }else if(l.equals(Level.FINE)){
            color = Color.BLUE;
        }else if(l.equals(Level.INFO)){
            color = Color.GREEN;
        }else if(l.equals(Level.WARNING)){
            color = Color.rgb(255,165,0);
        }else if(l.equals(Level.SEVERE)){
            color = Color.RED;
        }

        divider.setBackgroundColor(color);

        return v;
    }

    private static final class ViewHolder {
        protected TextView when = null;
        protected TextView what = null;
        protected View divider = null;
    }

}
