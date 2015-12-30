package com.example.mp3player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
 
/**
 * �Զ���Listview�������Լ�����ÿ����item�ĸ߶� ��������ScrollView��Ƕ��
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
        //����ģʽ����ÿ��child�ĸ߶ȺͿ��
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
    
	//���ScrollView��listView��ͻ������
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