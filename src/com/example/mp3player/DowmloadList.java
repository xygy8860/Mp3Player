
package com.example.mp3player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.example.mp3player.db.DatabaseHelper;
import com.example.mp3player.service.PlayerService;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DowmloadList extends ListFragment implements IXListViewListener{
	
	public static boolean isFromDownloadList = false;
	public static DowmloadList instance =null;
	public static ArrayList<Mp3> downloadListMp3 = null;
	public static int playingPosition = 0;
	public static boolean isSongListClick = false;
	public static DowmloadList inDowmloadList = null;
	public static ListView myListView = null;
	public static ArrayList<HashMap<String, String>> arrayList = null;
	public static ArrayList<HashMap<String, String>> list = null;
//	public static 
	ImageButton btn;
	TextView textView ;
	XListView xListView;
	PublicVariable publicVariable;
	ImageView imageView;
	ImageButton updateButton;
	RelativeLayout downloadListMainLayout;
	private int n = 0;//����ƫ����
	
	OverrideListAdapter overrideListAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		publicVariable = new PublicVariable();
		arrayList = new ArrayList<HashMap<String, String>>();
		

		
		publicVariable.dowanloadNetworkSongList(20, n,MainActivity.instance.handler);//�����б�
		n += 10;//ƫ����ÿ������10
		super.onCreate(savedInstanceState);		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		inDowmloadList = this;
		
		
		//�����������Ϊ�գ������
		if(downloadListMp3 != null){
			downloadListMp3.removeAll(downloadListMp3);
		}
		Cursor cursor = null;
		try{
			DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this.getActivity(), "test_db");
			SQLiteDatabase  db = databaseHelper.getReadableDatabase();

			//�α��ѯ����
			cursor = db.query("downloadSonglist", 
					new String[]{"id","songInfo","size","lrcName","lrcSize","songer","uri"}, null, null, null, null, null);
			readDataFromDatabase(cursor);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(cursor != null){
				cursor.close();
			}
			
		}
		
		
		mHandler = new Handler();
		
		myListView = getListView();
		btn = (ImageButton)getView().findViewById(R.id.listButton);
		updateButton = (ImageButton)getView().findViewById(R.id.updateButton);
		textView = (TextView)getView().findViewById(R.id.mainTextView);
		xListView = (XListView)getView().findViewById(R.id.xlistMain);
		imageView = (ImageView)getView().findViewById(R.id.image3);
		
		xListView.setPullLoadEnable(true);//����������Ϊtrue
		xListView.setXListViewListener(this);//���ü���
		
		downloadListMainLayout = (RelativeLayout) getView().findViewById(R.id.downloadListMainlayout);

		updateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//֪ͨ����ҳ��
				Message message = new Message();
				message.what = PublicVariable.WHAT_UPDATE_VIEW;
				MainActivity.instance.handler.sendMessage(message);
			}
		});
		
		
		btn.setOnClickListener(new OnClickListener() {       
			
			@Override
			public void onClick(View v) {
				if(PublicVariable.isListGone){
					if(list == null){
						Toast.makeText(getActivity(), "��û�и���Ŷ�������ظ�����~~", Toast.LENGTH_SHORT).show();
					}
					else{
						myListView.setVisibility(View.VISIBLE);
						Resources res = getResources();
						btn.setImageDrawable(res.getDrawable(R.drawable.button_delete));
						PublicVariable.isListGone = false;
//						textView.setText("��������");
//						xListView.setVisibility(View.GONE);
//						imageView.setVisibility(View.GONE);
					}
				}
				else{
//					xListView.setVisibility(View.VISIBLE);
					myListView.setVisibility(View.GONE);
					Resources res = getResources();
					btn.setImageDrawable(res.getDrawable(R.drawable.button_add));
					PublicVariable.isListGone = true;
//					textView.setText("չ���б���������֮��");
				}
			}
		});
		
		if(arrayList.size() > 0){
			xListView.setAdapter(overrideListAdapter);
		}
		
		//�����б����¼�
		myListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//���ú���
				dialog(position);
				return true;
			}
		});

		super.onResume();
	}
	
	
	/**
	 * ɾ��ѡ��
	 * listView�����༭�Ի���
	 */
	protected void dialog(final int position) {
		  AlertDialog.Builder builder = new Builder(getActivity());
		  
		  builder.setTitle("ɾ��");
		  builder.setMessage("��ʾ����ɾ�����������ļ�!");
		  builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(position < list.size()){
					HashMap<String, String> map = list.get(position);
					String songInfo = map.get("songInfo");//��ȡ����songid
					String songer = map.get("songer");
					
					//ɾ������������
					list.remove(position);
					songListUpdate(list);
					
					String where = MediaStore.Audio.Media.TITLE + "=?";
					//ɾ��getContentResolver�е�����
					int row = getActivity().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
							where, new String[]{map.get("songInfo")});
					Log.v("123", "getContentResolver --> delete-->" + row);
					
					
					//��ȡ���ݿ����
					DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getActivity(), "test_db");
					SQLiteDatabase  db = databaseHelper.getWritableDatabase();
					//ɾ�����ݿ��е��б�����
					//���ݸ������͸�����ɾ��
					int i = db.delete("downloadSonglist", "songInfo=? and  songer=?", new String[]{songInfo,songer});
					Log.v("128", "delete-->i-->" + i);
					
					//ɾ��sd������
					String pathUri = map.get("uri");
					File file = new File(pathUri);
					//����ļ�������ɾ��
					if(file.exists()){
						if(file.delete()){//ɾ���ļ�
							Toast.makeText(getActivity(), "ɾ���ɹ���", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(getActivity(), "ɾ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
						}
					}	
					else{
						Toast.makeText(getActivity(), "ɾ��ʧ�ܣ��ļ������ڣ�", Toast.LENGTH_SHORT).show();
					}
					
					//ɾ������ļ�
					String lrcPath = pathUri.replace(".mp3", ".lrc");
					File lrcFile = new File(pathUri);
					//�������ļ����ڣ���ɾ��
					if(lrcFile.exists()){
						lrcFile.delete();
					}
				}
			}
		});
		  
		  builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		  builder.create().show();
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/**
	 * listview�����������
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		
		//����ѡ������ɫ����
//		l.setSelection(position++);
		playingPosition = position;
		isSongListClick = true;
		
		MainActivity.instance.play(position);
		
		PlayerService.isPlayOnLine = false;//�����߲���
	}

	/**
	 * ��������䵽listView
	 */
	public void songListUpdate(ArrayList<HashMap<String, String>> list) {
		new DowmloadList();
		
		
		Activity activity = this.getActivity();
		SimpleAdapter adapter = new SimpleAdapter(activity, list,R.layout.song_list,  
				new String[]{"songInfo","songer","size","lrcName","lrcSize"                 }, 
				new int[]{R.id.songInfoList,R.id.songer,R.id.sizeList,R.id.LrcName,R.id.Lrcsize});
		
		
		//��listView��ʾ
		setListAdapter(adapter);
		
		setListViewHeightBasedOnChildren(getListView());
		
		//���û�б������֣��򵯳�ɨ�����
		if(list.size() == 0 && PublicVariable.isScanMusicInt < 2){
			startScanMusicActivity();  
		}
	}

	/**
	 * ����ɨ�����ֽ���
	 */
	public void startScanMusicActivity() {
		Intent i = new Intent(this.getActivity(),ScanMusicActivity.class);  
		i.putExtra("num", 0);  
		startActivity (i);
	}

	/**
	 * �����ݿ��ж�ȡ���ݣ����listview
	 * @param cursor ��ѯ���α�
	 */
	public void readDataFromDatabase(Cursor cursor) {
		downloadListMp3 = new ArrayList<Mp3>();//ʵ�����������
		//i��ʾ��ѯ����������Ŀ
		int i = cursor.getCount();
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
			
			Mp3 mp3 = new Mp3(id,songInfo,size,lrcName,lrcSize,songer,uri);
			downloadListMp3.add(mp3);//��������
		}
		songListUpdate(praseToListHashmap(downloadListMp3));
	}
	/**
	 * ��List<Mp3>��������ȡ���������� ArrayList<HashMap<String, String>>
	 * @param listMp3
	 * @return
	 */
	public ArrayList<HashMap<String, String>> praseToListHashmap(List<Mp3> listMp3) {
		// TODO Auto-generated method stub
		list = new ArrayList<HashMap<String,String>>();
		
		if(listMp3.isEmpty()){
			return list;
		}
		Iterator<Mp3> iterator = listMp3.iterator();
		
		//test_dbΪ���ݿ������
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(this.getActivity(),"test_db");
		dbHelper.getWritableDatabase();//��д�����ݿ�
		
		while(iterator.hasNext()){
			HashMap<String, String> map = new HashMap<String, String>();
			Mp3 mp3 = (Mp3)iterator.next();
			map.put("songInfo", mp3.getMp3Name());
			map.put("size", mp3.getMp3Size());
			map.put("songer", mp3.getSonger());
			map.put("lrcName", mp3.getLrcNmae());
			map.put("lrcSize", mp3.getLrcSize());
			map.put("uri", mp3.getUri());
			list.add(map);
		}
		return list;
		
	}
	
	
	//���ScrollView��listView��ͻ������
    public void setListViewHeightBasedOnChildren(ListView listView) {  
    	SimpleAdapter  listAdapter = (SimpleAdapter) listView.getAdapter();   
        if (listAdapter == null) {  
            return;  
        }  
     
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
        PublicVariable.isOnresumeFromSelectSongs = false;
     
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount())) + 20;  
        listView.setLayoutParams(params);  
    }

    //����ˢ��
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}
	//�������غͲ鿴����ѡ��
	private Handler mHandler;
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Log.v("124","����");
				if(n < 100){
					publicVariable.dowanloadNetworkSongList(20, n,MainActivity.instance.handler);//�����б�
					n += 10;
				}
				else{
					
				}
				//֪ͨ���ݼ������仯
				overrideListAdapter.notifyDataSetChanged();
			}
		}, 2000);
	}
	
	public Handler handler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg){
			
			if(msg.what == PublicVariable.WHAT_UPDATE_VIEW){
				ArrayList<HashMap<String, String>> listItemTemp = new ArrayList<HashMap<String, String>>();
				listItemTemp = (ArrayList<HashMap<String,String>>) msg.obj;
				
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
					 arrayList.add(listItemTemp.get(i));
				}
				
				overrideListAdapter = new OverrideListAdapter(getActivity(), arrayList,R.layout.play,  
						new String[]{"title","author","album_title","song_id","lrclink"                 }, 
						new int[]{R.id.title,R.id.author,R.id.album_title,R.id.song_id,R.id.lrclink});
				
				
				//��listView��ʾ
				xListView.setAdapter(overrideListAdapter);
				Log.v("124", "overrideListAdapter");
				xListView.setPullLoadEnable(true);
				Log.v("124",arrayList.size() +"");
				if(arrayList.size() > 0){
					//imageView.setVisibility(View.VISIBLE);
				}
				else{
					Toast.makeText(getActivity(), "���������Ƿ����ӣ�", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	
}
