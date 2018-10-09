package uk.co.puddle.photoframe;

import java.util.List;

import uk.co.puddle.photoframe.alarms.Alarms;
import uk.co.puddle.photoframe.alarms.RunningMode;
import uk.co.puddle.photoframe.photos.PhotoEntry;
import uk.co.puddle.photoframe.photos.PhotoReader;
import uk.co.puddle.photoframe.prefs.MyPrefs;
import uk.co.puddle.photoframe.storage.RecentPhotos;
import uk.co.puddle.photoframe.R;
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

public class PhotoFrontFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    
    private static final int NUM_START_PHOTOS_TO_LIST = 4;
    private static final int NUM_END_PHOTOS_TO_LIST   = 4;

    private View rootView;
    private List<PhotoEntry> images;

    public PhotoFrontFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        rootView = inflater.inflate(R.layout.fragment_front, container, false);
        hookupButtons(rootView);
        refreshPhotos();
        refreshTextWindow(rootView);

        return rootView;
    }
    
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshRecentPhotosWindow(rootView);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        sb.append("Found ").append(images.size()).append(" photo entries\n");
        int count = images.size();
        int fromStart = (count < NUM_START_PHOTOS_TO_LIST) ? count : NUM_START_PHOTOS_TO_LIST;
        int startEnd  = (count < NUM_END_PHOTOS_TO_LIST)   ? 0     : (count - NUM_END_PHOTOS_TO_LIST);
        if (startEnd < fromStart) { startEnd = fromStart; }
        for (int i = 0; i < fromStart; i++) {
            PhotoEntry image = images.get(i);
            sb.append(i).append("; ");
            sb.append(image.getBucketName()).append('/').append(image.getName());
            //sb.append(' ').append(image.getData());
            sb.append('\n');
        }
        if (fromStart < count) {
            sb.append("... \n");
            for (int i = startEnd; i < count; i++) {
                PhotoEntry image = images.get(i);
                sb.append(i).append("; ");
                sb.append(image.getBucketName()).append('/').append(image.getName());
                //sb.append(' ').append(image.getData());
                sb.append('\n');
            }
        }
        boolean enabledNow  = MyPrefs.getBooleanPref(getActivity(), MyPrefs.PREF_ENABLE_NOW_SLEEP, false);
        boolean enabledTime = MyPrefs.getBooleanPref(getActivity(), MyPrefs.PREF_ENABLE_TIMED_SLEEP, false);
        sb.append("Timers: Daily: ").append(enabledTime).append("; Now: ").append(enabledNow);
        
        photoTextView.setText(sb.toString());
    }

    private void refreshRecentPhotosWindow(View rootView) {
        TextView recentPhotosTextView = (TextView) rootView.findViewById(R.id.recentPhotos);
        StringBuilder sb = new StringBuilder();
        List<PhotoEntry> list = RecentPhotos.getInstance().getRecentPhotos();
        int count = 1;
        sb.append("Found ").append(list.size()).append(" recent entries\n");
        for (PhotoEntry image : list) {
            //sb.append(image.getTextOnScreen());
            sb.append(count).append("; ");
            sb.append(image.getBucketName()).append('/').append(image.getName());
            sb.append('\n');
            count++;
        }
        recentPhotosTextView.setText(sb.toString());
    }

    private void showPhotos() {
        int count = images.size();
        Log.i(Logging.TAG, "SleepFrontFragment; starting show photos; count: " + count);
        
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
        boolean enabledNow  = MyPrefs.getBooleanPref(getActivity(), MyPrefs.PREF_ENABLE_NOW_SLEEP, false);
        boolean enabledTime = MyPrefs.getBooleanPref(getActivity(), MyPrefs.PREF_ENABLE_TIMED_SLEEP, false);
        Log.i(Logging.TAG, "SleepFrontFragment; starting; Daily: " + enabledTime + "; Now: " + enabledNow);
        if (enabledNow) {
            Alarms.startAlarms(getActivity(), RunningMode.INTERVALS);
        } else if (enabledTime) {
            Alarms.startAlarms(getActivity(), RunningMode.DAILY);
        }
        showPhotos();
    }
    
    private void stopSleep() {
        Log.i(Logging.TAG, "SleepFrontFragment; stop sleep now");
        Alarms.stopAlarms(getActivity());
    }

}
