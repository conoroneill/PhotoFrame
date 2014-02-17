package uk.co.puddle.sleepcontrol;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

// See: http://developer.android.com/guide/topics/ui/settings.html#Fragment

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        // This fills in the summary correctly when we start
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        onSharedPreferenceChanged(sharedPref, SleepPrefs.PREF_DELAY_SECS);
    }

    @Override
    public void onSharedPreferenceChanged(
            SharedPreferences sharedPreferences, String key) {
        if (key.equals(SleepPrefs.PREF_DELAY_SECS)) {
            Preference delayPref = findPreference(key);
            
            String pt1 = getResources().getString(R.string.pref_delay_secs_summary_pt1);
            String pt2 = getResources().getString(R.string.pref_delay_secs_summary_pt2);
            String msg = pt1 + " " + sharedPreferences.getString(key, "") + " " + pt2;
            //Log.i(SleepLogging.TAG, msg);
            delayPref.setSummary(msg);
        }
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
}
