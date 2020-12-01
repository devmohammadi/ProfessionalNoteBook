package com.mohammadi.dashti.professionalnotebook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.mohammadi.dashti.professionalnotebook.activity.CreateNoteActivity;
import com.mohammadi.dashti.professionalnotebook.activity.UpdateShowDeleteNoteActivity;
import com.mohammadi.dashti.professionalnotebook.adapter.NoteAdapter;
import com.mohammadi.dashti.professionalnotebook.model.Note;
import com.mohammadi.dashti.professionalnotebook.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CategorySelectedFragment extends Fragment implements NoteAdapter.OnRecyclerItemClick, NoteAdapter.OnRecyclerItemClickDelete {

    private RecyclerView recyclerViewCategoryNote;
    private TextView categoryTitle;
    private NoteAdapter noteAdapter;
    private List<Note> mNote;

    private FirebaseAuth mAuth;
    private MaterialAlertDialogBuilder alertDialogBuilder;

    private String category;
    private List<String> listCategory;

    private TextView emptyMessage;
    private TextView create;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_selected, container, false);

        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            category = bundle.getString(Constants.CATEGORY);
        }

        categoryTitle = view.findViewById(R.id.tvTitleCategory);
        categoryTitle.setText(category);

        recyclerViewCategoryNote = view.findViewById(R.id.rvCategoryNote);
        recyclerViewCategoryNote.setHasFixedSize(true);
        recyclerViewCategoryNote.setLayoutManager(new GridLayoutManager(getContext(), 2));

        mNote = new ArrayList<>();
        noteAdapter = new NoteAdapter(getContext(), mNote);
        recyclerViewCategoryNote.setAdapter(noteAdapter);

        listCategory = new ArrayList<>();
        getKeyCategory(category);

        noteAdapter.setOnRecyclerItemClick(this);
        noteAdapter.setOnRecyclerItemClickDelete(this);


        emptyMessage = view.findViewById(R.id.tvEmptyMessage);
        create = view.findViewById(R.id.tvCreteNote);
        create.setOnClickListener(createView -> {
            startActivity(new Intent(getContext(), CreateNoteActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            getActivity().finish();
        });

        onBack();

        return view;
    }

    // onBackPress
    private void onBack() {
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                handelFragment(new CategoryFragment());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    //get note in firebase
    private void readCategoryNote() {
        FirebaseDatabase.getInstance().getReference().child("Notes")
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            String key = snapshot.getKey();
                            for (String keys : listCategory) {
                                if (key.equals(keys)) {
                                    String category = Objects.requireNonNull(snapshot.child("category").getValue()).toString();
                                    String note = Objects.requireNonNull(snapshot.child("note").getValue()).toString();
                                    Long time = (Long) snapshot.child("time").getValue();
                                    String title = Objects.requireNonNull(snapshot.child("title").getValue()).toString();
                                    Note myNote = new Note(category, title, note, time);
                                    mNote.add(myNote);
                            }
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

    // get kay note category
    private void getKeyCategory(String category) {
        FirebaseDatabase.getInstance().getReference().child("Category")
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    listCategory.add(snapshot.getKey());
                }
                if (listCategory.isEmpty()) {
                    emptyMessage.setVisibility(View.VISIBLE);
                    create.setVisibility(View.VISIBLE);
                } else {
                    readCategoryNote();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // start fragment
    private void handelFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
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
                readCategoryNote();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NoteAdapter", "onCancelledDeleteNote", databaseError.toException());
            }
        });
    }

}