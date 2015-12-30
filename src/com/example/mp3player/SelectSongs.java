package com.example.mp3player;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SelectSongs extends ListFragment{
	
	EditText songEdit ;
	ImageButton searchButton;
	ListView selectView;
	PublicVariable publicVariable;
	public static SelectSongs inSelectSongs = null;
	ImageView songImage1;
	ImageView songImage2;
	public static ArrayList<HashMap<String, String>> selectSongsListItemTemp;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.select_song, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//使onCreateOptionsMenu生效
//		setHasOptionsMenu(true);
		selectSongsListItemTemp = new ArrayList<HashMap<String, String>>();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		inSelectSongs = this;
		publicVariable = new PublicVariable();
		songImage1 = (ImageView)getView().findViewById(R.id.songImage1);
		songImage2 = (ImageView)getView().findViewById(R.id.songImage2);
		
		songEdit = (EditText)getView().findViewById(R.id.songEdit);
		selectView = getListView();
		searchButton = (ImageButton)getView().findViewById(R.id.selectButton);
		songImage2.setVisibility(View.GONE); 
		
//		if(selectSongsListItemTemp.size() > 0){
//			songImage2.setVisibility(View.GONE); 
//		}
		
		songEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() == 0) {  
                    //songImage2.setVisibility(View.VISIBLE); 
                    selectView.setVisibility(View.GONE);
                    setListViewHeightBasedOnChildren(selectView);
                   
                } else {  
                	songImage2.setVisibility(View.GONE); 
                	selectView.setVisibility(View.VISIBLE);
                }  
			}
		});
		
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchStr = songEdit.getText().toString();
				if(searchStr == "" || searchStr.equals("") || searchStr == null){
					Toast.makeText(getActivity(), "请输入搜索信息", Toast.LENGTH_SHORT).show();
					return;
				}
				else{
					songImage2.setVisibility(View.GONE);
					String url = null;
					try {
						searchStr = PublicVariable.UrlEncodeToUTF8(searchStr);
						url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.search.catalogSug&format=json&callback=&query=" +
						searchStr + "&_=1413017198449";
						
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.v("124", url);
					publicVariable.newTheard(url,PublicVariable.DOWNLOAD_NETWORK_SELECT_LIST,
							PublicVariable.DOWNLOAD_NETWORK_SONGS,MainActivity.instance.handler,null);
				}
			}
		});
		setListViewHeightBasedOnChildren(selectView);
	}
	
	//解决ScrollView与listView冲突的问题
    public void setListViewHeightBasedOnChildren(ListView listView) {  
    	SimpleAdapter  listAdapter = (SimpleAdapter) listView.getAdapter();   
        if (listAdapter == null) {  
            return;  
        }  
     
        int totalHeight = 56*selectSongsListItemTemp.size();  
        int i = listAdapter.getCount();
//        for (int i = 0; i < listAdapter.getCount(); i++) {  
//            View listItem = listAdapter.getView(i, null, listView);  
//            listItem.measure(0, 0);  
//            totalHeight += listItem.getMeasuredHeight();  
//        }  
        
        PublicVariable.isOnresumeFromSelectSongs = true;
     
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (selectSongsListItemTemp.size()*2)) + 20;  
        listView.setLayoutParams(params);  
    }
	
	/**
	 * 接收handler传过来的数据，填充listview
	 */
	public Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			
			if(msg.what == PublicVariable.WHAT_UPDATE_VIEW){
				
				selectSongsListItemTemp = (ArrayList<HashMap<String,String>>) msg.obj;
				
//				for(int i = 0; i < selectSongsListItemTemp.size(); i ++ ){
//					String pic_small = selectSongsListItemTemp.get(i).get("pic_small");
//					String song_id = selectSongsListItemTemp.get(i).get("song_id");
//					
//					File file = new File(PublicVariable.SDCardRoot + "cache" + File.separator + song_id + ".jpg");
//		        	 if (file.exists()){
//		        		 pic_small = Uri.fromFile(file) + "";
//		        	 }
//		        	 else if(pic_small != null){
//		        		 if(pic_small.indexOf("http://") < 0){
//		        			 pic_small = "http://" + pic_small;
//		 				}
//		        	 }
//		        	 //更新数据中的图片下载链接
//		        	 selectSongsListItemTemp.get(i).put("pic_small", pic_small);	
//				}
				selectSongsUpdateListView();
			}
		}
	};
	
	private void selectSongsUpdateListView() {
		if(selectSongsListItemTemp.size() > 0){
			Toast.makeText(getActivity(), "搜索成功", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(getActivity(), "未搜索到数据", Toast.LENGTH_SHORT).show();
			return;
		}
		OverrideListAdapter overrideListAdapter = new OverrideListAdapter(getActivity(), selectSongsListItemTemp,R.layout.play,  
				new String[]{"title","author","album_title","song_id","lrclink"                 }, 
				new int[]{R.id.title,R.id.author,R.id.album_title,R.id.song_id,R.id.lrclink});

		//将listView显示
		setListAdapter(overrideListAdapter);
		Log.v("124", "overrideListAdapter");
		setListViewHeightBasedOnChildren(selectView);
		
	}
}
