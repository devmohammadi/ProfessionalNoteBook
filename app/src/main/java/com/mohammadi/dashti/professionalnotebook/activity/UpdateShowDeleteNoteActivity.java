package com.mohammadi.dashti.professionalnotebook.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.model.Note;

import java.util.HashMap;
import java.util.Objects;

import saman.zamani.persiandate.PersianDate;

import static com.mohammadi.dashti.professionalnotebook.util.Constants.*;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.LOGIN_SIGN_UP_DELAY;

public class UpdateShowDeleteNoteActivity extends AppCompatActivity {

    private TextView edit;
    private TextView delete;
    private TextView save;
    private TextView back;
    private TextView showCategory;
    private TextView showTitle;
    private TextView showNote;
    private TextView category;
    private EditText title;
    private EditText note;
    private TextView time;
    private TextView lastModify;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog progressDialog;
    private MaterialAlertDialogBuilder alertDialogBuilder;
    private FirebaseAuth mAuth;
    private CharSequence[] listCategory;
    private Note mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_show_delete_note);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        edit = findViewById(R.id.tvEdit);
        save = findViewById(R.id.tvSave);
        back = findViewById(R.id.tvBack);
        delete = findViewById(R.id.tvDelete);

        category = findViewById(R.id.etCategory);
        title = findViewById(R.id.etTitle);
        note = findViewById(R.id.etNotes);
        time = findViewById(R.id.tvTime);
        lastModify = findViewById(R.id.tvLastModify);

        showCategory = findViewById(R.id.tvCategory);
        showTitle = findViewById(R.id.tvTitle);
        showNote = findViewById(R.id.tvNotes);

        coordinatorLayout = findViewById(R.id.clView);

        progressDialog = new ProgressDialog(this);
        alertDialogBuilder = new MaterialAlertDialogBuilder(this);

        mAuth = FirebaseAuth.getInstance();

        String cat = getIntent().getStringExtra(CATEGORY);

        mNote = new Note(cat,
                getIntent().getStringExtra(TITLE),
                getIntent().getStringExtra(NOTE),
                Long.parseLong(getIntent().getStringExtra(TIME)));


        showCategory.setText(cat);
        showTitle.setText(mNote.getTitle());
        showNote.setText(mNote.getNote());

        category.setText(mNote.getCategory());
        title.setText(mNote.getTitle());
        note.setText(mNote.getNote());
        PersianDate date = new PersianDate(mNote.getTime());
        String timeModify = date.getHour() + ":" + date.getMinute() + "  " + date.getShYear() + "/" + date.getShMonth() + "/" + date.getShDay();
        time.setText(timeModify);

        back.setOnClickListener(backView -> {
            if (save.getVisibility() == View.VISIBLE) {
                alertDialogBuilder.setTitle(R.string.titleSave)
                        .setMessage(R.string.messageSave)
                        .setIcon(R.drawable.ic_warning)
                        .setPositiveButton(R.string.yesSave, (dialog, which) -> updateNote(mNote))
                        .setNegativeButton(R.string.noSave, (dialog, which) -> back())
                        .setNeutralButton(R.string.cancel, (dialog, which) -> {
                        })
                        .show();
            } else {
                back();
            }

        });


        edit.setOnClickListener(editView -> {
            edit.setVisibility(View.INVISIBLE);
            save.setVisibility(View.VISIBLE);

            showCategory.setVisibility(View.INVISIBLE);
            category.setVisibility(View.VISIBLE);

            showTitle.setVisibility(View.INVISIBLE);
            title.setVisibility(View.VISIBLE);

            showNote.setVisibility(View.INVISIBLE);
            note.setVisibility(View.VISIBLE);

            title.requestFocus();
        });

        save.setOnClickListener(editView -> {
            updateNote(mNote);

            edit.setVisibility(View.VISIBLE);
            save.setVisibility(View.INVISIBLE);

            showCategory.setVisibility(View.VISIBLE);
            category.setVisibility(View.INVISIBLE);

            showTitle.setVisibility(View.VISIBLE);
            title.setVisibility(View.INVISIBLE);

            showNote.setVisibility(View.VISIBLE);
            note.setVisibility(View.INVISIBLE);

            lastModify.setVisibility(View.VISIBLE);
        });

        delete.setOnClickListener(saveView -> alertDialogDeleteNote(mNote));

        category.setOnClickListener(categoryView -> selectCategory());
    }

    private void alertDialogDeleteNote(Note note) {
        alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle(R.string.titleDelete)
                .setMessage(R.string.messageDelete)
                .setIcon(R.drawable.ic_delete_warning)
                .setPositiveButton(R.string.yesDelete, (dialog, which) -> deleteNote(note))
                .setNegativeButton(R.string.noDelete, (dialog, which) -> dialog.cancel())
                .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
    }

    private void deleteNote(Note note) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query noteQuery = ref.child("Notes").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .orderByChild("time").equalTo(note.getTime());
        DatabaseReference categoryQuery = ref.child("Category").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(note.getCategory());
        noteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                    categoryQuery.child(snapshot.getKey()).removeValue();
                }
                //back to home fragment
                startActivity(new Intent(UpdateShowDeleteNoteActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NoteAdapter", "onCancelledDeleteNote", databaseError.toException());
            }
        });
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

    private void updateNote(Note noteUpdate) {
        String txtCategory = category.getText().toString().trim();
        String txtTitle = title.getText().toString().trim();
        String txtNote = note.getText().toString().trim();
        Long timeNote = System.currentTimeMillis();

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


            HashMap<String, Object> mapNote = new HashMap<>();
            mapNote.put("category", txtCategory);
            mapNote.put("title", txtTitle);
            mapNote.put("note", txtNote);
            mapNote.put("time", timeNote);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query noteQuery = ref.child("Notes").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .orderByChild("time").equalTo(noteUpdate.getTime());

            String finalTxtCategory;
            if (txtCategory.equals(getString(R.string.Programming)))
                finalTxtCategory = "Programming";
            else if (txtCategory.equals(getString(R.string.Cleaning)))
                finalTxtCategory = "Cleaning";
            else if (txtCategory.equals(getString(R.string.Lesson)))
                finalTxtCategory = "Lesson";
            else if (txtCategory.equals(getString(R.string.Movie)))
                finalTxtCategory = "Movie";
            else if (txtCategory.equals(getString(R.string.Music)))
                finalTxtCategory = "Music";
            else if (txtCategory.equals(getString(R.string.Buy)))
                finalTxtCategory = "Buy";
            else if (txtCategory.equals(getString(R.string.Other)))
                finalTxtCategory = "Other";
            else
                finalTxtCategory = "Other";
            DatabaseReference categoryQuery = ref.child("Category").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(finalTxtCategory);

            String finalTxtCategory1 = txtCategory;
            noteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().setValue(mapNote);
                        categoryQuery.child(snapshot.getKey()).removeValue();
                        categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                ref.child("Category").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                        .child(finalTxtCategory).child(Objects.requireNonNull(snapshot.getKey()))
                                        .setValue(Objects.requireNonNull(snapshot.getKey()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("NoteAdapter", "onCancelledUpdateCategory", error.toException());
                            }
                        });
                    }

                    progressDialog.dismiss();

                    showCategory.setText(finalTxtCategory1);
                    showTitle.setText(txtTitle);
                    showNote.setText(txtNote);
                    PersianDate date = new PersianDate(timeNote);
                    String timeModify = date.getHour() + ":" + date.getMinute() + "  " + date.getShYear() + "/" + date.getShMonth() + "/" + date.getShDay();
                    time.setText(timeModify);

                    //back to home fragment
                    Snackbar.make(coordinatorLayout, getString(R.string.updateNoteSuccessful), Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.ok), view -> {
                                //delay for start activity
                                new Handler().postDelayed(() -> {
                                    Intent intent = new Intent(UpdateShowDeleteNoteActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }, LOGIN_SIGN_UP_DELAY);
                            }).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("NoteAdapter", "onCancelledUpdateNote", databaseError.toException());
                }
            });
        }
    }


    private void back() {
        Intent intent = new Intent(UpdateShowDeleteNoteActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBackPressed() {
        if (save.getVisibility() == View.VISIBLE) {
            alertDialogBuilder.setTitle(R.string.titleSave)
                    .setMessage(R.string.messageSave)
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(R.string.yesSave, (dialog, which) -> updateNote(mNote))
                    .setNegativeButton(R.string.noSave, (dialog, which) -> {
                        super.onBackPressed();
                        back();
                    })
                    .setNeutralButton(R.string.cancel, (dialog, which) -> {
                    })
                    .show();
        } else {
            super.onBackPressed();
            back();
        }

    }
}