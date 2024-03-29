package com.example.app_v1.activities;

import android.annotation.SuppressLint;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.res.Configuration;

import com.example.app_v1.viewmodels.DashboardActivityViewModel;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;


import com.example.app_v1.R;
import com.example.app_v1.adapters.SectionsPageAdapter;
import com.example.app_v1.fragments.MeasurementDetailsFragment;
import com.example.app_v1.models.Measurement;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.Locale;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity
{
    private static final String TAG = "DashboardActivity";

    private int selectedGreenhouseId;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;

    private DashboardActivityViewModel dashboardActivityViewModel;


    final int[] tabIcons = new int[]{R.drawable.tab_icon_temperature,R.drawable.tab_icon_humidity,R.drawable.tab_icon_co2};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            selectedGreenhouseId = bundle.getInt("selectedGreenhouseId");
        }

        setContentView(R.layout.activity_dashboard);

        //Toolbar settings
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(String.format(Locale.ENGLISH,"Dashboard - %d",selectedGreenhouseId));

        //Enable back-arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Init sections page adapter and setup with page viewer
        viewPager = findViewById(R.id.viewPager);
        sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPageAdapter);
        setupViewPager(viewPager);

        viewPager.setOffscreenPageLimit(2);

        //Set up tab layout with view pager and add icons to tabs
        tabLayout = findViewById(R.id.measurementDetailsTabs);
        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(tabIcons[0]);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(tabIcons[1]);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(tabIcons[2]);

        //Dummy tab titles
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("T: -");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("T: -");
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("-");

        dashboardActivityViewModel = ViewModelProviders.of(this).get(DashboardActivityViewModel.class);
        dashboardActivityViewModel.initViewModel(selectedGreenhouseId);

        dashboardActivityViewModel.setSelectedGreenhouseId(selectedGreenhouseId);

        dashboardActivityViewModel.getLatestMeasurementsFromRepo().observe(this, new Observer<ArrayList<Measurement>>() {
            @Override
            public void onChanged(ArrayList<Measurement> measurements)
            {
                String dateTimeNow = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.ENGLISH).format(new Date());
                getSupportActionBar().setSubtitle("Updated: " + dateTimeNow);

                if(!measurements.isEmpty())
                {
                    Objects.requireNonNull(tabLayout.getTabAt(0)).setText("");
                    Objects.requireNonNull(tabLayout.getTabAt(1)).setText("");
                    Objects.requireNonNull(tabLayout.getTabAt(2)).setText("");

                    Objects.requireNonNull(tabLayout.getTabAt(0)).setText(String.format("T: %s\u2103", measurements.get(0).getTemperature()));
                    Objects.requireNonNull(tabLayout.getTabAt(1)).setText(String.format("H: %s%%", measurements.get(0).getHumidity()));
                    Objects.requireNonNull(tabLayout.getTabAt(2)).setText(String.format("%s ppm", measurements.get(0).getcO2().toString().replace(".0","")));
                }
                else
                {
                    Objects.requireNonNull(tabLayout.getTabAt(0)).setText("T: -");
                    Objects.requireNonNull(tabLayout.getTabAt(1)).setText("T: -");
                    Objects.requireNonNull(tabLayout.getTabAt(2)).setText("-");
                }
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                dashboardActivityViewModel.setSelectedTabIndex(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(savedInstanceState != null)
        {
            Objects.requireNonNull(tabLayout.getTabAt(0)).setText("");
            Objects.requireNonNull(tabLayout.getTabAt(1)).setText("");
            Objects.requireNonNull(tabLayout.getTabAt(2)).setText("");

            Objects.requireNonNull(tabLayout.getTabAt(0)).setText(savedInstanceState.getString("temperatureTabValue"));
            Objects.requireNonNull(tabLayout.getTabAt(1)).setText(savedInstanceState.getString("humidityTabValue"));
            Objects.requireNonNull(tabLayout.getTabAt(2)).setText(savedInstanceState.getString("co2TabValue"));
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MeasurementDetailsFragment());
        adapter.addFragment(new MeasurementDetailsFragment());
        adapter.addFragment(new MeasurementDetailsFragment());

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_settings:

                Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                intent.putExtra("selectedGreenhouseId",selectedGreenhouseId);
                startActivity(intent);
                finish();
                break;

            case R.id.action_logout:

                Intent logoutIntent =new Intent(DashboardActivity.this, LogInActivity.class);
                logoutIntent.putExtra("finish", true);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
                FirebaseAuth.getInstance().signOut();
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(tabIcons[0]);
            Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(tabIcons[1]);
            Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(tabIcons[2]);
        }

        else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            tabLayout.getTabAt(0).setIcon(null);
            tabLayout.getTabAt(1).setIcon(null);
            tabLayout.getTabAt(2).setIcon(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        outState.putString("temperatureTabValue",tabLayout.getTabAt(0).getText().toString());
        outState.putString("humidityTabValue",tabLayout.getTabAt(1).getText().toString());
        outState.putString("co2TabValue",tabLayout.getTabAt(2).getText().toString());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        dashboardActivityViewModel.stopRepoRunnable();
        finish();
    }
}