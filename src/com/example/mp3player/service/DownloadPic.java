package com.example.mp3player.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.example.mp3player.LrcActivity;
import com.example.mp3player.MainActivity;
import com.example.mp3player.NetworkSongsActivity;
import com.example.mp3player.ScanMusicActivity;
import com.example.mp3player.SongInfoActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class DownloadPic extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.v("128", "LrcActivity.inLrcActivity.getCurrentActivity()    -->" + LrcActivity.inLrcActivity.getCurrentActivity());

		boolean isNotify = intent.getBooleanExtra("isNofity", false);

		if(isNotify){
			
			String packageName = this.getPackageName();
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			if (cn.getClassName().contains(packageName))
	        {
				 
	        }else{
	        	//如果界面未关，则关闭
//		        	if(!PublicVariable.isMainActivityId1){
//		        		NetworkSongsActivity.insActivity.onDestroy();
//		        		PublicVariable.isMainActivity = true;
//		        		PublicVariable.isFromNetToDown = true;
//		        	}
	        	 
	        	// 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程  
//		             List<ActivityManager.RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses(); 
//		        	 for(ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList){
//		        		
//		        		 if(appProcessInfo.processName.contains(getPackageName())){
////		        			 int id = appProcessInfo.;
//		        		 }
//		        	 }
	        	 
	        	 /*
	        	　//默认的跳转类型,会重新创建一个新的Activity
	        	　intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

	        	　　//如果activity在task存在，拿到最顶端,不会启动新的Activity
	        	　intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

	        	　　//如果activity在task存在，将Activity之上的所有Activity结束掉
	        	　intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	        	　　//如果Activity已经运行到了Task，再次跳转不会启动新的Activity
	        	　 intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        	*/ 
//		        	am.moveTaskToFront(taskId, flags);
	        	
	        	Intent intent2 = new Intent();
	        	switch (LrcActivity.inLrcActivity.getCurrentActivity()) {
				case 0:
					intent2.setClass(getApplicationContext(), MainActivity.class);
					break;
				case 1:
					intent2.setClass(getApplicationContext(), SongInfoActivity.class);
					break;
				case 2:
					intent2.setClass(getApplicationContext(), NetworkSongsActivity.class);
					break;
				case 3:
					intent2.setClass(getApplicationContext(), ScanMusicActivity.class);
					break;
				default:
					intent2.setClass(getApplicationContext(), MainActivity.class);
					break;
				}
	        	intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP 
	        			| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK);
//		        	intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP 
//		    				|Intent.FLAG_ACTIVITY_NEW_TASK);
           	 	getApplication().startActivity(intent2);
	        }             
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	 /*
     * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
     * 这里的path是图片的地址
     */
    public Uri getImageURI(String path, File cache) throws Exception {
        String name = getMD5(path) + path.substring(path.lastIndexOf("."));
        File file = new File(cache, name);
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            // 从网络上获取图片
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {
 
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
                // 返回一个URI对象
                return Uri.fromFile(file);
            }
        }
        return null;
    }

    
//    public class MD5 {
    	 
        public String getMD5(String content) {
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(content.getBytes());
                return getHashString(digest);
                 
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        }
         
        private String getHashString(MessageDigest digest) {
            StringBuilder builder = new StringBuilder();
            for (byte b : digest.digest()) {
                builder.append(Integer.toHexString((b >> 4) & 0xf));
                builder.append(Integer.toHexString(b & 0xf));
            }
            return builder.toString();
        }
        
        /**
         * 根据包名判断activity是否存在
         * @param packageName
         * @return
         */
        public boolean checkApplication(String packageName) {
			  if (packageName == null || "".equals(packageName)){
			      return false;
			  }
			  try {
			      ApplicationInfo info = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			      
			      return true;
			  } catch (NameNotFoundException e) {
			      return false;
			  }
			}
//    }
}
