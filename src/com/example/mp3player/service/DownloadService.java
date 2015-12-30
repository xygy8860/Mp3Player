package com.example.mp3player.service;

import mars.download.HttpDownloader;

import com.example.mp3player.MainActivity;
import com.example.mp3player.NowContext;
import com.example.mp3player.PublicVariable;
import com.example.mp3player.R;
import com.example.mp3player.db.DatabaseHelper;

import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class DownloadService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		Log.v("124", "onStartCommand-->" );
		
		String strTitle = (intent.getStringExtra("strSongInfo"));
		if(strTitle != "" && strTitle != null && !(strTitle.equals(null))){
			strTitle.replace(" ", "-");
		}
		String strAuthor = intent.getStringExtra("strSonger");
		String strSong_id =intent.getStringExtra("strSize");
		String strDownload_file_link = intent.getStringExtra("strLrcName");
		String strDownload_show_link = intent.getStringExtra("strLrcSize");
		String strFile_extension = intent.getStringExtra("file_extension");
		String strFile_size = intent.getStringExtra("file_size");
		String lrclink = intent.getStringExtra("lrclink");
		String strPic_small = intent.getStringExtra("pic_small");
		String artist_id = intent.getStringExtra("artist_id");
		
		DownloadThread downloadThread = new DownloadThread(strTitle,strDownload_file_link,strSong_id,
				strDownload_show_link,strAuthor,strFile_extension,strFile_size,lrclink,strPic_small,artist_id);
		downloadThread.start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	/**
	 * ���ظ����͸�ʵ��߳�
	 * @author Administrator
	 *
	 */
	class DownloadThread extends Thread{

		private String strTitle;
		private String strSong_id;
		private String strDownload_file_link;
		private String strDownload_show_link;
		private String strAuthor;
		private String strFile_extension;
		private String strFile_size;
		private String lrclink;
		private String pic_small;
		private String artist_id;

		public DownloadThread(String strTitle, String strSong_id,
				String strDownload_file_link, String strDownload_show_link,
				String strAuthor, String strFile_extension, String strFile_size,
				String lrclink, String strPic_small, String artist_id) {
			super();
			this.strTitle = strTitle;
			this.strSong_id = strSong_id;
			this.strDownload_file_link = strDownload_file_link;
			this.strDownload_show_link = strDownload_show_link;
			this.strAuthor = strAuthor;
			this.strFile_extension = strFile_extension;
			this.strFile_size = strFile_size;
			this.lrclink = lrclink;
			this.pic_small = strPic_small;
			this.artist_id = artist_id;
		}
		public DownloadThread() {
			super();
			// TODO Auto-generated constructor stub
		}
		@Override
		public  void run() {
			// TODO Auto-generated method stub
			downloadSongs(strTitle, strDownload_file_link, strSong_id, 
					strDownload_show_link,strAuthor,strFile_extension,strFile_size,lrclink,pic_small,artist_id);
		}
	}
	
	/**
	 * ���ظ����͸��
	 * @param strDownload_show_link 
	 * @param strSong_id 
	 * @param strDownload_file_link 
	 * @param strTitle 
	 * @param strAuthor 
	 * @param strFile_extension 
	 * @param strFile_size 
	 * @param lrclink 
	 * @param artist_id 
	 * @param pic_small 
	 */
	private void downloadSongs(String strTitle, String strSong_id, String strDownload_file_link, 
			String strDownload_show_link, String strAuthor, String strFile_extension, String strFile_size, 
			String lrclink, String pic_small, String artist_id) {
		boolean isNofi = false;

		HttpDownloader httpDownloader = new HttpDownloader();
		
		int resultSong = httpDownloader.downFile(strDownload_file_link, "mp3/", strTitle + "." +strFile_extension,strFile_size);
		//�����һ����ַ���س����ڶ�����ַ����
		int resultLrc = -1;
		if(resultSong == -1){
			resultLrc = httpDownloader.downFile(strDownload_show_link, "mp3/", strTitle + "." +strFile_extension,strFile_size);
		}

		String resultMessage = null;
		String messageTitle = null;
		if(resultSong == 0 || resultLrc == 0){
			resultMessage = "���سɹ�";
			messageTitle = "���سɹ�";
			
			//�������سɹ������ظ��,д��sd����MP3�ļ���
			//�����������һ������Ϊlrc����ִ�и���֪ͨ
			httpDownloader.downFile(lrclink, "mp3/", strTitle + ".lrc","lrc");
			
			String uri = PlayerService.getMp3Path(strTitle + "." +strFile_extension);
			//����������д�����ݿ�
			PublicVariable.downloadSongListInsertDatabase(strTitle,pic_small,strSong_id,artist_id,strAuthor,uri,true);	
		}
		else if(resultSong == -1 && resultLrc == -1){
			resultMessage = "����ʧ��";
			messageTitle = "����ʧ��";
			isNofi = true;
		}
		else{
			resultMessage = "�Ѿ����ڣ�����Ҫ�ظ�����";
			messageTitle = "������ʾ";
			isNofi = true;
		}

		if(isNofi){
			//ʹ��Notification��ʾ�ͻ����ؽ��
			NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);               
			Builder n = new Notification.Builder(MainActivity.instance);             

			 Notification notification = new NotificationCompat.Builder(MainActivity.instance)
	         .setSmallIcon(R.drawable.download1)
	         .setTicker(messageTitle).setContentInfo("")
	         .setContentTitle("����ʧ��").setContentText(strTitle + resultMessage)
	         .setNumber(++PublicVariable.messageNum)
	         .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
	         .build();
			 
			 notification.flags = Notification.FLAG_AUTO_CANCEL; 
			 nm.notify(++PublicVariable.messageNum, notification);
		}
	}


	
}
