package com.vickysg.myquizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private TextView question , noIndicator ;
    private FloatingActionButton bookmarkBtn ;
    private LinearLayout optionsContainer ;
    private Button shareBtn , nextBtn ;


    private Dialog loadingDialog ;

    private int count = 0 ;


    private List<QuestionModel> list ;


    private int position = 0 ;


    private int score = 0 ;


    private String category ;
    private String setId ;


    ////****

    ////****

    private AdView mAdView;

    private SharedPreferences preferences ;
    private SharedPreferences.Editor editor ;
    private Gson gson ;

    private List<QuestionModel> bookmarksList ;

    private static final String FILE_NAME = "QUIZZER";
    private static final String KEY_NAME = "QUESTIONS";

    private int matchedQuestionPosition ;

    ////****

    private InterstitialAd mInterstitialAd;

    ////****

    TextView timer;
    CountDownTimer timers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        //  Start method for Functionality of ToolBar With Back Button
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  Ending method Here , for Functionality of ToolBar With Back Button

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //  Ads Start here
        AdRequest adRequest1 = new AdRequest.Builder().build();

        InterstitialAd.load(QuestionsActivity.this,"ca-app-pub-3912259549278001/9918008344", adRequest1, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                mInterstitialAd.show(QuestionsActivity.this);


            }
        });
        ////****
        preferences = getSharedPreferences(FILE_NAME , Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();

        ////****



        question = findViewById(R.id.question);
        noIndicator = findViewById(R.id.no_indicator);
        bookmarkBtn = findViewById(R.id.bookmark_btn);
        shareBtn = findViewById(R.id.share_btn);
        nextBtn = findViewById(R.id.next_btn);
        optionsContainer = findViewById(R.id.options_container);


        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


//        list.add(new QuestionModel("question 1","a","b","c","d","a"));
//        list.add(new QuestionModel("question 2","a","b","c","d","d"));
//        list.add(new QuestionModel("question 3","a","b","c","d","b"));
//        list.add(new QuestionModel("question 4","a","b","c","d","c"));
//        list.add(new QuestionModel("question 5","a","b","c","d","d"));
//        list.add(new QuestionModel("question 6","a","b","c","d","a"));
//        list.add(new QuestionModel("question 7","a","b","c","d","c"));
//        list.add(new QuestionModel("question 8","a","b","c","d","a"));
//        list.add(new QuestionModel("question 9","a","b","c","d","d"));


        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMatch()){
                    bookmarksList.remove(matchedQuestionPosition);
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.ic_bookmark));
                }else{
                    bookmarksList.add(list.get(position));
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.ic_bookmarks));
                }
            }
        });


//        category = getIntent().getStringExtra("category");
        setId = getIntent().getStringExtra("setId" );



        list = new ArrayList<>();

        loadingDialog.show();

        myRef.child("SETS").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    String id = snapshot.getKey();
                    String question  = snapshot.child("question").getValue().toString();
                    String a  = snapshot.child("optionA").getValue().toString();
                    String b  = snapshot.child("optionB").getValue().toString();
                    String c  = snapshot.child("optionC").getValue().toString();
                    String d  = snapshot.child("optionD").getValue().toString();
                    String correctAns  = snapshot.child("correctANS").getValue().toString();

                    list.add(new QuestionModel(id , question ,a ,b , c , d ,correctAns , setId));
                }

                if (list.size() > 0){
                    timers.start();
                    for (int i=0 ; i < 4 ; i++){
                        optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                checkAnswer(((Button)v));

                            }
                        });
                    }

                    //     For Setting First Question
                    playAnim(question , 0 , list.get(position).getQuestion());


                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            optionsContainer.setEnabled(true);
                            optionsContainer.setAlpha(1);
                            enableOption(true);

                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.6f);

                            enableOption(true);

                            position++ ;
                            timers.start();

                            if (position == list.size()){
                                ///// Score Activity
                                timers.cancel();
                                Intent scoreIntent = new Intent(QuestionsActivity.this , ResultActivity.class);
                                scoreIntent.putExtra("score",score);
                                scoreIntent.putExtra("total",list.size());
                                startActivity(scoreIntent);
                                finish();
                                return;
                            }

                            count = 0 ;

                            playAnim(question , 0 , list.get(position).getQuestion());

                        }
                    });
                    //  else statement is below of Share Button Functionality

//                   //  Starting Here , This is only for functionality of share button

                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String body = list.get(position).getQuestion() + "\n" + "1) " +
                                    list.get(position).getA() + "\n" + "2) " +
                                    list.get(position).getB() + "\n" + "3) " +
                                    list.get(position).getC() + "\n" + "4) " +
                                    list.get(position).getD() ;
                            Intent shareIntent  = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT , "My Quiz Challange");
                            shareIntent.putExtra(Intent.EXTRA_TEXT , body);
                            startActivity(Intent.createChooser(shareIntent , "Share Via"));
                        }
                    });

                    //  else statement is below of Share Button Functionality
//                   //  Ending Here , This is only for functionality of share button

                } else {
                    finish();
                    Toast.makeText(QuestionsActivity.this, "No Questions Available", Toast.LENGTH_SHORT).show();
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });



        timer = findViewById(R.id.timer);

        timers = new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf(millisUntilFinished/1000));
            }

            public void onFinish() {
                timers.cancel();
                optionsContainer.setEnabled(false);
                optionsContainer.setAlpha(0.2f);
                enableOption(false);
                nextBtn.setEnabled(true);
                nextBtn.setAlpha(1);

//                optionsContainer.setEnabled(false);
//                optionsContainer.setAlpha(0.7f);
//
//                enableOption(true);
//                nextBtn.setEnabled(true);
            }
        };

    }



    // Start Creating Method for Animation when Clicking next button

    private void playAnim(final View view , final int value , final String data){

        // This for loop is used for Setting background color for moving next Question
        for (int i=0 ; i < 4 ; i++){
            optionsContainer.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.option_unselected));
        }

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (value == 0 && count < 4 ){

                    String option = "" ;
                    if (count == 0){
                        option = list.get(position).getA();
                    }else if (count == 1){
                        option = list.get(position).getB();
                    }else if (count == 2){
                        option = list.get(position).getC();
                    }else if (count == 3){
                        option = list.get(position).getD();
                    }

                    playAnim(optionsContainer.getChildAt(count), 0 , option);
                    count++ ;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //// Data Change
                if (value == 0){
                    try {
                        ((TextView)view).setText(data);

                        noIndicator.setText(position+1+"/"+list.size());

                        if (modelMatch()){
//                        bookmarksList.remove(matchedQuestionPosition);
                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.ic_bookmarks));
                        }else{
//                        bookmarksList.add(list.get(position));
                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.ic_bookmark));
                        }


                    }catch (ClassCastException ex){
                        ((Button)view).setText(data);
                    }

                    view.setTag(data);

                    playAnim(view , 1 , data);
                }else {
                    enableOption(true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    // Ending Here , Creating Method for Animation when Clicking next button


    private void checkAnswer(Button selectOption){
        enableOption(false);

        nextBtn.setEnabled(true);
        nextBtn.setAlpha(1);

        if (selectOption.getText().toString().equals(list.get(position).getAnswer())){
            //  Correct
            score++ ;
            selectOption.setBackground(getResources().getDrawable(R.drawable.option_right));
            timers.cancel();
        }else{
            //  InCorrect
            selectOption.setBackground(getResources().getDrawable(R.drawable.option_wrong));

            timers.cancel();
            Button correctOption = (Button) optionsContainer.findViewWithTag(list.get(position).getAnswer());
            correctOption.setBackground(getResources().getDrawable(R.drawable.option_right));
        }

    }

    private void enableOption(boolean enable){
        for (int i=0 ; i < 4 ; i++){
            optionsContainer.getChildAt(i).setEnabled(enable);
            if (enable){
                optionsContainer.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.option_unselected));
            }
        }
    }

    ////****


    @Override
    protected void onPause() {
        super.onPause();

        storeBookmarks();

    }

    private void getBookmarks(){

        String json = preferences.getString(KEY_NAME,"");

        Type type = new TypeToken<List<QuestionModel>>(){}.getType();

        bookmarksList = gson.fromJson(json,type);

        if (bookmarksList == null ){
            bookmarksList = new ArrayList<>();
        }
    }


    private boolean modelMatch(){
        boolean matched = false ;
        int i = 0 ;
        for (QuestionModel model: bookmarksList){

            if (model.getQuestion().equals(list.get(position).getQuestion())
                    && model.getAnswer().equals(list.get(position).getAnswer())
                    && model.getSet().equals(list.get(position).getSet() )){

                matched = true ;
                matchedQuestionPosition = i ;
            }

            i++ ;
        }

        return matched ;
    }

    private void storeBookmarks(){

        String json = gson.toJson(bookmarksList);

        editor.putString(KEY_NAME , json) ;

        editor.commit();
    }


    ////****



}