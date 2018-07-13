package com.java1.fullsail.dwtools.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.java1.fullsail.dwtools.R;
import com.java1.fullsail.dwtools.adapters.GridAdapter;
import com.java1.fullsail.dwtools.models.Images;
import com.java1.fullsail.dwtools.models.PictureModel;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class Gallery extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final int PICK_IMAGE = 2;
    static boolean aboolean =true;
    private GridAdapter gridAdapter;
    private static final int REQUEST_TAKE_PICTURE = 0x01001;
    private final ArrayList<Images> _collection = new ArrayList<>();
    private String currentFile;
    private Uri uri;
    private final int PERMISSIONS_REQUEST_USE_CAMERA = 7;
    private final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 8;
    PictureModel pictureModel;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private String mActivityTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.apptool);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);


        GridView gridView = findViewById(R.id.grid);
        gridAdapter=new GridAdapter(Gallery.this,_collection);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Objects.equals(getIntent().getAction(), "android..action.MAIN"))
            aboolean = launchUP();
        else
            aboolean = launchUP();
        Toast.makeText(this, ""+ aboolean, Toast.LENGTH_SHORT).show();
    }


    private boolean launchUP() {
        if (getIntent() == null) {
            return false;
        }
        boolean isActionMain = Intent.ACTION_MAIN.equals(getIntent().getAction());
        Set<String> categories = getIntent().getCategories();
        boolean isCategoryLauncher = categories != null && categories.contains(Intent.CATEGORY_LAUNCHER);
        return isActionMain && isCategoryLauncher;
    }

    private Uri getOutputUri() {
        //get the URI to return to another application
        File protectedStorage = Environment.getExternalStorageDirectory();
        currentFile = new SimpleDateFormat("yyyyMMddHHmm", Locale.US).format(new Date())+"te.jpg";
        File imageFile = new File(protectedStorage, currentFile);
        try{
            //noinspection ResultOfMethodCallIgnored
            imageFile.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT<24) {
            return  Uri.fromFile(imageFile);
        }
        else {
            return FileProvider.getUriForFile(Gallery.this, "com.java1.fullsail.dwtools", imageFile);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Images p=(Images) parent.getAdapter().getItem(position);
        pictureModel = new PictureModel(p.getUri());
        Intent intent = new Intent(Gallery.this,FullScreenActivity.class);
        intent.putExtra("photo",pictureModel);
        Toast.makeText(getApplicationContext(),"Image saved",Toast.LENGTH_SHORT).show();
        intent.setDataAndType(p.getUri(), "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_take_picture) {
            initAction();
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initAction(){
        if(Build.VERSION.SDK_INT<24) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputUri());
            startActivityForResult(intent, REQUEST_TAKE_PICTURE);

        }
        else
        {
            checkCAMERAPermissions();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==PICK_IMAGE) {

            getImageFromGallery();

        }

        if (resultCode == RESULT_OK) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 8;

            Bitmap bmp = BitmapFactory.decodeFile(getOutputFilePath());
            gridAdapter.addPhoto(bmp, uri);
        }

    }

    private String getOutputFilePath() {
        //get a patch to save an image to
        File protectedStorage = Environment.getExternalStorageDirectory();
        File imageFile = new File(protectedStorage, currentFile);
        try{
            //noinspection ResultOfMethodCallIgnored
            imageFile.createNewFile();
            if(Build.VERSION.SDK_INT<24) {
                uri = Uri.fromFile(imageFile);
            }
            else {
                uri = FileProvider.getUriForFile(Gallery.this,
                        "com.fullsail.android.dwtool",
                        imageFile);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return imageFile.getAbsolutePath();
    }

    private void getImageFromGallery(){

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE);

    }

    private void checkCAMERAPermissions() {

        if (ContextCompat.checkSelfPermission(Gallery.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Gallery.this,
                    Manifest.permission.CAMERA)) {

                Toast.makeText(Gallery.this, "You need to grant permission to use camera to take picture", Toast.LENGTH_LONG).show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_USE_CAMERA);
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_USE_CAMERA);
                }

            }
        } else {
            checkStoragePermissions();
        }

    }

    private void checkStoragePermissions() {

        if (ContextCompat.checkSelfPermission(Gallery.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Gallery.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(Gallery.this, "You need to grant permission to write external storage to this app "
                        , Toast.LENGTH_LONG).show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }

            }
        }
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputUri());
            startActivityForResult(intent, REQUEST_TAKE_PICTURE);
        }

    }

    private void addDrawerItems() {
        String[] osArray = { "Home", "Files", "Photo", "Calculation","Shopping"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.openDrawer, R.string.closeDrawer) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectItem(int position) {
        //menu items position as it appears in listview
        Intent i;
        switch (position) {
            case 0:
                i = new Intent(Gallery.this, HomePage.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(Gallery.this, FileList.class);
                startActivity(i);
                break;

            case 2:
                i = new Intent(Gallery.this,Gallery.class);
                startActivity(i);
                break;

            case 3:
                i = new Intent(Gallery.this,CalculationMenu.class);
                startActivity(i);
                break;

            case 4:
                i = new Intent(Gallery.this,ShoppingActvity.class);
                startActivity(i);
                break;
            //more case for you menu item
            default:
                //start default activity here
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_USE_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkStoragePermissions();


                } else {
                    Toast.makeText(Gallery.this,"You cannot take pictures without giving permission to the " +
                            "device to access camera.",Toast.LENGTH_LONG).show();
                }
                return;
            }

            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputUri());
                    startActivityForResult(intent, REQUEST_TAKE_PICTURE);

                } else {
                    Toast.makeText(Gallery.this,"You cannot store pictures without giving permission to the " +
                            "device to access storage.",Toast.LENGTH_LONG).show();

                }
            }

        }
    }

}
