package com.mohammadi.dashti.professionalnotebook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.fragment.CategoryFragment;
import com.mohammadi.dashti.professionalnotebook.fragment.HomeFragment;
import com.mohammadi.dashti.professionalnotebook.fragment.SettingsFragment;
import com.mohammadi.dashti.professionalnotebook.fragment.UserAccountFragment;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private TabLayout tableLayout;
    private View view;

    private TextView titleIcon;
    private ImageView imageIcon;

    private String home;
    private String category;
    private String setting;
    private String account;

    private FirebaseAuth mAuth;
    private CircleImageView imageProfile;
    private TextView name;
    private TextView email;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        imageProfile = findViewById(R.id.ivProfileImage);
        name = findViewById(R.id.ivName);
        email = findViewById(R.id.ivEmail);
        mAuth = FirebaseAuth.getInstance();
        readInfoUser();

        tableLayout = findViewById(R.id.tabLayout);
        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_background, null);
        titleIcon = view.findViewById(R.id.tvTitleIcon);
        imageIcon = view.findViewById(R.id.ivIcon);

        home = getString(R.string.home);
        category = getString(R.string.category);
        setting = getString(R.string.setting);
        account = getString(R.string.account);

        setCustomView(0, 1, 2, 3);
        setTextAndImageWithAnimation(home, R.drawable.ic_home);
        handelFragment(new HomeFragment());

        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:
                        setCustomView(1, 0, 2, 3);
                        setTextAndImageWithAnimation(category, R.drawable.ic_category);
                        handelFragment(new CategoryFragment());
                        break;
                    case 2:
                        setCustomView(2, 1, 0, 3);
                        setTextAndImageWithAnimation(setting, R.drawable.ic_settings);
                        handelFragment(new SettingsFragment());
                        break;
                    case 3:
                        setCustomView(3, 1, 2, 0);
                        setTextAndImageWithAnimation(account, R.drawable.ic_person);
                        handelFragment(new UserAccountFragment());
                        break;
                    default:
                        setCustomView(0, 1, 2, 3);
                        setTextAndImageWithAnimation(home, R.drawable.ic_home);
                        handelFragment(new HomeFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ImageView addNote = findViewById(R.id.ivAddNote);

        addNote.setOnClickListener(addView -> {
            startActivity(new Intent(MainActivity.this, CreateNoteActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
    }

    //get user information in firebase
    private void readInfoUser() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String txtName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String txtEmail = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                String imageUrl = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();

                name.setText(txtName);
                email.setText(txtEmail);
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setTextAndImageWithAnimation(String title, int icon) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        titleIcon.setText(title);
        imageIcon.setImageResource(icon);
        titleIcon.startAnimation(animation);
        imageIcon.startAnimation(animation);
    }

    private void setCustomView(int selectedTab, int non1, int non2, int non3) {
        Objects.requireNonNull(tableLayout.getTabAt(selectedTab)).setCustomView(view);
        Objects.requireNonNull(tableLayout.getTabAt(non1)).setCustomView(null);
        Objects.requireNonNull(tableLayout.getTabAt(non2)).setCustomView(null);
        Objects.requireNonNull(tableLayout.getTabAt(non3)).setCustomView(null);
    }

    private void handelFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }
}