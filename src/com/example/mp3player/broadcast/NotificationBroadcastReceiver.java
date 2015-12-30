package com.example.mp3player.broadcast;

import com.example.mp3player.MainActivity;
import com.example.mp3player.PublicVariable;
import com.example.mp3player.service.PlayerService;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(PublicVariable.ACTION_BTN)) {
            int btn_id = intent.getIntExtra(PublicVariable.INTENT_NAME, 0);
            NotificationManager notificationManager = (NotificationManager) 
            		MainActivity.instance.getSystemService(Context.NOTIFICATION_SERVICE);
            switch (btn_id) {
                //清除通知
                case PublicVariable.INTENT_BTN_CLEAR:
                	PlayerService.inPlayservice.unregeisterReceiver();
                	
                	PlayerService.inPlayservice.stopForeground(true); 
                	 
                    break;
            }
        }
    }
}
