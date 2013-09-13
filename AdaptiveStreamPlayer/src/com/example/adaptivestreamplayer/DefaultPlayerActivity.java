package com.example.adaptivestreamplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.VideoView;

public class DefaultPlayerActivity extends Activity {
	public VideoView videoView;
	private String TAG = "ShowPlayListActivity";
	private String url;
//	private SurfaceView videoView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		url = intent.getStringExtra(MainActivity.EXTRA_URL);
		
		setContentView(R.layout.default_play);
		videoView = (VideoView)findViewById(R.id.videoView);
		videoView.setVideoPath(url);
//		videoView.setMediaController(new MediaController(this));
		videoView.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
}