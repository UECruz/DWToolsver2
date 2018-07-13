package com.java1.fullsail.dwtools.items;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.java1.fullsail.dwtools.R;
import com.java1.fullsail.dwtools.activities.FullScreenActivity;
import com.java1.fullsail.dwtools.adapters.PDF_adapter;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class File_grid extends AppCompatActivity {

    GridView gridView;
    Intent intent;
    PDF_adapter pdf_adapter;
    String url;

    PdfiumCore pdfiumCore;
    ParcelFileDescriptor fd;
    PdfDocument pdfDocument;
    LinearLayout llProgress;

    Toolbar toolbar;
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_grid);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("PDF Thumbs");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView = findViewById(R.id.gridview);
        intent = this.getIntent();
        url = intent.getExtras().getString("urlToPdf");
        llProgress = findViewById(R.id.llProgress);

        final File file = new File(url);

        if (file.canRead()) {
            pdfiumCore = new PdfiumCore(this);
            try {
                fd = getContentResolver().openFileDescriptor(Uri.fromFile(file), "r");
                pdfDocument = pdfiumCore.newDocument(fd);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ie) {
                ie.printStackTrace();
            }

            final int count = countPages(file);
            new AsyncTask<Void,Void,Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    llProgress.setVisibility(View.VISIBLE);
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    for (int i = 0; i < count; i++) {
                        String name = file.getName().replace(".pdf","");
                        String thumPath = FOLDER+File.separator+name+i+".png";
                        File thumbPath = new File(thumPath);
                        if (!thumbPath.exists()) {
                            generateImageFromPdf(name+i+".png",i);
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    llProgress.setVisibility(View.GONE);
                    pdf_adapter=new PDF_adapter(File_grid.this,count,url);
                    gridView.setAdapter(pdf_adapter);
                    pdfiumCore.closeDocument(pdfDocument);
                }
            }.execute();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent(File_grid.this,FullScreenActivity.class);
                mIntent.putExtra("file",url);
                mIntent.putExtra("pos",position);
                startActivity(mIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void generateImageFromPdf(String pdfFileName, int pageNumber) {
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

    private int countPages(File pdfFile) {
        int count = 0;
        try {
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                count = pdfRenderer.getPageCount();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException io)
        {
            io.printStackTrace();
        }

        return count;
    }
/*
    @Override
    public void loadComplete(int nbPages) {
        Toast.makeText(File_grid.this, String.valueOf(nbPages), Toast.LENGTH_LONG).show();
    }*/
}
