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

public class DownloadPlaylistTask extends AsyncTask <String, InputStream, Integer>{
	private CallBackDownload callBack;
	private String TAG = "DownloadPlaylistTask";
	File file;
	
	public DownloadPlaylistTask(CallBackDownload callback){
		this.callBack = callback;
	}
	@Override
	protected Integer doInBackground(String...url) {
		for(int i = 0; i < url.length; i++)
			download(url[i]);
		return null;
	}
	
	@Override
	protected void onProgressUpdate(InputStream...data) {
		callBack.onDownloadedListener(data[0]);
	}
	
	private void download(String url){
		Log.e(TAG, url);
		HttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response;
		while(true){
			try{
				response = client.execute(httpget);
				HttpEntity entity = response.getEntity();
				InputStream data = entity.getContent();
				publishProgress(data);
				return;
			}catch(Exception e){
				Log.e(TAG, e.toString());
			}
		}
	}
}
