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
	
	//通知栏按钮广播常数量
    public final static String ACTION_BTN = "com.example.mp3player.notification";
    public final static String INTENT_NAME = "btnId";
    public final static int INTENT_BTN_PLAY = 1;//播放暂停
    public final static int INTENT_BTN_NEXT = 2;//下一首
    public final static int INTENT_BTN_CLEAR = 3;//清除通知
    public static NotificationBroadcastReceiver mReceiver;
    
	
	
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static  boolean isNUll(String str){
		if(str == "" || str == null || str.equals("") || str.equals(null)){
			return true;
		}
		return false;
	}
	
	//控制播放下一首歌曲时，是否加载对应歌手和歌曲信息
	public static boolean isStartSongInfoActivityFromNetworklist = false;
	public static String songerName = null;
	public static String songName = null;
	public static int playPosition = 0;
	
	public static List<String> lrcStrList = new ArrayList<String>();//显示歌词文件
	public static List<String> lrcStrListTemp = new ArrayList<String>();//显示歌词文件
	public static List<String> lrcStrTime = new ArrayList<String>();//显示歌词时间
	
	public static boolean isSongInfoActivityStart = false;
	
	public static boolean isOnresumeFromSelectSongs = false;
	
	public static int isScanMusicInt = 0;
	
	public static boolean isFromNetToDown = false;
	
	public static  String SDCardRoot = Environment.getExternalStorageDirectory()
			.getAbsolutePath()
			+ File.separator;
	
	
	
	
	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
//					.showImageOnLoading(R.drawable.liudehua)
//					// 正在加载
					.showImageForEmptyUri(R.drawable.liudehua)
					// 空图片
					.showImageOnFail(R.drawable.liudehua)
					// 错误图片
					.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
	
	
			public static DisplayImageOptions optionsHaveNoPic = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.song_image)
		//	// 正在加载
			.showImageForEmptyUri(R.drawable.song_image)
			// 空图片
			.showImageOnFail(R.drawable.song_image)
			// 错误图片
			.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();
	
	/**
	 * imageloader 圆形设置
	 */
	public static DisplayImageOptions optionsRound = new DisplayImageOptions.Builder()  
   // .showImageOnLoading(R.drawable.author)          // 设置图片下载期间显示的图片  
    //.showImageForEmptyUri(R.drawable.author)  // 设置图片Uri为空或是错误的时候显示的图片  
    //.showImageOnFail(R.drawable.author)       // 设置图片加载或解码过程中发生错误显示的图片      
    .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
    .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中  
    .displayer(new RoundedBitmapDisplayer(200))  // 设置成圆角图片  
    .build();                                   // 创建配置过得DisplayImageOption对象 
	
	
	public static ArrayList<DownloadMp3Model> networkMp3ModelList = new ArrayList<>();
	
	public static boolean isListGone = false;
	
	//判断当前活动的activity
	public static boolean isMainActivity = true;
	public static boolean isMainActivityId1 = true;
	
	//下载通知id
	public static int notiId = 0;
	public static int nowNotiId = 0;
	
	public static int messageNum = 0;

	//menu按钮键
	public static final int UODATE_SONG_LIST = 1;
	public static final int UPDATE_DOWNLOAD_LIST = 2;
	public static final int ABOUT = 3;
	public static final int EXIT = 4;
	public static final int DOWNLOAD = 5;
	
	//关于DOWNLOAD_NETWORK_SONGS中switch下载的定义
	public static final int DOWNLOAD_NETWORK_SONGS_LIST_XML = 1;
	public static final int DOWNLOAD_NETWORK_SONG_DOWNLOAD_XML_BY_SONGID = 2;
	public static final int DOWNLOAD_NETWORK_SONGS_PLAY_XML_BY_SONGID = 3;
	public static final int DOWNLOAD_NETWORK_SELECT_LIST = 4;
	
	//关于handler消息机制what值定义
	public static final int DOWNLOAD_NETWORK_SONGS = 0;
	public static final int WHAT_UPDATE_VIEW = 1;
	public static final int WHAT_START_NOTIFICATION_PROGRESSBAR = 2;
	public static final int WHAT_UPDATE_NOTIFICATION_PROGRESSBAR = 3;
	public static final int WHAT_TOASTS = 4;
	
	
	public static boolean isSongFileExist = true;//播放的歌曲文件默认存在
	
	//sqlite创建表的语句
	public static final String CREAT_TABLE_SONG_INFO_LIST =  "create table songInfoList" +
			"(id INTEGER PRIMARY KEY AUTOINCREMENT,songInfo text,size text,lrcName text,lrcSize text,songer text,uri text)";
	public static final String CREAT_TABLE_DOWNLOAD_SONG_LIST =  "create table downloadSonglist" +
			"(id INTEGER PRIMARY KEY AUTOINCREMENT,songInfo text,size text,lrcName text,lrcSize text,songer text,uri text)";
	public static final String CREAT_TABLE_NETWORK_SONGS_LIST =  "create table networkSongslist" +
			"(id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"type text," + //榜单类型
			"title text," + //歌曲名称
			"artist_id text," + //艺术家ID
			"pic_small text," + //小图像
			"lrclink text," + //歌词连接
			"song_id text," + //歌曲ID，根据此ID查询播放和下载的地址			
			"author text," + //作者，即演唱者
			"album_title text," + //专辑名称
			"download_show_link text," + //下载地址二			
			"download_file_link text," + //下载地址一
			"file_extension text," + //文件扩展名，如.mp3
			"paly_show_link text," + //播放地址二
			"play_file_link text" + //播放地址一
			")";
	
	/*
	 * baidu.ting.billboard.billList  {type:1,size:10, offset:0}

	type: //1、新歌榜，2、热歌榜，

	11、摇滚榜，12、爵士，16、流行

	21、欧美金曲榜，22、经典老歌榜，23、情歌对唱榜，24、影视金曲榜，25、网络歌曲榜

	size: 10 //返回条目数量

	offset: 0 //获取偏移
	 * 
	 */
	HttpDownloader httpDownloader = new HttpDownloader();
	
	
	
	public static String encode = null ;
	/**
	 * 将字符串转码为utf-8编码
	 * @param s 转码前的字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String UrlEncodeToUTF8(String s ) throws UnsupportedEncodingException {
		encode = URLEncoder.encode(s, "UTF-8");
		return encode;
	}
	
	public static String DOWNLOAD_BASE = "http://192.168.1.115:8081/mp3/"; 

	/**
	 * 将下载的歌曲信息写入到本地已下载歌曲数据库中
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
		
		contentValues.put("songInfo", strTitle);//歌曲信息
		contentValues.put("size", strDownload_file_link);//存在pic_big
		contentValues.put("lrcName", strSong_id);//song_id
		contentValues.put("lrcSize", strDownload_show_link);//存放歌手id  ting_uid
		contentValues.put("songer", strAuthor);//歌手信息
		contentValues.put("uri", uri);//uri播放信息
		
		Log.v("124", uri);
		
		db.insert("downloadSonglist", null, contentValues);
		
		if(flag){
			ArrayList<String> list = new ArrayList<>();
			list.add(strTitle);
			list.add(strAuthor);
			list.add(uri);
			
			Message message = new Message();
			message.obj = list;
			//发送handler消息，接收后更新Ui
			MainActivity.instance.handler2.sendMessage(message);
		}
	}

	/**
	 * 开启下载歌词的新线程，得到listMp3链表数据
	 */
	public void newTheard(final String downloadUrl,final int arg1,final int what,final Handler handler,final String songName) {
		//开启多线程下载xml文件
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//从服务器下载歌曲列表xml文件
				String xmlStr = download(downloadUrl);
				//将xml字符串和歌曲名称作为参数传递
				ArrayList<String> arrayList = new ArrayList<String>();
				arrayList.add(xmlStr);
				arrayList.add(songName);
				
				if(xmlStr.isEmpty()){
					Log.v("123", "xml--->null" + xmlStr);
					//Toast.makeText(MainActivity.instance, "网络异常，请检查网络设置！", Toast.LENGTH_SHORT).show();
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
		}).start();//启动新的线程
	}
	
	/**
	 * 下载文档类文件专用下载函数
	 * @param urlStr 下载链接
	 * @return 下载文件的字符串
	 */
	public String download(String urlStr) {
		
		String str = httpDownloader.download(urlStr);
		return str;
	}
	/**
	 * 将List<Mp3>中数据提取出，保存在 ArrayList<HashMap<String, String>>
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
		
		//test_db为数据库的名字
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.instance,"test_db");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		db.delete("songInfoList", null, null);//清空表中数据
		
		while(iterator.hasNext()){
			HashMap<String, String> map = new HashMap<String, String>();
			Mp3 mp3 = (Mp3)iterator.next();
			map.put("songInfo", mp3.getMp3Name());
			map.put("size", mp3.getMp3Size());
			map.put("songer", mp3.getSonger());
			map.put("lrcName", mp3.getLrcNmae());
			map.put("lrcSize", mp3.getLrcSize());
			list.add(map);
			
			//向数据库中插入最新更新的歌曲列表信息
			ContentValues values = new ContentValues();
			
			//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
			//values.put("id", 1);//id自增长，不需要插入
			values.put("songInfo",mp3.getMp3Name());
			values.put("size", mp3.getMp3Size());
			values.put("lrcName", mp3.getLrcNmae());
			values.put("lrcSize", mp3.getLrcSize());
			values.put("songer", mp3.getSonger());
			//调用insert方法，就可以将数据插入到数据库当中
			db.insert("songInfoList", null, values);
			
			
		}
		
		return list;
	}
	
	
	/**此为重载方法，将网络下载的歌曲读出保存在本地数据库
	 * 将List<Mp3>中数据提取出，保存在 ArrayList<HashMap<String, String>>
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
		
		//test_db为数据库的名字
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.instance,"test_db");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		//判断是否需要清除数据
		if(isInsert){
			db.delete("networkSongslist", null, null);//清空表中数据
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
				//向数据库中插入最新更新的歌曲列表信息
				ContentValues values = new ContentValues();
				
				//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
				//values.put("id", 1);//id自增长，不需要插入
				values.put("type", mp3.getType());
				values.put("title", mp3.getTitle());
				values.put("artist_id", mp3.getArtist_id());
				values.put("pic_small", mp3.getPic_small());
				values.put("lrclink", mp3.getLrclink());
				values.put("song_id", mp3.getSong_id());
				values.put("author", mp3.getAuthor());
				values.put("album_title", mp3.getAlbum_title());
				
				//调用insert方法，就可以将数据插入到数据库当中
				db.insert("networkSongslist", null, values);	
			}
		}
		
		return list;
	}
	
	/**
	 * 根据不同规则解析下载的XML文件
	 * @param xmlStr xml的内容
	 * @param handler 适用于解析XML的规则
	 */
	public  void saxXmlReader(String xmlStr,ContentHandler handler){
		synchronized(this){
			try{
				//sax解析xml文件
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
     *  网络歌单页面下载不同类型的歌单
     * @param type 歌单类型
     * @param offset 获取偏移 0，10,20，...，90
     * 
	 * baidu.ting.billboard.billList  {type:1,size:10, offset:0}
		type: //1、新歌榜，2、热歌榜，22、经典老歌榜，
		11、摇滚榜，16、流行 20,话语金曲榜
		21、欧美金曲榜，23、情歌对唱榜，24，14、影视金曲榜，25、网络歌曲榜 12、爵士，
		6,ktv热歌榜，8美国Billboard单曲榜
		
		size: 10 //返回条目数量
		offset: 0 //获取偏移
     */
    public void dowanloadNetworkSongList(int type, int offset, Handler handler){
    	String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?" +
				"from=webapp_music&method=baidu.ting.billboard.billList&format=xml&callback=&" +
				"type=" + type +
				"&size=10&offset=" + offset + "&_=1413017198449";
		newTheard(url,DOWNLOAD_NETWORK_SONGS_LIST_XML,PublicVariable.DOWNLOAD_NETWORK_SONGS,handler,null);
    }
	
}
