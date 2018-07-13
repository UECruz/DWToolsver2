//Ursula Cruz
//Framework 3
//GridAdapter
package com.java1.fullsail.dwtools.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.java1.fullsail.dwtools.R;
import com.java1.fullsail.dwtools.models.Images;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter implements View.OnClickListener {
    private final Context context;
    private final ArrayList<Images> data;
    private final ArrayList<Bitmap> im=new ArrayList<>();

    public GridAdapter(Context context, ArrayList<Images> data) {
        this.context = context;
        this.data = data;

    }

    public void addPhoto(Bitmap photo, Uri uri)
    {
        im.add(photo);
        Images photoImages=new Images();
        photoImages.setImages(photo);
        photoImages.setUri(uri);
        data.add(photoImages);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(data != null){
            return data.size();
        }
        return  0;
    }

    @Override
    public Object getItem(int position) {
        if(data != null && position >= 0 && position <data.size()){
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Images collect = (Images) getItem(position);
        if(collect != null) {
            viewHolder.imageView.setImageBitmap(collect.getImages());
        }
        viewHolder.imageView.setTag(position);
        viewHolder.imageView.setOnClickListener(this);

        return convertView;
    }
//
    @Override
    public void onClick(View v) {
        int pos=(int)v.getTag();
        Images p= data.get(pos);
        new Intent();
        Intent intent= new Intent();
        intent.setAction("android.intent.action.FullScreenActivity");
        intent.setDataAndType(p.getUri(), "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }


    private class ViewHolder {
        final ImageView imageView;
        ViewHolder(final View _layoutView){
            imageView =  _layoutView.findViewById(R.id.image);
        }

    }
}
