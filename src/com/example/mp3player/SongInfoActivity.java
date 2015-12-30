package com.example.mp3player;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import mars.download.HttpDownloader;

import com.example.mp3player.service.PlayerService;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.color;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SongInfoActivity extends FragmentActivity implements TabListener{
	
	public ViewPager mViewPager;  
    private ViewPagerAdapter mViewPagerAdapter;  

    public  Bitmap overlay;
    
    private SongerInfo mFragment1 = new SongerInfo();  
	private SongInfo mFragment2;  
	private LrcInfo mFragment3 = new LrcInfo();  
	      
	public static SongInfoActivity infoActivity;
	private static final int TAB_INDEX_COUNT = 3;  
	
	Handler handler_bitmap = new Handler();
	
	private static final int TAB_INDEX_ONE = 0;  
	private static final int TAB_INDEX_TWO = 1;  
	private static final int TAB_INDEX_THREE = 2; 
	
	RelativeLayout mainLayout;
	ImageView mainViewBackground;
	ImageButton playButton;
	ImageButton nextButton;
	ImageButton stopButton;
	ImageButton playModeButton;
	TextView timeTextView;
	TextView timeAllTextView;
	SeekBar seekBar;
	String jsonStr;
	
	String name = null;//歌手
	String avatar_middle = null;//歌手信息小图片地址
    String intro = null;//歌手信息
    String avatar_s500 = null;//歌手大图片 
    String country = null;//歌手国家或地区
    String avatar_mini = null;//mini图片，虚化背景用
    
    String songId = null;
    String lrclink = null;
	String author = null;	
	String title = null;
	String songerId = null;
	
	int position = 0;//点击信息的歌曲位置
	
	public String ting_uid = null;//歌手uid
    
	public static TextView songAndSonger;//显示歌曲和歌手信息
	public ImageButton songerImage;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		//取得链表
		ArrayList<String> list = new ArrayList<String>();
		
		list =	(ArrayList<String>) intent.getStringArrayListExtra("list");
		
		position = intent.getIntExtra("position", 0);
		
		
		infoActivity = this;
		
		PublicVariable.isSongInfoActivityStart = true;

		setUpActionBar();  
        setUpViewPager();  
        setUpTabs();   
        
        mViewPager.setCurrentItem(1);//设置首页为第二页
        
        mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
        playButton = (ImageButton)findViewById(R.id.StartAndStopButton);
		nextButton = (ImageButton)findViewById(R.id.nextButton);
		songAndSonger = (TextView)findViewById(R.id.songList);
		playModeButton = (ImageButton)findViewById(R.id.playMode);
		stopButton = (ImageButton)findViewById(R.id.stopButton);
		timeTextView = (TextView)findViewById(R.id.time);
		timeAllTextView = (TextView)findViewById(R.id.timeAll);
		seekBar = (SeekBar)findViewById(R.id.seekbar);
		songerImage = (ImageButton)findViewById(R.id.songerImagePlay);
		mainViewBackground = (ImageView)findViewById(R.id.mainImageBackground);
		mainLayout.setBackgroundResource(R.drawable.back_1);//设置背景图片
		
		mFragment2 = new SongInfo();  
		
		//将背景设置为透明
		playButton.setBackgroundColor(color.transparent);
		nextButton.setBackgroundColor(color.transparent);
		stopButton.setBackgroundColor(color.transparent);
		songerImage.setBackgroundColor(color.transparent);
		
		songerImage.setBackgroundResource(R.drawable.author);
		
		//设置字体为白色字体
		timeAllTextView.setTextColor(Color.parseColor("#F5F5FF"));
		timeTextView.setTextColor(Color.parseColor("#F5F5FF"));
		
		songerImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "此页按钮无点击效果", Toast.LENGTH_SHORT).show();
			}
		});
		
		
		//取得歌曲信息
		if(list != null && !(list.equals(null))){
			songerId = list.get(0);//歌手uid
			author = list.get(1);//歌手名称
			lrclink = list.get(2);//歌词地址
			songId = list.get(3);//歌曲id
			title = list.get(4); //歌曲名称  
			
			updateBitmap(songerId);
		}else{
			ting_uid = LrcActivity.inLrcActivity.preference.getString(PlayerService.strAuthor, null);
			updateBitmap(ting_uid);
		}

		
		
		//加载初始图片
		ImageLoader.getInstance().displayImage(PlayerService.strPic_small, 
				SongInfoActivity.infoActivity.songerImage, PublicVariable.optionsRound);
		
		//如果播放不为空，设置播放状态
		if(MainActivity.mediaPlayer != null || PlayerService.mediaPlayer != null){
        	//获取总的播放时长和当前播放进度
        	int timeAll = 0, time = 0;
        	if(PlayerService.isPlayOnLine){
        		timeAll = PlayerService.mediaPlayer.getDuration();
        		time = PlayerService.mediaPlayer.getCurrentPosition();
        	}
        	else{
        		timeAll = MainActivity.mediaPlayer.getDuration();
        		time = MainActivity.mediaPlayer.getCurrentPosition();
        	}
//        	prepareLrc(PlayerService.strTitle);//准备歌词
        	
        	timeAllTextView.setText(MainActivity.instance.longTime2Min(timeAll));//设置总时长
        	timeTextView.setText(MainActivity.instance.longTime2Min(time));//设置当前时长
        	seekBar.setMax(timeAll);//设置进度条最大值
        	seekBar.setProgress(time);//设置 进度
        	songAndSonger.setText(PlayerService.strTitle + "-" + PlayerService.inPlayservice.strAuthor + "                           ");//设置歌手歌曲信息
        	
        	//正在播放,替换图标
        	if(!MainActivity.isPause){
        		playButton.setImageDrawable(getResources().getDrawable(R.drawable.button_pause));
        	}
        }
		
		/**
		 * 改变播放模式
		 */
		playModeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.instance.playModeChanged();
			}
		});
		
		//歌词按钮
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.instance.lrcDisplayClick();
			}
		});
		
		/**
		 * 进度条改变监控
		 */
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				MainActivity.instance.moveSeekBar(progress, fromUser);
				
			}
		});
		
		playButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.instance.playAndPause();
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.instance.playNext();
			}
		});
		
	}



	/**
	 * 根据播放歌手信息下载歌手图片
	 * 并虚化作为背景图
	 */
	public void updateBitmap(String ting_uid) {
		//读取歌手编号信息
		
		//根据歌手编号查找歌手信息
		if(ting_uid != null && !(ting_uid.equals(null))){
			//返回json格式数据
			final String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=%20baidu.ting.artist.getInfo&format=json&callback=&tinguid=" +
					ting_uid + "&_=1413017198449";
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					HttpDownloader httpDownloader = new HttpDownloader();
					//返回json数据
					jsonStr = httpDownloader.download(url);
					if(jsonStr != null && !(jsonStr.equals(null))){
						//对json解析
					    try {
					    	JSONObject jsonObject = new JSONObject(jsonStr);
					    	
							name = jsonObject.getString("name");//歌手
							avatar_middle = jsonObject.getString("avatar_middle");//歌手信息小图片地址
						    intro = jsonObject.getString("intro");//歌手信息
						    avatar_s500 = jsonObject.getString("avatar_s500");//歌手大图片 
						    country = jsonObject.getString("country");//歌手国家或地区
						    avatar_mini = jsonObject.getString("avatar_mini");//歌手国家或地区
			                
			                HttpClient httpClient = new DefaultHttpClient();
			                HttpGet httpGet = new HttpGet(avatar_mini);
			                Bitmap bitmap = null;
			                
			                HttpResponse httpResponse = httpClient.execute(httpGet);
			                if (httpResponse.getStatusLine().getStatusCode() == 200) {
			                    HttpEntity httpEntity = httpResponse.getEntity();
			                    byte[] data = EntityUtils.toByteArray(httpEntity);
			                    bitmap = BitmapFactory
			                            .decodeByteArray(data, 0, data.length);
			                }
			                if( bitmap != null && !(bitmap.equals(null))){
			                	
			    				
			    				float x = MainActivity.instance.xheight/5;
			    				float y = MainActivity.instance.ywidth/5;
			    				Log.v("126", "x:"+ x + "   y:"+y);
			                	
			                	Matrix matrix = new Matrix(); 
			                	matrix.postScale(0.5f,0.5f); //先缩小损失像素，以虚化彻底 
			                	Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
			                	
			                	matrix.postScale(y,x); //宽和长放大缩小的比例 ，以适应屏幕
			                	Bitmap resizeBmp1 = Bitmap.createBitmap(resizeBmp,0,0,resizeBmp.getWidth(),resizeBmp.getHeight(),matrix,true); 
			                	
			                	Log.v("126", "bitmap --> not null" );
			                	blur(resizeBmp1, mainLayout);
			                }
						   
						    
						    Message message = new Message();
						    message.what = 0;
						    mFragment2.handler.sendMessage(message);
						    
						    Message message2 = new Message();
						    message2.what = 0;
						    mFragment1.handler.sendMessage(message2);
						    
						    Log.v("126", "avatar_s500-->" + avatar_s500);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}
	
	
	
	/**
	 * 设置保存当前activity
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		LrcActivity.inLrcActivity.setCurrentActivity(1);
		super.onResume();
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		PublicVariable.isSongInfoActivityStart = false;
		PublicVariable.isStartSongInfoActivityFromNetworklist = false;
		super.onBackPressed();
	}


	Handler handler_1 = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			//更新虚化背景
			if(msg.what == 0){
				mainLayout.setBackground(new BitmapDrawable(getResources(), overlay));  //虚化背景图片
				Log.v("126", "虚化背景图片");
			}
		}
		
	};
	
	/**
	 * RenderScript实现高斯模糊算法
	 * @param bkg
	 * @param view
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) 
	public void blur(final Bitmap bkg, final View view) { 
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				 //long startMs = System.currentTimeMillis();  
				    float radius = 20f;  
				  
				    Log.v("126","getMeasuredWidth： " + (int)(view.getMeasuredWidth()) + "   getMeasuredHeight：" +(int)(view.getMeasuredHeight()));
				    
				    Log.v("126","-view.getLeft()： " + view.getLeft()+ "    -view.getTop()：" + view.getTop()); 
				    
				    overlay = Bitmap.createBitmap(MainActivity.instance.ywidth, MainActivity.instance.xheight, Bitmap.Config.ARGB_8888);  
				    Canvas canvas = new Canvas(overlay);  
				    canvas.translate(0, 0);  
				    canvas.drawBitmap(bkg, 0, 0, null);  
				  
				    RenderScript rs = RenderScript.create(SongInfoActivity.this);  
				  
				    Allocation overlayAlloc = Allocation.createFromBitmap(rs, overlay);  
				    ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());  
				    blur.setInput(overlayAlloc);  
				    blur.setRadius(radius);  
				    blur.forEach(overlayAlloc);  
				    overlayAlloc.copyTo(overlay); 
				    
				    Log.v("126","cost " + (System.currentTimeMillis() ) + "ms");  
				    
				    Message message = new Message();
				    message.what = 0;
				    handler_1.sendMessage(message);
				    rs.destroy(); 
			}
		}).start();
	}  
	
	
	/**
	 * FastBlur虚化处理背景图片
	 * @param bkg
	 * @param view
	 */
//	private void blur(Bitmap bkg, View view) {
//	    long startMs = System.currentTimeMillis();
//	    float radius = 2;
//	    float scaleFactor = 8;
//
//	    Bitmap overlay = Bitmap.createBitmap((int)(view.getMeasuredWidth()/scaleFactor), (int)(view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
//	    Canvas canvas = new Canvas(overlay);
//	    canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
//	    canvas.scale(1 / scaleFactor, 1 / scaleFactor);
//	    Paint paint = new Paint();
//	    paint.setFlags(Paint.FILTER_BITMAP_FLAG);
//	    canvas.drawBitmap(bkg, 0, 0, paint);
//	    overlay = FastBlur.doBlur(overlay, (int)radius, true);
//	    view.setBackground(new BitmapDrawable(getResources(), overlay));
//	    
//	    Log.v("126","cost " + (System.currentTimeMillis() - startMs) + "ms");
//	} 

	private void setUpTabs() {  
        final ActionBar actionBar = getActionBar();  
        for (int i = 0; i < mViewPagerAdapter.getCount(); ++i) {  
            actionBar.addTab(actionBar.newTab()  
                    .setText(mViewPagerAdapter.getPageTitle(i))  
                    .setTabListener(this));  
        }  
    }  
	
	 private void setUpActionBar() {  
	        final ActionBar actionBar = getActionBar();  
	        actionBar.setHomeButtonEnabled(false);  
	        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
	        actionBar.setDisplayShowTitleEnabled(false);  
	        actionBar.setDisplayShowHomeEnabled(false);  
	    }  
	
    private void setUpViewPager() {  
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());  
          
        mViewPager = (ViewPager)findViewById(R.id.pager);  
        mViewPager.setAdapter(mViewPagerAdapter); 
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {  
            @Override  
            public void onPageSelected(int position) {  
                final ActionBar actionBar = getActionBar();  
                actionBar.setSelectedNavigationItem(position);  
            }  
              
            @Override  
            public void onPageScrollStateChanged(int state) {  
                switch(state) {  
                    case ViewPager.SCROLL_STATE_IDLE:  
                        //TODO  
                        break;  
                    case ViewPager.SCROLL_STATE_DRAGGING:  
                        //TODO  
                        break;  
                    case ViewPager.SCROLL_STATE_SETTLING:  
                        //TODO  
                        break;  
                    default:  
                        //TODO  
                        break;  
                }  
            }  
        });  
    }  
    
    public class ViewPagerAdapter extends FragmentPagerAdapter { 
    	  
        public ViewPagerAdapter(FragmentManager fm) {  
            super(fm);  
            // TODO Auto-generated constructor stub  
        }  
  
        @Override  
        public Fragment getItem(int position) {  
            // TODO Auto-generated method stub  
            switch (position) {  
                case TAB_INDEX_ONE:  
                    return mFragment1;  
                case TAB_INDEX_TWO:
                    return mFragment2;  
                case TAB_INDEX_THREE: 
                    return mFragment3;  
            }  
            throw new IllegalStateException("No fragment at position " + position);  
        }  

		@Override  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return TAB_INDEX_COUNT;  
        }  
          
        @Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
        	
        	return super.getItemPosition(object); 
        	//解决数据刷新的问题和notifyDataSetChanged配合使用
//        	return PagerAdapter.POSITION_NONE;
		}
        
		@Override  
        public CharSequence getPageTitle(int position) {  
            String tabLabel = null;  
            switch (position) {  
                case TAB_INDEX_ONE:  
                    tabLabel = getString(R.string.songinfo_tab_1);  
                    break;  
                case TAB_INDEX_TWO:  
                    tabLabel = getString(R.string.songinfo_tab_2);  
                    break;  
                case TAB_INDEX_THREE:  
                    tabLabel = getString(R.string.songinfo_tab_3);  
                    break;  
            }  
            return tabLabel;  
        }  
    }

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		 
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		mViewPager.setCurrentItem(tab.getPosition()); 
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}  
}

 