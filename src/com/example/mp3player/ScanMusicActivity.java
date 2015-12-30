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
	 * ���ñ��浱ǰactivity
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
		
		//ȥ��activity����
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
					textView.setText("ɨ����...");
					scanMusic();
				}
			});
		}
		else{
			button.setVisibility(View.GONE);
			textView.setText("ɨ����...");
			scanMusic();
		}
				
	}

	/**
	 * ɨ�豾�����֣�ֱ�Ӷ�ȡ�������ݿ�
	 * �ٶȿ죬�㲥�����ٶ���
	 */
	private void scanMusic() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Cursor cursor = null;
				try{
					int n = 0;
					int sleepTime = 1000;//��ʼ˯��ʱ��
					//ɨ�����ݿ�������Ϣ
					cursor =   getContentResolver().
							query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,null, null, 
									MediaStore.Audio.Media._ID);

					//������Ϣ������ɨ����ʾbar��󳤶�
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
						//���������� ��MediaStore.Audio.Media.TITLE
						String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));  
						//�����ļ���·�� ��MediaStore.Audio.Media.DATA
						String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)); 
						String size =cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE));
						String songer =cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST));  
				        
						String titleTemp1 = title;
						
						
						//����ͬһ�׸������ظ�ɨ��
						if((title == titleTemp || title.equals(titleTemp))&& 
								(songer == songerTemp || songer.equals(songerTemp))){
							continue;
						}else{
							titleTemp = title;
							songerTemp = songer;
						}
						
						//������Ϣδ֪���������Ϣ�ڸ���������Ϣ�У�һ���ʽΪ "���� - ����"
						if(songer.indexOf("unknown") > 0){
							title = title.replace(".", "");//�ṷ����������ĩβ����.
							//����������Ϣ����
							int i = title.indexOf("-");
							if(i > 0){
								songer = title.substring(0,i);
								songer = songer.replace("download:", "").replace("-", "");//�ṷ����������ǰ����downloaad:
								title = title.substring(i+1, title.length()).replace("-", "");
							}
						}
						
						//�ṷ����uri��ʼ����downlaod:����Ҫ����
						if(url.indexOf("kgmusic") > -1 && url.indexOf("download:") == -1){
							//url = url.replace("kgmusic/", "kgmusic/download:");
						}
						
						
							//��������Ϣд�����ݿ�
							PublicVariable.downloadSongListInsertDatabase(title, size, null, null, songer, url,false);
							
							//����ɨ��ʱ����ʾ��Ϣ���Զ�̬��ʾ
							Message msg1 = handler.obtainMessage();
							msg1.what = 1;
							msg1.arg1 = n;
							msg1.obj = songer + "-" + title;
							handler.sendMessage(msg1);
							
							Log.v("123", "tilte:"+title + "size:" + size);
							Log.v("123", songer + "   url:"+url);
							n++;//��������+1
							
							if(sleepTime > 5 && n < barMax){
								sleepTime = sleepTime/2;
								SystemClock.sleep(sleepTime/2);//˯��ʱ����˥��
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
	 * ui�����̸߳���ɨ����Ϣ
	 */
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			//ɨ�������
			if(msg.what == 1){
				bar.setProgress(msg.arg1);
				textView.setText("ɨ����..." + (String)msg.obj);
			}
			//ɨ�迪ʼ
			else if(msg.what == 0){
				bar.setMax(msg.arg1);
			}
			//ɨ�����
			else if(msg.what == 2){
				bar.setProgress(msg.arg2);
				textView.setText("ɨ����ɣ���ɨ�赽" + msg.arg1 + "������");
			}
		}
	};	
}
