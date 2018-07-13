package com.java1.fullsail.dwtools.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.PDFView;
import com.java1.fullsail.dwtools.R;
import com.java1.fullsail.dwtools.items.Constants;
import com.java1.fullsail.dwtools.items.File_grid;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDF_adapter extends BaseAdapter {
    Context context;
    int count;
    String url;
    PdfiumCore pdfiumCore;
    ParcelFileDescriptor fd;
    PdfDocument pdfDocument;
    public PDF_adapter(File_grid file_grid, int count, String url) {
        this.context=file_grid;
        this.count=count;
        this.url=url;
        pdfiumCore = new PdfiumCore(context);
        File file=new File(url);
        try {
            fd = context.getContentResolver().openFileDescriptor(Uri.fromFile(file), "r");
            pdfDocument = pdfiumCore.newDocument(fd);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView== null) {
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.row_pdf,null,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        File file=new File(url);
        String name = file.getName().replace(".pdf","");
        String thumPath = FOLDER+File.separator+name+position+".png";
        File thumbPath = new File(thumPath);
        if (thumbPath.exists()) {
            viewHolder.ivImage.setImageBitmap(Constants.handleSamplingAndRotationBitmap(thumPath));
        } else {
            generateImageFromPdf(name+position+".png",position);
            viewHolder.ivImage.setImageBitmap(Constants.handleSamplingAndRotationBitmap(thumPath));
        }
        //viewHolder.pdfView.fromFile(file).defaultPage(position+1).load();
        return convertView  ;
    }


    class ViewHolder {
        PDFView pdfView;
        ImageView ivImage;
        ViewHolder(View itemView) {
            ivImage = itemView.findViewById(R.id.ivImage);
            //pdfView = itemView.findViewById(R.id.pdfViewer);
        }
    }

    void generateImageFromPdf(String pdfFileName,int pageNumber) {
        try {
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            saveImage(bmp,pdfFileName);
            //pdfiumCore.closeDocument(pdfDocument); // important!
        } catch(Exception e) {
            //todo with exception
        }
    }

    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";
    private void saveImage(Bitmap bmp,String filename) {
        FileOutputStream out = null;
        try {
            File folder = new File(FOLDER);
            if(!folder.exists())
                folder.mkdirs();
            File file = new File(folder, filename);
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 80, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            //todo with exception
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //todo with exception
            }
        }
    }

}
