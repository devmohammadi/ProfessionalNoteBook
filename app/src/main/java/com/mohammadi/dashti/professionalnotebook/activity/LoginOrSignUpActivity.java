package com.mohammadi.dashti.professionalnotebook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.fragment.LoginFragment;
import com.mohammadi.dashti.professionalnotebook.fragment.SignUpFragment;

public class LoginOrSignUpActivity extends AppCompatActivity implements View.OnClickListener {

    TextView signup_fragment_tv;
    TextView login_fragment_tv;
    FrameLayout fragment_container_id;

    FragmentTransaction fragmentTransaction;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_sign_up);

        signup_fragment_tv = findViewById(R.id.signup_fragment_tv);
        login_fragment_tv = findViewById(R.id.login_fragment_tv);
        fragment_container_id = findViewById(R.id.fragment_container_id);


        signup_fragment_tv.setTextColor(Color.BLACK);
        login_fragment_tv.setTextColor(Color.GRAY);
        handelFragment(new  SignUpFragment());


        signup_fragment_tv.setOnClickListener(this);
        login_fragment_tv.setOnClickListener(this);
        login_fragment_tv.setTextSize(15);


    }

    private void handelFragment(SignUpFragment signUpFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_id , signUpFragment);
        transaction.commit();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signup_fragment_tv:

                signup_fragment_tv.setTextColor(Color.BLACK);
                login_fragment_tv.setTextColor(Color.GRAY);
                login_fragment_tv.setTextSize(15);
                signup_fragment_tv.setTextSize(35);

                fragment = new SignUpFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_id, fragment);
                fragmentTransaction.commit();
                break;

            case R.id.login_fragment_tv:
                signup_fragment_tv.setTextColor(Color.GRAY);
                login_fragment_tv.setTextColor(Color.BLACK);
                login_fragment_tv.setTextSize(35);
                signup_fragment_tv.setTextSize(15);
                fragment = new LoginFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_id, fragment);
                fragmentTransaction.commit();
                break;


        }

    }
}