package com.vickysg.myquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class SetsActivity extends AppCompatActivity {

    private GridView gridView ;

    private List<String> sets ;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        //  Start method for Functionality of ToolBar With Back Button
        Toolbar toolbar = findViewById(R.id.toolbar);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  Ending method Here , for Functionality of ToolBar With Back Button



        gridView = findViewById(R.id.gridview);

//        GridAdapter adapter = new GridAdapter(16);

        sets = CategoriesActivity.list.get(getIntent().getIntExtra("position" , 0)).getSets();

        GridAdapter adapter = new GridAdapter(sets , getIntent().getStringExtra("title"));
        gridView.setAdapter(adapter);

    }

//  Start method for Functionality of ToolBar With Back Button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    //  Ending method Here , for Functionality of ToolBar With Back Button
}