package com.example.adaptivestreamplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DownloadSegmentTask extends AsyncTask <String, Integer, Integer>{
	private CustomPlayActivity activity;
	private String TAG = "DownloadTask";
	File file;
	
	public DownloadSegmentTask(CustomPlayActivity activity){
		this.activity = activity;
	}
	@Override
	protected Integer doInBackground(String...url) {
		for(int i = 0; i < url.length; i++)
			download(url[i]);
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer...update) {
		activity.adjustStream(file.length());
	}
	
	private void download(String url){
		HttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response;
		while(true){
			try{
				response = client.execute(httpget);
				HttpEntity entity = response.getEntity();
				saveToFile(entity.getContent());
				return;
			}catch(Exception e){
				Log.e(TAG, e.toString());
			}
		}
	}
	
	private String saveToFile(InputStream input){
		activity.downloadedSegment++;
		file = new File(Environment.getExternalStoragePublicDirectory(
	            "/AdaptiveCache/"), ""+activity.downloadedSegment);
		try {
			FileOutputStream output = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[131072];
	 
			while ((read = input.read(bytes)) != -1) {
				output.write(bytes, 0, read);
			}
			
			output.close();
			input.close();
			activity.listFiles.add(file);
			publishProgress(null);
			Log.e(TAG, "number of file: "+activity.listFiles.size());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return null;
	}
}
