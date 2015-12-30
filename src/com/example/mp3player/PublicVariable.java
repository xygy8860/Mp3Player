package com.example.mp3player;

import java.io.File;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import mars.download.HttpDownloader;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.example.mp3player.broadcast.NotificationBroadcastReceiver;
import com.example.mp3player.db.DatabaseHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class PublicVariable {
	
	//֪ͨ����ť�㲥������
    public final static String ACTION_BTN = "com.example.mp3player.notification";
    public final static String INTENT_NAME = "btnId";
    public final static int INTENT_BTN_PLAY = 1;//������ͣ
    public final static int INTENT_BTN_NEXT = 2;//��һ��
    public final static int INTENT_BTN_CLEAR = 3;//���֪ͨ
    public static NotificationBroadcastReceiver mReceiver;
    
	
	
	/**
	 * �ж��ַ����Ƿ�Ϊ��
	 * @param str
	 * @return
	 */
	public static  boolean isNUll(String str){
		if(str == "" || str == null || str.equals("") || str.equals(null)){
			return true;
		}
		return false;
	}
	
	//���Ʋ�����һ�׸���ʱ���Ƿ���ض�Ӧ���ֺ͸�����Ϣ
	public static boolean isStartSongInfoActivityFromNetworklist = false;
	public static String songerName = null;
	public static String songName = null;
	public static int playPosition = 0;
	
	public static List<String> lrcStrList = new ArrayList<String>();//��ʾ����ļ�
	public static List<String> lrcStrListTemp = new ArrayList<String>();//��ʾ����ļ�
	public static List<String> lrcStrTime = new ArrayList<String>();//��ʾ���ʱ��
	
	public static boolean isSongInfoActivityStart = false;
	
	public static boolean isOnresumeFromSelectSongs = false;
	
	public static int isScanMusicInt = 0;
	
	public static boolean isFromNetToDown = false;
	
	public static  String SDCardRoot = Environment.getExternalStorageDirectory()
			.getAbsolutePath()
			+ File.separator;
	
	
	
	
	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
//					.showImageOnLoading(R.drawable.liudehua)
//					// ���ڼ���
					.showImageForEmptyUri(R.drawable.liudehua)
					// ��ͼƬ
					.showImageOnFail(R.drawable.liudehua)
					// ����ͼƬ
					.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
	
	
			public static DisplayImageOptions optionsHaveNoPic = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.song_image)
		//	// ���ڼ���
			.showImageForEmptyUri(R.drawable.song_image)
			// ��ͼƬ
			.showImageOnFail(R.drawable.song_image)
			// ����ͼƬ
			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();
	
	/**
	 * imageloader Բ������
	 */
	public static DisplayImageOptions optionsRound = new DisplayImageOptions.Builder()  
   // .showImageOnLoading(R.drawable.author)          // ����ͼƬ�����ڼ���ʾ��ͼƬ  
    //.showImageForEmptyUri(R.drawable.author)  // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ  
    //.showImageOnFail(R.drawable.author)       // ����ͼƬ���ػ��������з���������ʾ��ͼƬ      
    .cacheInMemory(true)                        // �������ص�ͼƬ�Ƿ񻺴����ڴ���  
    .cacheOnDisk(true)                          // �������ص�ͼƬ�Ƿ񻺴���SD����  
    .displayer(new RoundedBitmapDisplayer(200))  // ���ó�Բ��ͼƬ  
    .build();                                   // �������ù���DisplayImageOption���� 
	
	
	public static ArrayList<DownloadMp3Model> networkMp3ModelList = new ArrayList<>();
	
	public static boolean isListGone = false;
	
	//�жϵ�ǰ���activity
	public static boolean isMainActivity = true;
	public static boolean isMainActivityId1 = true;
	
	//����֪ͨid
	public static int notiId = 0;
	public static int nowNotiId = 0;
	
	public static int messageNum = 0;

	//menu��ť��
	public static final int UODATE_SONG_LIST = 1;
	public static final int UPDATE_DOWNLOAD_LIST = 2;
	public static final int ABOUT = 3;
	public static final int EXIT = 4;
	public static final int DOWNLOAD = 5;
	
	//����DOWNLOAD_NETWORK_SONGS��switch���صĶ���
	public static final int DOWNLOAD_NETWORK_SONGS_LIST_XML = 1;
	public static final int DOWNLOAD_NETWORK_SONG_DOWNLOAD_XML_BY_SONGID = 2;
	public static final int DOWNLOAD_NETWORK_SONGS_PLAY_XML_BY_SONGID = 3;
	public static final int DOWNLOAD_NETWORK_SELECT_LIST = 4;
	
	//����handler��Ϣ����whatֵ����
	public static final int DOWNLOAD_NETWORK_SONGS = 0;
	public static final int WHAT_UPDATE_VIEW = 1;
	public static final int WHAT_START_NOTIFICATION_PROGRESSBAR = 2;
	public static final int WHAT_UPDATE_NOTIFICATION_PROGRESSBAR = 3;
	public static final int WHAT_TOASTS = 4;
	
	
	public static boolean isSongFileExist = true;//���ŵĸ����ļ�Ĭ�ϴ���
	
	//sqlite����������
	public static final String CREAT_TABLE_SONG_INFO_LIST =  "create table songInfoList" +
			"(id INTEGER PRIMARY KEY AUTOINCREMENT,songInfo text,size text,lrcName text,lrcSize text,songer text,uri text)";
	public static final String CREAT_TABLE_DOWNLOAD_SONG_LIST =  "create table downloadSonglist" +
			"(id INTEGER PRIMARY KEY AUTOINCREMENT,songInfo text,size text,lrcName text,lrcSize text,songer text,uri text)";
	public static final String CREAT_TABLE_NETWORK_SONGS_LIST =  "create table networkSongslist" +
			"(id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"type text," + //������
			"title text," + //��������
			"artist_id text," + //������ID
			"pic_small text," + //Сͼ��
			"lrclink text," + //�������
			"song_id text," + //����ID�����ݴ�ID��ѯ���ź����صĵ�ַ			
			"author text," + //���ߣ����ݳ���
			"album_title text," + //ר������
			"download_show_link text," + //���ص�ַ��			
			"download_file_link text," + //���ص�ַһ
			"file_extension text," + //�ļ���չ������.mp3
			"paly_show_link text," + //���ŵ�ַ��
			"play_file_link text" + //���ŵ�ַһ
			")";
	
	/*
	 * baidu.ting.billboard.billList  {type:1,size:10, offset:0}

	type: //1���¸��2���ȸ��

	11��ҡ����12����ʿ��16������

	21��ŷ��������22�������ϸ��23�����Գ���24��Ӱ�ӽ�����25�����������

	size: 10 //������Ŀ����

	offset: 0 //��ȡƫ��
	 * 
	 */
	HttpDownloader httpDownloader = new HttpDownloader();
	
	
	
	public static String encode = null ;
	/**
	 * ���ַ���ת��Ϊutf-8����
	 * @param s ת��ǰ���ַ���
	 * @throws UnsupportedEncodingException
	 */
	public static String UrlEncodeToUTF8(String s ) throws UnsupportedEncodingException {
		encode = URLEncoder.encode(s, "UTF-8");
		return encode;
	}
	
	public static String DOWNLOAD_BASE = "http://192.168.1.115:8081/mp3/"; 

	/**
	 * �����صĸ�����Ϣд�뵽���������ظ������ݿ���
	 * @param strTitle
	 * @param strDownload_file_link
	 * @param strSong_id
	 * @param strDownload_show_link
	 * @param strAuthor 
	 */
	public static void downloadSongListInsertDatabase(String strTitle,
			String strDownload_file_link, String strSong_id, String strDownload_show_link, 
			String strAuthor,String uri,boolean flag) {
		// TODO Auto-generated method stub
		Activity activity = MainActivity.instance;
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance(MainActivity.instance, "test_db");
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("songInfo", strTitle);//������Ϣ
		contentValues.put("size", strDownload_file_link);//����pic_big
		contentValues.put("lrcName", strSong_id);//song_id
		contentValues.put("lrcSize", strDownload_show_link);//��Ÿ���id  ting_uid
		contentValues.put("songer", strAuthor);//������Ϣ
		contentValues.put("uri", uri);//uri������Ϣ
		
		Log.v("124", uri);
		
		db.insert("downloadSonglist", null, contentValues);
		
		if(flag){
			ArrayList<String> list = new ArrayList<>();
			list.add(strTitle);
			list.add(strAuthor);
			list.add(uri);
			
			Message message = new Message();
			message.obj = list;
			//����handler��Ϣ�����պ����Ui
			MainActivity.instance.handler2.sendMessage(message);
		}
	}

	/**
	 * �������ظ�ʵ����̣߳��õ�listMp3��������
	 */
	public void newTheard(final String downloadUrl,final int arg1,final int what,final Handler handler,final String songName) {
		//�������߳�����xml�ļ�
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//�ӷ��������ظ����б�xml�ļ�
				String xmlStr = download(downloadUrl);
				//��xml�ַ����͸���������Ϊ��������
				ArrayList<String> arrayList = new ArrayList<String>();
				arrayList.add(xmlStr);
				arrayList.add(songName);
				
				if(xmlStr.isEmpty()){
					Log.v("123", "xml--->null" + xmlStr);
					//Toast.makeText(MainActivity.instance, "�����쳣�������������ã�", Toast.LENGTH_SHORT).show();
					return ;
				}
				Log.v("123", "xml--->" + xmlStr);
				
				Message message = new Message();
				message.what = what;
				message.obj = arrayList;
				message.arg1 = arg1;
				xmlStr = null;
				handler.sendMessage(message);
			}
		}).start();//�����µ��߳�
	}
	
	/**
	 * �����ĵ����ļ�ר�����غ���
	 * @param urlStr ��������
	 * @return �����ļ����ַ���
	 */
	public String download(String urlStr) {
		
		String str = httpDownloader.download(urlStr);
		return str;
	}
	/**
	 * ��List<Mp3>��������ȡ���������� ArrayList<HashMap<String, String>>
	 * @param listMp3
	 * @return
	 */
	
	public ArrayList<HashMap<String, String>> praseToListHashmap(List<Mp3> listMp3) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		
		if(listMp3.isEmpty()){
			return list;
		}
		
		Iterator<Mp3> iterator = listMp3.iterator();
		
		//test_dbΪ���ݿ������
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.instance,"test_db");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		db.delete("songInfoList", null, null);//��ձ�������
		
		while(iterator.hasNext()){
			HashMap<String, String> map = new HashMap<String, String>();
			Mp3 mp3 = (Mp3)iterator.next();
			map.put("songInfo", mp3.getMp3Name());
			map.put("size", mp3.getMp3Size());
			map.put("songer", mp3.getSonger());
			map.put("lrcName", mp3.getLrcNmae());
			map.put("lrcSize", mp3.getLrcSize());
			list.add(map);
			
			//�����ݿ��в������¸��µĸ����б���Ϣ
			ContentValues values = new ContentValues();
			
			//��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��
			//values.put("id", 1);//id������������Ҫ����
			values.put("songInfo",mp3.getMp3Name());
			values.put("size", mp3.getMp3Size());
			values.put("lrcName", mp3.getLrcNmae());
			values.put("lrcSize", mp3.getLrcSize());
			values.put("songer", mp3.getSonger());
			//����insert�������Ϳ��Խ����ݲ��뵽���ݿ⵱��
			db.insert("songInfoList", null, values);
			
			
		}
		
		return list;
	}
	
	
	/**��Ϊ���ط��������������صĸ������������ڱ������ݿ�
	 * ��List<Mp3>��������ȡ���������� ArrayList<HashMap<String, String>>
	 * @param listMp3
	 * @return list
	 */
	public ArrayList<HashMap<String, String>> praseToListHashmap(ArrayList<DownloadMp3Model> listMp3,boolean isInsert) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		
		if(listMp3.isEmpty()){
			return list;
		}
		
		Iterator iterator = listMp3.iterator();
		
		//test_dbΪ���ݿ������
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.instance,"test_db");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		//�ж��Ƿ���Ҫ�������
		if(isInsert){
			db.delete("networkSongslist", null, null);//��ձ�������
			Log.v("123","delete");
		}
		
		while(iterator.hasNext()){
			HashMap<String, String> map = new HashMap<String, String>();
			DownloadMp3Model mp3 = (DownloadMp3Model)iterator.next();
			map.put("type", mp3.getType());
			map.put("title", mp3.getTitle());
			map.put("artist_id", mp3.getArtist_id());
			map.put("pic_small", mp3.getPic_small());
			map.put("lrclink", mp3.getLrclink());
			map.put("song_id", mp3.getSong_id());
			map.put("author", mp3.getAuthor());
			map.put("album_title", mp3.getAlbum_title());
			
			map.put("download_show_link", null);
			map.put("download_file_link", null);
			map.put("file_extension", null);
			
			map.put("paly_show_link", null);
			map.put("play_file_link", null);
			list.add(map);
			
			if(isInsert){
				//�����ݿ��в������¸��µĸ����б���Ϣ
				ContentValues values = new ContentValues();
				
				//��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��
				//values.put("id", 1);//id������������Ҫ����
				values.put("type", mp3.getType());
				values.put("title", mp3.getTitle());
				values.put("artist_id", mp3.getArtist_id());
				values.put("pic_small", mp3.getPic_small());
				values.put("lrclink", mp3.getLrclink());
				values.put("song_id", mp3.getSong_id());
				values.put("author", mp3.getAuthor());
				values.put("album_title", mp3.getAlbum_title());
				
				//����insert�������Ϳ��Խ����ݲ��뵽���ݿ⵱��
				db.insert("networkSongslist", null, values);	
			}
		}
		
		return list;
	}
	
	/**
	 * ���ݲ�ͬ����������ص�XML�ļ�
	 * @param xmlStr xml������
	 * @param handler �����ڽ���XML�Ĺ���
	 */
	public  void saxXmlReader(String xmlStr,ContentHandler handler){
		synchronized(this){
			try{
				//sax����xml�ļ�
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
				xmlReader.setContentHandler(handler);
				xmlReader.parse(new InputSource(new StringReader(xmlStr)));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	

	/**
     *  ����赥ҳ�����ز�ͬ���͵ĸ赥
     * @param type �赥����
     * @param offset ��ȡƫ�� 0��10,20��...��90
     * 
	 * baidu.ting.billboard.billList  {type:1,size:10, offset:0}
		type: //1���¸��2���ȸ��22�������ϸ��
		11��ҡ����16������ 20,���������
		21��ŷ��������23�����Գ���24��14��Ӱ�ӽ�����25����������� 12����ʿ��
		6,ktv�ȸ��8����Billboard������
		
		size: 10 //������Ŀ����
		offset: 0 //��ȡƫ��
     */
    public void dowanloadNetworkSongList(int type, int offset, Handler handler){
    	String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?" +
				"from=webapp_music&method=baidu.ting.billboard.billList&format=xml&callback=&" +
				"type=" + type +
				"&size=10&offset=" + offset + "&_=1413017198449";
		newTheard(url,DOWNLOAD_NETWORK_SONGS_LIST_XML,PublicVariable.DOWNLOAD_NETWORK_SONGS,handler,null);
    }
	
}
