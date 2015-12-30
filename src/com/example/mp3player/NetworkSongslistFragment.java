package com.example.mp3player;


import java.util.ArrayList;
import java.util.HashMap;

import me.maxwin.view.XListView.IXListViewListener;

import com.example.mp3player.db.DatabaseHelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class NetworkSongslistFragment extends Fragment{

	PublicVariable publicVariable = new PublicVariable();
//	ListView lv = getListView();
	View v;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.network_list, container, false);
		
		return v;
	}
	

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		
	}
	
    
	public static boolean isStartNetworkSongListFragment = false;
	@Override
	public void onResume() {
		
	
		super.onResume();
	}
	
	
	
}



