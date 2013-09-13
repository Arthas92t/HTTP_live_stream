package HLS;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.example.adaptivestreamplayer.CallBackDownload;
import com.example.adaptivestreamplayer.CustomPlayActivity;
import com.example.adaptivestreamplayer.DownloadPlaylistTask;

public class AlternativePlaylist implements CallBackDownload{
	public int bandwidth;
	public int programID;
	public String codes;
	public float targetDuration;
	private List<Segment> listSegments;
	private CustomPlayActivity activity;
	private String url;
	private String TAG = "AlternativePlaylist";
	
	public AlternativePlaylist(CustomPlayActivity activity, int bandwidth, String url){
		this.activity = activity;
		this.bandwidth = bandwidth;
		this.url = url;
		listSegments = null;
	}

	public AlternativePlaylist(CustomPlayActivity activity, int bandwidth, int programID, String url){
		this(activity, bandwidth, url);
		this.programID = programID;
	}

	public AlternativePlaylist(CustomPlayActivity activity, int bandwidth, int programID, String codes, String url){
		this(activity, bandwidth, programID, url);
		this.codes = codes;
	}
	
	public String absoluteURL(String url){
		try{
			URI u = new URI(url);
			if(u.isAbsolute())
				return u.toString();
			return new URI(this.url).resolve(u).toString();
		}catch(Exception e){
			Log.e(TAG, e.toString());
		}
		return "";
	}
	
	public void onDownloadedListener(InputStream data){
		listSegments = new ArrayList<Segment>();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(data));
        String line = null;
        long downloaded = 0;
        try {
            while ((line = buffer.readLine()) != null) {
            	downloaded = downloaded + line.length();
            	if(line.startsWith("#EXTINF:")){
            		line = buffer.readLine();
                	downloaded = downloaded + line.length();
            		listSegments.add(new Segment("", 0, absoluteURL(line)));
            	}
            }
            data.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, TAG + listSegments.size());
		activity.adjustStream(downloaded);
	}
	
	public void startDownload(){		
		DownloadPlaylistTask downloader = new DownloadPlaylistTask(this);
		downloader.execute(url);				
	}
	
	public String getSegment(int index){
		Log.e(TAG, "get segment");
		if (listSegments != null)
				return listSegments.get(index).url;
		Log.e(TAG, "download Alternative Playlist");
		startDownload();
		return null;
	}
}
