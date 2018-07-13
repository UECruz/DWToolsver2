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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.java1.fullsail.dwtools.R;
import com.java1.fullsail.dwtools.models.DrywallModel;
import com.mikepenz.materialdrawer.DrawerBuilder;


public class DrywallCalculator extends AppCompatActivity {

    EditText h1,rm;
    EditText l1,w1;
    EditText h2,w2;
    EditText h3,w3;

    TextView results,wal,exclude, ceil, soiling;
    Button cal;
    DrywallModel drywallModel;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drywall_screen);

        new DrawerBuilder().withActivity(this).build();

        mDrawerList = (ListView)findViewById(R.id.navList2);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drywall);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.apptool);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        h1 = findViewById(R.id.height);
        rm = findViewById(R.id.rmPerm);
        l1 = findViewById(R.id.length);
        w1 = findViewById(R.id.width);

        h2 = findViewById(R.id.height2);
        w2 = findViewById(R.id.width2);

        h3 = findViewById(R.id.height3);
        w3 = findViewById(R.id.width3);

        results = findViewById(R.id.area2);
        wal = findViewById(R.id.wallTotal);
        exclude = findViewById(R.id.excludedTotal);
        ceil = findViewById(R.id.ceilTotal);
        soiling = findViewById(R.id.slopTotal);


        cal = findViewById(R.id.calBtm);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate();
            }
        });

    }


    private void calculate(){
        String height1 = h1.getText().toString();
        String room = rm.getText().toString();

        String length = l1.getText().toString();
        String width1 = w1.getText().toString();

        String height2 = h2.getText().toString();
        String width2 = w2.getText().toString();

        String height3 = h3.getText().toString();
        String width3 = w3.getText().toString();

        String current1;
        String current2;
        String current3;
        String current4;
        String finalResult;
        float result1;
        float result2;
        float result3;
        float result4;
        float finale = 0;

        if(!"".equals(height1) && !"".equals(room) && !"".equals(length) && width1 != null & !"".equals(width1) && height2 != null & !"".equals(height2) && width2 != null & !"".equals(width2) && height3 != null & !"".equals(height3) && width3 != null & !"".equals(width3)){

            float heightValue = Float.parseFloat(height1);
            float roomValue = Float.parseFloat(room);

            result1 = heightValue * roomValue;
            current1 = String.valueOf(result1);

            wal.setText(current1);

            float lengthValue = Float.parseFloat(length);
            float widthValue = Float.parseFloat(width1);

            result2 = lengthValue * widthValue;
            current2 = String.valueOf(result2);

            exclude.setText(current2);

            float h2Value = Float.parseFloat(height2);
            float w2Value = Float.parseFloat(width2);

            result3 = h2Value * w2Value;
            current3 = String.valueOf(result3);
            ceil.setText(current3);

            float h3Value = Float.parseFloat(height3);
            float w3Value = Float.parseFloat(width3);

            result4 = h3Value * w3Value;
            current4 = String.valueOf(result4);
            soiling.setText(current4);

            finale = result1 + result2 + result3 + result4;
            finalResult = String.valueOf(finale);
            results.setText(finalResult);


            drywallModel = new DrywallModel(finalResult,current2,current1,current3,current4);

        }
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
                i = new Intent(DrywallCalculator.this, CalculationMenu.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(DrywallCalculator.this, CalculatorPage.class);
                startActivity(i);
                break;

            case 2:
                i = new Intent(DrywallCalculator.this,DrywallCalculator.class);
                startActivity(i);
                break;

            case 3:
                i = new Intent(DrywallCalculator.this,PaintCalculator.class);
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
            intent.putExtra("drywall",drywallModel);
            Toast.makeText(getApplicationContext(),"drywall data is saved",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
