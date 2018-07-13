package com.java1.fullsail.dwtools.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.java1.fullsail.dwtools.R;
import com.java1.fullsail.dwtools.api.ApiClient;
import com.java1.fullsail.dwtools.api.ApiInterface;
import com.java1.fullsail.dwtools.items.Constants;
import com.java1.fullsail.dwtools.items.File_grid;
import com.java1.fullsail.dwtools.models.Uploads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileList extends AppCompatActivity {

    //this object for pick pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;

    //firebase object for storage and database
    StorageReference storageReference;
    DatabaseReference mDatabaseReference;

    //object for view PDF list
    ListView listView;
    List<Uploads> uploadsList;
    LinearLayout llProgress;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_listing);

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference=FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        initViews();
        setupClicks();
        showFile();

    }

    @SuppressLint("ResourceType")
    public void initViews() {
        uploadsList = new ArrayList<>();
        listView = findViewById(R.id.listview);
        llProgress = findViewById(R.id.llProgress);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("PDF List");
        toolbar.setBackgroundColor(getResources().getColor(R.color.MID_NIGHT));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.upload){
            getFile();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getFile(){
        //runtime permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            //choose pdf file from internal storage
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(intent,"select PDF"), PICK_PDF_CODE);
         } else {
         ActivityCompat.requestPermissions(FileList.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadFile(data.getData());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadFile(final Uri data) {
        llProgress.setVisibility(View.VISIBLE);
        final String fileName = String.valueOf(System.currentTimeMillis());
        StorageReference reference=storageReference.child(Constants.DATABASE_PATH_UPLOADS).child(fileName+ ".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        llProgress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"File Uploaded Successfully",Toast.LENGTH_SHORT).show();

                        /*String uriString=data.toString();
                        File myFile=new File(uriString);
                        String path=myFile.getAbsolutePath();
                        String displayName =  null;

                        if (uriString.startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = getContentResolver().query(data, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                                    Uploads upload = new Uploads(displayName,taskSnapshot.getDownloadUrl().toString());
                                    mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload);
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                        }*/

                        HashMap<String,Object> data = new HashMap<>();
                        data.put(fileName,taskSnapshot.getDownloadUrl().toString());
                        //Uploads upload = new Uploads(fileName,taskSnapshot.getDownloadUrl().toString());
                        mDatabaseReference.updateChildren(data);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void setupClicks() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ContextCompat.checkSelfPermission(FileList.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    Uploads uploads = uploadsList.get(position);
                    String filename = "DWTtools_"+uploads.name+".pdf";
                    File file = new File(getExternalFilesDir(null) + File.separator + filename);
                    if (file.exists()) {
                        Intent i = new Intent(FileList.this, File_grid.class);
                        i.putExtra("urlToPdf", file.getAbsolutePath());
                        startActivity(i);
                    } else {
                        downloadFile(uploads.url,file);
                    }
                }else {
                    ActivityCompat.requestPermissions(FileList.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Now You can download pdf", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You can't download pdf without permission", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode==200) {
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                //pdf file choose from internal storage
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(Intent.createChooser(intent,"select PDF"), PICK_PDF_CODE);
            } else {
                Toast.makeText(this, "You can't select pdf without permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadFile(String url, final File fileName) {

        llProgress.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.downloadFileWithDynamicUrlAsync(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(">", "server contacted and has file");

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(),fileName);

                            Log.d(">", "file download was a success? " + writtenToDisk);
                            return null;
                        }
                    }.execute();
                } else {
                    Log.d(">", "server contact failed");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            llProgress.setVisibility(View.GONE);
                            Toast.makeText(FileList.this, "server contact failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(">", "error");
                llProgress.setVisibility(View.GONE);
            }
        });
    }

    private boolean writeResponseBodyToDisk(final ResponseBody body, final File file) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[2048];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(">", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                if (fileSizeDownloaded == fileSize) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(FileList.this, File_grid.class);
                            i.putExtra("urlToPdf", file.getAbsolutePath());
                            startActivity(i);
                        }
                    });
                }
                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llProgress.setVisibility(View.GONE);
                    }
                });

            }
        } catch (IOException e) {
            return false;
        }
    }

    public void showFile() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uploadsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Uploads upload = new Uploads(postSnapshot.getKey(), postSnapshot.getValue().toString());
                    //Uploads upload =postSnapshot.getValue(Uploads.class);

                    uploadsList.add(upload);
                }

                String[] uploads = new String[uploadsList.size()];
                Log.e("Uploads :-->" , uploads.toString());
                for (int i = 0; i < uploads.length; i++) {
                    uploads[i] = uploadsList.get(i).getName();
                }

                //displaying it to list
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, uploads){
                    @SuppressLint("ResourceAsColor")
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);

                        textView.setTextColor(R.color.colorBlack);

                        return textView;
                    }
                };
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
