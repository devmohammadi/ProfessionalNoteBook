package com.mohammadi.dashti.professionalnotebook.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohammadi.dashti.professionalnotebook.R;

import java.util.HashMap;

import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

import static com.mohammadi.dashti.professionalnotebook.util.Constants.LOGIN_SIGN_UP_DELAY;

public class CreateNoteActivity extends AppCompatActivity {

    private TextView save;
    private TextView cancel;
    private TextView category;
    private EditText title;
    private EditText note;

    private CoordinatorLayout coordinatorLayout;

    private ProgressDialog progressDialog;
    private MaterialAlertDialogBuilder alertDialogBuilder;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    private CharSequence[] listCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        save = findViewById(R.id.tvSave);
        cancel = findViewById(R.id.tvCancel);
        category = findViewById(R.id.etCategory);
        title = findViewById(R.id.etTitle);
        note = findViewById(R.id.etNotes);

        coordinatorLayout = findViewById(R.id.clView);

        progressDialog = new ProgressDialog(this);
        alertDialogBuilder = new MaterialAlertDialogBuilder(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        cancel.setOnClickListener(cancelView -> {
            startActivity(new Intent(CreateNoteActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });

        save.setOnClickListener(saveView -> saveNote());

        category.setOnClickListener(categoryView -> selectCategory());
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBackPressed() {
        alertDialogBuilder.setTitle(R.string.titleSave)
                .setMessage(R.string.messageSave)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(R.string.yesSave, (dialog, which) -> saveNote())
                .setNegativeButton(R.string.noSave, (dialog, which) -> {
                    super.onBackPressed();
                    startActivity(new Intent(CreateNoteActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                })
                .setNeutralButton(R.string.cancel, (dialog, which) -> {
                })
                .show();
    }


    private void saveNote() {
        String txtCategory = category.getText().toString().trim();
        String txtTitle = title.getText().toString().trim();
        String txtNote = note.getText().toString().trim();
        Long time = System.currentTimeMillis();
        if (txtCategory.isEmpty()) txtCategory = getString(R.string.other);
        if (TextUtils.isEmpty(txtTitle) ||
                TextUtils.isEmpty(txtNote)
        ) {
            Snackbar.make(coordinatorLayout, getString(R.string.emptyMessageField), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.ok), viewCheckEmpty -> {
                        if (TextUtils.isEmpty(txtTitle)) title.requestFocus();
                        else if (TextUtils.isEmpty(txtNote)) note.requestFocus();
                        else title.requestFocus();
                    }).show();
        } else {
            progressDialog.setMessage(getString(R.string.pleaseWait));
            progressDialog.show();

            mRootRef = mRootRef.child("Notes");
            String noteId = mRootRef.push().getKey();

            HashMap<String, Object> mapNote = new HashMap<>();
            mapNote.put("category", txtCategory);
            mapNote.put("title", txtTitle);
            mapNote.put("note", txtNote);
            mapNote.put("time", time);

            String finalTxtCategory;
            if(txtCategory.equals(getString(R.string.Programming)))
             finalTxtCategory = "Programming";
            else if(txtCategory.equals(getString(R.string.Cleaning)))
                finalTxtCategory = "Cleaning";
            else if(txtCategory.equals(getString(R.string.Lesson)))
                finalTxtCategory = "Lesson";
            else if(txtCategory.equals(getString(R.string.Movie)))
                finalTxtCategory = "Movie";
            else if(txtCategory.equals(getString(R.string.Music)))
                finalTxtCategory = "Music";
            else if(txtCategory.equals(getString(R.string.Buy)))
                finalTxtCategory = "Buy";
            else if(txtCategory.equals(getString(R.string.Other)))
                finalTxtCategory = "Other";
            else
                finalTxtCategory = "Other";

            mRootRef.child(mAuth.getCurrentUser().getUid()).child(noteId).setValue(mapNote)
                    .addOnCompleteListener(task -> {
                        DatabaseReference refCategory = FirebaseDatabase.getInstance().getReference("Category");

                        refCategory.child(mAuth.getCurrentUser().getUid()).child(finalTxtCategory).child(noteId).setValue(noteId)
                                .addOnCompleteListener(task1 -> {
                                    progressDialog.dismiss();
                                    Snackbar.make(coordinatorLayout, getString(R.string.noteSaveSuccessful), Snackbar.LENGTH_SHORT).show();

                                    //delay for start activity
                                    new Handler().postDelayed(() -> {
                                        Intent intent = new Intent(CreateNoteActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }, LOGIN_SIGN_UP_DELAY);
                                });
                    });
        }


    }

    private void selectCategory() {

        listCategory = new CharSequence[]{
                getString(R.string.Programming),
                getString(R.string.Cleaning),
                getString(R.string.Lesson),
                getString(R.string.Movie),
                getString(R.string.Music),
                getString(R.string.Buy),
                getString(R.string.Other)
        };
        alertDialogBuilder.setTitle(R.string.titleCategory)
                .setItems(listCategory, (dialog, which) -> category.setText(listCategory[which]))
                .setIcon(R.drawable.ic_list_category)
                .setNeutralButton(R.string.cancel, (dialog, which) -> {
                })
                .show();
    }
}