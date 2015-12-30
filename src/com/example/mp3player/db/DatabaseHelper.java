package com.example.mp3player.db;

import com.example.mp3player.PublicVariable;
import com.example.mp3player.SongList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

//DatabaseHelper��Ϊһ������SQLite�������࣬�ṩ��������Ĺ��ܣ�
//��һ��getReadableDatabase(),getWritableDatabase()���Ի��SQLiteDatabse����ͨ���ö�����Զ����ݿ���в���
//�ڶ����ṩ��onCreate()��onUpgrade()�����ص����������������ڴ������������ݿ�ʱ�������Լ��Ĳ���

public class DatabaseHelper extends SQLiteOpenHelper {

	//���ݿ�İ汾
	private static final int VERSION = 1;
	
	//volatile��֤ÿ��instance�������ڴ��ȡ
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

	
	//��SQLiteOepnHelper�����൱�У������иù��캯��
	//nameΪ�������
	private DatabaseHelper(Context context, String name, CursorFactory factory,int version) {
		//����ͨ��super���ø��൱�еĹ��캯��
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
//		* ��������ʵ��  
//		 */    
//		static final SingletonHolder INSTANCE = new DatabaseHelper(songList,name,VERSION);    
//		}  
//	
	
	

	//�ú������ڵ�һ�δ������ݿ��ʱ��ִ��,ʵ�������ڵ�һ�εõ�SQLiteDatabse�����ʱ�򣬲Ż�����������
	@Override
	public void onCreate(SQLiteDatabase db) {

		//execSQL��������ִ��SQL���
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
