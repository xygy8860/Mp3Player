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
 * ��ʾ������
 * @author Administrator
 * Android�н�Window�ֳɶ������
 * Ҫ��ʵ��������Ч��ֻҪ��Window�ļ�����������Window��������ˣ�
 * ͬʱҲ�߱��������ƶ�����������Ч����
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
	
	boolean isFirstDisplayLrc = false;//��ΪtrueΪĬ�ϸ����������falseΪ���ƶ�
	boolean isMainActivityStart = false;
	ArrayList<HashMap<String, String>> list = new ArrayList<>();
	
	public static LrcActivity inLrcActivity = null;
	//������������ʾ���
	public static boolean isLrcViewDisplay = true;
	boolean isLrcLock = false;
	boolean isLrcVisibility = false;
	
	public boolean isClickFromDisplayLrcView = false;
	
	static WindowManager mWM;
    static WindowManager.LayoutParams mWMParams;
    static View win;
    
    //�����С����ɫ���ر���
    int color_value = 0;
    float font_size = 20f;
    public SharedPreferences preference;
    public Editor editor;
    String lrcXmlStr = null;
    String url = null;
    PublicVariable publicVariable;
    public boolean isDownloadLrc = false;//���Ʊ��������û��������滹�Ǹ�����ؽ���
    boolean isUp = false;//���Ʊ������û�������ʽ�������ʾ�������أ�true��
    Context ctx;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
		
		//ȥ��activity����
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.lrc);
		
		//ע��WindowManager ��ͨ��getSystemService ����ȡ�����Ǳ�����getApplicationContext���������Ч�ˡ�
		//����һ�����Ҫ��Manifest.xml �����Ȩ�ޣ�
		//<uses-permission android:name =��android.permission.SYSTEM_ALERT_WINDOW��/>
		
		
		//imageloader��ʼ��
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
		
		//���ʱ�䲹��
		timeAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//ÿ������0.5�벹��ʱ��
				editor.putInt(PlayerService.strTitle + "lrcOffset", 
						500 + preference.getInt(PlayerService.strTitle + "lrcOffset", 0));
				editor.commit();  
				Toast.makeText(getApplicationContext(), "�������ǰ��0.5��",Toast.LENGTH_SHORT).show();
			}
		});
		
		timeJian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editor.putInt(PlayerService.strTitle + "lrcOffset", 
						preference.getInt(PlayerService.strTitle + "lrcOffset", 0) - 500);
				LrcActivity.inLrcActivity.editor.commit();
				
				Toast.makeText(getApplicationContext(), "����������0.5��",Toast.LENGTH_SHORT).show();
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
		
		textView1.getPaint().setFakeBoldText(true);//������������Ϊ����
		textView2.getPaint().setFakeBoldText(true);//������������Ϊ����
		
		//��ȡSharedPreferences����
        ctx = this;       
        preference = ctx.getSharedPreferences("mp3", MODE_PRIVATE);
        //��������
        editor = preference.edit();
        //editor.putInt("color", color_value);
        //editor.commit();
         
        //��ȡ����
        color_value = preference.getInt("color", 0);//���ȡ����ֵ��ȡֵ����Ĳ���  
        if(color_value == 1){
        	color_value = 0;
        }
        font_size = preference.getFloat("font", 20f);//���ȡ����ֵ��ȡֵ����Ĳ���
        
        Log.v("126", "color_value-->" + color_value +"    font_size-->" + font_size);
        textView1.setTextSize(font_size);//��������
		textView2.setTextSize(font_size);
		setColor();//������ɫ
		
		//���ݸ����������ظ��
		lrc_download.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//���api,����json����
				try {
					//�Ը������ƽ��д���
					//���û��ɨ�赽������Ϣ���������Ϣ��title�У���ʽ��  "���� ����" �� "���� - ����"
					String title = PlayerService.strTitle;

					//url = "http://geci.me/api/lyric/" + PublicVariable.UrlEncodeToUTF8(PlayerService.strTitle);//�����api����Դ����
					//url = "http://qqmusic.qq.com/fcgi-bin/qm_getLyricId.fcg?name=" + URLEncoder.encode("Сƻ��", "gbk") + 
							//"singer="+URLEncoder.encode("�����ֵ�", "gbk")+"from=qqplayer";��Ѷapi��ò�Ʋ�����
					
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

		//���������������
		lrcLock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click();
				lrcButtonDisplay();
			}
		});
		
		//����ȡ��
		lrcDisplay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				synchronized (this) {
					lrcButtonDisplay();//�������lrcview֮ǰ
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
		
		//��������
		font_big.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				font_size = preference.getFloat("font", 20f);//���ȡ����ֵ��ȡֵ����Ĳ���
				
				textView1.setTextSize(font_size + 1);
				textView2.setTextSize(font_size + 1);
				
				editor.putFloat("font", font_size + 1);
				Log.v("126", "font_small-->" + (font_size + 1));
		        editor.commit();
			}
		});
		
		//������С
		font_small.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				font_size = preference.getFloat("font", 20f);//���ȡ����ֵ��ȡֵ����Ĳ���
				textView1.setTextSize(font_size - 1);
				textView2.setTextSize(font_size - 1);
				
				editor.putFloat("font", font_size - 1);
				Log.v("126", "font_small-->" + (font_size - 1));
		        editor.commit();
			}
		});
		
		//������ɫ
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
		
		
		
		//�϶��ı��ʵ�λ��
		linearLayout.setOnTouchListener(new OnTouchListener() {
			float lastX, lastY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//�򿪸�ʵ�Ĭ��״̬������״̬
				//����ʱ��ֹ��Ĭ����������ΪisFirstDisplayLrc=false
				if(!isFirstDisplayLrc){
					    //���������û��������������û��϶�
						final int action = event.getAction();
						float x = event.getX();
		                float y = event.getY();
		                
		                if(action == MotionEvent.ACTION_DOWN) {
		                    lastX =  x;
		                    lastY =  y;
		                    
		                } else if(action == MotionEvent.ACTION_MOVE) {
		                	//�������״̬�²����ƶ�
		                	if(!isLrcLock){
		                		//����΢С�ƶ�
		                		mWMParams.x += (int) (x - lastX);
			                    mWMParams.y += (int) (y - lastY);
			                    mWM.updateViewLayout(win, mWMParams);
		                	}
		                }
		                else{
		                	//������΢С�ƶ�����
		                	if((x - lastX < 1) && (y - lastY) < 1){
		                		lrcButtonDisplay();
		                	}
		                }
				}

				//����¼��Ƿ񱻼���
				//ֻ�е�һ�ο������ʱ���ܼ���
	            return !isFirstDisplayLrc;
			}
		});
	}
	
	/**
	 * �����û�����¼����ݴ��ж������ʵ������ͽ���
	 */
	private void click(){
		if(!isLrcLock){ 
				isLrcLock = true;
				Toast.makeText(getApplicationContext(), "����������", Toast.LENGTH_SHORT).show();
		}else{
				isLrcLock = false;
				Toast.makeText(getApplicationContext(), "�������ѽ���", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * ��ʾ�����ظ�ʰ�ť
	 */
	public void lrcButtonDisplay() {
		if(isLrcVisibility){
			//�������ø߶�
			mWMParams.y += 20;
			mWM.updateViewLayout(win, mWMParams);
			
			layout.setVisibility(View.GONE);
			isLrcVisibility = false;
		}
		else{
			//�������ø߶�
			mWMParams.y -= 20;
			mWM.updateViewLayout(win, mWMParams);
			
			layout.setVisibility(View.VISIBLE);
			isLrcVisibility = true;
		}
	}

	/**
	 * ����������ɫ
	 */
	public void setColor() {
		//�����������ɫ
		switch (color_value) {
		case 0: 
			//��ɫ����
			textView1.setTextColor(Color.parseColor("#00DD00"));
			textView2.setTextColor(Color.parseColor("#00DD00"));
			color_value++;
			break;
		case 1: 
			//��ɫ����
			textView1.setTextColor(Color.parseColor("#F5F5FF"));
			textView2.setTextColor(Color.parseColor("#F5F5FF"));
			color_value++;
			break;
		case 2: 
			//��ɫ����
			textView1.setTextColor(Color.parseColor("#FFFF33"));
			textView2.setTextColor(Color.parseColor("#FFFF33"));
			color_value++;
			break;

		case 3: 
			//��ɫ����
			textView1.setTextColor(Color.parseColor("#0000CD"));
			textView2.setTextColor(Color.parseColor("#0000CD"));
			color_value++;
			break;
		case 4: 
			//��ɫ����
			textView1.setTextColor(Color.parseColor("#EE82EE"));
			textView2.setTextColor(Color.parseColor("#EE82EE"));
			color_value++;
			break;
		case 5: 
			//�ٺ�����
			textView1.setTextColor(Color.parseColor("#FF8800"));
			textView2.setTextColor(Color.parseColor("#FF8800"));
			color_value = 0;
			break;

		default:
			color_value = 0;
			break;
		}
		//�����ݴ��뱾��
		editor.putInt("color", color_value);
        editor.commit();
	}

	@SuppressWarnings("deprecation")
	public static void lrcView(boolean flag){
        //����Ϊ�棬����ʾ������
		//����Ϊ�٣����Ƴ�������
        if(flag){
        	isLrcViewDisplay = false;
    		//��ʾ������
            WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
            mWMParams = wmParams;
            wmParams.type = 2002;  //type�ǹؼ��������2002��ʾϵͳ�����ڣ���Ҳ��������2003��
            wmParams.format=1;
//            wmParams.flags= 40;//40Ҳ��ʵ��ʧȥ�����Ч��
            
            //ʹ������ʧȥ���㣬�����û����Բ�������Ӧ��
            wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_DIM_BEHIND;
            
            wmParams.width = LayoutParams.MATCH_PARENT;
            wmParams.height =LayoutParams.WRAP_CONTENT;
           
            //15. public float screenBrightness = -1.0f;
            // ���������û����õ���Ļ���ȡ���ʾӦ���û����õ���Ļ���ȡ�
            //��0��1�������ȴӰ������������仯��
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
					//Toast.makeText(getApplicationContext(), "�����ɹ�", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(getApplicationContext(), "δ���������", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(getApplicationContext(), "���سɹ�", Toast.LENGTH_SHORT).show();
//				listView.setVisibility(View.GONE);
//				lrc_download.setVisibility(View.GONE);
			}else if(msg.what == 11){
				Toast.makeText(getApplicationContext(), "����ʧ��", Toast.LENGTH_SHORT).show();
			}else if(msg.what == 12){
				Log.v("126", "handler-->what-->" + 12);
			}else if(msg.what == 20){
				//������Ϣҳ��
				ArrayList<String> list = new ArrayList<String>();
				list = (ArrayList<String>)msg.obj;
				
				int position = msg.arg1;
				
				Intent intent = new Intent(getApplicationContext(), SongInfoActivity.class);
				//activity��startActivity��context��startActivity������ͬ����Ҫ��Ӵ˾䣬��Ȼ����
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				
				intent.putStringArrayListExtra("list", list);
				intent.putExtra("position", position);
				
				getApplicationContext().startActivity(intent);
			}
			
			
			
			
//			try {  
			    //��������õ�һ�����룬��������Ҫ
				//JSONObject jsonObject = new JSONObject(jsonStr).getJSONObject("result"); 
				
//				JSONObject jsonObject = new JSONObject(lrcXmlStr);
//				JSONArray jsonArray = jsonObject.getJSONArray("result"); 
//				for(int i=0;i<jsonArray.length();i++){ 
//					JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i); 
//
//				    String lrc = jsonObject2.getString("lrc");//�������
//				    String artist = jsonObject2.getString("artist_id");//������
//				    String song = jsonObject2.getString("song");//��������
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
//				//��listView��ʾ
//				listView.setAdapter(adapter);
//			    
//			} catch (Exception ex) {  
//			    // �쳣�������  
//				ex.printStackTrace();
//			}
			
		}
		
	};
	
	/**
	 * ͨ��songid���ظ��
	 * @param songid
	 */
	public void downloadLrc(String songid) {
		Toast.makeText(getApplicationContext(), "��ʿ�ʼ����", Toast.LENGTH_SHORT).show();
		//������ص�ַ
		url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&method=baidu.ting.song.lry&format=json&callback=&songid=" +
				 songid +"&_=1413017198449";
		new Thread(new Runnable() {
			
			@SuppressWarnings("resource")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpDownloader httpDownloader = new HttpDownloader();
				String lrcStr = PlayerService.strTitle;
				lrcStr = lrcStr.replace(" ", "");//������ȥ���ո�
				
				String jsonLrc = httpDownloader.download(url);
				
				BufferedOutputStream stream = null;
			    File file = null;
			    Message message = new Message();
				//�������ݲ�Ϊ��
				if(jsonLrc != null && !(jsonLrc.equals(null)) && jsonLrc != "" && !(jsonLrc.equals(""))){
					try{
						JSONObject jsonObject = new JSONObject(jsonLrc);
				    	
						jsonLrc = jsonObject.getString("lrcContent");//����
						
						Log.v("127", jsonLrc);
						
						
						//����������д��sd��
						
					    lrcStr = lrcStr.replace(" ", "");//ȥ���ո�
					    String path = PublicVariable.SDCardRoot + "mp3" +File.separator + lrcStr +".lrc";
					    int i = getFileFromBytes(jsonLrc, path);
					    if(i == 1){
					    	MainActivity.instance.handler_lrc.removeCallbacks(MainActivity.instance.updateLrcCallback);						
							MainActivity.instance.prepareLrc(PlayerService.strTitle);
							//��ƫ��ʱ������Ϊ0����ʹ���ͬ��
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
	  * ��String���ݴ�Ϊ�ļ�  
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
	 * ��ʼ��ImageLoader
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
	 * ����activity�ı�־
	 * 0��mainactivity
	 * 1��SongInfoactivity
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
