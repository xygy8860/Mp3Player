package com.example.mp3player;


import com.example.mp3player.service.PlayerService;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SongerInfo extends Fragment{
	
	ImageView imageView;
	TextView songerName;
	TextView songName;
	TextView country;
	TextView songerInfo;
	public static SongerInfo inSongerInfo;
	boolean isAddPic = false;//ˢ�¿��Ʊ�����Ϊ��ʱ����ˢ��

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View  view = inflater.inflate(R.layout.songer_info, container, false);
		
		imageView = (ImageView) view.findViewById(R.id.songer_info_image_songer);
		songerInfo = (TextView) view.findViewById(R.id.songer_info_text);
		songerName = (TextView) view.findViewById(R.id.songer_info_songer_name);
		songName = (TextView) view.findViewById(R.id.songer_info_song_name);
		country = (TextView) view.findViewById(R.id.songer_info_county);
		
		return view;
		
		
	}

	@Override
	public void onResume() {
		inSongerInfo = this;

		//�������ݿ��Զ��й���
		songerInfo.setMovementMethod(new ScrollingMovementMethod());
		
		//�����Ϊ�գ������
		if(isAddPic){
			
			//���ظ���ͼƬ
			ImageLoader.getInstance().displayImage(SongInfoActivity.infoActivity.avatar_middle, 
					imageView, PublicVariable.optionsRound);  
			
			songerName.setText(PlayerService.strAuthor);
			songName.setText(PlayerService.strTitle);
			country.setText(SongInfoActivity.infoActivity.country);
			if(SongInfoActivity.infoActivity.intro == "" || SongInfoActivity.infoActivity.intro.equals(null)){
				songerInfo.setText("���޸�����Ϣ...");
			}else{
				songerInfo.setText(SongInfoActivity.infoActivity.intro);
			}
			
		}
		
		super.onResume();
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0){
				
				Log.v("126", "songerInfo-->������Ϣͼ");
				
				//���ظ���ͼƬ
				ImageLoader.getInstance().displayImage(SongInfoActivity.infoActivity.avatar_middle, 
						imageView, PublicVariable.optionsRound);  
				
				songerName.setText(PlayerService.strAuthor);
				songName.setText(PlayerService.strTitle);
				country.setText(SongInfoActivity.infoActivity.country);
				songerInfo.setText(SongInfoActivity.infoActivity.intro);
				
				isAddPic = true;//����ˢ�¿��Ʊ���Ϊ��
			}
		}
		
	};
	
}
