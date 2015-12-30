package com.example.mp3player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import mars.download.HttpDownloader;

import com.example.mp3player.DowmloadList;
import com.example.mp3player.broadcast.ScanSdReceiver;
import com.example.mp3player.service.DownloadService;
import com.example.mp3player.service.PlayerService;
import com.example.mp3player.xml.BaiduXmlHandler;
import com.example.mp3player.xml.NetworkSongsListXmlHandler;
import com.example.mp3player.xml.SelectSongsXmlHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.color;
import android.R.xml;
import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener  {
	private FragmentTabHost tabHost;
	HttpDownloader httpDownloader = new HttpDownloader();
	final PublicVariable publicVariable = new PublicVariable();
	public static MainActivity instance  = null;
	UpdateBackgroundCallBack backgroundCallBack = new UpdateBackgroundCallBack();
	
	public static int lrcOffset = 0;//��ʲ���ʱ��
	
	//�������ؽ�����
	private Notification notif;
	private NotificationManager  manager;
	
	ImageButton playButton;
	ImageButton nextButton;
	ImageButton stopButton;
	ImageButton playModeButton;
	TextView timeTextView;
	TextView timeAllTextView;
	SeekBar seekBar;
	RelativeLayout mainLayout;
	int backgroundNum = 0;
	
	public ImageButton songerImage;
	
	//ScanSdReceiver receiver;//ɨ�豾�����ֹ㲥�¼�
	
	public long offset = 0;
	public boolean isSeekbarOnTouch = false;
	private ArrayList<Queue> queues = null;
	public Handler handler_time = new Handler();
	public Handler handler_lrc = new Handler();
	public UpdateTimeCallback updateTimeCallback = null;
	public UpdateLrcCallback updateLrcCallback = null;
//	private long begin = 0;
	private long nextTimeMill = 0;
	private long currentTimeMill = 0;
	private String message = "\n";
	private String nextMessage = "\n";
	private String threeMessage = "\n";

	private long pauseTimeMills = 0;
	
	
	public static int playModeChangeInt = 1;//����ģʽ���Ʊ���
	
	public static TextView songAndSonger;//��ʾ�����͸�����Ϣ
	Intent intent;
	
	private DowmloadList mFragment1 = new DowmloadList();  
	private NetworkSongslistFragment mFragment2 = new NetworkSongslistFragment();  
	private SelectSongs mFragment3 = new SelectSongs();  
	      
	private static final int TAB_INDEX_COUNT = 3;  
	      
	private static final int TAB_INDEX_ONE = 0;  
	private static final int TAB_INDEX_TWO = 1;  
	private static final int TAB_INDEX_THREE = 2;  	
	
	public static MediaPlayer mediaPlayer;
	//����״̬���Ʊ���
	public static boolean isPlaying = false;
	public static boolean isPause = false;
	public static boolean isReleased = false;
	//���Ʊ�����ֻ��һ�δ������Ч
	//boolean issendBroadcastScanMusic = true;
	
	public int xheight;//��Ļ�߶�
	public int ywidth;//��Ļ���
	
	ScanSdReceiver receiver;
	
	public ViewPager mViewPager;  
    private ViewPagerAdapter mViewPagerAdapter;  
    List<View> viewList1;
    View view1;
    View view2;
    View view3;
    public static File cache;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//issendBroadcastScanMusic = LrcActivity.inLrcActivity.preference.getBoolean("scan", true);//���ȡ����ֵ��ȡֵ����Ĳ���
		
		//��һ�ΰ�װ�������ɨ��㲥��ϵͳɨ����ɺ�֪ͨ�û�ɨ�豾������
//		if(issendBroadcastScanMusic){
//			if(receiver != null){
//				//��ȡ��ע��
//				MainActivity.this.unregisterReceiver(receiver);
//			}
//			//ע��㲥���ջ�������SDcardɨ���¼�
//			//ʵ����������������Ҫ���˵Ĺ㲥  
//			IntentFilter intentfilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);  
//		    intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);    
//		    intentfilter.addDataScheme("file");
//		    //ʵ�����㲥����
//		    receiver = new ScanSdReceiver();  
//		    //ע��㲥 
//		    registerReceiver(receiver, intentfilter); 
//		    
//		    //д�뱾������
//		    LrcActivity.inLrcActivity.editor.putBoolean("scan", false);
//		}
		 

		//֪ͨ�㲥ɨ�豾������
	     //android 4.4 ֮��Ȩ��������������
//		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, 
//				Uri.parse("file:/"+ Environment.getExternalStorageDirectory())));
		
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));  
		//�������淢�͹㲥�����д��뻻�������һ��
		
//		MediaScannerConnection.scanFile(this, new String[] 
//				{ Environment .getExternalStorageDirectory().getAbsolutePath() }, null, null);

		//��������Ŀ¼��ϵͳһ���о͵ô�������Ŀ¼�ģ�
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
         
        if(!cache.exists()){
            cache.mkdirs();
        }
		
      //���Ȼ�ȡ�ֻ���Ļ�ߴ磬���������
    	DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		int height = metrics.widthPixels; // ��Ļ���
		int width = metrics.heightPixels; // ��Ļ�߶�
		Log.v("126", "height  " + height + "    width:"+ width);
		
		xheight = height > width ? height: width;//ȡ�����еĴ�ֵ
		ywidth = height > width ? width: height;//ȡ�����е�Сֵ
		
		
		instance = this;
		
		intent = new Intent();
		intent.setClass(MainActivity.this, PlayerService.class);
		intent.putExtra("strSongInfo", "");
		
		mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
		
		playButton = (ImageButton)findViewById(R.id.StartAndStopButton);
		nextButton = (ImageButton)findViewById(R.id.nextButton);
		songAndSonger = (TextView)findViewById(R.id.songList);
		playModeButton = (ImageButton)findViewById(R.id.playMode);
		stopButton = (ImageButton)findViewById(R.id.stopButton);
		timeTextView = (TextView)findViewById(R.id.time);
		timeAllTextView = (TextView)findViewById(R.id.timeAll);
		seekBar = (SeekBar)findViewById(R.id.seekbar);
		songerImage = (ImageButton)findViewById(R.id.songerImagePlay);
		
		updateTimeCallback = new UpdateTimeCallback();
		
		//����������Ϊ͸��
		playButton.setBackgroundColor(color.transparent);
		nextButton.setBackgroundColor(color.transparent);
		stopButton.setBackgroundColor(color.transparent);
		songerImage.setBackgroundColor(color.transparent);

		setUpActionBar();  
        setUpViewPager();  
        setUpTabs();  
        
		//������д���֮����������߳��в�����������
//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//			.detectDiskReads().detectDiskWrites().detectNetwork()  
//			.penaltyLog().build());  
//		
//		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
//        	.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()  
//        	.penaltyLog().penaltyDeath().build()); 

/*		
		try{
			tabHost = (FragmentTabHost)findViewById(R.id.tabhost);
			tabHost.setup(this,getSupportFragmentManager(), R.id.realtabcontent);

			//���tabhost��ǩ
			tabHost.addTab(tabHost.newTabSpec("tab1")
					.setIndicator("�����б�", this.getResources().getDrawable(R.drawable.download1)),DowmloadList.class, null);
			tabHost.addTab(tabHost.newTabSpec("tab2")
					.setIndicator("�����б�",this.getResources().getDrawable(R.drawable.update)),SongList.class, null);

		}
		catch(Exception e){
			e.printStackTrace();
		}
		*/
        
        //�ı���ҳ����
        //ÿ10��ı�һ��
        handler_background.postDelayed(backgroundCallBack, 10000);
        
        //������Ų�Ϊ��
        //�˳����ź����´򿪽���
        if(mediaPlayer != null || PlayerService.mediaPlayer != null){
        	
        	
        	//��ȡ�ܵĲ���ʱ���͵�ǰ���Ž���
        	int timeAll = 0, time = 0;
        	if(PlayerService.isPlayOnLine){
        		timeAll = PlayerService.mediaPlayer.getDuration();
        		time = PlayerService.mediaPlayer.getCurrentPosition();
        	}
        	else{
        		timeAll = mediaPlayer.getDuration();
        		time = mediaPlayer.getCurrentPosition();
        	}
//        	prepareLrc(PlayerService.strTitle);//׼�����
        	
        	timeAllTextView.setText(longTime2Min(timeAll));//������ʱ��
        	timeTextView.setText(longTime2Min(time));//���õ�ǰʱ��
        	seekBar.setMax(timeAll);//���ý��������ֵ
        	seekBar.setProgress(time);//���ý���
        	songAndSonger.setText(PlayerService.strTitle + "-" + PlayerService.inPlayservice.strAuthor + "                           ");//���ø��ָ�����Ϣ

        	if(MainActivity.isPlaying && !MainActivity.isPause){
        		handler_time.postDelayed(updateTimeCallback, 5);//�ص�����
        		handler_lrc.postDelayed(updateLrcCallback, 5);//�ص�����
        		playerPlay();//�ı�ͼ��
        	}
        	songerImage.setBackgroundResource(R.drawable.author);
        	//Ϊ���½ǲ��ż���ͼƬ
        	ImageLoader.getInstance().displayImage(PlayerService.strPic_small, 
					MainActivity.instance.songerImage, PublicVariable.optionsRound);
        	
        	
        }else{
        	//Ϊ���½Ǽ���Ĭ��ͼƬ
        	//ImageLoader.getInstance().displayImage("http://c.hiphotos.baidu.com/ting/pic/item/29381f30e924b89988a9b4536d061d950b7bf6c0.jpg", 
			//		MainActivity.instance.songerImage, PublicVariable.optionsRound);
        	songerImage.setBackgroundResource(R.drawable.author);
        }
		
        songerImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.instance, SongInfoActivity.class);
				//activity��startActivity��context��startActivity������ͬ����Ҫ��Ӵ˾䣬��Ȼ����
				
				MainActivity.instance.startActivity(intent);
			}
		});
        
        
        //seek�����������¼�
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
        	//��������
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				isSeekbarOnTouch = false;
			}			
			//��ʼ����
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				isSeekbarOnTouch = true;
			}
			//�������ı�
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				moveSeekBar(progress, fromUser);
			}

		});
        
        
		//��ʰ�ť
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//stopService(intent);//ֹͣ���ŷ���
	
//				prepareLrc(PlayerService.strTitle);
				
				//ת��Ϊ��ʰ�ť
//				Intent intent = new Intent();
//				intent.setClass(instance, LrcActivity.class);
//		    	startActivity(intent); 
				
				//�����activity��Ϊapplication���ʲ���Ҫ������
				lrcDisplayClick();
			}
		});
		
		
		
		
		//���ź���ͣ��ť
		playButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO �Զ����ɵķ������
//				if(mediaPlayer == null && !(DowmloadList.isSongListClick)){
//					isPlaying = false;
//					isPause = false;
//					DowmloadList.isSongListClick = true;
//				}
				
				playAndPause();
			}
		});
		
		//��һ��
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				playNext();
			}
		});
		
		//����ģʽ
		playModeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				playModeChanged();
				
			}
		});
		
	}

	
	
	/**
	 * ����׼����ʣ���ƥ��ʱ��
	 */
	private void seektoLrc() {
		handler_lrc.removeCallbacks(updateLrcCallback);
		prepareLrc(PlayerService.strTitle);
		handler_lrc.postDelayed(updateLrcCallback, 5);
	}
	
	/**
	 * ���µ�����ʺ�ʱ��
	 * @param progress
	 * @param fromUser
	 */
	public void moveSeekBar(int progress, boolean fromUser) {
		//����û��ı��˽��������򲥷Ž��ȸı�
		if(fromUser){
			
			
			//�����ͣ״̬�����Ȼָ�����
			if(isPause && isPlaying){
				playAndPause();
			}
			
			if(PlayerService.isPlayOnLine){
				if(progress < PlayerService.mediaPlayer.getCurrentPosition()){
					seektoLrc();
				}
				PlayerService.mediaPlayer.seekTo(progress);
			}else{
				if(isPlaying){
					if(progress < mediaPlayer.getCurrentPosition()){
						seektoLrc();
					}
					mediaPlayer.seekTo(progress);//���ֲ��Ž��ȵ���
				}
				//���û�в��ţ���ִ�в���
				else{
					playAndPause();
				}
			}
		}
	}
	
	/**
	 * ���ݸ���ļ������֣�����ȡ����ļ����е���Ϣ
	 * @param lrcName
	 */
	public void prepareLrc(String lrcName){
		InputStream inputStream = null;
		try {
			if(queues != null){
				queues.removeAll(queues);
			}
			lrcName = lrcName.replace(" ", "");//����ո�
			String path = PublicVariable.SDCardRoot + "mp3/" + lrcName + ".lrc";
			File file = new File(path);
			//�������ļ����ڲŶ�ȡ���
			if(file.exists()){
				currentTimeMill = 0 ;
				nextTimeMill = 0 ;
				//offset = 0;
				//�Ը�����Ʊ��봦��
				
				inputStream = new FileInputStream(path);
				if(inputStream != null){
					LrcProcessor lrcProcessor = new LrcProcessor();
					//��ʼ������	
					queues = lrcProcessor.process(inputStream);
				}
			}
			else{
				lrcDownloadButtonDisplay();
				Queue<Long> timeMills = new LinkedList<Long>();
				//���ʱ�������Ӧ�ĸ��
				Queue<String> messages = new LinkedList<String>();
				timeMills.add((long) 0);
				messages.add("Ŷ��~��û�и��Ŷ~~");
				queues.add(timeMills);
				queues.add(messages);
			}
			//����һ��UpdateTimeCallback����
			updateLrcCallback = new UpdateLrcCallback(queues);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	/**
	 * ��ʲ��Żص�����
	 * @author Administrator
	 *
	 */
	class UpdateLrcCallback implements Runnable{
		Queue times = null;
		Queue messages = null;
		int ii = 0;
		public UpdateLrcCallback(ArrayList<Queue> queues) {
			//��ArrayList����ȡ����Ӧ�Ķ������
			times = queues.get(0);
			messages = queues.get(1);
			ii = 0;
			
			Log.v("126", "0--->messages.size()-->" + messages.size() + "times.size()-->" + times.size());
		}
		@Override
		public void run() {
			//����ƫ������Ҳ����˵�ӿ�ʼ����MP3������Ϊֹ���������˶���ʱ�䣬�Ժ���Ϊ��λ

			//�û�����ʱ������������
			//�����û��϶�
			if(!isSeekbarOnTouch){
				//���ý�����
				seekBar.setProgress((int)offset);
			}
			if(currentTimeMill == 0){
				//��ո��
				if(publicVariable.lrcStrListTemp.size() > 0){
					publicVariable.lrcStrListTemp.removeAll(publicVariable.lrcStrListTemp);
				}
				
				if(times.size() > 0){
					nextTimeMill = (Long)times.poll();
					
					LrcActivity.inLrcActivity.textView2.setVisibility(View.VISIBLE);
					lrcDownloadButtonhide();
				}
				else{
					lrcDownloadButtonDisplay();
					return;
				}
				if(messages.size() > 2){
					message = (String)messages.poll();
					nextMessage = (String)messages.poll();
					threeMessage = (String)messages.poll();
					
//					if(publicVariable.lrcStrListTemp.size() > 0){
//						publicVariable.lrcStrListTemp.removeAll(publicVariable.lrcStrListTemp);
//					}
					
					LrcActivity.inLrcActivity.textView2.setVisibility(View.VISIBLE);
					LrcActivity.inLrcActivity.textView1.setText(PlayerService.strTitle);
					LrcActivity.inLrcActivity.textView1.setText(PlayerService.strAuthor);
					lrcDownloadButtonhide();
				}
				else{
					lrcDownloadButtonDisplay();
					return;
				}
			}
			//ȡ��ʲ���ʱ��
			lrcOffset = LrcActivity.inLrcActivity.preference.getInt(PlayerService.strTitle + "lrcOffset", 0);
			
			//Log.v("127", "lrcOffset-->" + lrcOffset);
			
			currentTimeMill = currentTimeMill + 10;
			//������Ž���С���ܽ��ȣ������ѭ���ص�������ֹͣ�ص�
			//offset < 10 ��Ϊ��ÿһ�׸�����һʱ��õ������Ϣ
			if(offset + 3500 + lrcOffset >= nextTimeMill){

				//message.indexOf("song_lry_response") < 0
				//���˵�xml�洢�ı�ǩ����
				
				publicVariable.lrcStrListTemp.add(message);
				Log.v("125", "message-->" + message +"||");
				
					//��һ��textview
					String t = message.charAt(0)+"";
					if(t != "\n" && !(t.equals("\n")) && message.indexOf("song_lry_response") < 0 && message.indexOf("elt>") < 0){
						 message = message.replace("&#13;", "");
						LrcActivity.inLrcActivity.textView1.setText(message);						
						
					}
					//�ڶ���textview
					String t1 = nextMessage.charAt(0)+"";
					if(t1 != "\n" && !(t1.equals("\n")) && nextMessage.indexOf("song_lry_response") < 0 && nextMessage.indexOf("elt>") < 0){
						nextMessage = nextMessage.replace("&#13;", "");
						LrcActivity.inLrcActivity.textView2.setText(nextMessage);
					}
					else if(threeMessage.indexOf("song_lry_response") < 0 && threeMessage.indexOf("elt>") < 0){
						threeMessage = threeMessage.replace("&#13;", "");
						LrcActivity.inLrcActivity.textView2.setText(threeMessage);
					}
				
				Log.v("125", "nextTimeMill-->" + nextTimeMill);
				

					message = nextMessage;
					nextMessage = threeMessage;

					//Log.v("124", "ii-->" + ii);
					//Log.v("124", "messages.size()-->" + messages.size());
					if(messages.size() > 0 ){
						threeMessage = (String)messages.poll();					
					}
					//ɾ���ص�����
					else if(ii > 3){
						handler_lrc.removeCallbacks(updateLrcCallback);
					}
					else{
						ii++;
					}
					if(times.size() > 0){
						nextTimeMill = (Long)times.poll();
					}
				
				Log.v("125", "nextTimeMill-->" + nextTimeMill);
				Log.v("125", "offset-->" + longTime2Min(offset));				
			}
			
			if(ii <= 1){
				handler_lrc.postDelayed(updateLrcCallback, 10);
			}
		}
	}
	
	/**
	 * ��������ʱ��
	 * @author Administrator
	 *
	 */
	long songTime = 0;
	class UpdateTimeCallback implements Runnable{
		@Override
		public void run() {
			
			
			if(PlayerService.isPlayOnLine){
			 	offset = PlayerService.mediaPlayer.getCurrentPosition();
				//songTime = PlayerService.mediaPlayer.getDuration();
				//Log.v("127", "getDuration");
				//Log.v("127", "offset-->" + offset + "songTime-->" + songTime);
			}else{
				if(mediaPlayer != null){
					offset = mediaPlayer.getCurrentPosition();
					songTime = mediaPlayer.getDuration();
				}
				else{
//					offset = songTime = 0;
				}
			}
		
			//����ƫ������Ҳ����˵�ӿ�ʼ����MP3������Ϊֹ���������˶���ʱ�䣬�Ժ���Ϊ��λ
//			offset = System.currentTimeMillis() - begin;
			//���ò���ʱ����ʾ����
			timeTextView.setText(longTime2Min(offset));
			
			//���¸�����Ϣҳ��ʱ����
			if(PublicVariable.isSongInfoActivityStart){
				SongInfoActivity.infoActivity.timeTextView.setText(longTime2Min(offset));
//				SongInfoActivity.infoActivity.timeAllTextView.setText(longTime2Min(songTime));
//				SongInfoActivity.infoActivity.seekBar.setMax((int)songTime);
//				Log.v("127", "songTime-->" + songTime);
//				SongInfoActivity.infoActivity.songAndSonger.setText(PlayerService.strTitle + "-" + PlayerService.strAuthor + "                   ");
			}
			//�û�����ʱ������������  
			if(!isSeekbarOnTouch){
				//���ý�����
				seekBar.setProgress((int)offset);
				
				if(PublicVariable.isSongInfoActivityStart){
					SongInfoActivity.infoActivity.seekBar.setProgress((int)offset);
				}
			}
			//������Ž���С���ܽ��ȣ������ѭ���ص�������ֹͣ�ص�
			if(offset < seekBar.getMax()){
				handler_time.postDelayed(updateTimeCallback, 10);
			}
			else{
				seekBar.setProgress(seekBar.getMax());
				handler_lrc.removeCallbacks(updateLrcCallback);	
				handler_time.removeCallbacks(updateTimeCallback);
			}
		}
	}
	
	Handler handler_background = new Handler();
	/**
	 * �ı䱳��ͼƬ
	 * 10��ı�һ��
	 * @author Administrator
	 *
	 */
	class UpdateBackgroundCallBack implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			switch (backgroundNum) {
			case 0:
				mainLayout.setBackgroundResource(R.drawable.xuhua1);
				mFragment1.downloadListMainLayout.setBackgroundResource(R.drawable.xuhua1);
//				if(!PublicVariable.isMainActivity){
//					
//				}
				backgroundNum++;
				
				break;
			case 1:
				mainLayout.setBackgroundResource(R.drawable.xuhua3);
				mFragment1.downloadListMainLayout.setBackgroundResource(R.drawable.xuhua3);
				backgroundNum++;
				
				break;
			case 2:
				mainLayout.setBackgroundResource(R.drawable.xuhua4);
				mFragment1.downloadListMainLayout.setBackgroundResource(R.drawable.xuhua4);
				backgroundNum = 0;
				
				break;



			default:
				backgroundNum = 0;
				break;
			}
			
			handler_background.postDelayed(backgroundCallBack, 10000);
		}
		
	}
	
	
	
    /**
     * ����seekbar������
     */
    public Handler handlerUpdateSeekBar = new Handler(){
    	@Override
		public void handleMessage(Message msg) {
    		if(msg.what == 0){
    			//����seekbar��󳤶�
    			seekBar.setMax(msg.arg1);
    			//������ʾ���ֲ���ʱ��
    			Log.v("127", "msg.arg1 -->" + msg.arg1);
    			timeAllTextView.setText(longTime2Min(msg.arg1));
    			songTime = (long) msg.arg1;
    			//���¸�����Ϣҳ��ʱ����
    			if(PublicVariable.isSongInfoActivityStart){
    				SongInfoActivity.infoActivity.timeAllTextView.setText(longTime2Min(songTime));
    				SongInfoActivity.infoActivity.seekBar.setMax((int)songTime);
    				SongInfoActivity.infoActivity.songAndSonger.setText(PlayerService.strTitle + "-" + PlayerService.strAuthor + "                   ");
    			}    			
				//���ûص���������ʱ��͸����Ϣ
    			handler_time.postDelayed(updateTimeCallback, 5);
    		}
    	}
    };
	
	/**
	 * ���ݲ���״̬���������
	 * @param position �����б��λ��
	 */
	public void play(int position) {
		
		//���û�в���
		if(!MainActivity.isPlaying){
			
			startPlayerService();
			
		}
		//���ڲ���
		else{
			//�ж��û��Ƿ����˸����б�
			//�л���������ֹͣ���ڲ��ŵģ��ٲ��ŵ���ĸ���
			if(DowmloadList.isSongListClick){
				if(mediaPlayer != null){
					mediaPlayer.stop();
					
					if(PlayerService.mediaPlayer != null){
						PlayerService.mediaPlayer.stop();
					}
				}
				else if(PlayerService.mediaPlayer != null){
					PlayerService.mediaPlayer.stop();
				}
				startPlayerService();
			}
			//��ͣ����
			else if(!isPause){
				playerPause();
				if(mediaPlayer != null){
					mediaPlayer.pause();
				}
				isPlaying = true;
				isPause = true;
				isReleased = false;
			}
			//�ָ�����
			else{
				playerPlay();
				mediaPlayer.start();
				
				isPlaying = true;
				isPause = false;
				isReleased = false;
			}
		}
	}

	
	/**
	 * ��ͼ�껻Ϊ��ͣͼ��
	 */
	public void playerPlay() {
		Resources res = getResources();
		playButton.setImageDrawable(res.getDrawable(R.drawable.button_pause));
		LrcActivity.inLrcActivity.play.setImageDrawable(res.getDrawable(R.drawable.pause1));
		if(publicVariable.isSongInfoActivityStart){
			SongInfoActivity.infoActivity.playButton.setImageDrawable(res.getDrawable(R.drawable.button_pause));
		}
	}

	/**
	 * ��ͼ�껻Ϊ��ʼͼ��
	 */
	public void playerPause() {
		Resources res = getResources();
		playButton.setImageDrawable(res.getDrawable(R.drawable.button_play));
		LrcActivity.inLrcActivity.play.setImageDrawable(res.getDrawable(R.drawable.play_1));
		if(publicVariable.isSongInfoActivityStart){
			SongInfoActivity.infoActivity.playButton.setImageDrawable(res.getDrawable(R.drawable.button_play));
		}
	}
	
	public void startPlayerService() {
		playerPlay();
		startService(intent);//�򿪲��ŷ���
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {

			
			Intent intent = new Intent();
			intent.setClass(this, ScanMusicActivity.class);
	    	startActivity(intent);
			
			//ʹ��dialog������ʾɨ����Ϣ
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			LayoutInflater inflater = null;
//			inflater = LayoutInflater.from(this);
//			@SuppressWarnings("null")
//			View v = inflater.inflate(R.layout.first, null);
//			builder
////			.setTitle("��¼").setIcon(R.drawable.flag)
//			.setView(v).show();
//			
////			PublicVariable.isScanMusicInt++;
//			
//			final TextView textView = (TextView) v.findViewById(R.id.scan_textview);
//			final Button button = (Button) v.findViewById(R.id.scan_button);
//			final ProgressBar bar = (ProgressBar) v.findViewById(R.id.sacn_progress);
//			
//			if(true){
//				textView.setWidth(300);
//				
//				button.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						button.setVisibility(View.GONE);
//						textView.setText("ɨ����...");
//						scanMusic(bar,button,textView);
//					}
//				});
//			}
//			else{
//				button.setVisibility(View.GONE);
//				textView.setText("ɨ����...");
//				scanMusic(bar,button,textView);
//			}
//	    	
//	    	ScanSdReceiver receiver = new ScanSdReceiver()
//
//			
//			if(receiver != null){
//				//��ȡ��ע��
//				MainActivity.this.unregisterReceiver(receiver);
//			}
//			//ע��㲥���ջ�������SDcardɨ���¼�
//			//ʵ����������������Ҫ���˵Ĺ㲥  
//			IntentFilter intentfilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);  
//		    intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);    
//		    intentfilter.addDataScheme("file");
//		    //ʵ�����㲥����
//		    receiver = new ScanSdReceiver();  
//		    //ע��㲥 
//		    registerReceiver(receiver, intentfilter);  
//
//		    //ɨ��SDCard����
//		    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file:/"+ Environment.getExternalStorageDirectory())));

			return true;
		}
		else if(id == R.id.update){
			stopService(intent);
			System.exit(0);//�˳�
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private String download(String urlStr) {
		
		String str = httpDownloader.download(urlStr);
		return str;
	}
	
	
	Calendar cal = Calendar.getInstance();

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		 mViewPager.setCurrentItem(tab.getPosition());  
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

    private void setUpActionBar() {  
        final ActionBar actionBar = getActionBar();  
        actionBar.setHomeButtonEnabled(false);  
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
        actionBar.setDisplayShowTitleEnabled(false);  
        actionBar.setDisplayShowHomeEnabled(false);  
    }  
    

	/**
	 * ������ʱ��ת��Ϊ���Ӻ�����ʾ	
	 * @param time ����ʱ��
	 * @return ���Ӻ�����ַ���
	 */
    public String longTime2Min(long time){
    	long minTime = time/1000/60;
    	long secondTime = (time - minTime*1000*60)/1000;
    	String temp = null;
    	if(secondTime < 10){
    		temp = ":0" + secondTime;
    	}
    	else{
    		temp = ":" + secondTime;
    	}
    	
		return minTime + temp;
    }
      
    public Handler handlerUpdateProgressBAR = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == PublicVariable.WHAT_UPDATE_NOTIFICATION_PROGRESSBAR){
				int x = msg.arg1;
				Log.v("123","x--->" + x);
				if(x < 100){
					notif.contentView.setTextViewText(com.example.mp3player.R.id.content_view_text1, msg.obj + "���ؽ��ȣ�" + x + "%");  
				}
				else{
					notif.contentView.setTextViewText(com.example.mp3player.R.id.content_view_text1, msg.obj + "�������");
				}
				notif.contentView.setProgressBar(com.example.mp3player.R.id.content_view_progress, 100, x, false);
				manager.notify(msg.arg2, notif);
			}
			else if(msg.what == PublicVariable.WHAT_START_NOTIFICATION_PROGRESSBAR){
				//newһ���µ����س���
				manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notif = new Notification();  
				notif.icon = R.drawable.download1;  
				notif.tickerText = "����֪ͨ";  
                //֪ͨ����ʾ���õ��Ĳ����ļ�  
				notif.contentView = new RemoteViews(getPackageName(), R.layout.noti_view);  
//                notif.contentIntent = pIntent;  
				manager.notify(msg.arg2, notif);  
			}
		}
    };
    
    /**
     * ��contentResolverд�������Ϣ
     */
    public Handler handler2 = new Handler(){
    	public void handleMessage(Message msg) {
    		
    		@SuppressWarnings("unchecked")
			ArrayList<String> list = (ArrayList<String>)msg.obj;
    		String strTitle = list.get(0);
    		String strAuthor = list.get(1);
    		String uri = list.get(2);
    		
    		//��contentResolverд�������Ϣ���Է��㱾��ɨ��
        	ContentResolver contentResolver = getContentResolver();
        	
        	ContentValues values1 = new ContentValues();
        	values1.put(MediaStore.Audio.Media.TITLE, strTitle);
        	values1.put(MediaStore.Audio.Media.DATA, uri);
        	values1.put(MediaStore.Audio.Media.SIZE, "");
        	values1.put(MediaStore.Audio.Media.ARTIST, strAuthor);
        	
        	contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values1);
    	}
    };
    

    
    
	//Handler��Ϣ����
    //����ui
	public Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			Log.v("123","handler");
			if(msg.what == PublicVariable.WHAT_UPDATE_VIEW){
				mViewPagerAdapter.notifyDataSetChanged();
			}
			else if(msg.what == publicVariable.DOWNLOAD_NETWORK_SONGS){
				int arg1 = msg.arg1;
				ArrayList<String> arrayList = new ArrayList<>();
				arrayList = (ArrayList<String>) msg.obj;
				String xmlStr = arrayList.get(0);
				String songName = arrayList.get(1);
				
				switch (arg1) {
				case PublicVariable.DOWNLOAD_NETWORK_SONGS_LIST_XML:
					
					publicVariable.saxXmlReader(xmlStr,new NetworkSongsListXmlHandler());
					
					listMp3ToListHashmap(DowmloadList.inDowmloadList.handler);
					break;
				case PublicVariable.DOWNLOAD_NETWORK_SONG_DOWNLOAD_XML_BY_SONGID:
					startService(xmlStr,DownloadService.class,songName);
					break;
				case PublicVariable.DOWNLOAD_NETWORK_SONGS_PLAY_XML_BY_SONGID:

					startService(xmlStr,PlayerService.class,songName);
					break;
					
				case PublicVariable.DOWNLOAD_NETWORK_SELECT_LIST:
					publicVariable.saxXmlReader(xmlStr,new SelectSongsXmlHandler());
					// json���ݽ��д���
					
					int i = xmlStr.indexOf('{');
					int j = xmlStr.lastIndexOf('}');
					xmlStr = xmlStr.substring(i, j+1);
					
					if(PublicVariable.networkMp3ModelList != null){
						PublicVariable.networkMp3ModelList.removeAll(PublicVariable.networkMp3ModelList);
					}
					
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(xmlStr);
						JSONArray jsonArray = jsonObject.getJSONArray("song");
						DownloadMp3Model mp3 = null;
						for(i = 0;i < jsonArray.length();i++){ 
							JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
							mp3 = new DownloadMp3Model();
		
						    String songname = jsonObject2.getString("songname");
						    String songid = jsonObject2.getString("songid");

						    mp3.setSong_id(songid);
						    mp3.setTitle(songname);
							mp3.setType("");
							mp3.setArtist_id("");
							mp3.setPic_small("");
							mp3.setLrclink("");
							mp3.setAlbum_title("");
							PublicVariable.networkMp3ModelList.add(mp3);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					//����û����ظ��
					if(LrcActivity.inLrcActivity.isDownloadLrc){
						listMp3ToListHashmap(LrcActivity.inLrcActivity.handler);
						LrcActivity.inLrcActivity.isDownloadLrc = false;
					}
					//����û�����������Ϣ
					else{
						listMp3ToListHashmap(SelectSongs.inSelectSongs.handler);
					}
					break;

				default:
					break;
				}
			}
			else if(msg.what == PublicVariable.WHAT_TOASTS){
				String songName = (String) msg.obj;
				switch (msg.arg1) {
				case 0:
					Toast.makeText(instance, songName + "�Ѽ��������б�", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(instance, songName + "��������", Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
				
			}
		}
	};
	
	private void startService(String xmlStr,final Class cls,final String SongName) {
		publicVariable.saxXmlReader(xmlStr,new BaiduXmlHandler());
		
		String file_link = BaiduXmlHandler.mp3.getDownload_file_link();
		String show_link = BaiduXmlHandler.mp3.getDownload_show_link();
		//������ŵ�ַΪ�գ���Դ����
		if(PublicVariable.isNUll(file_link) || PublicVariable.isNUll(show_link)){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					startServiceBySongid(cls, SongName,OverrideListAdapter.inOverrideListAdapter.songId);
				}
			}).start();

		}else{
			Intent intent = new Intent();
			// ��Mp3Info������뵽Intent������
			intent.putExtra("strSongInfo", (BaiduXmlHandler.mp3.getTitle() == "" || BaiduXmlHandler.mp3.getTitle() == null)?SongName:BaiduXmlHandler.mp3.getTitle());
			intent.putExtra("strSonger", BaiduXmlHandler.mp3.getAuthor());
			intent.putExtra("strSize", BaiduXmlHandler.mp3.getSong_id());
			intent.putExtra("strLrcName", BaiduXmlHandler.mp3.getDownload_file_link());
			intent.putExtra("strLrcSize", BaiduXmlHandler.mp3.getDownload_show_link());
			intent.putExtra("file_extension", BaiduXmlHandler.mp3.getFile_extension());
			intent.putExtra("file_size", BaiduXmlHandler.mp3.getFile_size());
			intent.putExtra("lrclink", BaiduXmlHandler.mp3.getLrclink());
			intent.putExtra("pic_small", BaiduXmlHandler.mp3.getPic_small());
			intent.putExtra("artist_id", BaiduXmlHandler.mp3.getArtist_id());
			
			//������idд�뱾��
			LrcActivity.inLrcActivity.editor.putString(BaiduXmlHandler.mp3.getTitle() + BaiduXmlHandler.mp3.getAuthor(), BaiduXmlHandler.mp3.getPic_small());
			LrcActivity.inLrcActivity.editor.commit();
			LrcActivity.inLrcActivity.editor.putString(BaiduXmlHandler.mp3.getAuthor(), BaiduXmlHandler.mp3.getArtist_id());
			LrcActivity.inLrcActivity.editor.commit();
			
			intent.setClass(MainActivity.this,cls);
			// ����Service
			MainActivity.this.startService(intent);
		}
	}

	public void listMp3ToListHashmap(Handler handler) {
		ArrayList<HashMap<String, String>> listItemTemp = new ArrayList<>();
		//��listMp3ת��Ϊlist<hashmap<String,String>>��Ȼ�������listView��
		listItemTemp = publicVariable.praseToListHashmap(PublicVariable.networkMp3ModelList,false);
		Message message = new Message();
		message.what = publicVariable.WHAT_UPDATE_VIEW;
		message.obj = listItemTemp;
		//����handler��Ϣ�����պ����Ui
		handler.sendMessage(message);
	}

    
    private void setUpViewPager() {  
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());  
          
        mViewPager = (ViewPager)findViewById(R.id.pager);  
        mViewPager.setAdapter(mViewPagerAdapter); 
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {  
            @Override  
            public void onPageSelected(int position) {  
                final ActionBar actionBar = getActionBar();  
                actionBar.setSelectedNavigationItem(position);  
            }  
              
            @Override  
            public void onPageScrollStateChanged(int state) {  
                switch(state) {  
                    case ViewPager.SCROLL_STATE_IDLE:  
                        //TODO  
                        break;  
                    case ViewPager.SCROLL_STATE_DRAGGING:  
                        //TODO  
                        break;  
                    case ViewPager.SCROLL_STATE_SETTLING:  
                        //TODO  
                        break;  
                    default:  
                        //TODO  
                        break;  
                }  
            }  
        });  
    }  
    /**
     * ���tab��ǩ  
     */
    private void setUpTabs() {  
        final ActionBar actionBar = getActionBar();  
        for (int i = 0; i < mViewPagerAdapter.getCount(); ++i) {  
            actionBar.addTab(actionBar.newTab()  
                    .setText(mViewPagerAdapter.getPageTitle(i))  
                    .setTabListener(this));  
        }  
    }  
    

    
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		LrcActivity.inLrcActivity.setCurrentActivity(-1);
		super.onStop();
	}



	/**
	 * ���ñ��浱ǰactivity��ֵ
	 * 0��ʾmainactivity
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		LrcActivity.inLrcActivity.setCurrentActivity(0);
		super.onResume();
	}
	
	//�˳�
    @Override  
    protected void onDestroy() {  
    	finish();
    	PlayerService.isNotify = true;
        super.onDestroy();  
    }  
      
    
    //����赥ҳ���񵥵���¼�
    //�¸��
    public void networkPlay(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(1);
    }
    
    
    //����赥ҳ���񵥵���¼�
    //����
    public void popularSongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(16);
    }
    
    //����赥ҳ���񵥵���¼�
    //�����ϸ�
    public void classicSongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(22);
    }
    
    
    //����赥ҳ���񵥵���¼�
    //�ȸ��
    public void hotSongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(2);
    }
    
    
    //����赥ҳ���񵥵���¼�
    //ҡ��
    public void rockSongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(11);
    }
    
    //����赥ҳ���񵥵���¼�
    //���
    public void loveSongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(23);
    }
    
    //����赥ҳ���񵥵���¼�
    //Ӱ��
    public void moveSongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(24);
    }
    
    //����赥ҳ���񵥵���¼�
    //ŷ������
    public void EnglishSongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(21);
    }
    
    //����赥ҳ���񵥵���¼�
    //��ʿ
    public void jazzSongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(12);
    }
    
    //����赥ҳ���񵥵���¼�
    //�������
    public void networkSongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(25);
    }
    
    //����赥ҳ���񵥵���¼�
    //KTV����
    public void KTVSongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(6);
    }
    
    
    //�������ֵ���¼�
    //��į����
    public void lonelySongsClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(9);
    }
    
    //�������ֵ���¼�
    //��ů�黳
    public void loveSongsClick2(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(7);
    }
    
    //�������ֵ���¼�
    //�������
    public void BillboardClick(View v){
    	
    	Log.v("123","����");
    	startNetworkSongs(8);
    }
    

    /**
     * ����type���Ͳ�ѯ��Ӧ�ĸ赥
     * @param type
     */
    private void startNetworkSongs(int type){
    	Intent it = new Intent();
    	it.setClass(this, NetworkSongsActivity.class);
    	it.putExtra("type", type);
    	startActivity(it);
    }


   /**
    * ���ź���ͣ��������
    */
    public void playAndPause() {
    	
    	if(!PlayerService.isPlayOnLine){
    		if(mFragment1.downloadListMp3.size() == 0){
    			Toast.makeText(instance, "��û�и���Ŷ~\n�����ظ�����~", Toast.LENGTH_SHORT).show();
    			return;
    		}
    	}
    	
		if(PlayerService.isPlayOnLine && MainActivity.isPlaying){
			if(MainActivity.isPause){
				if(PlayerService.mediaPlayer != null){
					PlayerService.mediaPlayer.start();
				}
				
				playerPlay();//�ı�ͼ��
				//�ı�״̬
				MainActivity.isPause = false;
				MainActivity.isPlaying = true;
			}
			else{
				if(PlayerService.mediaPlayer != null){
					PlayerService.mediaPlayer.pause();
				}
				MainActivity.isPause = true;
				MainActivity.isPlaying = true;
				playerPause();
			}
			
		}
		else{
			//û�в���
			//�������ŷ���
			play(DowmloadList.playingPosition);
			PlayerService.isPlayOnLine = false;
		}
		
		//���Ž������͸�ʿ���
		//���ڲ�����û����ͣ
		if(MainActivity.isPlaying && MainActivity.isPause){
			handler_time.removeCallbacks(updateTimeCallback);
			pauseTimeMills = System.currentTimeMillis();
		}
		//���ڲ��ţ��Ҵ�����ͣ״̬
		else if(MainActivity.isPlaying && !MainActivity.isPause){
			handler_time.postDelayed(updateTimeCallback, 5);
//			begin = System.currentTimeMillis() - pauseTimeMills + begin;
		}
	}

    /**
     * ������һ����������
     */
	public void playNext() {
	//��������߲��ţ��������б���һ��
	if(PlayerService.isPlayOnLine){
		 OverrideListAdapter.inOverrideListAdapter.click(++OverrideListAdapter.hearListPosition, 
	        		PublicVariable.DOWNLOAD_NETWORK_SONGS_PLAY_XML_BY_SONGID,1);
	}
	else{
		//��������
		
		DowmloadList.isSongListClick = true;
		if(playModeChangeInt == 3)
		{
			//����ѭ��
			play(DowmloadList.playingPosition);
		}
		else if(playModeChangeInt == 2){
			//�б�ѭ��
			play(++DowmloadList.playingPosition);
		}
		else{
			//˳�򲥷�
			int i = DowmloadList.downloadListMp3.size();
			if(DowmloadList.playingPosition < i-1 ){
				play(DowmloadList.playingPosition);
			}						
			DowmloadList.playingPosition ++ ;
		}
	}
}

	/**
	 * ��ʾ���������ť�����ı���Ӧ״̬
	 */
	private void lrcDownloadButtonDisplay() {
		LrcActivity.inLrcActivity.textView1.setText("Ŷ��~û���ҵ����Ŷ~");
		LrcActivity.inLrcActivity.textView2.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.timeAndLrcButtonlayout.setVisibility(View.VISIBLE);
		LrcActivity.inLrcActivity.Layout_upOrDown.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.isUp = true;
		LrcActivity.inLrcActivity.upOrDown.setImageDrawable(getResources().getDrawable(R.drawable.up));
	}
	
	/**
	 * ���ظ��������ť�����ı���Ӧ״̬
	 */
	private void lrcDownloadButtonhide() {

		LrcActivity.inLrcActivity.timeAndLrcButtonlayout.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.Layout_upOrDown.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.isUp = false;
		LrcActivity.inLrcActivity.upOrDown.setImageDrawable(getResources().getDrawable(R.drawable.down));
	}

	/**
	 * �����ʰ�ť
	 */
	public void lrcDisplayClick() {
//		int color = LrcActivity.inLrcActivity.preference.getInt("color", 0);
		//�״δ򿪸�ʲ�׼Ϊ��ɫ����
//		if(color == 2 ){
//			color = 0;
//			LrcActivity.inLrcActivity.color_value = 0;
//			LrcActivity.inLrcActivity.setColor();
//		}
		LrcActivity.inLrcActivity.lrcView(LrcActivity.inLrcActivity.isLrcViewDisplay);
		LrcActivity.inLrcActivity.Layout_upOrDown.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.isFirstDisplayLrc = true;
	}

	/**
	 * �ı䲥��ģʽ
	 */
	public void playModeChanged() {
		Resources res = getResources();
		
		// �жϲ���ģʽ
		// 1��˳�򲥷�
		// 2: �б�ѭ��
		// 3: ����ѭ��
		if(playModeChangeInt == 3){
			playModeChangeInt = 1;
		}
		else{
			++ playModeChangeInt;
		}
		switch (playModeChangeInt) {
		case 1:
			playModeButton.setBackgroundResource(R.drawable.button_list_play);
			if(PublicVariable.isSongInfoActivityStart){
				SongInfoActivity.infoActivity.playModeButton.setBackgroundResource(R.drawable.button_list_play);
			}
			
			Toast.makeText(MainActivity.this, "˳�򲥷�", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			playModeButton.setBackgroundResource(R.drawable.button_list_after_list);
			if(PublicVariable.isSongInfoActivityStart){
				SongInfoActivity.infoActivity.playModeButton.setBackgroundResource(R.drawable.button_list_after_list);
			}
			
			Toast.makeText(MainActivity.this, "�б�ѭ��", Toast.LENGTH_SHORT).show();
			break;
		case 3:
			playModeButton.setBackgroundResource(R.drawable.buttton_one_song_play);
			if(PublicVariable.isSongInfoActivityStart){
				SongInfoActivity.infoActivity.playModeButton.setBackgroundResource(R.drawable.buttton_one_song_play);
			}
			Toast.makeText(MainActivity.this, "����ѭ��", Toast.LENGTH_SHORT).show();
			break;

		default: playModeChangeInt = 1;
			break;
		}
	}

	/**
	 * ���ݲ�����������
	 * ���ػ򲥷�
	 * @param cls ��������Ķ���
	 * @param SongName ��������
	 * @param songid  ����id
	 */
	public boolean startServiceBySongid(final Class cls, final String SongName,String songid) {
		HttpDownloader httpDownloader = new HttpDownloader();
		String downloadUrl  = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.play&songid="+ 
				songid+"&format=json";
		Log.v("128", downloadUrl);
		String jsonStr = httpDownloader.download(downloadUrl);
		if(!PublicVariable.isNUll(jsonStr)){
			//����json
			try {
				JSONObject jsonObject = new JSONObject(jsonStr).getJSONObject("bitrate");
				String file_link = jsonObject.getString("file_link");
		    	String show_link = jsonObject.getString("show_link");
		    	String file_size = jsonObject.getString("file_size");
		    	String file_extension = jsonObject.getString("file_extension");
		    	
		    	jsonObject = new JSONObject(jsonStr).getJSONObject("songinfo");
		    	String lrcLink = jsonObject.getString("lrclink");
		    	String pic_small = jsonObject.getString("pic_small");
		    	String author = jsonObject.getString("author");
		    	String ting_uid = jsonObject.getString("ting_uid");
		    	String song_id = jsonObject.getString("song_id");
		    	String title = jsonObject.getString("title");
		    	
		    	Log.v("128", "file_link:"+file_link);
		    	Log.v("128", "show_link:"+show_link);
		    	
		    	Intent intent = new Intent();
				// ��Mp3Info������뵽Intent������
				intent.putExtra("strSongInfo", title);
				intent.putExtra("strSonger", author);
				intent.putExtra("strSize", song_id);
				intent.putExtra("strLrcName", file_link);
				intent.putExtra("strLrcSize", show_link);
				intent.putExtra("file_extension", file_extension);
				intent.putExtra("file_size", file_size);
				intent.putExtra("lrclink", lrcLink);
				intent.putExtra("pic_small", pic_small);
				intent.putExtra("artist_id", ting_uid);
				
				LrcActivity.inLrcActivity.editor.putString(author, ting_uid);
				LrcActivity.inLrcActivity.editor.commit();
				LrcActivity.inLrcActivity.editor.putString(title + author, pic_small);
				LrcActivity.inLrcActivity.editor.commit();
				
				intent.setClass(MainActivity.this,cls);
				// ����Service
				MainActivity.this.startService(intent);
				
				Log.v("128","ting_uid-->" + ting_uid);
				return true;
		    	
		    	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
		}
		return false;
	}

	public class ViewPagerAdapter extends FragmentPagerAdapter { 
  
        public ViewPagerAdapter(FragmentManager fm) {  
            super(fm);  
            // TODO Auto-generated constructor stub  
        }  
  
        @Override  
        public Fragment getItem(int position) {  
            // TODO Auto-generated method stub  
            switch (position) {  
                case TAB_INDEX_ONE:  
                    return mFragment1;  
                case TAB_INDEX_TWO:
                    return mFragment2;  
                case TAB_INDEX_THREE: 
                    return mFragment3;  
            }  
            throw new IllegalStateException("No fragment at position " + position);  
        }  
//        @Override
//		public void notifyDataSetChanged() {
//			// TODO Auto-generated method stub
//			super.notifyDataSetChanged();
//		}

		@Override  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return TAB_INDEX_COUNT;  
        }  
          
        @Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
        	
//        	return super.getItemPosition(object); 
        	//�������ˢ�µ������notifyDataSetChanged���ʹ��
        	return PagerAdapter.POSITION_NONE;
		}


		@Override  
        public CharSequence getPageTitle(int position) {  
            String tabLabel = null;  
            switch (position) {  
                case TAB_INDEX_ONE:  
                    tabLabel = getString(R.string.tab_1);  
                    break;   
                case TAB_INDEX_TWO:  
                    tabLabel = getString(R.string.tab_2);  
                    break;  
                case TAB_INDEX_THREE:  
                    tabLabel = getString(R.string.tab_3);  
                    break;  
            }  
            return tabLabel;  
        }  
    }  
	
	
	
    
}
