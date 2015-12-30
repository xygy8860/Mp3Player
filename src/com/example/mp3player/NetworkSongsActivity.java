package com.example.mp3player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import com.example.mp3player.xml.NetworkSongsListXmlHandler;


import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class NetworkSongsActivity extends Activity implements IXListViewListener{
	
	public static NetworkSongsActivity insActivity = null;
	
	PublicVariable publicVariable = new PublicVariable();
	OverrideListAdapter adapter;
	
	private ArrayList<HashMap<String, String>> listItemTemp = null;
	public static ArrayList<HashMap<String, String>> listItem = null;
	
	private ProgressBar bar;
	private ImageView imageView;
	
	private XListView mListView;
	private Handler mHandler;
	private int start = 0;
	private static int refreshCnt = 0;
	private int n = 0;
	int type = -1;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.x_network_songs);
		
		PublicVariable.isMainActivity = false;
		PublicVariable.isMainActivityId1 = false;
		
		Intent intent = getIntent();
		type = intent.getIntExtra("type", -1);
		
		listItem = new ArrayList<HashMap<String,String>>();
		listItemTemp = new ArrayList<HashMap<String,String>>();
		
		insActivity = this;
		
		publicVariable.dowanloadNetworkSongList(type, n,NetworkSongsActivity.insActivity.handlerNetworkSongs);//�����б�
		n += 10;
		
		imageView = (ImageView)findViewById(R.id.imageXNetwork);
		bar = (ProgressBar)findViewById(R.id.bar);
		mListView = (XListView) findViewById(R.id.xlist);
		mListView.setPullLoadEnable(true);
		//�������أ�����ˢ����Ϊfalse
//		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		
		mListView.setXListViewListener(this);
		mHandler = new Handler();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		PublicVariable.isMainActivity = true;
		PublicVariable.isFromNetToDown = true;
		Log.v("128", "onBackPressed");
		
		super.onBackPressed();
	}
	
	/**
	 * ���ñ��浱ǰactivity
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		LrcActivity.inLrcActivity.setCurrentActivity(2);
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		PublicVariable.isMainActivityId1 = true;
		
		Log.v("128", "onDestroy");
		
		super.onDestroy();
	}

	//ʧȥ����ͷ���
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}






	boolean isFirst = true;
	public Handler handlerNetworkSongs = new Handler(){
		@Override
		public void handleMessage(Message msg){
			
			if(msg.what == PublicVariable.DOWNLOAD_NETWORK_SONGS){
				ArrayList<String> arrayList = (ArrayList<String>) msg.obj;
				
				String xmlStr = arrayList.get(0);
				
				publicVariable.saxXmlReader(xmlStr,new NetworkSongsListXmlHandler());
				//��listMp3ת��Ϊlist<hashmap<String,String>>��Ȼ�������listView��
				listItemTemp = publicVariable.praseToListHashmap(PublicVariable.networkMp3ModelList,false);
			
				for(int i = 0; i < listItemTemp.size(); i ++){
					
					String pic_small = listItemTemp.get(i).get("pic_small");
					String song_id = listItemTemp.get(i).get("song_id");
					
					File file = new File(PublicVariable.SDCardRoot + "cache" + File.separator + song_id + ".jpg");
		        	 if (file.exists()){
		        		 pic_small = Uri.fromFile(file) + "";
		        	 }
		        	 else if(pic_small != null){
		        		 if(pic_small.indexOf("http://") < 0){
		        			 pic_small = "http://" + pic_small;
		 				}
		        	 }
		        	 //���������е�ͼƬ��������
		        	 listItemTemp.get(i).put("pic_small", pic_small);
					
					 listItem.add(listItemTemp.get(i));
				}
				songListUpdate(listItem);
				if(isFirst){
					bar.setVisibility(View.GONE);
					mListView.setVisibility(View.VISIBLE);
					imageView.setVisibility(View.VISIBLE);
					isFirst = false;
				}
				mListView.setPullLoadEnable(true);
//				Message m = new Message();
//				m.what = 100;
//				handlerNetworkSongs.sendMessage(m);
			}
			else if(msg.what == PublicVariable.WHAT_TOASTS){
				String songName = (String) msg.obj;
				switch (msg.arg1) {
				case 0:
					Toast.makeText(insActivity, songName + "�Ѽ��������б�", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(insActivity, songName + "��������", Toast.LENGTH_SHORT).show();
					break;

				default:
					break;
				}
			}
		}
	};

	private void geneItems() {
		if(n < 100){
			publicVariable.dowanloadNetworkSongList(type, n, NetworkSongsActivity.insActivity.handlerNetworkSongs);//�����б�
			n += 10;
		}
		else{
			Toast.makeText(this, "�赥�Ѽ������", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("�ո�");
	}
	
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Log.v("124","����");
				listItem.remove(listItem);
				geneItems();
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				geneItems();
				Log.v("124","����");
				adapter.notifyDataSetChanged();
//				onLoad();
			}
		}, 2000);
	}

	/**
	 * ��������䵽listView
	 */
	public void songListUpdate(ArrayList<HashMap<String, String>> list) {
		if(list.size() == 0){
			return;
		}
		Log.v("124","1111111111111");
		adapter = new OverrideListAdapter(this, list,R.layout.play,  
				new String[]{"title","author","album_title","song_id","lrclink"                 }, 
				new int[]{R.id.title,R.id.author,R.id.album_title,R.id.song_id,R.id.lrclink});
		//��listView��ʾ
		mListView.setAdapter(adapter);
	}
	
	

	
}
