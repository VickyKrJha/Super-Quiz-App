package com.vickysg.myquizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vickysg.myquizapp.databinding.FragmentHomeBinding;



public class HomeFragment extends Fragment {

    private ImageView startBtn , bookmarksBtn , spinwheel , exitBtn ;




    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private RewardedAd mRewardedAd;

    FragmentHomeBinding binding ;

    FirebaseFirestore database ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Rewarded Add

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(getContext(), "ca-app-pub-3912259549278001/7798562724",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        //  Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        //  Log.d(TAG, "Ad was loaded.");
                    }
                });

        //  End Process of Rewarded Ads

        startBtn = view.findViewById(R.id.start_btn);

        bookmarksBtn = view.findViewById(R.id.bookmark_btn);

        spinwheel = view.findViewById(R.id.spinwheel);

        exitBtn = view.findViewById(R.id.exitBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(getContext() , CategoriesActivity.class);
                startActivity(categoryIntent);
            }
        });

        bookmarksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookmarksIntent = new Intent(getContext() , BookmarkActivity.class);
                startActivity(bookmarksIntent);
            }
        });


        spinwheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRewardedAd != null ) {
                    mRewardedAd.show(getActivity(), new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull @org.jetbrains.annotations.NotNull RewardItem rewardItem) {
                            Intent i = new Intent(getContext(), SpinnerActivity.class);
                            startActivity(i);
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "Ads Not Load Yet , Please Try Again Later!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                getActivity().finish();
            }
        });

        return view ;
    }

}