package com.example.adaptivestreamplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import HLS.MasterPlaylist;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CustomPlayActivity extends Activity {
	public int downloadedSegment;
	public List<File> listFiles;

	private SegmentSwitcher segmentSwitcher;
	private String TAG = "ShowPlayListActivity";
	private String url;
	private MasterPlaylist masterPlaylist;
	private long beginDownload, totalTime, totalDownloaded;
    SurfaceView mSurfaceView1;
    SurfaceHolder mSurfaceHolder1;
    VideoSink mSelectedVideoSink;
    VideoSink mJavaMediaPlayerVideoSink;
    VideoSink mNativeMediaPlayerVideoSink;
    private boolean playing;

    SurfaceHolderVideoSink mSurfaceHolder1VideoSink, mSurfaceHolder2VideoSink;
//	private SurfaceView videoView;
	
	static {
		System.loadLibrary("native-media-jni");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_play_video);

		Intent intent = getIntent();
		url = intent.getStringExtra(MainActivity.EXTRA_URL);
		
		downloadedSegment = -1;
		listFiles = new ArrayList<File>();
		segmentSwitcher = new SegmentSwitcher();
		File file = new File(Environment.getExternalStoragePublicDirectory(
	            "/"), "AdaptiveCache");
		file.mkdirs();
		file = new File(Environment.getExternalStoragePublicDirectory(
	            "/AdaptiveCache/"), "init");
		try{
			FileOutputStream output = new FileOutputStream(file);
			output.close();
		}catch(Exception e){
			Log.e(TAG, e.toString());
		}
        // initialize native media system
        createEngine();

        // set up the Surface 1 video sink
        mSurfaceView1 = (SurfaceView) findViewById(R.id.videoView);
        mSurfaceHolder1 = mSurfaceView1.getHolder();
        playing = false;

        mSurfaceHolder1.addCallback(new SurfaceHolder.Callback() {

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.v(TAG, "surfaceChanged format=" + format + ", width=" + width + ", height="
                        + height);
            }

            public void surfaceCreated(SurfaceHolder holder) {
                Log.e(TAG, "surfaceCreated");
                setSurface(holder.getSurface());
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.v(TAG, "surfaceDestroyed");
            }

        });
		
		play();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_play_list, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.back:
        	back();
            return true;
        default:
            return super.onOptionsItemSelected(item);
    	}    
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
		ProgressDialog progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setMessage("Loading...");
		return progDialog;
	}
	
    /** Called when the activity is about to be destroyed. */
    @Override
    protected void onDestroy()
    {
        shutdown();
        super.onDestroy();
    }

    public void back(){
	}
	
	public void play(){
		masterPlaylist = new MasterPlaylist(this, url);
		beginDownload = System.currentTimeMillis();
		masterPlaylist.startDownload();
		showDialog(0);
	}
	
	public void resumePlay(){
		dismissDialog(0);
		Thread loadSegmentThread = new Thread(new Runnable(){
			public void run() {
		        createStreamingMediaPlayer(listFiles.get(0).getPath());
		        listFiles.remove(0);
		        playing = true;
		        setPlayingStreamingMediaPlayer(true);
				while(playing){
					if(listFiles.size()>0){
						File file = listFiles.get(0);
						listFiles.remove(0);
						Log.e(TAG, file.getPath());
						nextSegment(file.getPath());
					}
				}
			}
		});
		loadSegmentThread.start();
	}
	
	public void adjustStream(long downloaded){
		Log.e(TAG, "adjust stream, downloaded: " + downloaded);
		totalDownloaded = totalDownloaded + downloaded;
		totalTime = System.currentTimeMillis()- beginDownload;
		String nextSegment = masterPlaylist.getSegment(totalDownloaded * 1000 / totalTime, downloadedSegment + 1);
		if(nextSegment != null){
			Log.e(TAG, "adjust stream");
			DownloadSegmentTask downloader = new DownloadSegmentTask(this);
			downloader.execute(nextSegment);
			if(!playing && listFiles.size() > 0)
				resumePlay();
		}
	}

    /** Native methods, implemented in jni folder */
    public static native void createEngine();
    public static native boolean createStreamingMediaPlayer(String filename);
    public static native void setPlayingStreamingMediaPlayer(boolean isPlaying);
    public static native void shutdown();
    public static native void setSurface(Surface surface);
    public static native void nextSegment(String filename);

    // VideoSink abstracts out the difference between Surface and SurfaceTexture
    // aka SurfaceHolder and GLSurfaceView
    static abstract class VideoSink {

        abstract void setFixedSize(int width, int height);
        abstract void useAsSinkForJava(MediaPlayer mediaPlayer);
        abstract void useAsSinkForNative();

    }

    static class SurfaceHolderVideoSink extends VideoSink {

        private final SurfaceHolder mSurfaceHolder;

        SurfaceHolderVideoSink(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        void setFixedSize(int width, int height) {
            mSurfaceHolder.setFixedSize(width, height);
        }

        void useAsSinkForJava(MediaPlayer mediaPlayer) {
            // Use the newer MediaPlayer.setSurface(Surface) since API level 14
            // instead of MediaPlayer.setDisplay(mSurfaceHolder) since API level 1,
            // because setSurface also works with a Surface derived from a SurfaceTexture.
            Surface s = mSurfaceHolder.getSurface();
            mediaPlayer.setSurface(s);
            s.release();
        }

        void useAsSinkForNative() {
            Surface s = mSurfaceHolder.getSurface();
            setSurface(s);
            s.release();
        }

    }

    static class GLViewVideoSink extends VideoSink {

        private final MyGLSurfaceView mMyGLSurfaceView;

        GLViewVideoSink(MyGLSurfaceView myGLSurfaceView) {
            mMyGLSurfaceView = myGLSurfaceView;
        }

        void setFixedSize(int width, int height) {
        }

        void useAsSinkForJava(MediaPlayer mediaPlayer) {
            SurfaceTexture st = mMyGLSurfaceView.getSurfaceTexture();
            Surface s = new Surface(st);
            mediaPlayer.setSurface(s);
            s.release();
        }

        void useAsSinkForNative() {
            SurfaceTexture st = mMyGLSurfaceView.getSurfaceTexture();
            Surface s = new Surface(st);
            setSurface(s);
            s.release();
        }
    }

    private class SegmentSwitcher implements MediaPlayer.OnCompletionListener{
		public void onCompletion(MediaPlayer mp){
		}
	}
}
