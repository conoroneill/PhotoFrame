package uk.co.puddle.photoframe.prefs;

import uk.co.puddle.photoframe.Logging;
import uk.co.puddle.photoframe.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

// See: http://developer.android.com/guide/topics/ui/settings.html#Fragment

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        // This fills in the summary correctly when we start
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        onSharedPreferenceChanged(sharedPref, MyPrefs.PREF_DELAY_SECS);
        onSharedPreferenceChanged(sharedPref, MyPrefs.PREF_DISPLAY_ORDER);
        onSharedPreferenceChanged(sharedPref, MyPrefs.PREF_FONT_SIZE);
        onSharedPreferenceChanged(sharedPref, MyPrefs.PREF_FONT_COLOR);
    }

    @Override
    public void onSharedPreferenceChanged(
            SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case MyPrefs.PREF_DELAY_SECS: {
                Preference delayPref = findPreference(key);

                String currentValue = sharedPreferences.getString(key, "");
                String pt1 = getResources().getString(R.string.pref_delay_secs_summary_pt1);
                String pt2 = getResources().getString(R.string.pref_delay_secs_summary_pt2);
                String msg = pt1 + " " + currentValue + " " + pt2;
                Log.d(Logging.TAG, msg);
                delayPref.setSummary(msg);
                break;
            }
            case MyPrefs.PREF_DISPLAY_ORDER: {
                Preference orderPref = findPreference(key);

                String currentValue = sharedPreferences.getString(key, "");
                String currentLabel = getCurrentOrderLabel(currentValue);
                String pt1 = getResources().getString(R.string.pref_display_order_summary_pt1);
                String pt2 = getResources().getString(R.string.pref_display_order_summary_pt2);
                String msg = pt1 + " " + currentLabel + " " + pt2;
                Log.d(Logging.TAG, msg);
                orderPref.setSummary(msg);
                break;
            }
            case MyPrefs.PREF_FONT_SIZE: {
                Preference fontSizePref = findPreference(key);

                String currentValue = sharedPreferences.getString(key, "");
                String pt1 = getResources().getString(R.string.pref_text_font_size_summary_pt1);
                String pt2 = getResources().getString(R.string.pref_text_font_size_summary_pt2);
                String msg = pt1 + " " + currentValue + " " + pt2;
                Log.d(Logging.TAG, msg);
                fontSizePref.setSummary(msg);
                break;
            }
            case MyPrefs.PREF_FONT_COLOR: {
                Preference colorPref = findPreference(key);

                String currentValue = sharedPreferences.getString(key, "");
                String currentLabel = getCurrentColorLabel(currentValue);
                String pt1 = getResources().getString(R.string.pref_font_color_summary_pt1);
                String pt2 = getResources().getString(R.string.pref_font_color_summary_pt2);
                String msg = pt1 + " " + currentLabel + " " + pt2;
                Log.d(Logging.TAG, msg);
                colorPref.setSummary(msg);
                break;
            }
        }
    }
    
    private String getCurrentOrderLabel(String currentValue) {
        String[] values = getResources().getStringArray(R.array.display_order_values);
        String[] labels = getResources().getStringArray(R.array.display_order_labels);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(currentValue)) { return labels[i]; }
        }
        return "Unknown";
    }

    private String getCurrentColorLabel(String currentValue) {
        String[] values = getResources().getStringArray(R.array.font_color_values);
        String[] labels = getResources().getStringArray(R.array.font_color_labels);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(currentValue)) { return labels[i]; }
        }
        return "Unknown";
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
