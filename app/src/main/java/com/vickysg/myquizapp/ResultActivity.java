package com.vickysg.myquizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vickysg.myquizapp.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding binding;

    FirebaseAuth auth ;
    FirebaseUser user;

    int POINTS = 10 ;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //  Ads Start here
        AdRequest adRequest1 = new AdRequest.Builder().build();

        InterstitialAd.load(ResultActivity.this,"ca-app-pub-3912259549278001/9918008344", adRequest1, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                mInterstitialAd.show(ResultActivity.this);
            }
        });

        // Ads End Here


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        int correctAnswers = getIntent().getIntExtra("score",0);
        int totalQuestions = getIntent().getIntExtra("total",0);

        long points = correctAnswers * POINTS ;

        binding.score.setText(String.format("%d/%d",correctAnswers,totalQuestions));


        // we can not pass the integers directly
        binding.earnedCoins.setText(String.valueOf(points));


        //  Start method for Functionality of ToolBar With Back Button
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("You Earned "+points+" Coins");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  Ending method Here , for Functionality of ToolBar With Back Button


        binding.restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, CategoriesActivity.class);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                startActivity(intent);
                finish();
            }
        });

        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = "I won "+points+" Coins and Earn Real Money \nSo , Play Unlimited and Earn Unlimited Real Money \n Hurry up go and Download this My Quiz App from PlayStore Now \n it's Totally Free";
                Intent shareIntent  = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT , "My Quiz Challange");
                shareIntent.putExtra(Intent.EXTRA_TEXT , body);
                startActivity(Intent.createChooser(shareIntent , "Share Via"));
                finish();
            }
        });

        if (user.isAnonymous()){
            Toast.makeText(this, "For Earning Real Money You have to Login First", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, String.valueOf(points) + " Coins are Successfully Added in Your Account...", Toast.LENGTH_LONG).show();

            FirebaseFirestore database = FirebaseFirestore.getInstance();

            database.collection("users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .update("coins", FieldValue.increment(points));
        }
    }
}