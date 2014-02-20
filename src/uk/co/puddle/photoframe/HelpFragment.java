package uk.co.puddle.photoframe;

import uk.co.puddle.photoframe.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        hookupButtons(rootView);

        return rootView;
    }
    
    private void hookupButtons(View rootView) {
    }
    
}
