package com.example.mp3player;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import mars.download.HttpDownloader;

import com.example.mp3player.PublicVariable;
import com.example.mp3player.db.DatabaseHelper;
import com.example.mp3player.service.DownloadService;
import com.example.mp3player.xml.Mp3XmlHandler;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SongList extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.main, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		//清空listmp3
		Mp3XmlHandler.listMp3.removeAll(Mp3XmlHandler.listMp3);
		
		//从数据库读取服务器的歌曲列表
		queryData("songInfoList");
//		getListView().setOnCreateContextMenuListener(this);//设置view为可长按
		
//		newTheard();
		//使onCreateOptionsMenu生效
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		getListView().setOnCreateContextMenuListener(this);//设置view为可长按
		super.onResume();
	}

	/**
	 * 下载歌曲
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
	
		int itemID = item.getItemId();
		//删除本行数据
		if(itemID == PublicVariable.DOWNLOAD){
			
			AdapterView.AdapterContextMenuInfo info;
			String strSongInfo = null;
			String strSonger = null;
			String strSize = null;
			String strLrcName = null;
			String strLrcSize = null;
			try{
				info = (AdapterContextMenuInfo)item.getMenuInfo();
				TextView textView =(TextView)info.targetView.findViewById(R.id.songInfoList);
				TextView songerTextView = (TextView)info.targetView.findViewById(R.id.songer);
				TextView sizeTextView = (TextView)info.targetView.findViewById(R.id.sizeList);
				TextView lrcTextView = (TextView)info.targetView.findViewById(R.id.LrcName);
				TextView lrcSizeTextView = (TextView)info.targetView.findViewById(R.id.Lrcsize);
				
				strSongInfo = textView.getText().toString();//获取当前歌曲的名称
				strSonger = songerTextView.getText().toString();
				strSize = sizeTextView.getText().toString();
				strLrcName = lrcTextView.getText().toString();
				strLrcSize = lrcSizeTextView.getText().toString();
				
				Intent intent = new Intent();
				// 将Mp3Info对象存入到Intent对象当中
				intent.putExtra("strSongInfo", strSongInfo);
				intent.putExtra("strSonger", strSonger);
				intent.putExtra("strSize", strSize);
				intent.putExtra("strLrcName", strLrcName);
				intent.putExtra("strLrcSize", strLrcSize);
				
				intent.setClass(this.getActivity(),DownloadService.class);
				// 启动Service
				this.getActivity().startService(intent);
			}
			catch(Exception e){
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		//添加下载按钮
		menu.add(0, PublicVariable.DOWNLOAD, 1, "下载");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/**
	 * 查询数据库中本地歌曲列表
	 */
	public void queryData(String tableName) {
		
		Cursor cursor = null ;
		SQLiteDatabase db = null;
		try{	
				DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.instance,"test_db");
				db = dbHelper.getReadableDatabase();//返回可读的数据库对象
				//查询数据库所有数据
				//数据库查询语句，返回游标
				cursor = db.query(tableName, 
						new String[]{"id","songInfo","size","lrcName","lrcSize","songer"}, null, null, null, null, "id desc");
				
				readDataFromDatabase(cursor);
			}	
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally{
			if(cursor != null)
			{
				cursor.close();//关闭游标
			}
		}
	}

	/**
	 * 根据游标读取数据库数据
	 * @param cursor
	 */
	public void readDataFromDatabase(Cursor cursor) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();//实例化链表对象
		//i表示查询到的数据条目
		int i = cursor.getCount();
		HashMap[] map = new HashMap[i];//生成hashmap的数组，容量为i
		
		//游标在数据库中循环取数据
		while(cursor.moveToNext()){
			i -= 1;//数组从大往小，直到i=1
			//取出各个字段的值
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String songInfo = cursor.getString(cursor.getColumnIndex("songInfo"));
			String size = cursor.getString(cursor.getColumnIndex("size"));
			String lrcName = cursor.getString(cursor.getColumnIndex("lrcName"));
			String lrcSize = cursor.getString(cursor.getColumnIndex("lrcSize"));
			String songer = cursor.getString(cursor.getColumnIndex("songer"));
			String uri = cursor.getString(cursor.getColumnIndex("uri"));
			//装入map数组
			map[i] = new HashMap<String,String>();
			map[i].put("lrcName", lrcName);
			map[i].put("songInfo", songInfo);
			map[i].put("size", size);
			map[i].put("lrcSize", lrcSize);
			map[i].put("songer", songer);
//			map[i].put("uri", uri);
			list.add(map[i]);//载入链表
		}
			songListUpdate(list);
	}

	/**
	 * 将数据填充到listView
	 */
	private void songListUpdate(ArrayList<HashMap<String, String>> list) {
		//SimpleAdapter构造函数初始化adapter
		
		if(!list.isEmpty()){
			SimpleAdapter adapter = new SimpleAdapter(MainActivity.instance, list,R.layout.song_list,  
					new String[]{"songInfo","songer","size","lrcName","lrcSize"}, 
					new int[]{R.id.songInfoList,R.id.songer,R.id.sizeList,R.id.LrcName,R.id.Lrcsize});
			//将listView显示
			setListAdapter(adapter);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		menu.add(0, PublicVariable.UODATE_SONG_LIST, 1, "更新");
//		menu.add(0, 2, 2, "设置");
		
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		if (id == PublicVariable.UODATE_SONG_LIST) {
			
			//只有主线程才能调用setListAdapter修改视图
			//线程同步代码块，保证代码顺利执行，不然praseToListHashmap中Iterator有可能出错
			synchronized(this) {
				PublicVariable publicVariable = new PublicVariable();
				songListUpdate(publicVariable.praseToListHashmap(Mp3XmlHandler.listMp3));
				//将listMp3清空，释放内存
				Mp3XmlHandler.listMp3.removeAll(Mp3XmlHandler.listMp3);
			}
			//新线程下载最新歌单，用户更新歌单时可即时完成
//			newTheard();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}







}
