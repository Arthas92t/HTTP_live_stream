package com.example.adaptivestreamplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity {

	boolean defaultPlayer;
	public static String EXTRA_URL = "EXTRA_URL";
	private final String URL1 = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
	private final String URL2 = "http://arthas92.byethost18.com/tapdoan.m3u8";
	private final String URL3 = "http://arthas92.byethost18.com/QC.m3u8";
	private final String URL4 = "http://arthas92.byethost18.com/QCDT.m3u8";
	private String url;
	private ViewPager mViewPager;
	private String TAG = "MainActivity";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_video);

        Spinner sinkSpinner = (Spinner) findViewById(R.id.source_spinner);
        ArrayAdapter<CharSequence> sinkAdapter = ArrayAdapter.createFromResource(
                this, R.array.source_array, android.R.layout.simple_spinner_item);
        sinkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sinkSpinner.setAdapter(sinkAdapter);
        sinkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Log.v(TAG, "onItemSelected " + pos);
                if (pos == 0) {
                	url = URL1;
                }
                if (pos == 1) {
                	url = URL2;
                }
                if (pos == 2) {
                	url = URL3;
                }
                if (pos == 3) {
                	url = URL4;
                }
            }

            public void onNothingSelected(AdapterView parent) {
            	url = URL1;
            }

        });	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	public void play(View view){
		Intent intent = new Intent(this, CustomPlayActivity.class);
		intent.putExtra(EXTRA_URL, url);
		startActivity(intent);		
	}
	
	public void playURL(View view){
		EditText editText = (EditText) findViewById(R.id.custom_url);
		String url = editText.getText().toString();
		Intent intent = new Intent(this, CustomPlayActivity.class);
		intent.putExtra(EXTRA_URL, url);
		startActivity(intent);		
	}
	
	public void defaultPlayer(View view){
		Intent intent = new Intent(this, DefaultPlayerActivity.class);
		intent.putExtra(EXTRA_URL, url);
		startActivity(intent);
	}
	
	public void defaultPlayerURL(View view){
		EditText editText = (EditText) findViewById(R.id.custom_url);
		String url = editText.getText().toString();
		Intent intent = new Intent(this, DefaultPlayerActivity.class);
		Log.e(TAG,"custom url: " + url);
		intent.putExtra(EXTRA_URL, url);
		startActivity(intent);
	}
}
