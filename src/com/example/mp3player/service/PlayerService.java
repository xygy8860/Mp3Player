package com.example.mp3player.service;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import mars.download.HttpDownloader;

import com.example.mp3player.DowmloadList;
import com.example.mp3player.LrcActivity;
import com.example.mp3player.MainActivity;
import com.example.mp3player.Mp3;
import com.example.mp3player.NetworkSongsActivity;
import com.example.mp3player.OverrideListAdapter;
import com.example.mp3player.PublicVariable;
import com.example.mp3player.R;
import com.example.mp3player.SongInfo;
import com.example.mp3player.SongInfoActivity;
import com.example.mp3player.broadcast.NotificationBroadcastReceiver;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class PlayerService extends Service implements OnPreparedListener, OnBufferingUpdateListener, OnCompletionListener{
	
	Mp3 mp3 = null;
	TelephonyManager manager; 
	boolean isPhone = false;
	public static MediaPlayer mediaPlayer;
	public static boolean isPlayOnLine = false;
	public static PlayerService inPlayservice = null;
	
	public static String strTitle = null;
	public static String strAuthor = null;
	public static String strPic_small = null;
	public static String strSong_id = null;
	 
	public static ImageLoader loader = ImageLoader.getInstance();
	
	public static String name = null;//歌手
	public static String avatar_middle = null;//歌手信息小图片地址，歌手信息用
	public static String intro = null;//歌手信息
	public static String avatar_s500 = null;//歌手大图片  歌曲信息用
	public static String country = null;//歌手国家或地区
	public static String avatar_mini = null;//歌手mini如片，背景虚化用
	public static Bitmap bitmapMini = null;//设置虚化背景
	
	public static boolean isNotify = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.v("123", "service---->  creat");
		super.onCreate();
		
		inPlayservice = this;
		
        manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);  
        // 手动注册对PhoneStateListener中的listen_call_state状态进行监听  
        manager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE); 
        
        mediaPlayer = new MediaPlayer();  
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型  
        mediaPlayer.setOnBufferingUpdateListener(this);  
        mediaPlayer.setOnPreparedListener(this);  
        
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.v("123", "service---->  onStartCommand");
		
		//先清除时间
		MainActivity.instance.handler_time.removeCallbacks(MainActivity.instance.updateTimeCallback);
		//先清除歌词
		MainActivity.instance.handler_lrc.removeCallbacks(MainActivity.instance.updateLrcCallback);
		
		//试听播放
		strTitle = intent.getStringExtra("strSongInfo");
		//如果不为空，则为试听播放
	   	if(strTitle != null && strTitle != "" && !(strTitle.equals(""))){
			strAuthor = intent.getStringExtra("strSonger");
			strSong_id =intent.getStringExtra("strSize");
			String strDownload_file_link = intent.getStringExtra("strLrcName");
			String strDownload_show_link = intent.getStringExtra("strLrcSize");
			String strFile_extension = intent.getStringExtra("file_extension");
			strPic_small = intent.getStringExtra("pic_small");
			
			final String lrclink = intent.getStringExtra("lrclink");
			Log.v("126", "lrclink-->" + lrclink);
			
			//在试听音乐的同时下载歌词
			downloadByLrcLink(lrclink);
			
			if(MainActivity.mediaPlayer != null){
				if(MainActivity.isPlaying || MainActivity.isPause){
					MainActivity.mediaPlayer.stop();
				}
				stopStateBoolean();//状态全部设为false
			}
			mediaPlayer.reset();
			try {
				boolean isLinkNull = false;
				//第一个播放地址为空，则播放第二个地址
				if(strDownload_file_link == null || strDownload_file_link.equals(null)){
					isLinkNull = true;
				}else{
					mediaPlayer.setDataSource(strDownload_file_link);
				}
				
				//mediaPlayer.setDataSource(strDownload_file_link);
				//如果第一个地址为空或者资源错误，第二个地址播放
				if(mediaPlayer == null || isLinkNull){
					isLinkNull = false;
					if(strDownload_show_link == null || strDownload_show_link.equals(null)){
						//return playNext(intent, flags, startId, strTitle);
						isLinkNull = true;
					}else{
						mediaPlayer.setDataSource(strDownload_show_link);
					}
					
					//mediaPlayer.setDataSource(strDownload_show_link);
					if(mediaPlayer == null || isLinkNull){
						//如果两个地址都错误或都为空，则播放下一首
						return playNext(intent, flags, startId, strTitle);
					}
				}
				mediaPlayer.prepareAsync();//准备播放（异步准备）
				mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub
//						if(mediaPlayer != null){
//							mediaPlayer.stop();
//						}
						mp.start();

						//更新seekbar播放进度条
						Message message = new Message();
						message.what = 0;
						message.arg1 = mediaPlayer.getDuration();
						message.obj = System.currentTimeMillis();//当前系统时间作为开始时间
						MainActivity.instance.handlerUpdateSeekBar.sendMessage(message);
						Log.v("127", "准备完成");
					}
				});
				
				MainActivity.instance.playerPlay();//将图标换为暂停图标
				MainActivity.isPlaying = true;
				MainActivity.isPause = false;
				isPlayOnLine = true;
				//播放完毕后的监听
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						//播放完后播放下一首
					    OverrideListAdapter.inOverrideListAdapter.click(++OverrideListAdapter.hearListPosition, 
					        		PublicVariable.DOWNLOAD_NETWORK_SONGS_PLAY_XML_BY_SONGID,1);
					    Log.v("127", "网络播放完成");
					}
				});
				//跑马灯设置歌手信息
				MainActivity.songAndSonger.setText(strTitle + "-" + strAuthor + "                            ");
				
			} catch (IllegalArgumentException | SecurityException
					| IllegalStateException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//正常按钮播放
		else{
			playMp3(DowmloadList.playingPosition);
		}
		
		
		
		loadingPicSmall(strAuthor,strTitle);
		
		//清空歌词
		if(PublicVariable.lrcStrListTemp.size() > 0){
			PublicVariable.lrcStrListTemp.removeAll(PublicVariable.lrcStrListTemp);
		}
		
		startNofify(strTitle,strAuthor);
		
		
		return super.onStartCommand(intent, flags, startId);
		
	}

	/**
	 * 设置服务为前台服务，避免被后台系统杀死
	 * @param title 歌曲名称
	 * @param author 演唱艺人
	 */
	 
	private void startNofify(String title,String author ) {
		unregeisterReceiver();
        intiReceiver();
        
       // Log.v("124", "getPackageName-->" + getPackageName());
        Log.v("124", "MainActivity.instancegetPackageName-->" + MainActivity.instance.getPackageName());

        //填充标题和作者
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.tv_up, title);
        remoteViews.setTextViewText(R.id.tv_down, author);

        //为播放按钮添加广播
        Intent intent = new Intent(PublicVariable.ACTION_BTN);
        intent.putExtra(PublicVariable.INTENT_NAME, PublicVariable.INTENT_BTN_CLEAR);
        PendingIntent intentpi = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear, intentpi);

        Intent intent2 = new Intent();
        intent2.setClass(this, DownloadPic.class);
        intent2.putExtra("isNofity", true);
        //intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent intentContent = PendingIntent.getService(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setOngoing(false);
        builder.setAutoCancel(false);
        builder.setContent(remoteViews);
        builder.setTicker("520Music");
        builder.setSmallIcon(R.drawable.mp3);

        Notification notification = builder.build();
        //notification.defaults = Notification.DEFAULT_SOUND;
        //notification.flags = Notification.FLAG_NO_CLEAR;
        notification.contentIntent = intentContent;
        
        startForeground(0x111, notification);
	}
	

	/**
	 * 注册广播
	 */
	public void intiReceiver() {
		// TODO Auto-generated method stub
		PublicVariable.mReceiver = new NotificationBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PublicVariable.ACTION_BTN);
        getApplicationContext().registerReceiver(PublicVariable.mReceiver, intentFilter);
	}

	/**
	 * 取消注册广播
	 */
	public void unregeisterReceiver() {
		// TODO Auto-generated method stub
		if (PublicVariable.mReceiver != null) {
            getApplicationContext().unregisterReceiver(PublicVariable.mReceiver);
            PublicVariable.mReceiver = null;
        }
	}

	/**
	 * //为最下角播放加载图片
	 */
	public void loadingPicSmall(String songerName,String songName) {
		
		//如果开启了歌曲歌手activity，则同步更新背景图片
		if(PublicVariable.isSongInfoActivityStart ){//&& !PublicVariable.isStartSongInfoActivityFromNetworklist
			Log.v("126", "updateBitmap-->updateBitmap");
			//查找歌手uid
			String ting_uid = LrcActivity.inLrcActivity.preference.getString(songerName, null);
			SongInfoActivity.infoActivity.updateBitmap(ting_uid);
			SongInfo.info.songerName.setText(songerName);
			SongInfo.info.songName.setText(songName);
		}
				
		//加载最下角播放图片
		strPic_small = LrcActivity.inLrcActivity.preference.getString(songName + songerName, null);
		Log.v("126", "strPic_small-->" + strPic_small);
		if(strPic_small == null || strPic_small.equals(null)){
			
		}else{
			//为最下角播放加载图片
			loader.displayImage(strPic_small, 
					MainActivity.instance.songerImage, PublicVariable.optionsRound);
			
			if(PublicVariable.isSongInfoActivityStart){
				loader.displayImage(strPic_small, 
						SongInfoActivity.infoActivity.songerImage, PublicVariable.optionsRound);
			}
		}
	}
	
	/**
	 * 根据歌词连接下载歌词
	 * @param lrclink
	 */
	public void downloadByLrcLink(final String lrclink) {
		if(lrclink != "" && lrclink != null && !(lrclink.equals(null))){
			//如果有歌词连接，则开启线程下载
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					HttpDownloader httpDownloader = new HttpDownloader();
					int j = httpDownloader.downFile(lrclink, "mp3/", strTitle + ".lrc","lrc");
					Log.v("125", "j-->" + j);
					if(j != -1){
						MainActivity.instance.prepareLrc(strTitle);
						//将偏移时间设置为0，以使歌词同步
						MainActivity.instance.offset = 0;
						MainActivity.instance.handler_lrc.postDelayed(MainActivity.instance.updateLrcCallback, 5);
					}
				}
			}).start();				
		}
	}

	private int playNext(Intent intent, int flags, int startId, String strTitle) {
		Toast.makeText(MainActivity.instance, strTitle + "播放错误!", Toast.LENGTH_SHORT).show();
//		MainActivity.isPlaying = false;
//		MainActivity.isPause = false;
//		MainActivity.isReleased = false;
		//播放下一首
		OverrideListAdapter.inOverrideListAdapter.click(++OverrideListAdapter.hearListPosition, 
				PublicVariable.DOWNLOAD_NETWORK_SONGS_PLAY_XML_BY_SONGID,1);
		return super.onStartCommand(intent, flags, startId);
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		stopForeground(true);
		
		//释放资源
		Log.v("123", "service---->  stop");
		stopSelf();
		if(MainActivity.mediaPlayer != null){
			MainActivity.mediaPlayer.stop();
			MainActivity.mediaPlayer.release();
		}
		MainActivity.instance.playerPause();
		MainActivity.songAndSonger.setText("欢迎使用MP3播放器                            ");
		stopStateBoolean();
		if(mediaPlayer != null){
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		super.onDestroy();
	}

	/**停止状态
	 * 播放状态控制变量全为false
	 */
	public void stopStateBoolean() {
		MainActivity.isPlaying = false ;
		MainActivity.isPause = false;
		MainActivity.isReleased = false;
	}
	

	public void playMp3(int position) {
		
		Log.v("123", "playmp3--->"  + position);
		//未播放
		if(!MainActivity.isPlaying){
			playSetting(position);
		}
		//正在播放
		else{
			//判断用户是否点击了歌曲列表
			if(DowmloadList.isSongListClick){
				playSetting(position);
				DowmloadList.isSongListClick = false;
			}
		}
	}

	/**
	 * 播放相关设置
	 * @param position 播放列表的位置
	 */
	private void playSetting(int position) {
		
		int i = DowmloadList.downloadListMp3.size();
		//如果播放列表到最后，回到第一首播放
		//实现列表循环播放
		//playModeChangeInt == 2 才为列表循环
		if(MainActivity.playModeChangeInt == 2 && position >= DowmloadList.downloadListMp3.size()){
			position = 0;
			DowmloadList.playingPosition = 0;
			//如果歌曲不存在，列表循环失效，不然会无限死循环
			if(!PublicVariable.isSongFileExist){
				MainActivity.instance.playerPause();
				return;
			}
		}      
		//playModeChangeInt == 1  顺序播放
		else if(MainActivity.playModeChangeInt == 1 && position >= DowmloadList.downloadListMp3.size()){
			if(MainActivity.mediaPlayer == null){
				MainActivity.instance.playerPause();
				return;
			}
			MainActivity.mediaPlayer.stop();
			DowmloadList.playingPosition = 0;
			
			MainActivity.instance.playerPause();
			stopStateBoolean();
			
			return;
		}
		else if (position >= DowmloadList.downloadListMp3.size()){
			//如果歌曲不存在，列表循环失效，不然会无限死循环
			if(!PublicVariable.isSongFileExist){
				MainActivity.instance.playerPause();
				return;
			}
			position = 0;
			DowmloadList.playingPosition = 0;
		}

		Log.v("123","position-->" + position);
		
		mp3 = DowmloadList.downloadListMp3.get(position);
		String songName = mp3.getMp3Name();
		String songerName = mp3.getSonger();
		
		loadingPicSmall(songerName,songName);
		
		startNofify(songName,songerName);

		//获取歌曲播放路径
		String path = "file://" + mp3.getUri();
		
		Log.v("124",path);
		try{
			path = PublicVariable.UrlEncodeToUTF8(path);
			path = path.replace("+", "%20");
			String filePath = path.replace("file://", "");
			File file = new File(mp3.getUri());
			if(file.exists()){
				Log.v("128","播放路径存在         " + mp3.getUri());
			}else{
				Log.v("128","播放路径    不   存在        " + mp3.getUri());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
//		if()
//		MainActivity.mediaPlayer.release(); 
		path = path.replace("%3A", ":").replace("%2F", "/");
		Uri uri = Uri.parse(path);
		
		Log.v("128","uri-->  " + uri);
		
		MainActivity.mediaPlayer = MediaPlayer.create(this,uri);
		if(MainActivity.mediaPlayer == null){
			PublicVariable.isSongFileExist = false;
			Toast.makeText(MainActivity.instance, songName + "不存在", Toast.LENGTH_SHORT).show();
			MainActivity.instance.playerPause();
			playSetting(++DowmloadList.playingPosition);//自动播放下一首
			return;
		}
		//错误监听器
		MainActivity.mediaPlayer.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				MainActivity.mediaPlayer.stop();
				stopStateBoolean();//改变播放状态
				playSetting(++DowmloadList.playingPosition);
				Log.v("123","error--->play");
				return false;
			}
		});
		
		//播放完毕监听器
		MainActivity.mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				Log.v("123","Completion--->play");
				
				MainActivity.instance.playerPause();
				
				stopStateBoolean();
				
				//playModeChangeInt == 3 单曲循环
				if(MainActivity.playModeChangeInt == 3){
					playMp3(DowmloadList.playingPosition);
				}
				else{
					playMp3(++DowmloadList.playingPosition);
				}
			}
		});	
		
		MainActivity.mediaPlayer.setLooping(false);//设置单曲循环为false
		MainActivity.mediaPlayer.start();//启动播放
		MainActivity.instance.playerPlay();//变换播放图标
		//跑马灯显示歌曲和歌手信息
		MainActivity.songAndSonger.setText(mp3.getMp3Name() + "-" + mp3.getSonger() + "                         ");
		//设置播放状态
		MainActivity.isPlaying = true ;
		MainActivity.isPause = false;
		MainActivity.isReleased = false;
		
		//从sd卡读取歌词文件做准备
		//根据歌曲名称在sd卡MP3文件夹下准备文件
		MainActivity.instance.prepareLrc(mp3.getMp3Name());
		
		strTitle = mp3.getMp3Name();
		strAuthor = mp3.getSonger();
		
		//更新seekbar播放进度条
		Message message = new Message();
		message.what = 0;
		message.arg1 = MainActivity.mediaPlayer.getDuration();
		long sysTime = System.currentTimeMillis();
		message.obj = sysTime;//当前系统时间作为开始时间
		MainActivity.instance.handlerUpdateSeekBar.sendMessage(message);
		
		MainActivity.instance.handler_lrc.postDelayed(MainActivity.instance.updateLrcCallback, 5);

	}

	public static String getMp3Path(String mp3Name){
		String sdcardRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
		String mp3Path =sdcardRoot + File.separator + "mp3" + File.separator + mp3Name;
		return mp3Path;
	}
	
	

	   /*** 
	    * 继承PhoneStateListener类，我们可以重新其内部的各种监听方法 
	    *然后通过手机状态改变时，系统自动触发这些方法来实现我们想要的功能 
	    */  
	    class MyPhoneStateListener extends PhoneStateListener{  
	  
	        @Override  
	        public void onCallStateChanged(int state, String incomingNumber) {  
	            switch (state) {  
	            case TelephonyManager.CALL_STATE_IDLE:  
//	               String result=" 手机空闲起来了  ";  
	            	if(isPhone){
	            		if(PlayerService.isPlayOnLine){
	            			PlayerService.mediaPlayer.start();
	            		}else{
	            			MainActivity.mediaPlayer.start();
	            		}
	            		MainActivity.isPause = false;
	            		MainActivity.isPlaying = true;
	            		MainActivity.isReleased = false;
	            		isPhone = false;
	            	}
	                break;  
	            case TelephonyManager.CALL_STATE_RINGING:  
//	               String  result="  手机铃声响了，来电号码:"+incomingNumber;  
	            	pauseMediaPlayer();
	            	
	                break;  
	            case TelephonyManager.CALL_STATE_OFFHOOK:  
//	                result=" 电话被挂起了 ";  
	            	pauseMediaPlayer();
	            	
	            default:  
	                break;  
	            }  
//	            textView.setText(result);  
	            super.onCallStateChanged(state, incomingNumber);  
	        }  
	          
	    }   
	    
	// 播放完成  
	@Override  
	public void onCompletion(MediaPlayer mp) {  
	        Log.v("124", "onCompletion"); 
	        OverrideListAdapter.inOverrideListAdapter.click(++OverrideListAdapter.hearListPosition, 
	        		PublicVariable.DOWNLOAD_NETWORK_SONGS_PLAY_XML_BY_SONGID,1);
	}  
	
	   
	//缓冲更新   
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		
	}
	
	// 加载完毕才可以播放
	//速度太慢，网速卡的时候就是个悲剧
	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mp.start();
	}

	/**
	 * 监听来电去电和通话状态，执行播放暂停
	 */
	public void pauseMediaPlayer() {
		//电话来时只有处于播放状态且不是暂停状态，才执行如下操作
		if(MainActivity.isPlaying && !MainActivity.isPause){
			
			if(PlayerService.isPlayOnLine){
				PlayerService.mediaPlayer.pause();
			}else{
				MainActivity.mediaPlayer.pause();
			}
			isPhone = true;
			MainActivity.isPause = true;
			MainActivity.isPlaying = true;
			MainActivity.isReleased = false;
		}
	} 
}
