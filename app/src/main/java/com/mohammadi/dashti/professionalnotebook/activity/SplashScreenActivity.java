package com.mohammadi.dashti.professionalnotebook.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.mohammadi.dashti.professionalnotebook.R;

import java.util.Locale;

import static com.mohammadi.dashti.professionalnotebook.util.Constants.DAY_MODE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.EDITOR_LANGUAGE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.EN;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.FA;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.LANGUAGE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.NIGHT_DAY_MODE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.SHARED_PREFERENCES_LANGUAGE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.SPLASH_TIME_OUT;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.SHARED_PREFERENCES_NIGHT_DAY_MODE;

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
        //set language
        SHARED_PREFERENCES_LANGUAGE = getSharedPreferences(LANGUAGE, Context.MODE_PRIVATE);
        if (SHARED_PREFERENCES_LANGUAGE.getString(LANGUAGE, EN).equals(EN)) {
            setLocale("en");
        } else {
            setLocale("fa");
        }
        setContentView(R.layout.activity_screen_splash);

        //set theme
        SHARED_PREFERENCES_NIGHT_DAY_MODE = getSharedPreferences(NIGHT_DAY_MODE, Context.MODE_PRIVATE);
        if (SHARED_PREFERENCES_NIGHT_DAY_MODE.getString(NIGHT_DAY_MODE, DAY_MODE).equals(DAY_MODE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }


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
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreenActivity.this, LoginOrSignUpActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }, SPLASH_TIME_OUT);

    }

    public void setLocale(String lang) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(lang.toLowerCase());
        resources.updateConfiguration(config, dm);
    }
}