package com.java1.fullsail.dwtools.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.java1.fullsail.dwtools.R;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class HomePage extends AppCompatActivity {

    public Toolbar toolBar;

    Button logout ;

    TextView userName ;

    // Creating FirebaseAuth.
    FirebaseAuth auth;

    // Creating FirebaseAuth.
    FirebaseUser firebaseUser;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;
    byte [] bytes;
    volatile boolean stopWorking;
    int buffer;
    TextView printerName;


    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        userName = (TextView) findViewById(R.id.userName);
        logout = (Button) findViewById(R.id.button);
        printerName = (TextView) findViewById(R.id.printerName);

        new DrawerBuilder().withActivity(this).build();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(auth.getCurrentUser() == null){

            // Finishing current Profile activity.
            finish();

            // If user already not log in then Redirect to LoginActivity .
            Intent intent = new Intent(HomePage.this, LoginActivtiy.class);
            startActivity(intent);

            // Showing toast message.
            Toast.makeText(HomePage.this, "Please Log in to continue", Toast.LENGTH_LONG).show();

        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               setLogout();

            }
        });



        userName.setText("Welcome " + firebaseUser.getDisplayName());
    }

    public void setLogout(){
        // Destroying login season.
        auth.signOut();

        // Finishing current User Profile activity.
        finish();

        // Redirect to Login Activity after click on logout button.
        Intent intent = new Intent(HomePage.this, LoginActivtiy.class);
        startActivity(intent);

        // Showing toast message on logout.
        Toast.makeText(HomePage.this, "Logged Out Successfully.", Toast.LENGTH_LONG).show();
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

    private void selectItem(int position) {
        //menu items position as it appears in listview
        Intent i;
        switch (position) {
            case 0:
                i = new Intent(HomePage.this, HomePage.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(HomePage.this, FileList.class);
                startActivity(i);
                break;

            case 2:
                i = new Intent(HomePage.this,Gallery.class);
                startActivity(i);
                break;

            case 3:
                i = new Intent(HomePage.this,CalculationMenu.class);
                startActivity(i);
                break;

            case 4:
                i = new Intent(HomePage.this,ShoppingActvity.class);
                startActivity(i);
                break;
            //more case for you menu item
            default:
                //start default activity here
                break;
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.printer) {
            FindPrinter();
            OpenPrinter();
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void FindPrinter(){
        printerName = (TextView) findViewById(R.id.printerName);
        try{

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(bluetoothAdapter==null){
                printerName.setText(R.string.blue_print1);
            }
            if(bluetoothAdapter.isEnabled()){
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT,0);
                printerName.setText(getString(R.string.blue_print2)+ bluetoothDevice.getName());
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if(pairedDevice.size()>0){
                for(BluetoothDevice pairedDev:pairedDevice){

                        bluetoothDevice=pairedDev;
                        printerName.setText("Bluetooth Printer Attached: "+ pairedDev.getName());
                        break;

                }
            }

            printerName.setText(R.string.blue_print3);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void OpenPrinter() {
        try{

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream=bluetoothSocket.getOutputStream();
            inputStream=bluetoothSocket.getInputStream();

            BeginData();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void BeginData(){
        try{

            final Handler handler =new Handler();
            final byte x=10;
            stopWorking =false;
            buffer=0;
            bytes = new byte[1024];

            thread=new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorking){
                        try{
                            int byteAvailable = inputStream.available();
                            if(byteAvailable>0){
                                byte[] packet = new byte[byteAvailable];
                                inputStream.read(packet);

                                for(int i=0; i<byteAvailable; i++){
                                    byte b = packet[i];
                                    if(b==x){
                                        byte[] encodedByte = new byte[buffer];
                                        System.arraycopy(
                                                bytes,0,
                                                encodedByte,0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte,"US-ASCII");
                                        buffer=0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                printerName.setText(data);
                                            }
                                        });
                                    }else{
                                        bytes[buffer++]=b;
                                    }
                                }
                            }
                        }catch(Exception ex){
                            stopWorking=true;
                        }
                    }

                }
            });

            thread.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
