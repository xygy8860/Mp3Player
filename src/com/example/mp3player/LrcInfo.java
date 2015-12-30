package com.example.mp3player;


import com.example.mp3player.service.PlayerService;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class LrcInfo extends Fragment{
	
	
	TextView lrc_info_1;
	TextView lrc_info_2;
	TextView lrc_info_3;
	
	TextView textViewTime;
	ImageButton timeAdd;
	ImageButton timeJian;
	
	ScrollView lrcScroll;
	LinearLayout lrcLayout;
	
	Handler handler = new Handler();
	DisplayLrcCallBack displayLrcCallBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.lrc_info, container, false);
	}

	@Override
	public void onResume() {
		
		lrcLayout = (LinearLayout) getView().findViewById(R.id.lrc_info_ll);
		lrcScroll = (ScrollView) getView().findViewById(R.id.lrc_infon);
		lrc_info_1 = (TextView) getView().findViewById(R.id.lrc_info_1);
		lrc_info_2 = (TextView) getView().findViewById(R.id.lrc_info_2);
		lrc_info_3 = (TextView) getView().findViewById(R.id.lrc_info_3);
		
		textViewTime = (TextView) getView().findViewById(R.id.lrc_time);
		timeAdd = (ImageButton) getView().findViewById(R.id.lrc_add_time);
		timeJian = (ImageButton) getView().findViewById(R.id.lrc_jian_time);
		
		timeAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//ÿ������0.5�벹��ʱ��
				LrcActivity.inLrcActivity.editor.putInt(PlayerService.strTitle + "lrcOffset", 
						500 + LrcActivity.inLrcActivity.preference.getInt(PlayerService.strTitle + "lrcOffset", 0));
				LrcActivity.inLrcActivity.editor.commit();  
				Toast.makeText(getActivity(), "�������ǰ��0.5��",Toast.LENGTH_SHORT).show();
				displayTimeButton();
			}
		});
		
		
		
		timeJian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LrcActivity.inLrcActivity.editor.putInt(PlayerService.strTitle + "lrcOffset", 
						LrcActivity.inLrcActivity.preference.getInt(PlayerService.strTitle + "lrcOffset", 0) - 500);
				LrcActivity.inLrcActivity.editor.commit();
				
				Toast.makeText(getActivity(), "����������0.5��",Toast.LENGTH_SHORT).show();
				
				displayTimeButton();
			}
		});
		
		//���ø�ʻ����
		lrcLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				final int action = event.getAction();
				//����û�������Ļ�������ò�����ʹ��ʹ���Ч����ʱ��ʧ
				if(action == MotionEvent.ACTION_DOWN){
					//����û���������ͣ3��
					handler.removeCallbacks(displayLrcCallBack);
					handler.postDelayed(displayLrcCallBack, 3000);
				}
				return true;
			}
		});
		
		displayLrcCallBack = new DisplayLrcCallBack();
		
		handler.postDelayed(displayLrcCallBack, 5);

		super.onResume();
	}
	
	/**
	 * �ص�����
	 * ��̬���¸��
	 * @author Administrator
	 *
	 */
	class DisplayLrcCallBack implements Runnable{

		@Override
		public void run() {
			//ȡ���Ѿ����ŵĸ��
			int lrcTempSize = PublicVariable.lrcStrListTemp.size(); 
			String lrcAbove = "";
			
			//Log.v("127", "lrcTempSize-->"+ lrcTempSize);
			if(lrcTempSize > 0){
				for(int i = 0; i < lrcTempSize -1; i++){
					String temp = PublicVariable.lrcStrListTemp.get(i);
					//���û�и�ʣ�����ʾ��ʾ��Ϣ
					if(temp.indexOf("Ŷ��~��û�и��Ŷ~~") > -1){
						//handler.removeCallbacks(displayLrcCallBack);
						lrc_info_1.setText(" ");
						lrc_info_3.setText(" ");
						lrc_info_2.setText("Ŷ��~��û�и��Ŷ~~");
						return;
					}else{
						lrcAbove += temp;
					}
				}
				//���д��textview
				lrc_info_1.setText(lrcAbove);
				
				
				//��ǰ���ŵĸ��
				int lrcSize = PublicVariable.lrcStrList.size(); 
				if(lrcTempSize > 0){
					//�������Ϊ�գ��򲻸���
					String temp = PublicVariable.lrcStrListTemp.get(lrcTempSize - 1).charAt(0) + "";
					if(temp != "\n" && !(temp.equals("\n"))){
						
						lrc_info_2.setText(PublicVariable.lrcStrListTemp.get(lrcTempSize - 1));
						
						//ʵ�ָ���Զ����Ϲ�����Ч��
						//off�������룬ʹ��������Ļ�м�
						int off = lrc_info_1.getHeight() + lrc_info_2.getHeight() - MainActivity.instance.ywidth/2;  
						
						//�������벻������Ļ
						//�û��ִ���ʱҲ�����ع���Ч��
			            if (off > 0) 
			            {   
			            	lrcScroll.scrollTo(0, off);   
			            }              
					}         
				}
				//��δ���ŵĸ��
				String lrcBelow = "";
				if(lrcTempSize  < lrcSize){
					for(int i = lrcTempSize; i < lrcSize; i++){
						lrcBelow += PublicVariable.lrcStrList.get(i);
					}
					//Log.v("128", "lrcBelow-->" + lrcBelow);
					lrc_info_3.setText(lrcBelow);
				}
			}else{
				lrc_info_1.setText(" ");
				lrc_info_3.setText(" ");
				lrc_info_2.setText(" ");
			}
			//�����ص�
			handler.postDelayed(displayLrcCallBack, 10);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(displayLrcCallBack);//�����ص�
		
		LrcActivity.inLrcActivity.timeAdd.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.timeJian.setVisibility(View.GONE);
		
		super.onDestroy();
	}

	/**
	 * ��ʾ��ʵ��������水ť�������û��������
	 */
	private void displayTimeButton() {
		//��ʾ�����ʱ�����Ӻͼ��ٵİ�ť
		//���Ϊfalse��ִ������
		if(!LrcActivity.inLrcActivity.isLrcViewDisplay){
			MainActivity.instance.lrcDisplayClick();
		}
		
		MainActivity.instance.lrcDisplayClick();
		LrcActivity.inLrcActivity.timeAndLrcButtonlayout.setVisibility(View.VISIBLE);
		LrcActivity.inLrcActivity.timeAdd.setVisibility(View.VISIBLE);
		LrcActivity.inLrcActivity.timeJian.setVisibility(View.VISIBLE);
		LrcActivity.inLrcActivity.lrc_download.setVisibility(View.VISIBLE);
		LrcActivity.inLrcActivity.isUp = true;
		LrcActivity.inLrcActivity.upOrDown.setImageDrawable(getResources().getDrawable(R.drawable.up));
	}
	

}
