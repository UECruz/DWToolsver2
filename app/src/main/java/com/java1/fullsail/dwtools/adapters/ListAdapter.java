package com.java1.fullsail.dwtools.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.java1.fullsail.dwtools.R;
import com.java1.fullsail.dwtools.models.Inventory;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Inventory> inventories;
    private final ArrayList<Bitmap> pic=new ArrayList<>();

    ListAdapter(Context context, ArrayList<Inventory> items) {
        this.context = context;
        this.inventories = items;
    }

    @Override
    public int getCount() {
        if(inventories != null){
            return inventories.size();
        }
        return  0;
    }

    @Override
    public Object getItem(int position) {
        if(inventories != null && position >= 0 && position <inventories.size()){
            return inventories.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.inventory_items, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

       Inventory collect = (Inventory) getItem(i);
        if(collect != null) {
            viewHolder.imageView.setImageBitmap(collect.getImages());
            viewHolder.textView.setText(collect.getData());
        }
        viewHolder.imageView.setTag(i);
        viewHolder.textView.setTag(i);

        return view;
    }

    private class ViewHolder {
        final ImageView imageView;
        final TextView textView;
        ViewHolder(final View _layoutView){
            imageView =  _layoutView.findViewById(R.id.image);
            textView = _layoutView.findViewById(R.id.text);
        }

    }
}
