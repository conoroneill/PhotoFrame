package uk.co.puddle.sleepcontrol;

import uk.co.puddle.sleepcontrol.alarms.Alarms;
import uk.co.puddle.sleepcontrol.alarms.RunningMode;
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
import android.widget.NumberPicker;
import android.widget.TextView;

public class SleepNowFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    private int delayBeforeSleep = SleepPrefs.DEFAULT_WAKE_INTERVAL;
    private int delayBeforeWake  = SleepPrefs.DEFAULT_SNOOZE_INTERVAL;
    
    public SleepNowFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_sleep_now, container, false);
//        TextView dummyTextView = (TextView) rootView
//                .findViewById(R.id.section_label);
//        dummyTextView.setText(Integer.toString(getArguments().getInt(
//                ARG_SECTION_NUMBER)));
        
        hookupTimeButton(rootView);
        return rootView;
    }

    private void hookupTimeButton(final View rootView) {
        NumberPicker delayBeforeSleepNumberPicker = (NumberPicker) rootView.findViewById(R.id.delayBeforeSleepNumberPicker);
        delayBeforeSleepNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                delayBeforeSleep = newVal;
                SleepPrefs.setIntPref(getActivity(), SleepPrefs.PREF_DELAY_BEFORE_SLEEP, delayBeforeSleep);
                Log.d(SleepLogging.TAG, "delayBeforeSleepNumberPicker; set to: " + delayBeforeSleep);
            }
        });
        NumberPicker delayBeforeWakeNumberPicker = (NumberPicker) rootView.findViewById(R.id.delayBeforeWakeNumberPicker);
        delayBeforeWakeNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                delayBeforeWake = newVal;
                SleepPrefs.setIntPref(getActivity(), SleepPrefs.PREF_DELAY_BEFORE_WAKE, delayBeforeWake);
                Log.d(SleepLogging.TAG, "delayBeforeWakeNumberPicker; set to: " + delayBeforeWake);
            }
        });
        CheckBox enabledCheckBox = (CheckBox)rootView.findViewById(R.id.sleepNowEnabledCheckBox);
        enabledCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SleepPrefs.setBooleanPref(getActivity(), SleepPrefs.PREF_ENABLE_NOW_SLEEP, isChecked);
                Log.d(SleepLogging.TAG, "enabledCheckBox; (now) set to: " + isChecked);
                enableDisablePageElements(rootView, isChecked);
            }
        });
        
        Button sleepButton = (Button)rootView.findViewById(R.id.startIntervalsButton);
        sleepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeSleep();
            }
        });

        Button stopSleepButton = (Button)rootView.findViewById(R.id.stopIntervalsButton);
        stopSleepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSleep();
            }
        });
        boolean enabled = SleepPrefs.getBooleanPref(getActivity(), SleepPrefs.PREF_ENABLE_NOW_SLEEP, false);
        enabledCheckBox.setChecked(enabled);
        Log.d(SleepLogging.TAG, "enabledCheckBox; (now); initialised to: " + enabled);


        delayBeforeSleep = SleepPrefs.getIntPref(getActivity(), SleepPrefs.PREF_DELAY_BEFORE_SLEEP, delayBeforeSleep);
        delayBeforeWake  = SleepPrefs.getIntPref(getActivity(), SleepPrefs.PREF_DELAY_BEFORE_WAKE,  delayBeforeWake);
        Log.d(SleepLogging.TAG, "delayBeforeSleepNumberPicker; init: " + delayBeforeSleep);
        Log.d(SleepLogging.TAG, "delayBeforeWakeNumberPicker; init: " + delayBeforeWake);
        delayBeforeSleepNumberPicker.setMinValue(0);
        delayBeforeSleepNumberPicker.setMaxValue(60);
        delayBeforeSleepNumberPicker.setValue(delayBeforeSleep);
        delayBeforeWakeNumberPicker.setMinValue(1);
        delayBeforeWakeNumberPicker.setMaxValue(60);
        delayBeforeWakeNumberPicker.setValue(delayBeforeWake);

        enableDisablePageElements(rootView, enabled);
}
    
    private void invokeSleep() {
        Log.i(SleepLogging.TAG, "SleepNowFragment; starting; delayBeforeSleep: " + delayBeforeSleep + "; delayBeforeWake: " + delayBeforeWake);
        Alarms.startAlarms(getActivity(), RunningMode.INTERVALS);
    }
    
    private void stopSleep() {
        Log.i(SleepLogging.TAG, "SleepNowFragment; stop sleep now");
        Alarms.stopAlarms(getActivity());
    }

    private void enableDisablePageElements(View rootView, boolean isEnabled) {
        TextView startSleepLabel                  = (TextView)     rootView.findViewById(R.id.startSleepNowLabel);
        TextView endSleepLabel                    = (TextView)     rootView.findViewById(R.id.endSleepNowLabel);
        NumberPicker delayBeforeSleepNumberPicker = (NumberPicker) rootView.findViewById(R.id.delayBeforeSleepNumberPicker);
        NumberPicker delayBeforeWakeNumberPicker  = (NumberPicker) rootView.findViewById(R.id.delayBeforeWakeNumberPicker);
        Button sleepButton                        = (Button)       rootView.findViewById(R.id.startIntervalsButton);
        startSleepLabel.setEnabled(isEnabled);
        endSleepLabel.setEnabled(isEnabled);
        delayBeforeSleepNumberPicker.setEnabled(isEnabled);
        delayBeforeWakeNumberPicker.setEnabled(isEnabled);
        sleepButton.setEnabled(isEnabled);
    }

}
