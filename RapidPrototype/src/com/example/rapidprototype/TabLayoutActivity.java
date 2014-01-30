package com.example.rapidprototype;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TabLayoutActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_layout);
		
		TabHost tabhost = getTabHost();
		
		TabSpec currentspec = tabhost.newTabSpec( "Current" );
		
		 currentspec.setIndicator("Current", getResources().getDrawable(R.drawable.current));
	     Intent currentIntent = new Intent(this, CurrentActivity.class);
	     currentspec.setContent(currentIntent);
	     
	     TabSpec historyspec = tabhost.newTabSpec("History");        
	     historyspec.setIndicator("History", getResources().getDrawable(R.drawable.history));
	     Intent historyIntent = new Intent(this, HistoryActivity.class);
	     historyspec.setContent(historyIntent);
	     
	     TabSpec settingsspec = tabhost.newTabSpec("Settings");        
	     settingsspec.setIndicator("Settings", getResources().getDrawable(R.drawable.settings));
	     Intent settingsIntent = new Intent(this, SettingsActivity.class);
	     settingsspec.setContent(settingsIntent);
	     
	     tabhost.addTab(currentspec); 
	     tabhost.addTab(historyspec); 
	     tabhost.addTab(settingsspec);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tab_layout, menu);
		return true;
	}

}
