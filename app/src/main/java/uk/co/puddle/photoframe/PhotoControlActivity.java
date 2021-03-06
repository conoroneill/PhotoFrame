package uk.co.puddle.photoframe;

import java.util.Locale;

import uk.co.puddle.photoframe.prefs.MyPrefs;
import uk.co.puddle.photoframe.prefs.SettingsActivity;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PhotoControlActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.d(Logging.TAG, "PhotoControlActivity; onCreate...");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sleep_control);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		// Create the adapter that will return a fragment for each of the
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
        int tabPosition = MyPrefs.getIntPref(this, MyPrefs.PREF_TAB_POSITION, 0);
        mViewPager.setCurrentItem(tabPosition);
	}
	
	@Override
	protected void onStart() {
	    Log.d(Logging.TAG, "PhotoControlActivity; onStart...");
	    super.onStart();
	}

	@Override
	protected void onRestart() {
	    Log.d(Logging.TAG, "PhotoControlActivity; onRestart...");
	    super.onRestart();
	}

	@Override
	protected void onResume() {
	    // Now in the foreground
	    Log.d(Logging.TAG, "PhotoControlActivity; onResume...");
	    super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    Log.d(Logging.TAG, "PhotoControlActivity; onSaveInstanceState...");
	    super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
	    // No longer in the foreground
	    Log.d(Logging.TAG, "PhotoControlActivity; onPause...");
	    super.onPause();
	}

	@Override
	protected void onStop() {
	    Log.d(Logging.TAG, "PhotoControlActivity; onStop...");
	    super.onStop();
	}

	@Override
	protected void onDestroy() {
	    Log.d(Logging.TAG, "PhotoActivity; onDestroy...");
	    super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.sleep_control, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            showSettings();
	            return true;
	        case R.id.action_help:
	            showHelp();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
    private void showSettings() {
        Log.d(Logging.TAG, "showSettings");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
    private void showHelp() {
        Log.d(Logging.TAG, "showHelp");
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
	
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
        MyPrefs.setIntPref(this, MyPrefs.PREF_TAB_POSITION, tab.getPosition());
        Log.d(Logging.TAG, "selectedTab; set to: " + tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			String argName = null;
			switch(position) {
			case 0:
                fragment = new PhotoFrontFragment();
                argName = PhotoFrontFragment.ARG_SECTION_NUMBER;
                break;
			case 1:
                fragment = new SleepTimingsFragment();
                argName = SleepTimingsFragment.ARG_SECTION_NUMBER;
                break;
			case 2:
                fragment = new SleepNowFragment();
                argName = SleepNowFragment.ARG_SECTION_NUMBER;
                break;
			default:
			}
			Bundle args = new Bundle();
			args.putInt(argName, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_tab0).toUpperCase(l);
			case 1:
				return getString(R.string.title_tab1).toUpperCase(l);
			case 2:
				return getString(R.string.title_tab2).toUpperCase(l);
			}
			return null;
		}
	}

}
