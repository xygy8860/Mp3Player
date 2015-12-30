package com.example.mp3player.broadcast;

import com.example.mp3player.DowmloadList;
import com.example.mp3player.MainActivity;
import com.example.mp3player.ScanMusicActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScanSdReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 String action = intent.getAction();  
         if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action))  
         {  
             //开始扫描，把你的代码放这里  
         }  
         else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action))  
         {  
             //扫描结束，把你的代码放这里  
//        	 MainActivity.instance.scanner();
        	 Log.v("126", "startScanMusicActivity");
        	 DowmloadList.inDowmloadList.startScanMusicActivity();
         }  
		
	}
	

}
