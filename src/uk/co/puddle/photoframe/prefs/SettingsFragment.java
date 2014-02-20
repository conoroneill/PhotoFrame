package uk.co.puddle.photoframe.prefs;

import uk.co.puddle.photoframe.Logging;
import uk.co.puddle.photoframe.R;
import uk.co.puddle.photoframe.R.array;
import uk.co.puddle.photoframe.R.string;
import uk.co.puddle.photoframe.R.xml;
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
    }

    @Override
    public void onSharedPreferenceChanged(
            SharedPreferences sharedPreferences, String key) {
        if (key.equals(MyPrefs.PREF_DELAY_SECS)) {
            Preference delayPref = findPreference(key);
            
            String currentValue = sharedPreferences.getString(key, "");
            String pt1 = getResources().getString(R.string.pref_delay_secs_summary_pt1);
            String pt2 = getResources().getString(R.string.pref_delay_secs_summary_pt2);
            String msg = pt1 + " " + currentValue + " " + pt2;
            Log.d(Logging.TAG, msg);
            delayPref.setSummary(msg);
        } else if (key.equals(MyPrefs.PREF_DISPLAY_ORDER)) {
            Preference orderPref = findPreference(key);
            
            String currentValue = sharedPreferences.getString(key, "");
            String currentLabel = getCurrentOrderLabel(currentValue);
            String pt1 = getResources().getString(R.string.pref_display_order_summary_pt1);
            String pt2 = getResources().getString(R.string.pref_display_order_summary_pt2);
            String msg = pt1 + " " + currentLabel + " " + pt2;
            Log.d(Logging.TAG, msg);
            orderPref.setSummary(msg);
        }
    }
    
    private String getCurrentOrderLabel(String currentValue) {
        String values[] = getResources().getStringArray(R.array.display_order_values);
        String labels[] = getResources().getStringArray(R.array.display_order_labels);
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
