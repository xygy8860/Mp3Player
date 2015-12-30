package com.example.mp3player;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import mars.download.HttpDownloader;

import com.example.mp3player.service.PlayerService;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 显示桌面歌词
 * @author Administrator
 * Android中将Window分成多个级别。
 * 要想实现桌面歌词效果只要将Window的级别高于桌面的Window级别就行了，
 * 同时也具备可自由移动的悬浮窗口效果。
 */
public class LrcActivity extends Application {
	
	TextView textView1;
	TextView textView2;
	TextView textView3;
	ListView listView;
	
	RelativeLayout timeAndLrcButtonlayout;
	RelativeLayout layout;
	LinearLayout linearLayout;
	LinearLayout Layout_upOrDown;
	ImageButton play;
	ImageButton next;
	ImageButton lrcLock;
	ImageButton lrcDisplay;
	ImageButton font_big;
	ImageButton font_small;
	ImageButton color;
	ImageButton lrc_download;
	ImageButton upOrDown;
	
	ImageButton timeAdd;
	ImageButton timeJian;
	
	boolean isFirstDisplayLrc = false;//设为true为默认歌词烂锁定，false为可移动
	boolean isMainActivityStart = false;
	ArrayList<HashMap<String, String>> list = new ArrayList<>();
	
	public static LrcActivity inLrcActivity = null;
	//程序启动不显示歌词
	public static boolean isLrcViewDisplay = true;
	boolean isLrcLock = false;
	boolean isLrcVisibility = false;
	
	public boolean isClickFromDisplayLrcView = false;
	
	static WindowManager mWM;
    static WindowManager.LayoutParams mWMParams;
    static View win;
    
    //字体大小和颜色本地保存
    int color_value = 0;
    float font_size = 20f;
    public SharedPreferences preference;
    public Editor editor;
    String lrcXmlStr = null;
    String url = null;
    PublicVariable publicVariable;
    public boolean isDownloadLrc = false;//控制变量，是用户搜索界面还是歌词下载界面
    boolean isUp = false;//控制变量，用户搜索歌词界面是显示还是隐藏，true是
    Context ctx;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
		
		//去掉activity标题
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.lrc);
		
		//注：WindowManager 是通过getSystemService 来获取，但是必须先getApplicationContext，否则就无效了。
		//还有一点就是要在Manifest.xml 中添加权限：
		//<uses-permission android:name =”android.permission.SYSTEM_ALERT_WINDOW”/>
		
		
		//imageloader初始化
		initImageLoader(getApplicationContext());
		
		mWM =(WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        win = LayoutInflater.from(this).inflate(R.layout.lrc, null);
		
		inLrcActivity = this;
		publicVariable = new PublicVariable();

		Layout_upOrDown = (LinearLayout)win.findViewById(R.id.lrcTextviewManger1);
		linearLayout = (LinearLayout)win.findViewById(R.id.layout);
		textView1 = (TextView)win.findViewById(R.id.lrc_textView_1);
		textView2 = (TextView)win.findViewById(R.id.lrc_textView_2);
		textView3 = (TextView)win.findViewById(R.id.lrc_textView_3);
		
		layout = (RelativeLayout)win.findViewById(R.id.playLayout);
		timeAndLrcButtonlayout = (RelativeLayout)win.findViewById(R.id.timeAndLrcButton);
		
		play = (ImageButton)win.findViewById(R.id.lrcPlay);
		next = (ImageButton)win.findViewById(R.id.lrcNext);
		lrcLock = (ImageButton)win.findViewById(R.id.lrcLock);
		lrcDisplay = (ImageButton)win.findViewById(R.id.lrc_click);
		font_big = (ImageButton)win.findViewById(R.id.font_big);
		font_small = (ImageButton)win.findViewById(R.id.font_small);
		color = (ImageButton)win.findViewById(R.id.color);
		lrc_download = (ImageButton)win.findViewById(R.id.lrcButton);
		//listView = (ListView)win.findViewById(R.id.lrcListView);
		upOrDown = (ImageButton)win.findViewById(R.id.up_or_down);
		
		timeAdd = (ImageButton)win.findViewById(R.id.timeAddButton);
		timeJian = (ImageButton)win.findViewById(R.id.timeJianButton);
		
		//歌词时间补偿
		timeAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//每次增加0.5秒补偿时间
				editor.putInt(PlayerService.strTitle + "lrcOffset", 
						500 + preference.getInt(PlayerService.strTitle + "lrcOffset", 0));
				editor.commit();  
				Toast.makeText(getApplicationContext(), "歌词整体前移0.5秒",Toast.LENGTH_SHORT).show();
			}
		});
		
		timeJian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editor.putInt(PlayerService.strTitle + "lrcOffset", 
						preference.getInt(PlayerService.strTitle + "lrcOffset", 0) - 500);
				LrcActivity.inLrcActivity.editor.commit();
				
				Toast.makeText(getApplicationContext(), "歌词整体后移0.5秒",Toast.LENGTH_SHORT).show();
			}
		});
		
		upOrDown.setOnClickListener(new OnClickListener() {
			Resources res = getResources();
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(isUp){
					isUp = false;
					upOrDown.setImageDrawable(res.getDrawable(R.drawable.down));
					timeAndLrcButtonlayout.setVisibility(View.GONE);
					lrc_download.setVisibility(View.GONE);
				}else{
					timeAndLrcButtonlayout.setVisibility(View.VISIBLE);
					lrc_download.setVisibility(View.VISIBLE);
					upOrDown.setImageDrawable(res.getDrawable(R.drawable.up));
					isUp = true;
				}
			}
		});
		
		textView1.getPaint().setFakeBoldText(true);//设置中文字体为粗体
		textView2.getPaint().setFakeBoldText(true);//设置中文字体为粗体
		
		//获取SharedPreferences对象
        ctx = this;       
        preference = ctx.getSharedPreferences("mp3", MODE_PRIVATE);
        //存入数据
        editor = preference.edit();
        //editor.putInt("color", color_value);
        //editor.commit();
         
        //获取数据
        color_value = preference.getInt("color", 0);//如果取不到值就取值后面的参数  
        if(color_value == 1){
        	color_value = 0;
        }
        font_size = preference.getFloat("font", 20f);//如果取不到值就取值后面的参数
        
        Log.v("126", "color_value-->" + color_value +"    font_size-->" + font_size);
        textView1.setTextSize(font_size);//设置字体
		textView2.setTextSize(font_size);
		setColor();//设置颜色
		
		//根据歌曲名称下载歌词
		lrc_download.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//歌词api,返回json数据
				try {
					//对歌曲名称进行处理
					//如果没有扫描到歌手信息，则歌手信息在title中，格式如  "歌手 歌曲" 或 "歌手 - 歌曲"
					String title = PlayerService.strTitle;

					//url = "http://geci.me/api/lyric/" + PublicVariable.UrlEncodeToUTF8(PlayerService.strTitle);//歌词迷api，资源较少
					//url = "http://qqmusic.qq.com/fcgi-bin/qm_getLyricId.fcg?name=" + URLEncoder.encode("小苹果", "gbk") + 
							//"singer="+URLEncoder.encode("筷子兄弟", "gbk")+"from=qqplayer";腾讯api，貌似不能用
					
					url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.search.catalogSug&format=xml&callback=&query=" +
							PublicVariable.UrlEncodeToUTF8(title) + "&_=1413017198449";
					isDownloadLrc = true;
					publicVariable.newTheard(url,PublicVariable.DOWNLOAD_NETWORK_SELECT_LIST,
							PublicVariable.DOWNLOAD_NETWORK_SONGS,MainActivity.instance.handler,null);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.v("126", "url-->" + url);
			}
		});

		//桌面歌词锁定与解锁
		lrcLock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click();
				lrcButtonDisplay();
			}
		});
		
		//桌面取消
		lrcDisplay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				synchronized (this) {
					lrcButtonDisplay();//必须放在lrcview之前
				}
				lrcView(isLrcViewDisplay);
			}
		});
		
		play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isClickFromDisplayLrcView = true;
				MainActivity.instance.playAndPause();
			}
		});

		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isClickFromDisplayLrcView = true;
				MainActivity.instance.playNext();
			}
		});
		
		//字体增大
		font_big.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				font_size = preference.getFloat("font", 20f);//如果取不到值就取值后面的参数
				
				textView1.setTextSize(font_size + 1);
				textView2.setTextSize(font_size + 1);
				
				editor.putFloat("font", font_size + 1);
				Log.v("126", "font_small-->" + (font_size + 1));
		        editor.commit();
			}
		});
		
		//字体缩小
		font_small.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				font_size = preference.getFloat("font", 20f);//如果取不到值就取值后面的参数
				textView1.setTextSize(font_size - 1);
				textView2.setTextSize(font_size - 1);
				
				editor.putFloat("font", font_size - 1);
				Log.v("126", "font_small-->" + (font_size - 1));
		        editor.commit();
			}
		});
		
		//设置颜色
		color.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				setColor();
			}
		});

		
		linearLayout.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				lrcButtonDisplay();
				isFirstDisplayLrc = false;
			}
		});
		
		
		
		//拖动改变歌词的位置
		linearLayout.setOnTouchListener(new OnTouchListener() {
			float lastX, lastY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//打开歌词的默认状态是锁定状态
				//此暂时废止，默认重新设置为isFirstDisplayLrc=false
				if(!isFirstDisplayLrc){
					    //如果桌面歌词没有锁定则可以随用户拖动
						final int action = event.getAction();
						float x = event.getX();
		                float y = event.getY();
		                
		                if(action == MotionEvent.ACTION_DOWN) {
		                    lastX =  x;
		                    lastY =  y;
		                    
		                } else if(action == MotionEvent.ACTION_MOVE) {
		                	//歌词锁定状态下不能移动
		                	if(!isLrcLock){
		                		//忽略微小移动
		                		mWMParams.x += (int) (x - lastX);
			                    mWMParams.y += (int) (y - lastY);
			                    mWM.updateViewLayout(win, mWMParams);
		                	}
		                }
		                else{
		                	//单击或微小移动触发
		                	if((x - lastX < 1) && (y - lastY) < 1){
		                		lrcButtonDisplay();
		                	}
		                }
				}

				//点击事件是否被监听
				//只有第一次开启歌词时才能监听
	            return !isFirstDisplayLrc;
			}
		});
	}
	
	/**
	 * 监听用户点击事件，据此判断桌面歌词的锁定和解锁
	 */
	private void click(){
		if(!isLrcLock){ 
				isLrcLock = true;
				Toast.makeText(getApplicationContext(), "桌面歌词锁定", Toast.LENGTH_SHORT).show();
		}else{
				isLrcLock = false;
				Toast.makeText(getApplicationContext(), "桌面歌词已解锁", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * 显示与隐藏歌词按钮
	 */
	public void lrcButtonDisplay() {
		if(isLrcVisibility){
			//重新设置高度
			mWMParams.y += 20;
			mWM.updateViewLayout(win, mWMParams);
			
			layout.setVisibility(View.GONE);
			isLrcVisibility = false;
		}
		else{
			//重新设置高度
			mWMParams.y -= 20;
			mWM.updateViewLayout(win, mWMParams);
			
			layout.setVisibility(View.VISIBLE);
			isLrcVisibility = true;
		}
	}

	/**
	 * 设置字体颜色
	 */
	public void setColor() {
		//分情况设置颜色
		switch (color_value) {
		case 0: 
			//绿色字体
			textView1.setTextColor(Color.parseColor("#00DD00"));
			textView2.setTextColor(Color.parseColor("#00DD00"));
			color_value++;
			break;
		case 1: 
			//白色字体
			textView1.setTextColor(Color.parseColor("#F5F5FF"));
			textView2.setTextColor(Color.parseColor("#F5F5FF"));
			color_value++;
			break;
		case 2: 
			//黄色字体
			textView1.setTextColor(Color.parseColor("#FFFF33"));
			textView2.setTextColor(Color.parseColor("#FFFF33"));
			color_value++;
			break;

		case 3: 
			//蓝色字体
			textView1.setTextColor(Color.parseColor("#0000CD"));
			textView2.setTextColor(Color.parseColor("#0000CD"));
			color_value++;
			break;
		case 4: 
			//粉色字体
			textView1.setTextColor(Color.parseColor("#EE82EE"));
			textView2.setTextColor(Color.parseColor("#EE82EE"));
			color_value++;
			break;
		case 5: 
			//橘红字体
			textView1.setTextColor(Color.parseColor("#FF8800"));
			textView2.setTextColor(Color.parseColor("#FF8800"));
			color_value = 0;
			break;

		default:
			color_value = 0;
			break;
		}
		//将数据存入本地
		editor.putInt("color", color_value);
        editor.commit();
	}

	@SuppressWarnings("deprecation")
	public static void lrcView(boolean flag){
        //参数为真，则显示桌面歌词
		//参数为假，则移除桌面歌词
        if(flag){
        	isLrcViewDisplay = false;
    		//显示桌面歌词
            WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
            mWMParams = wmParams;
            wmParams.type = 2002;  //type是关键，这里的2002表示系统级窗口，你也可以试试2003。
            wmParams.format=1;
//            wmParams.flags= 40;//40也是实现失去焦点的效果
            
            //使桌面歌词失去焦点，这样用户可以操作其他应用
            wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_DIM_BEHIND;
            
            wmParams.width = LayoutParams.MATCH_PARENT;
            wmParams.height =LayoutParams.WRAP_CONTENT;
           
            //15. public float screenBrightness = -1.0f;
            // 用来覆盖用户设置的屏幕亮度。表示应用用户设置的屏幕亮度。
            //从0到1调整亮度从暗到最亮发生变化。
            wmParams.dimAmount = 0.5f;
            
            mWM.addView(win, wmParams);
            
            win.invalidate();
        }
        else{
        	mWM.removeView(win);
        	isLrcViewDisplay = true;
        }
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			if(msg.what == publicVariable.WHAT_UPDATE_VIEW){
				
				list = (ArrayList<HashMap<String,String>>)msg.obj;
				if(list.size() > 0){
					//Toast.makeText(getApplicationContext(), "搜索成功", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(getApplicationContext(), "未搜索到歌词", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Layout_upOrDown.setVisibility(View.VISIBLE);
				LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll1);
				linearLayout11.setVisibility(View.GONE);
				View view1 = (View)win.findViewById(R.id.v1);
				view1.setVisibility(View.GONE);
				
				LinearLayout linearLayout21 = (LinearLayout)win.findViewById(R.id.ll2);
				linearLayout21.setVisibility(View.GONE);
				View view2 = (View)win.findViewById(R.id.v2);
				view2.setVisibility(View.GONE);
				
				LinearLayout linearLayout31 = (LinearLayout)win.findViewById(R.id.ll3);
				linearLayout31.setVisibility(View.GONE);
				View view3 = (View)win.findViewById(R.id.v3);
				view3.setVisibility(View.GONE);
				
				LinearLayout linearLayout41 = (LinearLayout)win.findViewById(R.id.ll4);
				linearLayout41.setVisibility(View.GONE);
				View view4 = (View)win.findViewById(R.id.v4);
				view4.setVisibility(View.GONE);
				
				LinearLayout linearLayout51 = (LinearLayout)win.findViewById(R.id.ll5);
				linearLayout51.setVisibility(View.GONE);
				View view5 = (View)win.findViewById(R.id.v5);
				view5.setVisibility(View.GONE);
				
				LinearLayout linearLayout61 = (LinearLayout)win.findViewById(R.id.ll6);
				linearLayout61.setVisibility(View.GONE);
				View view6 = (View)win.findViewById(R.id.v6);
				view6.setVisibility(View.GONE);
				
				LinearLayout linearLayout71 = (LinearLayout)win.findViewById(R.id.ll7);
				linearLayout61.setVisibility(View.GONE);
				View view7 = (View)win.findViewById(R.id.v7);
				view7.setVisibility(View.GONE);
				
				LinearLayout linearLayout81 = (LinearLayout)win.findViewById(R.id.ll9);
				linearLayout61.setVisibility(View.GONE);
				View view8 = (View)win.findViewById(R.id.v8);
				view8.setVisibility(View.GONE);
				
				LinearLayout linearLayout91 = (LinearLayout)win.findViewById(R.id.ll9);
				linearLayout61.setVisibility(View.GONE);
				View view9 = (View)win.findViewById(R.id.v9);
				view9.setVisibility(View.GONE);
				
				LinearLayout linearLayout101 = (LinearLayout)win.findViewById(R.id.ll10);
				linearLayout61.setVisibility(View.GONE);
				View view10 = (View)win.findViewById(R.id.v10);
				view10.setVisibility(View.GONE);
				
				for(int i = 0; i < list.size(); i++){
					
					switch (i) {
					case 0:
						addTextView(i);
						break;
					case 1:
						addTextView1(i);
						break;
					case 2:
						addTextView2(i);
						break;
					case 3:
						addTextView3(i);
						break;
					case 4:
						addTextView4(i);
						break;
					case 5:
						addTextView5(i);
						break;
					case 6:
						addTextView6(i);
						break;
					case 7:
						addTextView7(i);
						break;
					case 8:
						addTextView8(i);
						break;
					case 9:
						addTextView9(i);
						break;
					default:
						break;
					}					
				}
				
				Log.v("126", "handler-->what-->" + 11);
				
			}else if(msg.what == 10){
				Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
//				listView.setVisibility(View.GONE);
//				lrc_download.setVisibility(View.GONE);
			}else if(msg.what == 11){
				Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 12){
				Log.v("126", "handler-->what-->" + 12);
			}else if(msg.what == 20){
				//启动信息页面
				ArrayList<String> list = new ArrayList<String>();
				list = (ArrayList<String>)msg.obj;
				
				int position = msg.arg1;
				
				Intent intent = new Intent(getApplicationContext(), SongInfoActivity.class);
				//activity的startActivity和context的startActivity方法不同，需要添加此句，不然报错
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				
				intent.putStringArrayListExtra("list", list);
				intent.putExtra("position", position);
				
				getApplicationContext().startActivity(intent);
			}
			
			
			
			
//			try {  
			    //两层或多层用第一个代码，本例不需要
				//JSONObject jsonObject = new JSONObject(jsonStr).getJSONObject("result"); 
				
//				JSONObject jsonObject = new JSONObject(lrcXmlStr);
//				JSONArray jsonArray = jsonObject.getJSONArray("result"); 
//				for(int i=0;i<jsonArray.length();i++){ 
//					JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
//
//				    String lrc = jsonObject2.getString("lrc");//歌词连接
//				    String artist = jsonObject2.getString("artist_id");//艺术家
//				    String song = jsonObject2.getString("song");//歌曲名称
//				    
//				    HashMap<String, String> map = new HashMap<String, String>();
//				    map.put("songInfo", song);
//					map.put("size", "");
//					map.put("songer", artist);
//					map.put("lrcName", "");
//					map.put("lrcSize", "");
//					map.put("uri", lrc);
//				    list.add(map);
//				}
//				Log.v("126", "list-->" + list.toString());
//						
//				//将listView显示
//				listView.setAdapter(adapter);
//			    
//			} catch (Exception ex) {  
//			    // 异常处理代码  
//				ex.printStackTrace();
//			}
			
		}
		
	};
	
	/**
	 * 通过songid下载歌词
	 * @param songid
	 */
	public void downloadLrc(String songid) {
		Toast.makeText(getApplicationContext(), "歌词开始下载", Toast.LENGTH_SHORT).show();
		//歌词下载地址
		url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.song.lry&format=json&callback=&songid=" +
				 songid +"&_=1413017198449";
		new Thread(new Runnable() {
			
			@SuppressWarnings("resource")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpDownloader httpDownloader = new HttpDownloader();
				String lrcStr = PlayerService.strTitle;
				lrcStr = lrcStr.replace(" ", "");//保存歌词去除空格
				
				String jsonLrc = httpDownloader.download(url);
				
				BufferedOutputStream stream = null;
			    File file = null;
			    Message message = new Message();
				//返回数据不为空
				if(jsonLrc != null && !(jsonLrc.equals(null)) && jsonLrc != "" && !(jsonLrc.equals(""))){
					try{
						JSONObject jsonObject = new JSONObject(jsonLrc);
				    	
						jsonLrc = jsonObject.getString("lrcContent");//歌手
						
						Log.v("127", jsonLrc);
						
						
						//将歌曲内容写入sd卡
						
					    lrcStr = lrcStr.replace(" ", "");//去除空格
					    String path = PublicVariable.SDCardRoot + "mp3" +File.separator + lrcStr +".lrc";
					    int i = getFileFromBytes(jsonLrc, path);
					    if(i == 1){
					    	MainActivity.instance.handler_lrc.removeCallbacks(MainActivity.instance.updateLrcCallback);						
							MainActivity.instance.prepareLrc(PlayerService.strTitle);
							//将偏移时间设置为0，以使歌词同步
							MainActivity.instance.offset = 0;
							MainActivity.instance.handler_lrc.postDelayed(MainActivity.instance.updateLrcCallback, 5);						
							message.what = 10;
					    }
					    else{
					    	message.what = 11;
					    }
					    handler.sendMessage(message);
						Log.v("127", "jsonLrc-->done");
						
					}catch(Exception e){
						e.printStackTrace();
					}
					finally{
						if(stream != null){
							try {
								stream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}).start();
	}
	
	/**  
	  * 将String数据存为文件  
	  */  
	 public static int getFileFromBytes(String str,String path) {  
	    byte[] b = str.getBytes();  
	     BufferedOutputStream stream = null;  
	     File file = null;  
	     int i = 0;
	     try {  
	         file = new File(path);  
	         FileOutputStream fstream = new FileOutputStream(file);  
	         stream = new BufferedOutputStream(fstream);  
	         stream.write(b);  
	         i = 1;
	     } catch (Exception e) {  
	    	 i = -1;
	         e.printStackTrace();  
	     } finally {  
	         if (stream != null) {  
	             try {  
	                 stream.close();  
	             } catch (IOException e1) {  
	                 e1.printStackTrace();  
	             }  
	         }  
	     }  
	     return i;  
	 }  
	
	public void addTextView(int i) {
		final String title = list.get(i).get("title");
		final String author = list.get(i).get("author");
		final String song_id = list.get(i).get("song_id");
		TextView textView11 = (TextView)win.findViewById(R.id.t11);
		TextView textView12 = (TextView)win.findViewById(R.id.t12);
		LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll1);
		linearLayout11.setVisibility(View.VISIBLE);
		
		View view1 = (View)win.findViewById(R.id.v1);
		view1.setVisibility(View.VISIBLE);
		
		textView11.setText(title);
		textView12.setText(author);
		linearLayout11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadLrc(song_id);
				Log.v("126", "title-->" + title  + "       author-->" + author + "    song_id--> " + song_id);
			}
		});
	}

	public void addTextView1(int i) {
		final String title = list.get(i).get("title");
		final String author = list.get(i).get("author");
		final String song_id = list.get(i).get("song_id");
		TextView textView11 = (TextView)win.findViewById(R.id.t21);
		TextView textView12 = (TextView)win.findViewById(R.id.t22);
		LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll2);
		linearLayout11.setVisibility(View.VISIBLE);
		textView11.setText(title);
		textView12.setText(author);
		View view1 = (View)win.findViewById(R.id.v2);
		view1.setVisibility(View.VISIBLE);
		linearLayout11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadLrc(song_id);
				Log.v("126", "title-->" + title  + "       author-->" + author + "    song_id--> " + song_id);
			}
		});
	}
	
	public void addTextView2(int i) {
		final String title = list.get(i).get("title");
		final String author = list.get(i).get("author");
		final String song_id = list.get(i).get("song_id");
		TextView textView11 = (TextView)win.findViewById(R.id.t31);
		TextView textView12 = (TextView)win.findViewById(R.id.t32);
		LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll3);
		linearLayout11.setVisibility(View.VISIBLE);
		textView11.setText(title);
		textView12.setText(author);
		View view1 = (View)win.findViewById(R.id.v3);
		view1.setVisibility(View.VISIBLE);
		linearLayout11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadLrc(song_id);
				Log.v("126", "title-->" + title  + "       author-->" + author + "    song_id--> " + song_id);
			}
		});
	}
	
	public void addTextView3(int i) {
		final String title = list.get(i).get("title");
		final String author = list.get(i).get("author");
		final String song_id = list.get(i).get("song_id");
		TextView textView11 = (TextView)win.findViewById(R.id.t41);
		TextView textView12 = (TextView)win.findViewById(R.id.t42);
		LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll4);
		linearLayout11.setVisibility(View.VISIBLE);
		textView11.setText(title);
		textView12.setText(author);
		View view1 = (View)win.findViewById(R.id.v4);
		view1.setVisibility(View.VISIBLE);
		linearLayout11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadLrc(song_id);
				Log.v("126", "title-->" + title  + "       author-->" + author + "    song_id--> " + song_id);
			}
		});
	}
	
	public void addTextView4(int i) {
		final String title = list.get(i).get("title");
		final String author = list.get(i).get("author");
		final String song_id = list.get(i).get("song_id");
		TextView textView11 = (TextView)win.findViewById(R.id.t51);
		TextView textView12 = (TextView)win.findViewById(R.id.t52);
		LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll5);
		linearLayout11.setVisibility(View.VISIBLE);
		textView11.setText(title);
		textView12.setText(author);
		View view1 = (View)win.findViewById(R.id.v5);
		view1.setVisibility(View.VISIBLE);
		linearLayout11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadLrc(song_id);
				Log.v("126", "title-->" + title  + "       author-->" + author + "    song_id--> " + song_id);
			}
		});
	}
	
	public void addTextView5(int i) {
		final String title = list.get(i).get("title");
		final String author = list.get(i).get("author");
		final String song_id = list.get(i).get("song_id");
		TextView textView11 = (TextView)win.findViewById(R.id.t61);
		TextView textView12 = (TextView)win.findViewById(R.id.t62);
		LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll6);
		linearLayout11.setVisibility(View.VISIBLE);
		textView11.setText(title);
		textView12.setText(author);
		View view1 = (View)win.findViewById(R.id.v6);
		view1.setVisibility(View.VISIBLE);
		linearLayout11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadLrc(song_id);
				Log.v("126", "title-->" + title  + "       author-->" + author);
				
			}
		});
	}
	
	
	public void addTextView6(int i) {
		final String title = list.get(i).get("title");
		final String author = list.get(i).get("author");
		final String song_id = list.get(i).get("song_id");
		TextView textView11 = (TextView)win.findViewById(R.id.t71);
		TextView textView12 = (TextView)win.findViewById(R.id.t72);
		LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll7);
		linearLayout11.setVisibility(View.VISIBLE);
		textView11.setText(title);
		textView12.setText(author);
		View view1 = (View)win.findViewById(R.id.v7);
		view1.setVisibility(View.VISIBLE);
		linearLayout11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadLrc(song_id);
				Log.v("126", "title-->" + title  + "       author-->" + author);
				
			}
		});
	}
	
	public void addTextView7(int i) {
		final String title = list.get(i).get("title");
		final String author = list.get(i).get("author");
		final String song_id = list.get(i).get("song_id");
		TextView textView11 = (TextView)win.findViewById(R.id.t81);
		TextView textView12 = (TextView)win.findViewById(R.id.t82);
		LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll8);
		linearLayout11.setVisibility(View.VISIBLE);
		textView11.setText(title);
		textView12.setText(author);
		View view1 = (View)win.findViewById(R.id.v8);
		view1.setVisibility(View.VISIBLE);
		linearLayout11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadLrc(song_id);
				Log.v("126", "title-->" + title  + "       author-->" + author);
				
			}
		});
	}
	
	public void addTextView8(int i) {
		final String title = list.get(i).get("title");
		final String author = list.get(i).get("author");
		final String song_id = list.get(i).get("song_id");
		TextView textView11 = (TextView)win.findViewById(R.id.t91);
		TextView textView12 = (TextView)win.findViewById(R.id.t92);
		LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll6);
		linearLayout11.setVisibility(View.VISIBLE);
		textView11.setText(title);
		textView12.setText(author);
		View view1 = (View)win.findViewById(R.id.v9);
		view1.setVisibility(View.VISIBLE);
		linearLayout11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadLrc(song_id);
				Log.v("126", "title-->" + title  + "       author-->" + author);
				
			}
		});
	}
	
	public void addTextView9(int i) {
		final String title = list.get(i).get("title");
		final String author = list.get(i).get("author");
		final String song_id = list.get(i).get("song_id");
		TextView textView11 = (TextView)win.findViewById(R.id.t101);
		TextView textView12 = (TextView)win.findViewById(R.id.t102);
		LinearLayout linearLayout11 = (LinearLayout)win.findViewById(R.id.ll10);
		linearLayout11.setVisibility(View.VISIBLE);
		textView11.setText(title);
		textView12.setText(author);
		View view1 = (View)win.findViewById(R.id.v10);
		view1.setVisibility(View.VISIBLE);
		linearLayout11.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadLrc(song_id);
				Log.v("126", "title-->" + title  + "       author-->" + author);
				
			}
		});
	}

	/**
	 * 初始化ImageLoader
	 * @param context
	 */
	public static void initImageLoader(Context context) {

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().build();


		@SuppressWarnings("deprecation")
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}
	
	/**
	 * 保存activity的标志
	 * 0：mainactivity
	 * 1：SongInfoactivity
	 * 2: NetworksActivity
	 * 3: ScanMUsicActivity
	 */
	private int i = -1;
	
	public void setCurrentActivity(int i){
	 this.i = i;
	}

	public int getCurrentActivity(){
	 return this.i;
	}
	
}
