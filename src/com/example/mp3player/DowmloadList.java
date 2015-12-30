
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
	private int n = 0;//歌曲偏移量
	
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
		

		
		publicVariable.dowanloadNetworkSongList(20, n,MainActivity.instance.handler);//下载列表
		n += 10;//偏移量每次增加10
		super.onCreate(savedInstanceState);		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		inDowmloadList = this;
		
		
		//如果数据链表不为空，则清空
		if(downloadListMp3 != null){
			downloadListMp3.removeAll(downloadListMp3);
		}
		Cursor cursor = null;
		try{
			DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this.getActivity(), "test_db");
			SQLiteDatabase  db = databaseHelper.getReadableDatabase();

			//游标查询数据
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
		
		xListView.setPullLoadEnable(true);//下拉加载设为true
		xListView.setXListViewListener(this);//设置监听
		
		downloadListMainLayout = (RelativeLayout) getView().findViewById(R.id.downloadListMainlayout);

		updateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//通知更新页面
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
						Toast.makeText(getActivity(), "还没有歌曲哦，先下载歌曲吧~~", Toast.LENGTH_SHORT).show();
					}
					else{
						myListView.setVisibility(View.VISIBLE);
						Resources res = getResources();
						btn.setImageDrawable(res.getDrawable(R.drawable.button_delete));
						PublicVariable.isListGone = false;
//						textView.setText("本地音乐");
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
//					textView.setText("展开列表—开启音乐之旅");
				}
			}
		});
		
		if(arrayList.size() > 0){
			xListView.setAdapter(overrideListAdapter);
		}
		
		//播放列表长按事件
		myListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//调用函数
				dialog(position);
				return true;
			}
		});

		super.onResume();
	}
	
	
	/**
	 * 删除选项
	 * listView长按编辑对话框
	 */
	protected void dialog(final int position) {
		  AlertDialog.Builder builder = new Builder(getActivity());
		  
		  builder.setTitle("删除");
		  builder.setMessage("提示：将删除本地音乐文件!");
		  builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(position < list.size()){
					HashMap<String, String> map = list.get(position);
					String songInfo = map.get("songInfo");//获取歌曲songid
					String songer = map.get("songer");
					
					//删除链表中数据
					list.remove(position);
					songListUpdate(list);
					
					String where = MediaStore.Audio.Media.TITLE + "=?";
					//删除getContentResolver中的数据
					int row = getActivity().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
							where, new String[]{map.get("songInfo")});
					Log.v("123", "getContentResolver --> delete-->" + row);
					
					
					//获取数据库对象
					DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getActivity(), "test_db");
					SQLiteDatabase  db = databaseHelper.getWritableDatabase();
					//删除数据库中的列表数据
					//根据歌曲名和歌手名删除
					int i = db.delete("downloadSonglist", "songInfo=? and  songer=?", new String[]{songInfo,songer});
					Log.v("128", "delete-->i-->" + i);
					
					//删除sd卡数据
					String pathUri = map.get("uri");
					File file = new File(pathUri);
					//如果文件存在则删除
					if(file.exists()){
						if(file.delete()){//删除文件
							Toast.makeText(getActivity(), "删除成功！", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(getActivity(), "删除失败！", Toast.LENGTH_SHORT).show();
						}
					}	
					else{
						Toast.makeText(getActivity(), "删除失败，文件不存在！", Toast.LENGTH_SHORT).show();
					}
					
					//删除歌词文件
					String lrcPath = pathUri.replace(".mp3", ".lrc");
					File lrcFile = new File(pathUri);
					//如果歌词文件存在，则删除
					if(lrcFile.exists()){
						lrcFile.delete();
					}
				}
			}
		});
		  
		  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
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
	 * listview点击播放音乐
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		
		//设置选中行颜色高亮
//		l.setSelection(position++);
		playingPosition = position;
		isSongListClick = true;
		
		MainActivity.instance.play(position);
		
		PlayerService.isPlayOnLine = false;//非在线播放
	}

	/**
	 * 将数据填充到listView
	 */
	public void songListUpdate(ArrayList<HashMap<String, String>> list) {
		new DowmloadList();
		
		
		Activity activity = this.getActivity();
		SimpleAdapter adapter = new SimpleAdapter(activity, list,R.layout.song_list,  
				new String[]{"songInfo","songer","size","lrcName","lrcSize"                 }, 
				new int[]{R.id.songInfoList,R.id.songer,R.id.sizeList,R.id.LrcName,R.id.Lrcsize});
		
		
		//将listView显示
		setListAdapter(adapter);
		
		setListViewHeightBasedOnChildren(getListView());
		
		//如果没有本地音乐，则弹出扫描界面
		if(list.size() == 0 && PublicVariable.isScanMusicInt < 2){
			startScanMusicActivity();  
		}
	}

	/**
	 * 启动扫描音乐界面
	 */
	public void startScanMusicActivity() {
		Intent i = new Intent(this.getActivity(),ScanMusicActivity.class);  
		i.putExtra("num", 0);  
		startActivity (i);
	}

	/**
	 * 从数据库中读取数据，填充listview
	 * @param cursor 查询的游标
	 */
	public void readDataFromDatabase(Cursor cursor) {
		downloadListMp3 = new ArrayList<Mp3>();//实例化链表对象
		//i表示查询到的数据条目
		int i = cursor.getCount();
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
			
			Mp3 mp3 = new Mp3(id,songInfo,size,lrcName,lrcSize,songer,uri);
			downloadListMp3.add(mp3);//载入链表
		}
		songListUpdate(praseToListHashmap(downloadListMp3));
	}
	/**
	 * 将List<Mp3>中数据提取出，保存在 ArrayList<HashMap<String, String>>
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
		
		//test_db为数据库的名字
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(this.getActivity(),"test_db");
		dbHelper.getWritableDatabase();//打开写的数据库
		
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
	
	
	//解决ScrollView与listView冲突的问题
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

    //下拉刷新
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}
	//上拉加载和查看更多选项
	private Handler mHandler;
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Log.v("124","上拉");
				if(n < 100){
					publicVariable.dowanloadNetworkSongList(20, n,MainActivity.instance.handler);//下载列表
					n += 10;
				}
				else{
					
				}
				//通知数据集发生变化
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
		        	 //更新数据中的图片下载链接
		        	 listItemTemp.get(i).put("pic_small", pic_small);					
					 arrayList.add(listItemTemp.get(i));
				}
				
				overrideListAdapter = new OverrideListAdapter(getActivity(), arrayList,R.layout.play,  
						new String[]{"title","author","album_title","song_id","lrclink"                 }, 
						new int[]{R.id.title,R.id.author,R.id.album_title,R.id.song_id,R.id.lrclink});
				
				
				//将listView显示
				xListView.setAdapter(overrideListAdapter);
				Log.v("124", "overrideListAdapter");
				xListView.setPullLoadEnable(true);
				Log.v("124",arrayList.size() +"");
				if(arrayList.size() > 0){
					//imageView.setVisibility(View.VISIBLE);
				}
				else{
					Toast.makeText(getActivity(), "请检查网络是否连接？", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	
}
