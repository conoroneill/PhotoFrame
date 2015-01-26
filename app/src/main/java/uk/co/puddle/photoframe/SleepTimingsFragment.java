package uk.co.puddle.photoframe;

import uk.co.puddle.photoframe.alarms.Alarms;
import uk.co.puddle.photoframe.alarms.RunningMode;
import uk.co.puddle.photoframe.prefs.MyPrefs;
import uk.co.puddle.photoframe.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

public class SleepTimingsFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    public SleepTimingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_sleep_control, container, false);
//        TextView dummyTextView = (TextView) rootView
//                .findViewById(R.id.section_label);
//        dummyTextView.setText(Integer.toString(getArguments().getInt(
//                ARG_SECTION_NUMBER)));
        
        hookupTimePickers(rootView);

        return rootView;
    }

    private void hookupTimePickers(final View rootView) {
        TimePicker startSleepTimePicker = (TimePicker) rootView.findViewById(R.id.startSleepTimePicker);
        startSleepTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker paramTimePicker, int hour, int minute) {
                MyPrefs.setIntPref(getActivity(), MyPrefs.PREF_START_SLEEP_TIME_HOURS, hour);
                MyPrefs.setIntPref(getActivity(), MyPrefs.PREF_START_SLEEP_TIME_MINS, minute);
                Log.d(Logging.TAG, "startSleepTimePicker; set to: " + hour + "; " + minute);
            }
        });
        TimePicker endSleepTimePicker = (TimePicker) rootView.findViewById(R.id.endSleepTimePicker);
        endSleepTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker paramTimePicker, int hour, int minute) {
                MyPrefs.setIntPref(getActivity(), MyPrefs.PREF_END_SLEEP_TIME_HOURS, hour);
                MyPrefs.setIntPref(getActivity(), MyPrefs.PREF_END_SLEEP_TIME_MINS, minute);
                Log.d(Logging.TAG, "endSleepTimePicker; set to: " + hour + "; " + minute);
            }
        });
        CheckBox enabledCheckBox = (CheckBox)rootView.findViewById(R.id.sleepTimeEnabledCheckBox);
        enabledCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyPrefs.setBooleanPref(getActivity(), MyPrefs.PREF_ENABLE_TIMED_SLEEP, isChecked);
                Log.d(Logging.TAG, "enabledCheckBox; (timed) set to: " + isChecked);
                enableDisablePageElements(rootView, isChecked);
            }
        });
        
        Button sleepButton = (Button)rootView.findViewById(R.id.startDailyButton);
        sleepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeSleep();
            }
        });

        Button stopSleepButton = (Button)rootView.findViewById(R.id.stopDailyButton);
        stopSleepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSleep();
            }
        });

        boolean enabled = MyPrefs.getBooleanPref(getActivity(), MyPrefs.PREF_ENABLE_TIMED_SLEEP, false);
        enabledCheckBox.setChecked(enabled);
        Log.d(Logging.TAG, "enabledCheckBox; (timed); initialised to: " + enabled);

        initialiseTimePicker(startSleepTimePicker, "start",
                MyPrefs.PREF_START_SLEEP_TIME_HOURS,
                MyPrefs.PREF_START_SLEEP_TIME_MINS);
        initialiseTimePicker(endSleepTimePicker, "end",
                MyPrefs.PREF_END_SLEEP_TIME_HOURS,
                MyPrefs.PREF_END_SLEEP_TIME_MINS);
        
        enableDisablePageElements(rootView, enabled);
    }
    
    private void initialiseTimePicker(TimePicker timePicker, String desc,
            String prefsKeyHours, String prefsKeyMinutes) {
        int hour   = MyPrefs.getIntPref(getActivity(), prefsKeyHours, 18);
        int minute = MyPrefs.getIntPref(getActivity(), prefsKeyMinutes, 0);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        Log.d(Logging.TAG, "initialiseTimePicker; " + desc + "; "+ hour + "; " + minute);
    }
    
    private void enableDisablePageElements(View rootView, boolean isEnabled) {
        TextView startSleepLabel        = (TextView)   rootView.findViewById(R.id.startSleepLabel);
        TextView endSleepLabel          = (TextView)   rootView.findViewById(R.id.endSleepLabel);
        TimePicker startSleepTimePicker = (TimePicker) rootView.findViewById(R.id.startSleepTimePicker);
        TimePicker endSleepTimePicker   = (TimePicker) rootView.findViewById(R.id.endSleepTimePicker);
        Button startTimingsButton       = (Button)     rootView.findViewById(R.id.startDailyButton);
        startSleepLabel.setEnabled(isEnabled);
        endSleepLabel.setEnabled(isEnabled);
        startSleepTimePicker.setEnabled(isEnabled);
        endSleepTimePicker.setEnabled(isEnabled);
        startTimingsButton.setEnabled(isEnabled);
    }

    private void invokeSleep() {
        Log.i(Logging.TAG, "SleepTimingsFragment; starting timing alarms now ...");
        Alarms.startAlarms(getActivity(), RunningMode.DAILY);
    }
    
    private void stopSleep() {
        Log.i(Logging.TAG, "SleepTimingsFragment; stop sleep now");
        Alarms.stopAlarms(getActivity());
    }
}
