package com.java1.fullsail.dwtools.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.java1.fullsail.dwtools.R;
import com.java1.fullsail.dwtools.models.PaintInfoModel;

import java.text.DecimalFormat;

public class PaintCalculator extends AppCompatActivity {

    TextView textView1,textView2, textView3, textView4, textView5, textView6;
    EditText editText1, editText2, editText3;
    Button calBt;
    PaintInfoModel paintInfoModel;

    int sizeOfWall= 115;
    int gallon= 1;
    int hoursOfLabor= 8;
    int laborCostPerHour= 18;
    DecimalFormat formatter= new DecimalFormat("#0.00");
    String result1,result2, result3,result4,result5;
    String finale;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint_screen);

        mDrawerList = (ListView)findViewById(R.id.navList2);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.paintwall);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.apptool);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        textView1 = findViewById(R.id.rmCostResult);
        textView2 = findViewById(R.id.gallonResult);
        textView3 = findViewById(R.id.paintResult);
        textView4 = findViewById(R.id.hoursResult);
        textView5 = findViewById(R.id.laborResult);
        textView6 = findViewById(R.id.totalResult);

        editText1 = findViewById(R.id.rmEdit);
        editText2 = findViewById(R.id.spaceEdit);
        editText3 = findViewById(R.id.paintEdit);

        calBt = findViewById(R.id.calBtm);
        calBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaintCalculate();
            }
        });
    }

    public void PaintCalculate(){
        String roomStr = editText1.getText().toString();
        String spaceStr = editText2.getText().toString();
        String paintStr = editText3.getText().toString();


        if(roomStr != null & !"".equals(roomStr) && spaceStr != null & !"".equals(spaceStr) && paintStr != null & !"".equals(paintStr)){
            double numberOfRooms = Double.parseDouble(roomStr);
            double sizeRoom = Double.parseDouble(spaceStr);
            double priceOfPaint = Double.parseDouble(paintStr);

            double roomCostUnit = (sizeRoom * numberOfRooms)/sizeOfWall;
            result1 = String.valueOf(roomCostUnit);
            textView1.setText(result1);

            double numberOfGallons = met1(gallon, roomCostUnit);
            result2 = String.valueOf(numberOfGallons);
            textView2.setText(result2);

            double hoursRequired = met2(hoursOfLabor, roomCostUnit);
            result3 = String.valueOf(hoursRequired);
            textView3.setText(result3);

            double ans3 = met3 (priceOfPaint, numberOfGallons);
            result4 = String.valueOf(ans3);
            textView4.setText(result4);

            double ans4 = met4 (hoursRequired, laborCostPerHour);
            result5 = String.valueOf(ans4);
            textView5.setText(result5);

            double ans5 = ans3 + ans4 ;
            finale = String.valueOf(ans5);
            textView6.setText(finale);


            paintInfoModel=new PaintInfoModel(roomStr,spaceStr,paintStr,result1,result2,result3,result4,result5,finale);
        }
    }

    public static double met1( double gallon, double roomCostUnit) {
        double result;


        result = gallon *roomCostUnit;
        return result;

    }


    public static double met2 (double hoursOfLabor,double roomCostUnit) {

        double result;

        result = hoursOfLabor * roomCostUnit;

        return result;

    }

    public static double met3 (double priceOfPaint,double numberOfGallons)

    {
        double result;



        result = numberOfGallons * priceOfPaint;

        return result;

    }

    public static double met4 ( double hoursRequired, double laborCostPerHour) {

        double result;

        result = hoursRequired*laborCostPerHour;

        return result;

    }

    private void addDrawerItems() {
        String[] osArray = { "CalculationMenu", "Basic", "Drywall", "Paint"};
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
    }


    private void selectItem(int position) {
        Intent i;
        switch (position) {
            case 0:
                i = new Intent(PaintCalculator.this, CalculationMenu.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(PaintCalculator.this, CalculatorPage.class);
                startActivity(i);
                break;

            case 2:
                i = new Intent(PaintCalculator.this,DrywallCalculator.class);
                startActivity(i);
                break;

            case 3:
                i = new Intent(PaintCalculator.this,PaintCalculator.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.openDrawer, R.string.closeDrawer) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
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
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.save_data) {
            Intent intent = new Intent(this,FullScreenActivity.class);
            intent.putExtra("paint",paintInfoModel);
            Toast.makeText(getApplicationContext(),"Paint data is saved",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
