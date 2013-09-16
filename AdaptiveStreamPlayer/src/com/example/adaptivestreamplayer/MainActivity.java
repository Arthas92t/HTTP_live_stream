package com.example.adaptivestreamplayer;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends FragmentActivity {

	boolean defaultPlayer;
	public static String EXTRA_URL = "EXTRA_URL";
	private String url = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private String TAG = "MainActivity";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	public void showSetting(){
		setContentView(R.layout.settings);		
		CheckBox useDefault = (CheckBox) findViewById(R.id.default_player);
		useDefault.setChecked(defaultPlayer);
	}
	
	public void switchPlayer(View view){
		CheckBox useDefault = (CheckBox) findViewById(R.id.default_player);
		defaultPlayer = useDefault.isChecked(); 
	}
	
	public void play(View view){
		if(defaultPlayer){
			Intent intent = new Intent(this, DefaultPlayerActivity.class);
			intent.putExtra(EXTRA_URL, url);
			startActivity(intent);
			return;
		}
		Intent intent = new Intent(this, CustomPlayActivity.class);
		intent.putExtra(EXTRA_URL, url);
		startActivity(intent);		
	}
	
	public void customUrl(View view){
		EditText editText = (EditText) findViewById(R.id.custom_url);
		String url = editText.getText().toString();
		if(defaultPlayer){
			Intent intent = new Intent(this, DefaultPlayerActivity.class);
			Log.e(TAG,"custom url: " + url);
			intent.putExtra(EXTRA_URL, url);
			startActivity(intent);
			return;
		}
		Intent intent = new Intent(this, CustomPlayActivity.class);
		intent.putExtra(EXTRA_URL, url);
		startActivity(intent);		
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			int layout;
			layout = R.layout.list_video;
			if(position == 1)
				layout = R.layout.custom_uri;
			if(position == 2)
				layout = R.layout.settings;
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, layout);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int layout = getArguments().getInt(ARG_SECTION_NUMBER);
			View rootView = inflater.inflate(layout, container, false);
			return rootView;
		}
	}
}
