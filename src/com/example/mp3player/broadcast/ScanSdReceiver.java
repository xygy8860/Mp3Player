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
             //��ʼɨ�裬����Ĵ��������  
         }  
         else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action))  
         {  
             //ɨ�����������Ĵ��������  
//        	 MainActivity.instance.scanner();
        	 Log.v("126", "startScanMusicActivity");
        	 DowmloadList.inDowmloadList.startScanMusicActivity();
         }  
		
	}
	

}
