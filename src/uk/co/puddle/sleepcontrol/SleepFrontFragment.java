package uk.co.puddle.sleepcontrol;

import java.util.List;

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
    }
    
    private void refreshPhotos() {
        images = new PhotoReader().list(getActivity());
    }
    
    private void refreshTextWindow(View rootView) {
        TextView photoTextView = (TextView) rootView.findViewById(R.id.photoTextView);
        //photoTextView.setText("This is some text\nThis is a second line");
        StringBuilder sb = new StringBuilder();
        sb.append("Found " + images.size() + " photo entries\n");
        for (PhotoEntry image : images) {
            sb.append(image.getBucketName()).append('/').append(image.getName()).append('\n');
        }
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
    
}
