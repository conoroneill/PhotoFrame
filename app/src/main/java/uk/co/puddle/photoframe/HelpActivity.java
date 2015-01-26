package uk.co.puddle.photoframe;

import android.app.Activity;
import android.os.Bundle;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, new HelpFragment())
                .commit();
    }
}
