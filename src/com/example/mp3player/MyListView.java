package com.example.mp3player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
 
/**
 * 自定义Listview，可以自己测量每个自item的高度 适用于在ScrollView中嵌套
 * 
 * @author liubing
 * 
 */
public class MyListView extends ListView {
 
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public MyListView(Context context) {
        super(context);
    }
 
    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //根据模式计算每个child的高度和宽度
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
    
	//解决ScrollView与listView冲突的问题
    public void fixListView(ListView listView) {  
    	SimpleAdapter  listAdapter = (SimpleAdapter) listView.getAdapter();   
        if (listAdapter == null) {  
            return;  
        }  
     
        int totalHeight = 0;  
        for (int i = 0; i < listAdapter.getCount(); i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount())) + 20;  
        listView.setLayoutParams(params);  
    }
}