package com.mohammadi.dashti.professionalnotebook.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.activity.LoginOrSignUpActivity;
import com.mohammadi.dashti.professionalnotebook.activity.MainActivity;

import java.util.HashMap;
import java.util.Objects;

import static com.mohammadi.dashti.professionalnotebook.util.Constants.LOGIN_FRAGMENT;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.LOGIN_SIGN_UP_DELAY;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.SIGN_UP_FRAGMENT;

public class SignUpFragment extends Fragment {

    private TextInputEditText email;
    private TextInputEditText name;
    private TextInputEditText password;
    private TextInputEditText confirmPassword;

    private CoordinatorLayout coordinatorLayout;

    private ProgressDialog progressDialog;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_sign_up, container, false);

        email = view.findViewById(R.id.etEmail);
        name = view.findViewById(R.id.etName);
        password = view.findViewById(R.id.etPassword);
        confirmPassword = view.findViewById(R.id.etConfirmPassword);

        TextView signUp = view.findViewById(R.id.tvSignUp);

        coordinatorLayout = view.findViewById(R.id.clView);

        progressDialog = new ProgressDialog(getContext());

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(viewSignUp -> {
            String textEmail = Objects.requireNonNull(email.getText()).toString().trim();
            String textName = Objects.requireNonNull(name.getText()).toString().trim();
            String textPassword = Objects.requireNonNull(password.getText()).toString().trim();
            String textConfirmPassword = Objects.requireNonNull(confirmPassword.getText()).toString().trim();

            //if all editText is Empty
            if (TextUtils.isEmpty(textEmail) ||
                    TextUtils.isEmpty(textName) ||
                    TextUtils.isEmpty(textPassword) ||
                    TextUtils.isEmpty(textConfirmPassword)) {
                Snackbar.make(viewSignUp, "please enter information", Snackbar.LENGTH_LONG)
                        .setAction("OK", viewCheckEmpty -> {
                        }).show();

                //if password not equals confirm password
            } else if (!textPassword.equals(textConfirmPassword)) {
                Snackbar.make(viewSignUp, "A password is not equal to confirm password", Snackbar.LENGTH_LONG).setAction("OK", view1 -> {
                    confirmPassword.setText(null);
                    password.setText(null);
                    password.requestFocus();
                }).show();
            } else {
                createUserWithEmailPassword(textEmail, textPassword, textName);
            }
        });

        return view;
    }

    private void createUserWithEmailPassword(
            String txtEmail,
            String txtPassword,
            String txtName) {

        progressDialog.setMessage("Please Wait ....");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", txtName);
                map.put("email", txtEmail);
                map.put("uid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                map.put("bio", "");
                map.put("imageUrl", "default");

                mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map);

                progressDialog.dismiss();
                Snackbar.make(coordinatorLayout, "SignUp is Successful", Snackbar.LENGTH_SHORT).show();

                //delay for start activity
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                }, LOGIN_SIGN_UP_DELAY);


            } else {
                progressDialog.dismiss();
                try {
                    throw Objects.requireNonNull(task.getException());
                }
                // if user enters exist email
                catch (FirebaseAuthUserCollisionException existEmail) {
                    Snackbar.make(coordinatorLayout, "You are already signUp. Please login", Snackbar.LENGTH_LONG)
                            .setAction("OK", view -> {
                                SIGN_UP_FRAGMENT = false;
                                LOGIN_FRAGMENT = true;
                                Intent intent = new Intent(getContext(), LoginOrSignUpActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                Objects.requireNonNull(getActivity()).finish();
                            }).show();
                }
                // if user enters wrong email.
                catch (FirebaseAuthWeakPasswordException weakPassword) {
                    Snackbar.make(coordinatorLayout, "Please choose a strong password", Snackbar.LENGTH_LONG)
                            .setAction("OK", view -> {
                                confirmPassword.setText(null);
                                password.setText(null);
                                password.requestFocus();
                            }).show();
                }
                // if user enters invalid email.
                catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                    Snackbar.make(coordinatorLayout, "The entered email is invalid", Snackbar.LENGTH_LONG)
                            .setAction("OK", view -> {
                                email.setText(null);
                                email.requestFocus();
                            }).show();
                }
                //signUp failed Try again
                catch (Exception exception) {
                    Snackbar.make(coordinatorLayout, Objects.requireNonNull(exception.getMessage()), Snackbar.LENGTH_LONG)
                            .setAction("OK", view -> {
                                name.setText(txtName);
                                confirmPassword.setText(null);
                                password.setText(null);
                                email.setText(txtEmail);
                                email.requestFocus();
                            }).show();
                }
            }

        });
    }
}