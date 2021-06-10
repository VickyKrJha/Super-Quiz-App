package com.vickysg.myquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView recyclerView ;

    ////****

    private SharedPreferences preferences ;
    private SharedPreferences.Editor editor ;
    private Gson gson ;

    private List<QuestionModel> bookmarksList ;

    private static final String FILE_NAME = "QUIZZER";
    private static final String KEY_NAME = "QUESTIONS";

    ////****

    private AdView mAdView;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        //  Start method for Functionality of ToolBar With Back Button
        Toolbar toolbar = findViewById(R.id.toolbar);

        //  Ads Start here
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //  Ads Start here
        AdRequest adRequest1 = new AdRequest.Builder().build();

        InterstitialAd.load(BookmarkActivity.this,"ca-app-pub-3912259549278001/9918008344", adRequest1, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                mInterstitialAd.show(BookmarkActivity.this);
            }
        });

        // Ads End Here

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bookmarks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  Ending method Here , for Functionality of ToolBar With Back Button


        ////****
        preferences = getSharedPreferences(FILE_NAME , Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();

        ////****


        recyclerView = findViewById(R.id.rv_bookmarks);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        List<QuestionModel> list = new ArrayList<>();


//        list.add(new QuestionModel("What is Your Name ? " , "" , "","","","Vicky",0));
//        list.add(new QuestionModel("What is Your Name ? " , "" , "","","","Vicky",0));
//        list.add(new QuestionModel("What is Your Name ? " , "" , "","","","Vicky",0));
//        list.add(new QuestionModel("What is Your Name ? " , "" , "","","","Vicky",0));
//        list.add(new QuestionModel("What is Your Name ? " , "" , "","","","Vicky",0));
//        list.add(new QuestionModel("What is Your Name ? " , "" , "","","","Vicky",0));
//        list.add(new QuestionModel("What is Your Name ? " , "" , "","","","Vicky",0));
//        list.add(new QuestionModel("What is Your Name ? " , "" , "","","","Vicky",0));

        BookmarksAdapter adapter = new BookmarksAdapter(bookmarksList);

        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();







    }


    @Override
    protected void onPause() {
        super.onPause();

        storeBookmarks();

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

    ////****




    private void getBookmarks(){

        String json = preferences.getString(KEY_NAME,"");

        Type type = new TypeToken<List<QuestionModel>>(){}.getType();

        bookmarksList = gson.fromJson(json,type);

        if (bookmarksList == null ){
            bookmarksList = new ArrayList<>();
        }
    }



    private void storeBookmarks(){

        String json = gson.toJson(bookmarksList);

        editor.putString(KEY_NAME , json) ;

        editor.commit();
    }


    ////****
}