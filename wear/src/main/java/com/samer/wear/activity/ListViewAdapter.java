package com.samer.wear.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Note> {


    public ListViewAdapter(Context context, int resource, List<Note> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(this.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);

        Note note = getItem(position);

        TextView title = (TextView) convertView.findViewById(android.R.id.text1);

        title.setText(note.getTitle());

        return convertView;
    }
}