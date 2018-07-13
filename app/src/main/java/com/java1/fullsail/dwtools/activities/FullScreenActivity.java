package com.java1.fullsail.dwtools.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.java1.fullsail.dwtools.R;

import java.io.File;

public class FullScreenActivity extends AppCompatActivity {

    PDFView pdfViewer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        pdfViewer = findViewById(R.id.pdfViewer);

        File file = new File(getIntent().getStringExtra("file"));
        pdfViewer.fromFile(file).defaultPage(getIntent().getIntExtra("pos",0)).load();
    }
}
