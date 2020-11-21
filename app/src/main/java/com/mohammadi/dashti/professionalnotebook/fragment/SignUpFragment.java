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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.activity.MainActivity;

import java.util.HashMap;
import java.util.Objects;

public class SignUpFragment extends Fragment {
    private TextInputEditText email;
    private TextInputEditText name;
    TextInputEditText password;
    private TextInputEditText confirmPassword;
    private CoordinatorLayout coordinatorLayout;
    private TextView signUp;
    private ProgressDialog progressDialog;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_sign_up, container, false);

        email = view.findViewById(R.id.etEmail);
        name = view.findViewById(R.id.etName);
        password = view.findViewById(R.id.etPassword);
        confirmPassword = view.findViewById(R.id.etConfirmPassword);
        signUp = view.findViewById(R.id.tvSignUp);
        coordinatorLayout = view.findViewById(R.id.clView);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = email.getText().toString().trim();
                String textName = name.getText().toString().trim();
                String textPassword = password.getText().toString().trim();
                String textConfirmPassword = confirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(textEmail) ||
                        TextUtils.isEmpty(textName) ||
                        TextUtils.isEmpty(textPassword) ||
                        TextUtils.isEmpty(textConfirmPassword)) {

                    Snackbar.make(view, "please enter information", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                    //Toast.makeText(getContext(), "please enter information", Toast.LENGTH_SHORT).show();

                } else if (!textPassword.equals(textConfirmPassword)) {
                    Snackbar.make(view, "A password is not equal to confirm password", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            confirmPassword.setText(null);
                            password.setText(null);

                        }
                    }).show();
                } else {
                    createUserWithEmailPassword(textEmail, textPassword, textName);
                }
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
        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", txtName);
                map.put("email", txtEmail);
                map.put("uid", mAuth.getCurrentUser().getUid());
                map.put("bio", "");
                map.put("imageUrl", "default");

                mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Snackbar.make(coordinatorLayout, "SignUp is Successful", Snackbar.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                } else {
                                    progressDialog.dismiss();
                                    try {
                                        throw task.getException();
                                    }
                                    // if user enters exist email
                                    catch (FirebaseAuthUserCollisionException existEmail) {
                                        Snackbar.make(coordinatorLayout, "You are already signUp. Please login", Snackbar.LENGTH_SHORT)
                                                .setAction("OK", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        LoginFragment loginFragment = new LoginFragment();
                                                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                                                                .replace(R.id.fragment_container_id, loginFragment)
                                                                .addToBackStack(null)
                                                                .commit();
                                                    }
                                                }).show();
                                    }
                                    // if user enters wrong email.
                                    catch (FirebaseAuthWeakPasswordException weakPassword) {
                                        Snackbar.make(coordinatorLayout, "Please choose a strong password", Snackbar.LENGTH_SHORT)
                                                .setAction("OK", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        confirmPassword.setText(null);
                                                        password.setText(null);
                                                    }
                                                }).show();
                                    }
                                    // if user enters invalid email.
                                    catch (FirebaseAuthInvalidUserException invalidEmail) {
                                        Snackbar.make(coordinatorLayout, "The entered email is invalid", Snackbar.LENGTH_SHORT)
                                                .setAction("OK", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        email.setText(null);
                                                    }
                                                }).show();
                                    }
                                    //signUp failed Try again
                                    catch (Exception exception) {
                                        Snackbar.make(coordinatorLayout, exception.getMessage() + "signUp failed Try again", Snackbar.LENGTH_SHORT)
                                                .setAction("OK", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Snackbar.make(coordinatorLayout, exception.getMessage() + "signUp failed Try again", Snackbar.LENGTH_SHORT)
                                                                .setAction("OK", new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        name.setText(txtName);
                                                                        confirmPassword.setText(null);
                                                                        password.setText(null);
                                                                        email.setText(txtEmail);
                                                                    }
                                                                }).show();
                                                    }
                                                }).show();
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Snackbar.make(coordinatorLayout, e.getMessage() + "signUp failed Try again", Snackbar.LENGTH_SHORT)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        name.setText(txtName);
                                        confirmPassword.setText(null);
                                        password.setText(null);
                                        email.setText(txtEmail);
                                    }
                                }).show();
                    }
                });
            }
        });

    }
}