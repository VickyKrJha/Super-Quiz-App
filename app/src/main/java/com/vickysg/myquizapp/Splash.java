package com.vickysg.myquizapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        TextView appname = findViewById(R.id.textView3);

//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splashscreenanimation);

//        Animation topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        Animation bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);


        appname.startAnimation(bottomAnimation);
        
        fAuth = FirebaseAuth.getInstance();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                
                // check if user is logged in 
                if(fAuth.getCurrentUser() != null){
                    startActivity(new Intent(Splash.this,MainActivity.class));
                    finish();
                }else {
                    // create new anonymous account 
                    fAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Splash.this, "Logged in With Temporary Account.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Splash.this,MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Splash.this, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Splash.this,MainActivity.class));
                            finish();
                        }
                    });
                }
                
                
                
                
            }
        },1000);
    }
}
