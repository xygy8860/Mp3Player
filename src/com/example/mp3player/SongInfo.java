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
 * ������Ϣ
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
		//����û�����������б�ͼ�����Ľ���
		if(PublicVariable.isStartSongInfoActivityFromNetworklist){
			songName.setText(PublicVariable.songName);
			songerName.setText(PublicVariable.songerName);
			
			final int position = PublicVariable.playPosition;
			//���ذ�ť
			downloadSong.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					OverrideListAdapter.inOverrideListAdapter.click(position,PublicVariable.DOWNLOAD_NETWORK_SONG_DOWNLOAD_XML_BY_SONGID, 0);
				}
			});
			
			//�������Ű�ť
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
		//����û�������½�Բ��ͷ�����Ľ���
		else{
			songName.setText(PlayerService.strTitle);
			songerName.setText(PlayerService.strAuthor);
			
			//���ذ�ť
			downloadSong.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(PlayerService.isPlayOnLine){
//						MainActivity.instance.startServiceBySongid(DownloadService.class, 
//								PlayerService.strTitle, PlayerService.strSong_id);
						
						String downloadUrl = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.song.downWeb&format=xml&callback=&songid=" +
								PlayerService.strSong_id + "&_=1413017198449";
						PublicVariable publicVariable = new PublicVariable();
						//��������xml�ļ��߳�
						publicVariable.newTheard(downloadUrl,PublicVariable.DOWNLOAD_NETWORK_SONG_DOWNLOAD_XML_BY_SONGID,
								PublicVariable.DOWNLOAD_NETWORK_SONGS,MainActivity.instance.handler,PlayerService.strTitle);
						
					}
					else{
						Toast.makeText(getActivity(), "�������֣�����Ҫ����", Toast.LENGTH_SHORT).show();
					}
					
					// TODO Auto-generated method stub
					
				}
			});
			
			//�������Ű�ť
			playSong.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), "�������ڲ���\n������ӵ��б�", Toast.LENGTH_SHORT).show();
				}
			});
			
		}

		info = this;

		

		//��̬����imageView�Ŀ�ߣ��ֻ�������
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
			//���±���ͼƬ
			if(msg.what == 0){
				Log.v("126", "���±���ͼƬ");
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
							songerName.setText(name);//��������
							songName.setText(title);//��������
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
			
			//���س�ʼͼƬ
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

