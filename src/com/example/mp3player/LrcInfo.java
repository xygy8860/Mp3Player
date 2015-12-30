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
				//每次增加0.5秒补偿时间
				LrcActivity.inLrcActivity.editor.putInt(PlayerService.strTitle + "lrcOffset", 
						500 + LrcActivity.inLrcActivity.preference.getInt(PlayerService.strTitle + "lrcOffset", 0));
				LrcActivity.inLrcActivity.editor.commit();  
				Toast.makeText(getActivity(), "歌词整体前移0.5秒",Toast.LENGTH_SHORT).show();
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
				
				Toast.makeText(getActivity(), "歌词整体后移0.5秒",Toast.LENGTH_SHORT).show();
				
				displayTimeButton();
			}
		});
		
		//设置歌词活动监听
		lrcLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				final int action = event.getAction();
				//如果用户滑动屏幕，则设置参数，使歌词滚动效果暂时消失
				if(action == MotionEvent.ACTION_DOWN){
					//如果用户操作，暂停3秒
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
	 * 回调函数
	 * 动态更新歌词
	 * @author Administrator
	 *
	 */
	class DisplayLrcCallBack implements Runnable{

		@Override
		public void run() {
			//取出已经播放的歌词
			int lrcTempSize = PublicVariable.lrcStrListTemp.size(); 
			String lrcAbove = "";
			
			//Log.v("127", "lrcTempSize-->"+ lrcTempSize);
			if(lrcTempSize > 0){
				for(int i = 0; i < lrcTempSize -1; i++){
					String temp = PublicVariable.lrcStrListTemp.get(i);
					//如果没有歌词，则显示提示信息
					if(temp.indexOf("哦活~还没有歌词哦~~") > -1){
						//handler.removeCallbacks(displayLrcCallBack);
						lrc_info_1.setText(" ");
						lrc_info_3.setText(" ");
						lrc_info_2.setText("哦活~还没有歌词哦~~");
						return;
					}else{
						lrcAbove += temp;
					}
				}
				//歌词写入textview
				lrc_info_1.setText(lrcAbove);
				
				
				//当前播放的歌词
				int lrcSize = PublicVariable.lrcStrList.size(); 
				if(lrcTempSize > 0){
					//如果本行为空，则不更新
					String temp = PublicVariable.lrcStrListTemp.get(lrcTempSize - 1).charAt(0) + "";
					if(temp != "\n" && !(temp.equals("\n"))){
						
						lrc_info_2.setText(PublicVariable.lrcStrListTemp.get(lrcTempSize - 1));
						
						//实现歌词自动向上滚动的效果
						//off滚动距离，使保持在屏幕中间
						int off = lrc_info_1.getHeight() + lrc_info_2.getHeight() - MainActivity.instance.ywidth/2;  
						
						//滚动距离不超过屏幕
						//用户手触摸时也不加载滚动效果
			            if (off > 0) 
			            {   
			            	lrcScroll.scrollTo(0, off);   
			            }              
					}         
				}
				//还未播放的歌词
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
			//持续回调
			handler.postDelayed(displayLrcCallBack, 10);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		handler.removeCallbacks(displayLrcCallBack);//结束回调
		
		LrcActivity.inLrcActivity.timeAdd.setVisibility(View.GONE);
		LrcActivity.inLrcActivity.timeJian.setVisibility(View.GONE);
		
		super.onDestroy();
	}

	/**
	 * 显示歌词调整的桌面按钮，方便用户调整歌词
	 */
	private void displayTimeButton() {
		//显示歌词中时间增加和减少的按钮
		//如果为false，执行两次
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
