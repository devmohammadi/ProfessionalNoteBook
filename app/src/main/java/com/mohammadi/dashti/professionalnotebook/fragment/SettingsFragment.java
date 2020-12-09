package com.mohammadi.dashti.professionalnotebook.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohammadi.dashti.professionalnotebook.R;
import com.mohammadi.dashti.professionalnotebook.activity.MainActivity;
import com.mohammadi.dashti.professionalnotebook.activity.SplashScreenActivity;

import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.DAY_MODE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.EDITOR_LANGUAGE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.EDITOR_NIGHT_DAY_MODE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.EN;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.FA;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.LANGUAGE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.NIGHT_DAY_MODE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.NIGHT_MODE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.SHARED_PREFERENCES_LANGUAGE;
import static com.mohammadi.dashti.professionalnotebook.util.Constants.SHARED_PREFERENCES_NIGHT_DAY_MODE;

public class SettingsFragment extends PreferenceFragmentCompat {

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        loadSettings();
    }

    private void loadSettings() {
        //set theme
        if (SHARED_PREFERENCES_NIGHT_DAY_MODE.getString(NIGHT_DAY_MODE, DAY_MODE).equals(DAY_MODE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        //set language
        if (SHARED_PREFERENCES_LANGUAGE.getString(LANGUAGE, EN).equals(EN)) {
            setLocaleDef("en");
        } else {
            setLocaleDef("fa");
        }

        //set theme
        SwitchPreferenceCompat switchNightInstant = findPreference("NIGHT");
        switchNightInstant.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean yes = (boolean) newValue;
            EDITOR_NIGHT_DAY_MODE = SHARED_PREFERENCES_NIGHT_DAY_MODE.edit();
            EDITOR_NIGHT_DAY_MODE.putString(NIGHT_DAY_MODE, DAY_MODE).apply();
            if (yes) {
                EDITOR_NIGHT_DAY_MODE.putString(NIGHT_DAY_MODE, NIGHT_MODE).apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                EDITOR_NIGHT_DAY_MODE.putString(NIGHT_DAY_MODE, DAY_MODE).apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            return true;
        });

        // set language
        ListPreference listLanguage = findPreference("LANGUAGE");
        listLanguage.setOnPreferenceChangeListener((preference, newValue) -> {
            EDITOR_LANGUAGE = SHARED_PREFERENCES_LANGUAGE.edit();
            EDITOR_LANGUAGE.putString(LANGUAGE, EN).apply();
            String item = (String) newValue;
            if (preference.getKey().equals("LANGUAGE")) {
                if ("fa-IR".equals(item)) {
                    EDITOR_LANGUAGE.putString(LANGUAGE, FA).apply();
                    setLocale("fa");
                } else {
                    EDITOR_LANGUAGE.putString(LANGUAGE, EN).apply();
                    setLocale("en");
                }
            }
            return true;
        });


        //send feedback
        EditTextPreference editTextPreference = findPreference("FEEDBACK");
        editTextPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String feedback = (String) newValue;
            if (preference.getKey().equals("FEEDBACK")) {
                if (!TextUtils.isEmpty(feedback)) {
                    saveFeedBack(feedback);
                }
            }
            return true;
        });
    }

    private void saveFeedBack(String feedback) {
        mRootRef.child("FeedBack").child(mAuth.getCurrentUser().getUid())
                .push().setValue(feedback).addOnCompleteListener(task ->
                Snackbar.make(getView(), getString(R.string.sendFeedback), Snackbar.LENGTH_SHORT).show());
    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        requireActivity().finish();
    }


    public void setLocaleDef(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

}