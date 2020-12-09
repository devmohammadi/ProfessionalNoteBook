package com.mohammadi.dashti.professionalnotebook.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.activity.LoginOrSignUpActivity;
import com.mohammadi.dashti.professionalnotebook.activity.MainActivity;

import java.util.Objects;

import static com.mohammadi.dashti.professionalnotebook.util.Constants.LOGIN_FRAGMENT;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.LOGIN_SIGN_UP_DELAY;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.SIGN_UP_FRAGMENT;


public class LoginFragment extends Fragment {

    private TextInputEditText email;
    private TextInputEditText password;


    private CoordinatorLayout coordinatorLayout;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.etEmail);
        password = view.findViewById(R.id.etPassword);
        TextView login = view.findViewById(R.id.tvLogin);
        coordinatorLayout = view.findViewById(R.id.clView);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());


        login.setOnClickListener(viewLogin -> {
            String txt_email = Objects.requireNonNull(email.getText()).toString().trim();
            String txt_password = Objects.requireNonNull(password.getText()).toString().trim();

            //if all editText is Empty
            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                Snackbar.make(viewLogin, getString(R.string.enterInfo), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.ok), viewCheckEmpty -> {
                        }).show();
            } else {
                loginUser(txt_email, txt_password);
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loginUser(String txtEmail, String txtPassword) {
        progressDialog.setMessage(getString(R.string.pleaseWait));
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                Snackbar.make(coordinatorLayout, getString(R.string.loginSuccessful), Snackbar.LENGTH_SHORT).show();

                //delay for start activity
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    requireActivity().finish();
                }, LOGIN_SIGN_UP_DELAY);

            } else {
                progressDialog.dismiss();
                try {
                    throw Objects.requireNonNull(task.getException());
                }
                // if user enters invalid email.
                catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                    Snackbar.make(coordinatorLayout, getString(R.string.invalidEmail), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.ok), view -> {
                                email.setText(null);
                                email.requestFocus();
                            }).show();
                }
                //Login failed Try again
                catch (Exception exception) {
                    //if email or password incorrect
                    if (Objects.equals(exception.getMessage(), "The password is invalid or the user does not have a password.")) {
                        Snackbar.make(coordinatorLayout, getString(R.string.incorrectInfo), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.ok), view -> {
                                }).show();
                    }
                    //if email not exist
                    if (Objects.equals(exception.getMessage(), "There is no user record corresponding to this identifier. The user may have been deleted.")) {
                        Snackbar.make(coordinatorLayout, getString(R.string.existEmailLogin), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.ok), view -> {
                                    SIGN_UP_FRAGMENT = true;
                                    LOGIN_FRAGMENT = false;
                                    Intent intent = new Intent(getContext(), LoginOrSignUpActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    requireActivity().finish();
                                }).show();
                    } else {
                        Snackbar.make(coordinatorLayout, getString(R.string.tryAgain), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.ok), view -> {
                                    password.setText(null);
                                    email.setText(txtEmail);
                                    email.requestFocus();
                                }).show();
                    }
                }
            }
        });
    }
}