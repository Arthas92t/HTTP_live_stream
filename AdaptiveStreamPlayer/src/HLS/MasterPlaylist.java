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

public class MasterPlaylist implements CallBackDownload{
	public List<AlternativePlaylist> listStreams;
	private String url;
	private CustomPlayActivity activity;
	private String TAG = "MasterPlaylist";
	private String STREAM_INF = "#EXT-X-STREAM-INF:";
	private String BANDWIDTH = "BANDWIDTH";
	
	public MasterPlaylist(CustomPlayActivity activity, String url){
		this.url = url;
		this.activity = activity;
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
		Log.e(TAG,"call back");
		listStreams = new ArrayList<AlternativePlaylist>();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(data));
        String line = null;
        long downloaded = 0;
        try {
            while ((line = buffer.readLine()) != null) {
//        		Log.e(TAG,line);
            	downloaded = downloaded + line.length();
            	if(line.startsWith(STREAM_INF)){
            		int i, requiredBandwidth;
            		i = line.indexOf(BANDWIDTH); requiredBandwidth = 0;
            		while(line.charAt(i) <'0' || line.charAt(i) > '9')
            			i++;
            		while(i < line.length() && line.charAt(i) >='0' && line.charAt(i) <= '9'){
            			requiredBandwidth = requiredBandwidth * 10 + (line.charAt(i) - '0');
            			i++;
            		}
            		line = buffer.readLine();

            		downloaded = downloaded + line.length();
            		Log.e(TAG, "bandwidth: " + requiredBandwidth + "\n URL: " + absoluteURL(line));
            		listStreams.add(new AlternativePlaylist(activity, requiredBandwidth, absoluteURL(line)));
            	}
            }
            data.close();
        } catch (Exception e) {
        	Log.e(TAG,e.toString());
        }
		
		activity.adjustStream(downloaded * 100);
	}
	
	public void startDownload(){		
		DownloadPlaylistTask downloader = new DownloadPlaylistTask(this);
		downloader.execute(url);
	}
	
	public String getSegment(float bandwidth, int index){
		Log.e(TAG, "curent bandwidth: "+ bandwidth);
		int bestStream = 0;
		bandwidth = bandwidth*8;
		for(int i = 0; i < listStreams.size(); i++)
			if(listStreams.get(i).bandwidth < listStreams.get(bestStream).bandwidth)
				bestStream = i;
		
		for(int i = 0; i < listStreams.size(); i++)
			if(listStreams.get(i).bandwidth <= bandwidth && listStreams.get(i).bandwidth > listStreams.get(bestStream).bandwidth)
				bestStream = i;
		
		
		return listStreams.get(bestStream).getSegment(index);
	}	
}