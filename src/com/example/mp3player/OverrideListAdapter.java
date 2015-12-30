package com.example.mp3player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.mp3player.service.PlayerService;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

	
public class OverrideListAdapter extends SimpleAdapter {
	
	public static OverrideListAdapter inOverrideListAdapter = null;
	
	public static int hearListPosition = 0;
	private LayoutInflater mInflater = null;
	private ImageButton downloadImage; 
	private ImageButton hearImage; 
	public TextView songid;
	DownloadMp3Model  mp3 = new DownloadMp3Model();
	PublicVariable publicVariable = new PublicVariable();
	
	private ImageLoader loader = ImageLoader.getInstance();
	
	public OverrideListAdapter(Context context){
		super(context, null, 0, null, null);
		mInflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public OverrideListAdapter(Activity activity,
			ArrayList<HashMap<String, String>> list, int play,
			String[] strings, int[] is) {
		// TODO Auto-generated constructor stub
		super(activity, list, play, strings, is);  
	}
	
	public String songId = null;
	String songName = null;
	String pic_small = null;
	//歌手点击用字段
	
			
	boolean flag = false;

	private static  class ViewHolder {
		private ImageButton songerImage;		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		ViewHolder holder;

		if(convertView != null && !flag){
			holder = (ViewHolder) convertView.getTag();
			imageLoader(position, holder);
			return convertView;
		}

		flag = false;
		holder = new ViewHolder();
		inOverrideListAdapter = this;
//		synchronized (this) {
		
		//搜索页避免超出索引
		if(PublicVariable.isOnresumeFromSelectSongs){
			if(position >= SelectSongs.inSelectSongs.selectSongsListItemTemp.size()){
				PublicVariable.isOnresumeFromSelectSongs = false;
				position = position % SelectSongs.inSelectSongs.selectSongsListItemTemp.size();
			}
		}
		
			convertView = super.getView(position, convertView, parent); 
			
			holder.songerImage = (ImageButton) convertView.findViewById(R.id.songerImage); 
			
			convertView.setTag(holder);	
			imageLoader(position, holder);
			
			hearImage = (ImageButton) convertView.findViewById(R.id.hearImage);  
			songid = (TextView) convertView.findViewById(R.id.song_id);  
			downloadImage = (ImageButton) convertView.findViewById(R.id.downloadImage);
			//获取主页索引

//		final	String lrclink = lrclink1;
//		final	String author = author1;	
//		final	String artist_id = artist_id1;
//		final	String title = title1;
		
		final int position_temp = position;
		
		//网络播放
		hearImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				click(position_temp,PublicVariable.DOWNLOAD_NETWORK_SONGS_PLAY_XML_BY_SONGID,1);
			}
		});

		//下载按钮
		downloadImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				click(position_temp,PublicVariable.DOWNLOAD_NETWORK_SONG_DOWNLOAD_XML_BY_SONGID, 0);
			}
		});
		
		//歌手图片点击监听事件
		//查看歌曲歌词和歌手信息
		holder.songerImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String lrclink = null;
				String author = null;	
				String artist_id = null;
				String title = null;
				String song_id = null;
				
				int j = MainActivity.instance.mViewPager.getCurrentItem();
				//如果是在主页
				if(PublicVariable.isMainActivity){
					if(j == 0 ){
							song_id = DowmloadList.arrayList.get(position_temp).get("song_id");
							lrclink = DowmloadList.arrayList.get(position_temp).get("lrclink");
							author = DowmloadList.arrayList.get(position_temp).get("author");	
							artist_id = DowmloadList.arrayList.get(position_temp).get("artist_id");
							title = DowmloadList.arrayList.get(position_temp).get("title");	
					}
					//搜索页
					else{

					}
				}
				else{
						 pic_small = NetworkSongsActivity.listItem.get(position_temp).get("pic_small");
						 song_id = NetworkSongsActivity.listItem.get(position_temp).get("song_id");

						 lrclink = NetworkSongsActivity.listItem.get(position_temp).get("lrclink");
						 author = NetworkSongsActivity.listItem.get(position_temp).get("author");	
						 artist_id = NetworkSongsActivity.listItem.get(position_temp).get("artist_id");
						 title = NetworkSongsActivity.listItem.get(position_temp).get("title");	
				}
				
				
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 20;
				
				ArrayList<String> list = new ArrayList<String>();
				list.add(artist_id);//歌手tinguid
				list.add(author);//歌手
				list.add(lrclink);//歌词连接
				list.add(song_id);//歌曲id
				list.add(title);//歌曲名称
				
				PublicVariable.songerName = author;
				PublicVariable.songName = title;
				PublicVariable.playPosition = position_temp;
				PublicVariable.isStartSongInfoActivityFromNetworklist = true;
				
				message.obj = list;
				message.arg1 = position_temp;//播放位置
				LrcActivity.inLrcActivity.handler.sendMessage(message);
				
			}
		});
		
		return convertView;
	}

	/**
	 * imageloader
	 * @param position
	 * @param holder
	 */
	private void imageLoader(final int position, ViewHolder holder) {
//		int j = MainActivity.instance.mViewPager.getCurrentItem();
		if(PublicVariable.isMainActivity){
//			if( j == 0 || j == 1){
				loader.displayImage(DowmloadList.arrayList.get(position).get("pic_small"), 
						holder.songerImage, PublicVariable.options);
//			}
//			else{
////				loader.displayImage(SelectSongs.inSelectSongs.selectSongsListItemTemp.get(position).get("pic_small"), 
////						holder.songerImage, PublicVariable.options);
//			}
		}
		else{
			loader.displayImage(NetworkSongsActivity.listItem.get(position).get("pic_small"), 
					holder.songerImage, PublicVariable.options);
		}
	}  
	
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(PublicVariable.isMainActivity){
			int j = MainActivity.instance.mViewPager.getCurrentItem();
			if(j ==0 || j == 1){
				return DowmloadList.arrayList.size();
			}			
			else{
				return SelectSongs.inSelectSongs.selectSongsListItemTemp.size();
			}
		}else{
			return NetworkSongsActivity.listItem.size();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void click(final int position, final int arg1,final int toastArg1) {
		
		synchronized (this) {
			//试听按钮
			if(arg1 == 3){
				hearListPosition = position;
			}
			
			int i = MainActivity.instance.mViewPager.getCurrentItem();
			Log.v("124","mViewPager" + i);
			
			if(PublicVariable.isMainActivity){
				if(i == 0){
					if(position > DowmloadList.arrayList.size() - 1 ){
						return;
					}
					songId = DowmloadList.arrayList.get(position).get("song_id");
					songName = DowmloadList.arrayList.get(position).get("title");
				}
				else{
					if(position > SelectSongs.selectSongsListItemTemp.size() - 1){
						return;
					}
					songId = SelectSongs.selectSongsListItemTemp.get(position).get("song_id");
					songName = SelectSongs.selectSongsListItemTemp.get(position).get("title");
				}
			}
			else{
				if(position > NetworkSongsActivity.listItem.size() - 1){
					return;
				}
				songId = NetworkSongsActivity.listItem.get(position).get("song_id");
				songName = NetworkSongsActivity.listItem.get(position).get("title");
			}
			
//			if(arg1 == 3){
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						boolean isStartService = MainActivity.instance.startServiceBySongid(PlayerService.class, songName, songId);
//						if(!isStartService){
//							downloadXml(arg1);
//						}
//					}
//				}).start();
//			}else{
//				downloadXml(arg1);
//			}
			
			downloadXml(arg1);
			
			Message message = new Message();
			message.what = PublicVariable.WHAT_TOASTS;
			message.arg1 = toastArg1;
			message.obj = songName;
			if(PublicVariable.isMainActivity){
				MainActivity.instance.handler.sendMessage(message);
			}
			else{
				NetworkSongsActivity.insActivity.handlerNetworkSongs.sendMessage(message);
			}
		}
		
	}

	/**
	 * 根据下载地址下载xml
	 * @param arg1 通知handler对象不同，3为播放，2 为下载
	 */
	private void downloadXml(final int arg1) {
		Log.v("123","songid-->" + songId);
		String downloadUrl = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.song.downWeb&format=xml&callback=&songid=" +
				songId + "&_=1413017198449";
		//String downloadUrl  = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.play&songid="+ songId+"&format=xml";
			
		//进入下载xml文件线程
		publicVariable.newTheard(downloadUrl,arg1,
				PublicVariable.DOWNLOAD_NETWORK_SONGS,MainActivity.instance.handler,songName);
	}

//	/**
//	 * 从网络下载图片，如何缓存中存在则直接本地取
//	 * 不存在则从网上下载存在本地
//	 * @param imageUrl 图片下载链接
//	 * @param songid  歌曲id
//	 * @return
//	 */
//	public Uri getPicSmall(String imageUrl,String songid){ 
//        try {  
//        	File file = new File(PublicVariable.SDCardRoot + "cache" + File.separator + songid + imageUrl.substring(imageUrl.lastIndexOf(".")));
//        	Log.v("124","file-->" + file);
//        	 if (file.exists()) {
//        		 Log.v("124","exists-->true");
//                 return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
//             } else {
//            	 Log.v("124","exists-->false");
//                 // 从网络上获取图片
//                 URL url = new URL(imageUrl);
//                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                 conn.setConnectTimeout(5000);
//                 conn.setRequestMethod("GET");
//                 conn.setDoInput(true);
//                 if (conn.getResponseCode() == 200) {
//      
//                     InputStream is = conn.getInputStream();
//                     FileOutputStream fos = new FileOutputStream(file);
//                     byte[] buffer = new byte[1024];
//                     int len = 0;
//                     while ((len = is.read(buffer)) != -1) {
//                         fos.write(buffer, 0, len);
//                     }
//                     is.close();
//                     fos.close();
//                     // 返回一个URI对象
//                     return Uri.fromFile(file);
//                 }
//             }
//        } catch (ClientProtocolException e) {  
//            // TODO Auto-generated catch block  
//            e.printStackTrace();     
//        } catch (IOException e) {  
//            // TODO Auto-generated catch block  
//            e.printStackTrace();  
//        }
//		return null;  
//	}

//	Handler handler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			Bundle bundle = msg.getData();
//			String uri = bundle.getString("uri");
//			uri = uri.replace("///", "//");
//			Log.v("124","uri-->" + uri);
//			Uri imageUri = Uri.parse(uri);
//			ImageButton button = (ImageButton)msg.obj;
//			button.setImageURI(imageUri);
//		}
//		
//	};
	
}