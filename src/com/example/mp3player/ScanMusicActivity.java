package com.example.mp3player;

import java.io.File;

import com.example.mp3player.db.DatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScanMusicActivity extends Activity{
	
	
	
	TextView textView;
	Button button;
	ProgressBar bar;
	
	/**
	 * 设置保存当前activity
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		LrcActivity.inLrcActivity.setCurrentActivity(3);
		super.onResume();
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//去掉activity标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.first);
		
		PublicVariable.isScanMusicInt++;
		
		textView = (TextView)findViewById(R.id.scan_textview);
		button = (Button)findViewById(R.id.scan_button);
		bar = (ProgressBar)findViewById(R.id.sacn_progress);
		
		Intent intent = getIntent();
		int num = intent.getIntExtra("num", -1);
//		num == 0
		if(num == 0){
			textView.setWidth(300);
			
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					button.setVisibility(View.GONE);
					textView.setText("扫描中...");
					scanMusic();
				}
			});
		}
		else{
			button.setVisibility(View.GONE);
			textView.setText("扫描中...");
			scanMusic();
		}
				
	}

	/**
	 * 扫描本地音乐，直接读取本地数据库
	 * 速度快，广播机制速度慢
	 */
	private void scanMusic() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Cursor cursor = null;
				try{
					int n = 0;
					int sleepTime = 1000;//初始睡眠时间
					//扫描数据库音乐信息
					cursor =   getContentResolver().
							query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,null, null, 
									MediaStore.Audio.Media._ID);

					//发送消息，设置扫描显示bar最大长度
					int barMax = cursor.getCount();
					Message msg0 = new Message();
					msg0.what = 0;
					msg0.arg1 = barMax;
					handler.sendMessage(msg0);

					DatabaseHelper databaseHelper = DatabaseHelper.getInstance(MainActivity.instance, "test_db");
					SQLiteDatabase db = databaseHelper.getWritableDatabase();
					db.delete("downloadSonglist", null, null);
					
					String titleTemp = null;
					String songerTemp = null;
					
					while (cursor.moveToNext()) {
						//歌曲的名称 ：MediaStore.Audio.Media.TITLE
						String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));  
						//歌曲文件的路径 ：MediaStore.Audio.Media.DATA
						String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)); 
						String size =cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE));
						String songer =cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST));  
				        
						String titleTemp1 = title;
						
						
						//避免同一首歌曲被重复扫描
						if((title == titleTemp || title.equals(titleTemp))&& 
								(songer == songerTemp || songer.equals(songerTemp))){
							continue;
						}else{
							titleTemp = title;
							songerTemp = songer;
						}
						
						//歌手信息未知，则歌手信息在歌曲名称信息中，一般格式为 "歌手 - 歌曲"
						if(songer.indexOf("unknown") > 0){
							title = title.replace(".", "");//酷狗音乐在名称末尾含有.
							//歌曲歌手信息处理
							int i = title.indexOf("-");
							if(i > 0){
								songer = title.substring(0,i);
								songer = songer.replace("download:", "").replace("-", "");//酷狗音乐在名称前含有downloaad:
								title = title.substring(i+1, title.length()).replace("-", "");
							}
						}
						
						//酷狗音乐uri开始不含downlaod:，需要加上
						if(url.indexOf("kgmusic") > -1 && url.indexOf("download:") == -1){
							//url = url.replace("kgmusic/", "kgmusic/download:");
						}
						
						
							//将音乐信息写入数据库
							PublicVariable.downloadSongListInsertDatabase(title, size, null, null, songer, url,false);
							
							//更新扫描时的显示信息，以动态显示
							Message msg1 = handler.obtainMessage();
							msg1.what = 1;
							msg1.arg1 = n;
							msg1.obj = songer + "-" + title;
							handler.sendMessage(msg1);
							
							Log.v("123", "tilte:"+title + "size:" + size);
							Log.v("123", songer + "   url:"+url);
							n++;//音乐数量+1
							
							if(sleepTime > 5 && n < barMax){
								sleepTime = sleepTime/2;
								SystemClock.sleep(sleepTime/2);//睡眠时间逐步衰减
							}
						}
					
				
					Message msg2 = handler.obtainMessage();
					msg2.what = 2;
					msg2.arg1 = n;
					msg2.arg2 = barMax;
					handler.sendMessage(msg2);
					
					Message msg = new Message();
					msg.what = PublicVariable.WHAT_UPDATE_VIEW;
					MainActivity.instance.handler.sendMessage(msg);

				}
				catch(Exception e){
					e.printStackTrace(); 
				}
				finally{
					if(cursor != null){
						cursor.close();
					}
				}
			}
		}).start();

	}
	
	/**
	 * ui在主线程更新扫描信息
	 */
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			//扫描进行中
			if(msg.what == 1){
				bar.setProgress(msg.arg1);
				textView.setText("扫描中..." + (String)msg.obj);
			}
			//扫描开始
			else if(msg.what == 0){
				bar.setMax(msg.arg1);
			}
			//扫描结束
			else if(msg.what == 2){
				bar.setProgress(msg.arg2);
				textView.setText("扫描完成，共扫描到" + msg.arg1 + "首音乐");
			}
		}
	};	
}
