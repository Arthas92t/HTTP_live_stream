package com.example.adaptivestreamplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class MainActivity extends Activity {

	boolean defaultPlayer;
	public static String EXTRA_URL = "EXTRA_URL";
	private String url = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_video);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.default_tests:
				setContentView(R.layout.list_video);
				return true;
			case R.id.server_tests:
				setContentView(R.layout.list_video);
				return true;
			case R.id.settings:
				showSetting();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}    
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
	
}
