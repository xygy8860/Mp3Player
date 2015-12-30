package com.example.mp3player.db;

import com.example.mp3player.PublicVariable;
import com.example.mp3player.SongList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

//DatabaseHelper作为一个访问SQLite的助手类，提供两个方面的功能，
//第一，getReadableDatabase(),getWritableDatabase()可以获得SQLiteDatabse对象，通过该对象可以对数据库进行操作
//第二，提供了onCreate()和onUpgrade()两个回调函数，允许我们在创建和升级数据库时，进行自己的操作

public class DatabaseHelper extends SQLiteOpenHelper {

	//数据库的版本
	private static final int VERSION = 1;
	
	//volatile保证每次instance都从主内存读取
	private volatile static DatabaseHelper instance = null;
	
	public static DatabaseHelper getInstance(Context context, String name){
		if(instance == null){
			synchronized (DatabaseHelper.class) {
				if(instance == null){
					instance  = new DatabaseHelper(context, name);
				}
			}
			
		}
		return instance;
	}

	
	//在SQLiteOepnHelper的子类当中，必须有该构造函数
	//name为表的名字
	private DatabaseHelper(Context context, String name, CursorFactory factory,int version) {
		//必须通过super调用父类当中的构造函数
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	private DatabaseHelper(Context songList,String name){
		this(songList,name,VERSION);
	}
	private DatabaseHelper(Context context,String name,int version){
		this(context, name,null,version);
	}
	
//	public static DatabaseHelper getgetInstance(Context songList,String name){
//		 return SingletonHolder.INSTANCE;   
//	}
//	
//	private static DatabaseHelper SingletonHolder(Context songList,String name) {    
//		/**  
//		* 单例对象实例  
//		 */    
//		static final SingletonHolder INSTANCE = new DatabaseHelper(songList,name,VERSION);    
//		}  
//	
	
	

	//该函数是在第一次创建数据库的时候执行,实际上是在第一次得到SQLiteDatabse对象的时候，才会调用这个方法
	@Override
	public void onCreate(SQLiteDatabase db) {

		//execSQL函数用于执行SQL语句
		db.execSQL(PublicVariable.CREAT_TABLE_DOWNLOAD_SONG_LIST);
		db.execSQL(PublicVariable.CREAT_TABLE_SONG_INFO_LIST);
		db.execSQL(PublicVariable.CREAT_TABLE_NETWORK_SONGS_LIST);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//System.out.println("update a Database");
	}

}
