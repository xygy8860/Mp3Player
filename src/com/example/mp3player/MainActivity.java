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
	
	public static int lrcOffset = 0;//歌词补偿时间
	
	//关于下载进度条
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
	
	//ScanSdReceiver receiver;//扫描本地音乐广播事件
	
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
	
	
	public static int playModeChangeInt = 1;//播放模式控制变量
	
	public static TextView songAndSonger;//显示歌曲和歌手信息
	Intent intent;
	
	private DowmloadList mFragment1 = new DowmloadList();  
	private NetworkSongslistFragment mFragment2 = new NetworkSongslistFragment();  
	private SelectSongs mFragment3 = new SelectSongs();  
	      
	private static final int TAB_INDEX_COUNT = 3;  
	      
	private static final int TAB_INDEX_ONE = 0;  
	private static final int TAB_INDEX_TWO = 1;  
	private static final int TAB_INDEX_THREE = 2;  	
	
	public static MediaPlayer mediaPlayer;
	//播放状态控制变量
	public static boolean isPlaying = false;
	public static boolean isPause = false;
	public static boolean isReleased = false;
	//控制变量，只第一次打开软件有效
	//boolean issendBroadcastScanMusic = true;
	
	public int xheight;//屏幕高度
	public int ywidth;//屏幕宽度
	
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

		//issendBroadcastScanMusic = LrcActivity.inLrcActivity.preference.getBoolean("scan", true);//如果取不到值就取值后面的参数
		
		//第一次安装软件则发送扫描广播，系统扫描完成后通知用户扫描本地音乐
//		if(issendBroadcastScanMusic){
//			if(receiver != null){
//				//先取消注册
//				MainActivity.this.unregisterReceiver(receiver);
//			}
//			//注册广播接收机，监听SDcard扫描事件
//			//实例化过滤器并设置要过滤的广播  
//			IntentFilter intentfilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);  
//		    intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);    
//		    intentfilter.addDataScheme("file");
//		    //实例化广播处理
//		    receiver = new ScanSdReceiver();  
//		    //注册广播 
//		    registerReceiver(receiver, intentfilter); 
//		    
//		    //写入本地数据
//		    LrcActivity.inLrcActivity.editor.putBoolean("scan", false);
//		}
		 

		//通知广播扫描本地音乐
	     //android 4.4 之后，权限提升，不可用
//		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, 
//				Uri.parse("file:/"+ Environment.getExternalStorageDirectory())));
		
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));  
		//把你上面发送广播的所有代码换成这个试一下
		
//		MediaScannerConnection.scanFile(this, new String[] 
//				{ Environment .getExternalStorageDirectory().getAbsolutePath() }, null, null);

		//创建缓存目录，系统一运行就得创建缓存目录的，
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
         
        if(!cache.exists()){
            cache.mkdirs();
        }
		
      //首先获取手机屏幕尺寸，解决兼容性
    	DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		int height = metrics.widthPixels; // 屏幕宽度
		int width = metrics.heightPixels; // 屏幕高度
		Log.v("126", "height  " + height + "    width:"+ width);
		
		xheight = height > width ? height: width;//取两者中的大值
		ywidth = height > width ? width: height;//取两者中的小值
		
		
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
		
		//将背景设置为透明
		playButton.setBackgroundColor(color.transparent);
		nextButton.setBackgroundColor(color.transparent);
		stopButton.setBackgroundColor(color.transparent);
		songerImage.setBackgroundColor(color.transparent);

		setUpActionBar();  
        setUpViewPager();  
        setUpTabs();  
        
		//添加下列代码之后可以在主线程中操作网络请求
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

			//添加tabhost标签
			tabHost.addTab(tabHost.newTabSpec("tab1")
					.setIndicator("本地列表", this.getResources().getDrawable(R.drawable.download1)),DowmloadList.class, null);
			tabHost.addTab(tabHost.newTabSpec("tab2")
					.setIndicator("歌曲列表",this.getResources().getDrawable(R.drawable.update)),SongList.class, null);

		}
		catch(Exception e){
			e.printStackTrace();
		}
		*/
        
        //改变主页背景
        //每10秒改变一次
        handler_background.postDelayed(backgroundCallBack, 10000);
        
        //如果播放不为空
        //退出播放后重新打开界面
        if(mediaPlayer != null || PlayerService.mediaPlayer != null){
        	
        	
        	//获取总的播放时长和当前播放进度
        	int timeAll = 0, time = 0;
        	if(PlayerService.isPlayOnLine){
        		timeAll = PlayerService.mediaPlayer.getDuration();
        		time = PlayerService.mediaPlayer.getCurrentPosition();
        	}
        	else{
        		timeAll = mediaPlayer.getDuration();
        		time = mediaPlayer.getCurrentPosition();
        	}
//        	prepareLrc(PlayerService.strTitle);//准备歌词
        	
        	timeAllTextView.setText(longTime2Min(timeAll));//设置总时长
        	timeTextView.setText(longTime2Min(time));//设置当前时长
        	seekBar.setMax(timeAll);//设置进度条最大值
        	seekBar.setProgress(time);//设置进度
        	songAndSonger.setText(PlayerService.strTitle + "-" + PlayerService.inPlayservice.strAuthor + "                           ");//设置歌手歌曲信息

        	if(MainActivity.isPlaying && !MainActivity.isPause){
        		handler_time.postDelayed(updateTimeCallback, 5);//回调函数
        		handler_lrc.postDelayed(updateLrcCallback, 5);//回调函数
        		playerPlay();//改变图标
        	}
        	songerImage.setBackgroundResource(R.drawable.author);
        	//为最下角播放加载图片
        	ImageLoader.getInstance().displayImage(PlayerService.strPic_small, 
					MainActivity.instance.songerImage, PublicVariable.optionsRound);
        	
        	
        }else{
        	//为最下角加载默认图片
        	//ImageLoader.getInstance().displayImage("http://c.hiphotos.baidu.com/ting/pic/item/29381f30e924b89988a9b4536d061d950b7bf6c0.jpg", 
			//		MainActivity.instance.songerImage, PublicVariable.optionsRound);
        	songerImage.setBackgroundResource(R.drawable.author);
        }
		
        songerImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.instance, SongInfoActivity.class);
				//activity的startActivity和context的startActivity方法不同，需要添加此句，不然报错
				
				MainActivity.instance.startActivity(intent);
			}
		});
        
        
        //seek进度条监听事件
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
        	//结束触摸
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				isSeekbarOnTouch = false;
			}			
			//开始触摸
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				isSeekbarOnTouch = true;
			}
			//进度条改变
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				moveSeekBar(progress, fromUser);
			}

		});
        
        
		//歌词按钮
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//stopService(intent);//停止播放服务
	
//				prepareLrc(PlayerService.strTitle);
				
				//转变为歌词按钮
//				Intent intent = new Intent();
//				intent.setClass(instance, LrcActivity.class);
//		    	startActivity(intent); 
				
				//歌词有activity变为application，故不需要再启动
				lrcDisplayClick();
			}
		});
		
		
		
		
		//播放和暂停按钮
		playButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
//				if(mediaPlayer == null && !(DowmloadList.isSongListClick)){
//					isPlaying = false;
//					isPause = false;
//					DowmloadList.isSongListClick = true;
//				}
				
				playAndPause();
			}
		});
		
		//下一曲
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				playNext();
			}
		});
		
		//播放模式
		playModeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				playModeChanged();
				
			}
		});
		
	}

	
	
	/**
	 * 重新准备歌词，以匹配时间
	 */
	private void seektoLrc() {
		handler_lrc.removeCallbacks(updateLrcCallback);
		prepareLrc(PlayerService.strTitle);
		handler_lrc.postDelayed(updateLrcCallback, 5);
	}
	
	/**
	 * 重新调整歌词和时间
	 * @param progress
	 * @param fromUser
	 */
	public void moveSeekBar(int progress, boolean fromUser) {
		//如果用户改变了进度条，则播放进度改变
		if(fromUser){
			
			
			//如果暂停状态，首先恢复播放
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
					mediaPlayer.seekTo(progress);//音乐播放进度调整
				}
				//如果没有播放，则执行播放
				else{
					playAndPause();
				}
			}
		}
	}
	
	/**
	 * 根据歌词文件的名字，来读取歌词文件当中的信息
	 * @param lrcName
	 */
	public void prepareLrc(String lrcName){
		InputStream inputStream = null;
		try {
			if(queues != null){
				queues.removeAll(queues);
			}
			lrcName = lrcName.replace(" ", "");//处理空格
			String path = PublicVariable.SDCardRoot + "mp3/" + lrcName + ".lrc";
			File file = new File(path);
			//如果歌词文件存在才读取歌词
			if(file.exists()){
				currentTimeMill = 0 ;
				nextTimeMill = 0 ;
				//offset = 0;
				//对歌词名称编码处理
				
				inputStream = new FileInputStream(path);
				if(inputStream != null){
					LrcProcessor lrcProcessor = new LrcProcessor();
					//初始化数据	
					queues = lrcProcessor.process(inputStream);
				}
			}
			else{
				lrcDownloadButtonDisplay();
				Queue<Long> timeMills = new LinkedList<Long>();
				//存放时间点所对应的歌词
				Queue<String> messages = new LinkedList<String>();
				timeMills.add((long) 0);
				messages.add("哦活~还没有歌词哦~~");
				queues.add(timeMills);
				queues.add(messages);
			}
			//创建一个UpdateTimeCallback对象
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
	 * 歌词播放回调函数
	 * @author Administrator
	 *
	 */
	class UpdateLrcCallback implements Runnable{
		Queue times = null;
		Queue messages = null;
		int ii = 0;
		public UpdateLrcCallback(ArrayList<Queue> queues) {
			//从ArrayList当中取出相应的对象对象
			times = queues.get(0);
			messages = queues.get(1);
			ii = 0;
			
			Log.v("126", "0--->messages.size()-->" + messages.size() + "times.size()-->" + times.size());
		}
		@Override
		public void run() {
			//计算偏移量，也就是说从开始播放MP3到现在为止，共消耗了多少时间，以毫秒为单位

			//用户触摸时进度条不更新
			//方便用户拖动
			if(!isSeekbarOnTouch){
				//设置进度条
				seekBar.setProgress((int)offset);
			}
			if(currentTimeMill == 0){
				//清空歌词
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
			//取歌词补偿时间
			lrcOffset = LrcActivity.inLrcActivity.preference.getInt(PlayerService.strTitle + "lrcOffset", 0);
			
			//Log.v("127", "lrcOffset-->" + lrcOffset);
			
			currentTimeMill = currentTimeMill + 10;
			//如果播放进度小于总进度，则继续循环回调；否则，停止回调
			//offset < 10 是为了每一首歌曲第一时间得到歌词信息
			if(offset + 3500 + lrcOffset >= nextTimeMill){

				//message.indexOf("song_lry_response") < 0
				//过滤掉xml存储的标签数据
				
				publicVariable.lrcStrListTemp.add(message);
				Log.v("125", "message-->" + message +"||");
				
					//第一个textview
					String t = message.charAt(0)+"";
					if(t != "\n" && !(t.equals("\n")) && message.indexOf("song_lry_response") < 0 && message.indexOf("elt>") < 0){
						 message = message.replace("&#13;", "");
						LrcActivity.inLrcActivity.textView1.setText(message);						
						
					}
					//第二个textview
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
					//删除回调函数
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
	 * 歌曲播放时长
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
		
			//计算偏移量，也就是说从开始播放MP3到现在为止，共消耗了多少时间，以毫秒为单位
//			offset = System.currentTimeMillis() - begin;
			//设置播放时间显示进度
			timeTextView.setText(longTime2Min(offset));
			
			//更新歌曲信息页面时间轴
			if(PublicVariable.isSongInfoActivityStart){
				SongInfoActivity.infoActivity.timeTextView.setText(longTime2Min(offset));
//				SongInfoActivity.infoActivity.timeAllTextView.setText(longTime2Min(songTime));
//				SongInfoActivity.infoActivity.seekBar.setMax((int)songTime);
//				Log.v("127", "songTime-->" + songTime);
//				SongInfoActivity.infoActivity.songAndSonger.setText(PlayerService.strTitle + "-" + PlayerService.strAuthor + "                   ");
			}
			//用户触摸时进度条不更新  
			if(!isSeekbarOnTouch){
				//设置进度条
				seekBar.setProgress((int)offset);
				
				if(PublicVariable.isSongInfoActivityStart){
					SongInfoActivity.infoActivity.seekBar.setProgress((int)offset);
				}
			}
			//如果播放进度小于总进度，则继续循环回调；否则，停止回调
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
	 * 改变背景图片
	 * 10秒改变一次
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
     * 更新seekbar进度条
     */
    public Handler handlerUpdateSeekBar = new Handler(){
    	@Override
		public void handleMessage(Message msg) {
    		if(msg.what == 0){
    			//设置seekbar最大长度
    			seekBar.setMax(msg.arg1);
    			//设置显示音乐播放时长
    			Log.v("127", "msg.arg1 -->" + msg.arg1);
    			timeAllTextView.setText(longTime2Min(msg.arg1));
    			songTime = (long) msg.arg1;
    			//更新歌曲信息页面时间轴
    			if(PublicVariable.isSongInfoActivityStart){
    				SongInfoActivity.infoActivity.timeAllTextView.setText(longTime2Min(songTime));
    				SongInfoActivity.infoActivity.seekBar.setMax((int)songTime);
    				SongInfoActivity.infoActivity.songAndSonger.setText(PlayerService.strTitle + "-" + PlayerService.strAuthor + "                   ");
    			}    			
				//调用回调函数更新时间和歌词信息
    			handler_time.postDelayed(updateTimeCallback, 5);
    		}
    	}
    };
	
	/**
	 * 根据播放状态分情况播放
	 * @param position 播放列表的位置
	 */
	public void play(int position) {
		
		//如果没有播放
		if(!MainActivity.isPlaying){
			
			startPlayerService();
			
		}
		//正在播放
		else{
			//判断用户是否点击了歌曲列表
			//切换歌曲，先停止正在播放的，再播放点击的歌曲
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
			//暂停播放
			else if(!isPause){
				playerPause();
				if(mediaPlayer != null){
					mediaPlayer.pause();
				}
				isPlaying = true;
				isPause = true;
				isReleased = false;
			}
			//恢复播放
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
	 * 将图标换为暂停图标
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
	 * 将图标换为开始图标
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
		startService(intent);//打开播放服务
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
			
			//使用dialog对象显示扫描信息
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			LayoutInflater inflater = null;
//			inflater = LayoutInflater.from(this);
//			@SuppressWarnings("null")
//			View v = inflater.inflate(R.layout.first, null);
//			builder
////			.setTitle("登录").setIcon(R.drawable.flag)
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
//						textView.setText("扫描中...");
//						scanMusic(bar,button,textView);
//					}
//				});
//			}
//			else{
//				button.setVisibility(View.GONE);
//				textView.setText("扫描中...");
//				scanMusic(bar,button,textView);
//			}
//	    	
//	    	ScanSdReceiver receiver = new ScanSdReceiver()
//
//			
//			if(receiver != null){
//				//先取消注册
//				MainActivity.this.unregisterReceiver(receiver);
//			}
//			//注册广播接收机，监听SDcard扫描事件
//			//实例化过滤器并设置要过滤的广播  
//			IntentFilter intentfilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);  
//		    intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);    
//		    intentfilter.addDataScheme("file");
//		    //实例化广播处理
//		    receiver = new ScanSdReceiver();  
//		    //注册广播 
//		    registerReceiver(receiver, intentfilter);  
//
//		    //扫描SDCard音乐
//		    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file:/"+ Environment.getExternalStorageDirectory())));

			return true;
		}
		else if(id == R.id.update){
			stopService(intent);
			System.exit(0);//退出
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
	 * 将毫秒时间转换为分钟和秒显示	
	 * @param time 毫秒时间
	 * @return 分钟和秒的字符串
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
					notif.contentView.setTextViewText(com.example.mp3player.R.id.content_view_text1, msg.obj + "下载进度：" + x + "%");  
				}
				else{
					notif.contentView.setTextViewText(com.example.mp3player.R.id.content_view_text1, msg.obj + "下载完成");
				}
				notif.contentView.setProgressBar(com.example.mp3player.R.id.content_view_progress, 100, x, false);
				manager.notify(msg.arg2, notif);
			}
			else if(msg.what == PublicVariable.WHAT_START_NOTIFICATION_PROGRESSBAR){
				//new一个新的下载程序
				manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notif = new Notification();  
				notif.icon = R.drawable.download1;  
				notif.tickerText = "下载通知";  
                //通知栏显示所用到的布局文件  
				notif.contentView = new RemoteViews(getPackageName(), R.layout.noti_view);  
//                notif.contentIntent = pIntent;  
				manager.notify(msg.arg2, notif);  
			}
		}
    };
    
    /**
     * 向contentResolver写入歌曲信息
     */
    public Handler handler2 = new Handler(){
    	public void handleMessage(Message msg) {
    		
    		@SuppressWarnings("unchecked")
			ArrayList<String> list = (ArrayList<String>)msg.obj;
    		String strTitle = list.get(0);
    		String strAuthor = list.get(1);
    		String uri = list.get(2);
    		
    		//向contentResolver写入歌曲信息，以方便本地扫描
        	ContentResolver contentResolver = getContentResolver();
        	
        	ContentValues values1 = new ContentValues();
        	values1.put(MediaStore.Audio.Media.TITLE, strTitle);
        	values1.put(MediaStore.Audio.Media.DATA, uri);
        	values1.put(MediaStore.Audio.Media.SIZE, "");
        	values1.put(MediaStore.Audio.Media.ARTIST, strAuthor);
        	
        	contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values1);
    	}
    };
    

    
    
	//Handler消息机制
    //更新ui
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
					// json数据进行处理
					
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
					
					
					//如果用户下载歌词
					if(LrcActivity.inLrcActivity.isDownloadLrc){
						listMp3ToListHashmap(LrcActivity.inLrcActivity.handler);
						LrcActivity.inLrcActivity.isDownloadLrc = false;
					}
					//如果用户搜索歌曲信息
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
					Toast.makeText(instance, songName + "已加入下载列表", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(instance, songName + "即将播放", Toast.LENGTH_SHORT).show();
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
		//如果播放地址为空，则换源下载
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
			// 将Mp3Info对象存入到Intent对象当中
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
			
			//将艺人id写入本地
			LrcActivity.inLrcActivity.editor.putString(BaiduXmlHandler.mp3.getTitle() + BaiduXmlHandler.mp3.getAuthor(), BaiduXmlHandler.mp3.getPic_small());
			LrcActivity.inLrcActivity.editor.commit();
			LrcActivity.inLrcActivity.editor.putString(BaiduXmlHandler.mp3.getAuthor(), BaiduXmlHandler.mp3.getArtist_id());
			LrcActivity.inLrcActivity.editor.commit();
			
			intent.setClass(MainActivity.this,cls);
			// 启动Service
			MainActivity.this.startService(intent);
		}
	}

	public void listMp3ToListHashmap(Handler handler) {
		ArrayList<HashMap<String, String>> listItemTemp = new ArrayList<>();
		//将listMp3转换为list<hashmap<String,String>>，然后更新在listView中
		listItemTemp = publicVariable.praseToListHashmap(PublicVariable.networkMp3ModelList,false);
		Message message = new Message();
		message.what = publicVariable.WHAT_UPDATE_VIEW;
		message.obj = listItemTemp;
		//发送handler消息，接收后更新Ui
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
     * 添加tab标签  
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
	 * 设置保存当前activity的值
	 * 0表示mainactivity
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		LrcActivity.inLrcActivity.setCurrentActivity(0);
		super.onResume();
	}
	
	//退出
    @Override  
    protected void onDestroy() {  
    	finish();
    	PlayerService.isNotify = true;
        super.onDestroy();  
    }  
      
    
    //网络歌单页面歌榜单点击事件
    //新歌榜
    public void networkPlay(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(1);
    }
    
    
    //网络歌单页面歌榜单点击事件
    //流行
    public void popularSongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(16);
    }
    
    //网络歌单页面歌榜单点击事件
    //经典老歌
    public void classicSongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(22);
    }
    
    
    //网络歌单页面歌榜单点击事件
    //热歌榜
    public void hotSongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(2);
    }
    
    
    //网络歌单页面歌榜单点击事件
    //摇滚
    public void rockSongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(11);
    }
    
    //网络歌单页面歌榜单点击事件
    //情歌
    public void loveSongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(23);
    }
    
    //网络歌单页面歌榜单点击事件
    //影视
    public void moveSongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(24);
    }
    
    //网络歌单页面歌榜单点击事件
    //欧美金曲
    public void EnglishSongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(21);
    }
    
    //网络歌单页面歌榜单点击事件
    //爵士
    public void jazzSongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(12);
    }
    
    //网络歌单页面歌榜单点击事件
    //网络歌曲
    public void networkSongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(25);
    }
    
    //网络歌单页面歌榜单点击事件
    //KTV歌曲
    public void KTVSongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(6);
    }
    
    
    //本地音乐点击事件
    //寂寞天籁
    public void lonelySongsClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(9);
    }
    
    //本地音乐点击事件
    //耳暖情怀
    public void loveSongsClick2(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(7);
    }
    
    //本地音乐点击事件
    //醉歌曼舞
    public void BillboardClick(View v){
    	
    	Log.v("123","网络");
    	startNetworkSongs(8);
    }
    

    /**
     * 根据type类型查询对应的歌单
     * @param type
     */
    private void startNetworkSongs(int type){
    	Intent it = new Intent();
    	it.setClass(this, NetworkSongsActivity.class);
    	it.putExtra("type", type);
    	startActivity(it);
    }


   /**
    * 播放和暂停监听方法
    */
    public void playAndPause() {
    	
    	if(!PlayerService.isPlayOnLine){
    		if(mFragment1.downloadListMp3.size() == 0){
    			Toast.makeText(instance, "还没有歌曲哦~\n先下载歌曲吧~", Toast.LENGTH_SHORT).show();
    			return;
    		}
    	}
    	
		if(PlayerService.isPlayOnLine && MainActivity.isPlaying){
			if(MainActivity.isPause){
				if(PlayerService.mediaPlayer != null){
					PlayerService.mediaPlayer.start();
				}
				
				playerPlay();//改变图标
				//改变状态
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
			//没有播放
			//开启播放服务
			play(DowmloadList.playingPosition);
			PlayerService.isPlayOnLine = false;
		}
		
		//播放进度条和歌词控制
		//正在播放且没有暂停
		if(MainActivity.isPlaying && MainActivity.isPause){
			handler_time.removeCallbacks(updateTimeCallback);
			pauseTimeMills = System.currentTimeMillis();
		}
		//正在播放，且处于暂停状态
		else if(MainActivity.isPlaying && !MainActivity.isPause){
			handler_time.postDelayed(updateTimeCallback, 5);
//			begin = System.currentTimeMillis() - pauseTimeMills + begin;
		}
	}

    /**
     * 播放下一曲监听方法
     */
	public void playNext() {
	//如果是在线播放，则在线列表下一曲
	if(PlayerService.isPlayOnLine){
		 OverrideListAdapter.inOverrideListAdapter.click(++OverrideListAdapter.hearListPosition, 
	        		PublicVariable.DOWNLOAD_NETWORK_SONGS_PLAY_XML_BY_SONGID,1);
	}
	else{
		//启动播放
		
		DowmloadList.isSongListClick = true;
		if(playModeChangeInt == 3)
		{
			//单曲循环
			play(DowmloadList.playingPosition);
		}
		else if(playModeChangeInt == 2){
			//列表循环
			play(++DowmloadList.playingPosition);
		}
		else{
			//顺序播放
			int i = DowmloadList.downloadListMp3.size();
			if(DowmloadList.playingPosition < i-1 ){
				play(DowmloadList.playingPosition);
			}						
			DowmloadList.playingPosition ++ ;
		}
	}
}

	/**
	 * 显示歌词搜索按钮，并改变相应状态
	 */
	private void lrcDownloadButtonDisplay() {
		LrcActivity.inLrcActivity.textView1.setText("哦活~没有找到歌词哦~");
		LrcActivity.inLrcActivity.textView2.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.timeAndLrcButtonlayout.setVisibility(View.VISIBLE);
		LrcActivity.inLrcActivity.Layout_upOrDown.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.isUp = true;
		LrcActivity.inLrcActivity.upOrDown.setImageDrawable(getResources().getDrawable(R.drawable.up));
	}
	
	/**
	 * 隐藏歌词搜索按钮，并改变相应状态
	 */
	private void lrcDownloadButtonhide() {

		LrcActivity.inLrcActivity.timeAndLrcButtonlayout.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.Layout_upOrDown.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.isUp = false;
		LrcActivity.inLrcActivity.upOrDown.setImageDrawable(getResources().getDrawable(R.drawable.down));
	}

	/**
	 * 点击歌词按钮
	 */
	public void lrcDisplayClick() {
//		int color = LrcActivity.inLrcActivity.preference.getInt("color", 0);
		//首次打开歌词不准为白色字体
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
	 * 改变播放模式
	 */
	public void playModeChanged() {
		Resources res = getResources();
		
		// 判断播放模式
		// 1：顺序播放
		// 2: 列表循环
		// 3: 单曲循环
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
			
			Toast.makeText(MainActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			playModeButton.setBackgroundResource(R.drawable.button_list_after_list);
			if(PublicVariable.isSongInfoActivityStart){
				SongInfoActivity.infoActivity.playModeButton.setBackgroundResource(R.drawable.button_list_after_list);
			}
			
			Toast.makeText(MainActivity.this, "列表循环", Toast.LENGTH_SHORT).show();
			break;
		case 3:
			playModeButton.setBackgroundResource(R.drawable.buttton_one_song_play);
			if(PublicVariable.isSongInfoActivityStart){
				SongInfoActivity.infoActivity.playModeButton.setBackgroundResource(R.drawable.buttton_one_song_play);
			}
			Toast.makeText(MainActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
			break;

		default: playModeChangeInt = 1;
			break;
		}
	}

	/**
	 * 根据参数启动服务
	 * 下载或播放
	 * @param cls 启动服务的对象
	 * @param SongName 歌曲名称
	 * @param songid  歌曲id
	 */
	public boolean startServiceBySongid(final Class cls, final String SongName,String songid) {
		HttpDownloader httpDownloader = new HttpDownloader();
		String downloadUrl  = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.play&songid="+ 
				songid+"&format=json";
		Log.v("128", downloadUrl);
		String jsonStr = httpDownloader.download(downloadUrl);
		if(!PublicVariable.isNUll(jsonStr)){
			//解析json
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
				// 将Mp3Info对象存入到Intent对象当中
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
				// 启动Service
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
        	//解决数据刷新的问题和notifyDataSetChanged配合使用
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
