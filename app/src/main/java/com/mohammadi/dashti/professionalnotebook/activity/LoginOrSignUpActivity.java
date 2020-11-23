package com.mohammadi.dashti.professionalnotebook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.fragment.LoginFragment;
import com.mohammadi.dashti.professionalnotebook.fragment.SignUpFragment;

import static com.mohammadi.dashti.professionalnotebook.util.Constants.LOGIN_FRAGMENT;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.SIGN_UP_FRAGMENT;

public class LoginOrSignUpActivity extends AppCompatActivity implements View.OnClickListener {

    TextView signUp;
    TextView login;
    FrameLayout frameLayout;

    FragmentTransaction fragmentTransaction;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_sign_up);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        signUp = findViewById(R.id.tvSignUp);
        login = findViewById(R.id.tvLogin);
        frameLayout = findViewById(R.id.flContainer);


        if (SIGN_UP_FRAGMENT) {
            signUp.setTextColor(Color.BLACK);
            login.setTextColor(Color.GRAY);
            login.setTextSize(15);
            handelFragmentSignUp(new SignUpFragment());
        }
        if (LOGIN_FRAGMENT) {
            signUp.setTextColor(Color.GRAY);
            login.setTextColor(Color.BLACK);
            signUp.setTextSize(15);
            handelFragmentLogin(new LoginFragment());
        }

        signUp.setOnClickListener(this);
        login.setOnClickListener(this);

    }

    private void handelFragmentSignUp(SignUpFragment signUpFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContainer, signUpFragment);
        transaction.commit();
    }

    private void handelFragmentLogin(LoginFragment loginFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContainer, loginFragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginOrSignUpActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSignUp:

                signUp.setTextColor(Color.BLACK);
                login.setTextColor(Color.GRAY);
                login.setTextSize(15);
                signUp.setTextSize(35);

                fragment = new SignUpFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment);
                fragmentTransaction.commit();
                break;

            case R.id.tvLogin:
                signUp.setTextColor(Color.GRAY);
                login.setTextColor(Color.BLACK);
                login.setTextSize(35);
                signUp.setTextSize(15);
                fragment = new LoginFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment);
                fragmentTransaction.commit();
                break;


        }

    }
}