package com.ksaakstudio.joanna.artistsearch.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ksaakstudio.joanna.artistsearch.R;

/**
 * Created by joanna on 07/08/16.
 *
 * Adapter for Drawer List View.
 */
public class DrawerAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final int resource;
    private final String[] names;

    public DrawerAdapter(Context context, String[] names) {
        super(context, R.layout.drawer_list_item, names);
        this.mContext = context;
        this.resource = R.layout.drawer_list_item;
        this.names = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new ItemHolder();
            holder.icon = (ImageView)row.findViewById(R.id.icon);
            holder.title = (TextView)row.findViewById(R.id.drawer_textView);

            row.setTag(holder);
        }
        else
        {
            holder = (ItemHolder) row.getTag();
        }

        String name = names[position];
        holder.title.setText(name);
        if (name.equals("Search")) {
            holder.icon.setImageResource(R.drawable.ic_search_white_24dp);
        } else {
            holder.icon.setImageResource(R.drawable.ic_star_white_24dp);
        }
        holder.icon.setContentDescription(name);

        return row;
    }

    static class ItemHolder
    {
        ImageView icon;
        TextView title;
    }
}
