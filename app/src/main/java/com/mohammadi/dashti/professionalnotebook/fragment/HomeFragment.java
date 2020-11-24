package com.mohammadi.dashti.professionalnotebook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.List;


public class HomeFragment extends Fragment implements NoteAdapter.OnRecyclerItemClick, NoteAdapter.OnRecyclerItemClickDelete {

    private RecyclerView recyclerViewNoteItem;
    private NoteAdapter noteAdapter;
    private List<Note> mNote;

    private FirebaseAuth mAuth;
    private MaterialAlertDialogBuilder alertDialogBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();

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
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                mNote.clear();
                String key = snapshot.getKey();
                reference.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String category = snapshot.child("category").getValue().toString();
                        String note = snapshot.child("note").getValue().toString();
                        Long time = (Long) snapshot.child("time").getValue();
                        String title = snapshot.child("title").getValue().toString();
                        Note myNote = new Note(category, title, note, time);
                        mNote.add(myNote);
                        noteAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
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


    @Override
    public void onClick(Note note) {
        Intent intentUpdateShowDeleteNote = new Intent(getActivity(), UpdateShowDeleteNoteActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentUpdateShowDeleteNote.putExtra(Constants.TITLE, note.getTitle());
        intentUpdateShowDeleteNote.putExtra(Constants.NOTE, note.getNote());
        intentUpdateShowDeleteNote.putExtra(Constants.TIME, note.getTime());
        intentUpdateShowDeleteNote.putExtra(Constants.CATEGORY, note.getCategory());
        startActivity(intentUpdateShowDeleteNote);
    }


    @Override
    public void onClickDelete(Note note, int pos) {
        alertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        alertDialogBuilder.setTitle(R.string.titleDelete)
                .setMessage(R.string.messageDelete)
                .setIcon(R.drawable.ic_delete_warning)
                .setPositiveButton(R.string.yesDelete, (dialog, which) -> deleteNote(note, pos))
                .setNegativeButton(R.string.noDelete, (dialog, which) -> dialog.cancel())
                .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
    }


    private void deleteNote(Note note, int pos) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query noteQuery = ref.child("Notes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NoteAdapter", "onCancelledDeleteNote", databaseError.toException());
            }
        });
    }


}