package uk.co.puddle.sleepcontrol;

import java.util.List;

import uk.co.puddle.sleepcontrol.alarms.Alarms;
import uk.co.puddle.sleepcontrol.photos.PhotoEntry;
import uk.co.puddle.sleepcontrol.photos.PhotoReader;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SleepFrontFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    
    private static final int NUM_START_PHOTOS_TO_LIST = 6;
    private static final int NUM_END_PHOTOS_TO_LIST   = 6;

    private List<PhotoEntry> images;

    public SleepFrontFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        View rootView = inflater.inflate(R.layout.fragment_front, container, false);
        hookupButtons(rootView);
        refreshPhotos();
        refreshTextWindow(rootView);

        return rootView;
    }
    
    private void hookupButtons(final View rootView) {
        Button refreshButton = (Button)rootView.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPhotos();
                refreshTextWindow(rootView);
            }
        });
        Button showPhotosButton = (Button)rootView.findViewById(R.id.showPhotosButton);
        showPhotosButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotos();
            }
        });
        Button sleepButton = (Button)rootView.findViewById(R.id.startfrontButton);
        sleepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                invokeSleep();
            }
        });

        Button stopSleepButton = (Button)rootView.findViewById(R.id.stopFrontButton);
        stopSleepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSleep();
            }
        });
    }
    
    private void refreshPhotos() {
        images = new PhotoReader().list(getActivity());
    }
    
    private void refreshTextWindow(View rootView) {
        TextView photoTextView = (TextView) rootView.findViewById(R.id.photoTextView);
        //photoTextView.setText("This is some text\nThis is a second line");
        StringBuilder sb = new StringBuilder();
        sb.append("Found " + images.size() + " photo entries\n");
        int count = images.size();
        int fromStart = (count < NUM_START_PHOTOS_TO_LIST) ? count : NUM_START_PHOTOS_TO_LIST;
        int startEnd  = (count < NUM_END_PHOTOS_TO_LIST)   ? 0     : (count - NUM_END_PHOTOS_TO_LIST);
        if (startEnd < fromStart) { startEnd = fromStart; }
        for (int i = 0; i < fromStart; i++) {
            PhotoEntry image = images.get(i);
            sb.append("" + i + "; " + image.getBucketName()).append('/').append(image.getName()).append(' ').append(image.getData()).append('\n');
        }
        if (fromStart < count) {
            sb.append("... \n");
            for (int i = startEnd; i < count; i++) {
                PhotoEntry image = images.get(i);
                sb.append("" + i + "; " + image.getBucketName()).append('/').append(image.getName()).append(' ').append(image.getData()).append('\n');
            }
        }
        boolean enabledNow  = SleepPrefs.getBooleanPref(getActivity(), SleepPrefs.PREF_ENABLE_NOW_SLEEP, false);
        boolean enabledTime = SleepPrefs.getBooleanPref(getActivity(), SleepPrefs.PREF_ENABLE_TIMED_SLEEP, false);
        sb.append("Timers: Timed: " + enabledTime + "; Now: " + enabledNow);
        
        photoTextView.setText(sb.toString());
    }

    private void showPhotos() {
        int count = images.size();
        Log.i(SleepLogging.TAG, "SleepFrontFragment; starting show photos; count: " + count);
        
        Intent intent = new Intent(this.getActivity(), PhotoActivity.class);
        Bundle sendBundle = new Bundle();
        sendBundle.putInt("count", count);
        if (count > 0) {
            PhotoEntry photoEntry = images.iterator().next();
            sendBundle.putString("photo_data", photoEntry.getData());
            sendBundle.putString("photo_thumb", photoEntry.getThumb());
        }
        intent.putExtras(sendBundle);
        startActivity(intent);
    }
    
    private void invokeSleep() {
        boolean enabledNow  = SleepPrefs.getBooleanPref(getActivity(), SleepPrefs.PREF_ENABLE_NOW_SLEEP, false);
        boolean enabledTime = SleepPrefs.getBooleanPref(getActivity(), SleepPrefs.PREF_ENABLE_TIMED_SLEEP, false);
        Log.i(SleepLogging.TAG, "SleepFrontFragment; starting; Timed: " + enabledTime + "; Now: " + enabledNow);
        if (enabledNow) {
            Alarms.startAlarms(getActivity(), RunningMode.INTERVALS);
        } else if (enabledTime) {
            Alarms.startAlarms(getActivity(), RunningMode.DAILY);
        }
        showPhotos();
    }
    
    private void stopSleep() {
        Log.i(SleepLogging.TAG, "SleepFrontFragment; stop sleep now");
        Alarms.stopAlarms(getActivity());
    }

}
