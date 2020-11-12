package com.mohammadi.dashti.professionalnotebook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.util.Constants;

import static com.mohammadi.dashti.professionalnotebook.util.Constants.SPLASH_TIME_OUT;

public class SplashScreenActivity extends AppCompatActivity {

    //Lines
    View first, second, third, fourth, fifth, sixth;

    //TextView
    TextView noteBook, mohammadi, dashti;

    //Animations
    Animation topAnimation, middleAnimation, bottomAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_splash);

        //Animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Lines
        first = findViewById(R.id.vFirstLine);
        second = findViewById(R.id.vSecondLine);
        third = findViewById(R.id.vThirdLine);
        fourth = findViewById(R.id.vFourthLine);
        fifth = findViewById(R.id.vFifthLine);
        sixth = findViewById(R.id.vSixthLine);

        //TextView
        noteBook = findViewById(R.id.tvNoteBook);
        mohammadi = findViewById(R.id.tvMohammadi);
        dashti = findViewById(R.id.tvDashti);

        first.setAnimation(topAnimation);
        second.setAnimation(topAnimation);
        third.setAnimation(topAnimation);
        fourth.setAnimation(topAnimation);
        fifth.setAnimation(topAnimation);
        sixth.setAnimation(topAnimation);

        noteBook.setAnimation(middleAnimation);
        mohammadi.setAnimation(bottomAnimation);
        dashti.setAnimation(bottomAnimation);

        //Splash Screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}