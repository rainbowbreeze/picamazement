package it.rainbowbreeze.picama.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import it.rainbowbreeze.picama.R;
import it.rainbowbreeze.picama.data.AppPrefsManager;

/**
 * Created by alfredomorresi on 05/12/14.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String PREFKEY_SYNC_FREQUENCY = "pref_syncFrequency";
    private Preference mPreSyncFrequency;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(AppPrefsManager.PREFS_FILE_NAME);
        prefMgr.setSharedPreferencesMode(PreferenceActivity.MODE_PRIVATE);
        addPreferencesFromResource(R.xml.pref_general);

        mPreSyncFrequency = findPreference(PREFKEY_SYNC_FREQUENCY);
        setSyncFrequencySummary(getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PREFKEY_SYNC_FREQUENCY.equals(key)) {
            setSyncFrequencySummary(sharedPreferences);
        }
    }

    private void setSyncFrequencySummary(SharedPreferences sharedPreferences) {
        // Finds the index of the value in the array of potential values
        String prefValue = sharedPreferences.getString(PREFKEY_SYNC_FREQUENCY, "");
        String[] values = getActivity().getApplicationContext().getResources().getStringArray(R.array.pref_syncFrequency_entryValues);
        int i;
        for (i=0; i < values.length; i++) {
            if (values[i].equals(prefValue)) break;
        }
        if (i < values.length) {
            String[] entries = getActivity().getResources().getStringArray(R.array.pref_syncFrequency_entries);
            mPreSyncFrequency.setSummary(entries[i]);
        } else {
            mPreSyncFrequency.setSummary(getActivity().getString(R.string.pref_syncFrequency_valueNotSet));
        }
    }
}