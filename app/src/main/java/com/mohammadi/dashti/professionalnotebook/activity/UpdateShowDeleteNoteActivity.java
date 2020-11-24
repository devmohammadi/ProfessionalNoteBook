package com.mohammadi.dashti.professionalnotebook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.mohammadi.dashti.professionalnotebook.R;

public class UpdateShowDeleteNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_show_delete_note);
        overridePendingTransition(android.R.anim.slide_in_left , android.R.anim.slide_out_right);

    }
}