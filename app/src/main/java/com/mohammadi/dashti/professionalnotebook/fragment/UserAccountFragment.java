package com.mohammadi.dashti.professionalnotebook.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.activity.LoginOrSignUpActivity;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class UserAccountFragment extends Fragment {

    private CircleImageView imageAccount;
    private EditText name;
    private TextView email;
    private EditText biography;
    private FirebaseAuth mAuth;
    private String imageUrl;
    private StorageReference mStorageRef;

    private AlertDialog alertDialog;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_account, container, false);

        imageAccount = view.findViewById(R.id.ivAccount);
        CircleImageView imageChangePicture = view.findViewById(R.id.ivChangePicture);
        name = view.findViewById(R.id.etName);
        email = view.findViewById(R.id.etEmail);
        biography = view.findViewById(R.id.textMultiLine);
        TextView logout = view.findViewById(R.id.tvLogout);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        readInfoUser();

        logout.setOnClickListener(view1 -> Logout());

        imageChangePicture.setOnClickListener(v -> CropImage.activity()
                .setAspectRatio(1, 1)
                .start(requireContext(), this));

        name.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                startDialog(getString(R.string.enterName), getString(R.string.name), name.getText().toString(), "name");
                alertDialog.show();
            }
            return true;
        });

        biography.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                startDialog(getString(R.string.enterBio), getString(R.string.biography), biography.getText().toString(), "bio");
                alertDialog.show();
            }
            return true;
        });

        return view;
    }

    private void startDialog(String txtTitle, String txtHint, String txtChange, String nameField) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.account_dialog, null, false);
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext()).setView(view);
        alertDialog = alertDialogBuilder.create();
        TextView title = view.findViewById(R.id.tvTitle);
        EditText change = view.findViewById(R.id.etChange);
        TextView cancel = view.findViewById(R.id.tvCancelDialog);
        TextView save = view.findViewById(R.id.tvSaveDialog);
        title.setText(txtTitle);
        change.setHint(txtHint);
        change.setText(txtChange);
        cancel.setText(getString(R.string.cancel));
        save.setText(getString(R.string.save));
        cancel.setOnClickListener(v1 -> alertDialog.dismiss());

        save.setOnClickListener(v1 -> {
            String textChange = change.getText().toString().trim();
            if (TextUtils.isEmpty(textChange)) {
                Snackbar.make(requireView(), getString(R.string.emptyMessage), Snackbar.LENGTH_LONG).show();
            } else {
                alertDialog.dismiss();
                saveUserInfo(nameField, textChange);
            }
        });

    }

    private void saveUserInfo(String field, String change) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.pleaseWait));
        progressDialog.show();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child(field);
        ref.setValue(change);
        progressDialog.dismiss();
        readInfoUser();
        Snackbar.make(requireView(), getString(R.string.updateUserSuccessful), Snackbar.LENGTH_LONG).show();
    }


    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(requireActivity().getContentResolver().getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            Uri imageUri = result.getUri();
            imageAccount.setImageURI(imageUri);

            if (resultCode == RESULT_OK) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getString(R.string.pleaseWait));
                progressDialog.show();

                if (imageUri != null) {
                    final StorageReference filePath = mStorageRef.child("imageProfile")
                            .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + getFileExtension(imageUri));

                    StorageTask uploadTask = filePath.putFile(imageUri);
                    uploadTask.continueWithTask((Continuation) task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        imageUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                                .child(mAuth.getCurrentUser().getUid()).child("imageUrl");

                        ref.setValue(imageUrl);

                        progressDialog.dismiss();

                        Snackbar.make(requireView(), getString(R.string.updateUserSuccessful), Snackbar.LENGTH_LONG).show();

                    }).addOnFailureListener(e -> Snackbar.make(requireView(), getString(R.string.tryAgain), Snackbar.LENGTH_LONG).show());
                } else {
                    Snackbar.make(requireView(), getString(R.string.noImageSelected), Snackbar.LENGTH_LONG).show();

                }
            }
        } else {
            Snackbar.make(requireView(), getString(R.string.tryAgain), Snackbar.LENGTH_LONG).show();
        }
    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), LoginOrSignUpActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        requireActivity().finish();
    }

    //get user information in firebase
    private void readInfoUser() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String txtName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String txtEmail = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                String imageUrl = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();
                String txtBiography = Objects.requireNonNull(snapshot.child("bio").getValue()).toString();

                name.setText(txtName);
                email.setText(txtEmail);
                biography.setText(txtBiography);
                if (!imageUrl.equals("default")) {
                    Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .into(imageAccount);
                } else {
                    imageAccount.setImageResource(R.drawable.ic_person);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}