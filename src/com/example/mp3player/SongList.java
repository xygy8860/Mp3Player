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

		//���listmp3
		Mp3XmlHandler.listMp3.removeAll(Mp3XmlHandler.listMp3);
		
		//�����ݿ��ȡ�������ĸ����б�
		queryData("songInfoList");
//		getListView().setOnCreateContextMenuListener(this);//����viewΪ�ɳ���
		
//		newTheard();
		//ʹonCreateOptionsMenu��Ч
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		getListView().setOnCreateContextMenuListener(this);//����viewΪ�ɳ���
		super.onResume();
	}

	/**
	 * ���ظ���
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
	
		int itemID = item.getItemId();
		//ɾ����������
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
				
				strSongInfo = textView.getText().toString();//��ȡ��ǰ����������
				strSonger = songerTextView.getText().toString();
				strSize = sizeTextView.getText().toString();
				strLrcName = lrcTextView.getText().toString();
				strLrcSize = lrcSizeTextView.getText().toString();
				
				Intent intent = new Intent();
				// ��Mp3Info������뵽Intent������
				intent.putExtra("strSongInfo", strSongInfo);
				intent.putExtra("strSonger", strSonger);
				intent.putExtra("strSize", strSize);
				intent.putExtra("strLrcName", strLrcName);
				intent.putExtra("strLrcSize", strLrcSize);
				
				intent.setClass(this.getActivity(),DownloadService.class);
				// ����Service
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
		//������ذ�ť
		menu.add(0, PublicVariable.DOWNLOAD, 1, "����");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/**
	 * ��ѯ���ݿ��б��ظ����б�
	 */
	public void queryData(String tableName) {
		
		Cursor cursor = null ;
		SQLiteDatabase db = null;
		try{	
				DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.instance,"test_db");
				db = dbHelper.getReadableDatabase();//���ؿɶ������ݿ����
				//��ѯ���ݿ���������
				//���ݿ��ѯ��䣬�����α�
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
				cursor.close();//�ر��α�
			}
		}
	}

	/**
	 * �����α��ȡ���ݿ�����
	 * @param cursor
	 */
	public void readDataFromDatabase(Cursor cursor) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();//ʵ�����������
		//i��ʾ��ѯ����������Ŀ
		int i = cursor.getCount();
		HashMap[] map = new HashMap[i];//����hashmap�����飬����Ϊi
		
		//�α������ݿ���ѭ��ȡ����
		while(cursor.moveToNext()){
			i -= 1;//����Ӵ���С��ֱ��i=1
			//ȡ�������ֶε�ֵ
			String id = cursor.getString(cursor.getColumnIndex("id"));
			String songInfo = cursor.getString(cursor.getColumnIndex("songInfo"));
			String size = cursor.getString(cursor.getColumnIndex("size"));
			String lrcName = cursor.getString(cursor.getColumnIndex("lrcName"));
			String lrcSize = cursor.getString(cursor.getColumnIndex("lrcSize"));
			String songer = cursor.getString(cursor.getColumnIndex("songer"));
			String uri = cursor.getString(cursor.getColumnIndex("uri"));
			//װ��map����
			map[i] = new HashMap<String,String>();
			map[i].put("lrcName", lrcName);
			map[i].put("songInfo", songInfo);
			map[i].put("size", size);
			map[i].put("lrcSize", lrcSize);
			map[i].put("songer", songer);
//			map[i].put("uri", uri);
			list.add(map[i]);//��������
		}
			songListUpdate(list);
	}

	/**
	 * ��������䵽listView
	 */
	private void songListUpdate(ArrayList<HashMap<String, String>> list) {
		//SimpleAdapter���캯����ʼ��adapter
		
		if(!list.isEmpty()){
			SimpleAdapter adapter = new SimpleAdapter(MainActivity.instance, list,R.layout.song_list,  
					new String[]{"songInfo","songer","size","lrcName","lrcSize"}, 
					new int[]{R.id.songInfoList,R.id.songer,R.id.sizeList,R.id.LrcName,R.id.Lrcsize});
			//��listView��ʾ
			setListAdapter(adapter);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		menu.add(0, PublicVariable.UODATE_SONG_LIST, 1, "����");
//		menu.add(0, 2, 2, "����");
		
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		if (id == PublicVariable.UODATE_SONG_LIST) {
			
			//ֻ�����̲߳��ܵ���setListAdapter�޸���ͼ
			//�߳�ͬ������飬��֤����˳��ִ�У���ȻpraseToListHashmap��Iterator�п��ܳ���
			synchronized(this) {
				PublicVariable publicVariable = new PublicVariable();
				songListUpdate(publicVariable.praseToListHashmap(Mp3XmlHandler.listMp3));
				//��listMp3��գ��ͷ��ڴ�
				Mp3XmlHandler.listMp3.removeAll(Mp3XmlHandler.listMp3);
			}
			//���߳��������¸赥���û����¸赥ʱ�ɼ�ʱ���
//			newTheard();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}







}
