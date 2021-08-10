package com.google.zxing.client.android;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import barcodescanner.xservices.nl.barcodescanner.R;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

public final class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private CheckBoxPreference[] checkBoxPrefs;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceScreen preferences = getPreferenceScreen();
        preferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        this.checkBoxPrefs = findDecodePrefs(preferences, PreferencesActivity.KEY_DECODE_1D_PRODUCT, PreferencesActivity.KEY_DECODE_1D_INDUSTRIAL, PreferencesActivity.KEY_DECODE_QR, PreferencesActivity.KEY_DECODE_DATA_MATRIX, PreferencesActivity.KEY_DECODE_AZTEC, PreferencesActivity.KEY_DECODE_PDF417);
        disableLastCheckedPref();
        ((EditTextPreference) preferences.findPreference(PreferencesActivity.KEY_CUSTOM_PRODUCT_SEARCH)).setOnPreferenceChangeListener(new CustomSearchURLValidator());
    }

    private static CheckBoxPreference[] findDecodePrefs(PreferenceScreen preferences, String... keys) {
        CheckBoxPreference[] prefs = new CheckBoxPreference[keys.length];
        for (int i = 0; i < keys.length; i++) {
            prefs[i] = (CheckBoxPreference) preferences.findPreference(keys[i]);
        }
        return prefs;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        disableLastCheckedPref();
    }

    private void disableLastCheckedPref() {
        Collection<CheckBoxPreference> checked = new ArrayList<>(this.checkBoxPrefs.length);
        CheckBoxPreference[] checkBoxPreferenceArr = this.checkBoxPrefs;
        for (CheckBoxPreference pref : checkBoxPreferenceArr) {
            if (pref.isChecked()) {
                checked.add(pref);
            }
        }
        boolean disable = checked.size() <= 1;
        CheckBoxPreference[] checkBoxPreferenceArr2 = this.checkBoxPrefs;
        for (CheckBoxPreference pref2 : checkBoxPreferenceArr2) {
            pref2.setEnabled(!disable || !checked.contains(pref2));
        }
    }

    private class CustomSearchURLValidator implements Preference.OnPreferenceChangeListener {
        private CustomSearchURLValidator() {
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (isValid(newValue)) {
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesFragment.this.getActivity());
            builder.setTitle(R.string.msg_error);
            builder.setMessage(R.string.msg_invalid_value);
            builder.setCancelable(true);
            builder.show();
            return false;
        }

        private boolean isValid(Object newValue) {
            if (newValue == null) {
                return true;
            }
            String valueString = newValue.toString();
            if (valueString.isEmpty()) {
                return true;
            }
            try {
                if (new URI(valueString.replaceAll("%[st]", "").replaceAll("%f(?![0-9a-f])", "")).getScheme() != null) {
                    return true;
                }
                return false;
            } catch (URISyntaxException e) {
                return false;
            }
        }
    }
}
