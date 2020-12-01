package com.mohammadi.dashti.professionalnotebook.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.activity.UpdateShowDeleteNoteActivity;
import com.mohammadi.dashti.professionalnotebook.adapter.NoteAdapter;
import com.mohammadi.dashti.professionalnotebook.model.Note;
import com.mohammadi.dashti.professionalnotebook.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.mohammadi.dashti.professionalnotebook.util.Constants.CATEGORY;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.LANGUAGE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.NEWEST;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.OLDEST;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.SORT;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.TIME;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.TITLE;


public class HomeFragment extends Fragment implements NoteAdapter.OnRecyclerItemClick, NoteAdapter.OnRecyclerItemClickDelete {

    private RecyclerView recyclerViewNoteItem;
    private NoteAdapter noteAdapter;
    private List<Note> mNote;

    private FirebaseAuth mAuth;
    private MaterialAlertDialogBuilder alertDialogBuilder;

    private TextView sort;
    private CharSequence[] listSort;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LANGUAGE, Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        sort = view.findViewById(R.id.tvSortBy);
        sort.setOnClickListener(sortView -> sort());

        recyclerViewNoteItem = view.findViewById(R.id.recyclerItemNote);
        recyclerViewNoteItem.setHasFixedSize(true);
        recyclerViewNoteItem.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mNote = new ArrayList<>();
        noteAdapter = new NoteAdapter(getContext(), mNote);
        recyclerViewNoteItem.setAdapter(noteAdapter);
        readNote();
        noteAdapter.setOnRecyclerItemClick(this);
        noteAdapter.setOnRecyclerItemClickDelete(this);
        return view;
    }

    //get note in firebase
    private void readNote() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notes")
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        mNote.clear();
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String category = Objects.requireNonNull(snapshot.child("category").getValue()).toString();
                String note = Objects.requireNonNull(snapshot.child("note").getValue()).toString();
                Long time = (Long) snapshot.child("time").getValue();
                String title = Objects.requireNonNull(snapshot.child("title").getValue()).toString();
                Note myNote = new Note(category, title, note, time);
                mNote.add(myNote);

                //get setting from shared preferences of sort option
                String mSortSetting = sharedPreferences.getString(SORT, NEWEST);
                if (mSortSetting.equals(NEWEST)) {
                    Collections.reverse(mNote);
                } else if (mSortSetting.equals(CATEGORY)) {
                    Collections.sort(mNote, Note.BY_CATEGORY);
                } else if (mSortSetting.equals(TITLE)) {
                    Collections.sort(mNote, Note.BY_TITLE);
                } else if (mSortSetting.equals(TIME) || mSortSetting.equals(OLDEST)) {
                    Collections.sort(mNote, Note.BY_TIME);
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // when click on note go to Activity for show and update and delete
    @Override
    public void onClick(Note note) {
        Intent intentUpdateShowDeleteNote = new Intent(getActivity(), UpdateShowDeleteNoteActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentUpdateShowDeleteNote.putExtra(Constants.TITLE, note.getTitle());
        intentUpdateShowDeleteNote.putExtra(Constants.NOTE, note.getNote());
        intentUpdateShowDeleteNote.putExtra(Constants.TIME, note.getTime().toString());
        intentUpdateShowDeleteNote.putExtra(Constants.CATEGORY, note.getCategory());
        startActivity(intentUpdateShowDeleteNote);
    }

    // when click on button delete show alert dialog for delete
    @Override
    public void onClickDelete(Note note, int pos) {
        alertDialogBuilder = new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()));
        alertDialogBuilder.setTitle(R.string.titleDelete)
                .setMessage(R.string.messageDelete)
                .setIcon(R.drawable.ic_delete_warning)
                .setPositiveButton(R.string.yesDelete, (dialog, which) -> deleteNote(note))
                .setNegativeButton(R.string.noDelete, (dialog, which) -> dialog.cancel())
                .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
    }

    // when click yes delete note
    private void deleteNote(Note note) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query noteQuery = ref.child("Notes").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .orderByChild("time").equalTo(note.getTime());
        Query categoryQuery = ref.child("Category").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(note.getCategory());

        noteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNote.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                    categoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().removeValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("NoteAdapter", "onCancelledDeleteCategory", error.toException());
                        }
                    });
                }
                readNote();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NoteAdapter", "onCancelledDeleteNote", databaseError.toException());
            }
        });
    }

    // when clicked sort button
    private void sort() {
        listSort = new CharSequence[]{
                "Category",
                "Title",
                "Time",
                "Newest",
                "Oldest"
        };
        alertDialogBuilder = new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()));
        alertDialogBuilder.setTitle(R.string.sort_by)
                .setItems(listSort, (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    switch (which) {
                        case 0:
                            editor.putString(SORT, CATEGORY);
                            editor.apply();
                            readNote();
                            break;
                        case 1:
                            editor.putString(SORT, TITLE);
                            editor.apply();
                            readNote();
                            break;
                        case 2:
                            editor.putString(SORT, TIME);
                            editor.apply();
                            readNote();
                            break;
                        case 4:
                            editor.putString(SORT, OLDEST);
                            editor.apply();
                            readNote();
                            break;
                        default:
                            editor.putString(SORT, NEWEST);
                            editor.apply();
                            readNote();
                            break;
                    }
                })
                .setIcon(R.drawable.ic_sort)
                .setNeutralButton(R.string.cancel, (dialog, which) -> {
                })
                .show();

    }
}