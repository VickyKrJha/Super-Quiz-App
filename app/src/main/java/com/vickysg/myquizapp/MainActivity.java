package com.vickysg.myquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vickysg.myquizapp.databinding.ActivityMainBinding;

import me.ibrahimsn.lib.OnItemSelectedListener;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;


    private Dialog loadingDialog ;

    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //   for Ads Starting here

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // ending here

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        setSupportActionBar(binding.toolbar);


        // Starting Here  , This codes are used for Showing Loading Dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        // This method is Calling for showing add Category dialog

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new HomeFragment());
        transaction.commit();

        auth = FirebaseAuth.getInstance();

        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i) {
                    case 0:
                        transaction.replace(R.id.content, new HomeFragment());
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        transaction.commit();
                        break;
                    case 1:
                        if (user.isAnonymous()){
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                            Toast.makeText(MainActivity.this, "See Your Rank to Login Quickly and Earn Money ", Toast.LENGTH_LONG).show();
                        }
                        transaction.replace(R.id.content, new LeaderboardsFragment());
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        transaction.commit();
                        break;
                    case 2:
                        if (user.isAnonymous()){
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                            displayAlert();
                        }else {
                            transaction.replace(R.id.content, new WalletFragment());
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                            transaction.commit();
                        }
                        break;
                    case 3:
                        if (user.isAnonymous()){
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                            displayAlert();
                        }else {
                            transaction.replace(R.id.content, new ProfileFragment());
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                            transaction.commit();
                        }
                        break;
                }
                return false;
            }
        });


    }



    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("For Earning Coins You have to Login with Your Account")
                .setMessage("Create an Account and Login to Account to Earn Coins")
                .setPositiveButton("Earn Coins", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                    }
                }).setNegativeButton("Not Interested", null);

        warning.show();
    }


    private void displayAlertProfile() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("For See Your Profile Information You have to Login Your Account")
                .setMessage("Create an Account and Login to Account to Earn Real Money")
                .setPositiveButton("Login Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                }).setNegativeButton("Not Interested", null);

        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.profile){

            if (user.isAnonymous()){
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                displayAlertProfile();
            }else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, new ProfileFragment());
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                transaction.commit();
            }
        }


        if(item.getItemId() == R.id.wallet){

            if (user.isAnonymous()){
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                displayAlert();
            }else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, new WalletFragment());
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                transaction.commit();
            }
            Toast.makeText(this, "Wallet is Clicked", Toast.LENGTH_SHORT).show();
        }

        if(item.getItemId() == R.id.logout){
            if (user.isAnonymous()){
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                displayAlert();
            }else {
                new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog)
                        .setTitle("Logout")
                        .setMessage("Are You Sure You want to Logout? ")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                loadingDialog.show();

                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_LONG).show();
                                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                                startActivity(intent);
                                finish();

                            }
                        }).setNegativeButton("CANCEL", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


}


