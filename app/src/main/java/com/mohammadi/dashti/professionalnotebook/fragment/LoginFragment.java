package com.mohammadi.dashti.professionalnotebook.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.activity.MainActivity;


public class LoginFragment extends Fragment {

    private TextInputEditText email;
    private TextInputEditText password;
    private TextView login;
    private FirebaseAuth mAuth;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.etEmail);
        password = view.findViewById(R.id.etPassword);
        login = view.findViewById(R.id.tvLogin);
        coordinatorLayout = view.findViewById(R.id.clView);
        mAuth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString().trim();
                String txt_password = password.getText().toString().trim();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Snackbar.make(view, "Email or Password is Empty", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                } else {
                    loginUser(txt_email, txt_password);

                }
            }
        });

        return view;
    }


    private void loginUser(String txtEmail, String txtPassword) {
        progressDialog.setMessage("Please Wait ....");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(txtEmail, txtPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(coordinatorLayout, "Login is Successful", Snackbar.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Snackbar.make(coordinatorLayout, e.getMessage() + "Login failed Try again", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                password.setText(null);
                                email.setText(txtEmail);
                            }
                        }).show();
            }
        });;
    }


}