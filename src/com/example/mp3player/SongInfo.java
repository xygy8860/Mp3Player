package com.example.mp3player;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mp3player.service.DownloadService;
import com.example.mp3player.service.PlayerService;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 歌曲信息
 * @author xygy
 *
 */
public class SongInfo extends Fragment{
	 RelativeLayout layout;
	 ImageView imageView;
	 public TextView songName;
	 public TextView songerName;
	 
	 ImageButton downloadSong;
	 ImageButton playSong;
	 
	 public static SongInfo info;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.song_info, container, false);
		
		layout = (RelativeLayout)view.findViewById(R.id.sencondView);
		imageView = (ImageView)view.findViewById(R.id.scan_textView_Image_1);
		songerName = (TextView) view.findViewById(R.id.songer_name);
		songName = (TextView) view.findViewById(R.id.song_name);
		playSong = (ImageButton) view.findViewById(R.id.song_download);
		downloadSong = (ImageButton) view.findViewById(R.id.song_hear);
		
		return view;
	}

	@Override
	public void onResume() {
		//如果用户点击的网络列表图像进入的界面
		if(PublicVariable.isStartSongInfoActivityFromNetworklist){
			songName.setText(PublicVariable.songName);
			songerName.setText(PublicVariable.songerName);
			
			final int position = PublicVariable.playPosition;
			//下载按钮
			downloadSong.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					OverrideListAdapter.inOverrideListAdapter.click(position,PublicVariable.DOWNLOAD_NETWORK_SONG_DOWNLOAD_XML_BY_SONGID, 0);
				}
			});
			
			//试听播放按钮
			playSong.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					OverrideListAdapter.inOverrideListAdapter.click(position,PublicVariable.DOWNLOAD_NETWORK_SONGS_PLAY_XML_BY_SONGID,1);
					PublicVariable.isStartSongInfoActivityFromNetworklist = false;
					Log.v("126", "isStartSongInfoActivityFromNetworklist-->" + false);
				}
			});
			
		}
		//如果用户点击左下角圆角头像进入的界面
		else{
			songName.setText(PlayerService.strTitle);
			songerName.setText(PlayerService.strAuthor);
			
			//下载按钮
			downloadSong.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(PlayerService.isPlayOnLine){
//						MainActivity.instance.startServiceBySongid(DownloadService.class, 
//								PlayerService.strTitle, PlayerService.strSong_id);
						
						String downloadUrl = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.song.downWeb&format=xml&callback=&songid=" +
								PlayerService.strSong_id + "&_=1413017198449";
						PublicVariable publicVariable = new PublicVariable();
						//进入下载xml文件线程
						publicVariable.newTheard(downloadUrl,PublicVariable.DOWNLOAD_NETWORK_SONG_DOWNLOAD_XML_BY_SONGID,
								PublicVariable.DOWNLOAD_NETWORK_SONGS,MainActivity.instance.handler,PlayerService.strTitle);
						
					}
					else{
						Toast.makeText(getActivity(), "本地音乐，不需要下载", Toast.LENGTH_SHORT).show();
					}
					
					// TODO Auto-generated method stub
					
				}
			});
			
			//试听播放按钮
			playSong.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), "歌曲正在播放\n无需添加到列表", Toast.LENGTH_SHORT).show();
				}
			});
			
		}

		info = this;

		

		//动态设置imageView的宽高，手机兼容性
		LayoutParams para;
        para = imageView.getLayoutParams();        

        
        para.width = para.height = MainActivity.instance.ywidth;
        imageView.setLayoutParams(para);
		

        handler2.postDelayed(updatePicCallback, 15);

		super.onResume();
	}
	
	public	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			//更新背景图片
			if(msg.what == 0){
				Log.v("126", "更新背景图片");
				handler2.postDelayed(updatePicCallback, 5);
			}
			else if(msg.what == 1){
				ArrayList<String> list = new ArrayList<>();
				list = (ArrayList<String>)msg.obj;
				Log.v("128", "list.size() -->" + list.size());
				
				if(list.size() > 4){
					final String name = list.get(1);
					final String title = list.get(4);
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							songerName.setText(name);//歌手名称
							songName.setText(title);//歌曲名称
						}
					},500);
				}

				
				
			}
		}
	};
	Handler handler2 = new Handler();
	
	UpdatePicCallback updatePicCallback = new UpdatePicCallback();
	
	class UpdatePicCallback implements Runnable{
		@Override
		public void run() {
			
			//加载初始图片
			ImageLoader.getInstance().displayImage(SongInfoActivity.infoActivity.avatar_s500, 
					imageView, PublicVariable.optionsHaveNoPic);
		}
	}

	@Override
	public void onDestroyOptionsMenu() {
		// TODO Auto-generated method stub
		handler2.removeCallbacks(updatePicCallback);
		super.onDestroyOptionsMenu();
	}
	
	
}

