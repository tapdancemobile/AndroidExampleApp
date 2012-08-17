package com.goodguide.android.app;


import com.goodguide.android.app.R;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.GoodGuideDataSource;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class GoodGuide extends GenericTabActivity {
	public static TabHost tabHost;
	public static ImageButton browseButton = null;
	public static ImageButton scanButton = null;
	public static ImageButton searchButton = null;
	public static RelativeLayout topBar = null;
	
	public static void browseButtonClicked(){
		tabHost.setCurrentTab(0);
	}
	
	public static void scanButtonClicked(){
		tabHost.setCurrentTab(1);
	}
	
	public static void searchButtonClicked(){
		tabHost.setCurrentTab(2);
	}

	
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    trackPageView(Constants.PAGEVIEW_APP_LAUNCHED);
	    
	    setContentView(R.layout.main);
	    
	   browseButton = (ImageButton)findViewById(R.id.browsebutton);
	   scanButton = (ImageButton)findViewById(R.id.scanbutton);
	   searchButton = (ImageButton)findViewById(R.id.searchbutton);
	   topBar = (RelativeLayout)findViewById(R.id.TitleBar);
	  
	   topBar.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ProductsGroup.group.home();
				tabHost.setCurrentTab(0);
			}
		}); 
	   
	    browseButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {		  	  
				tabHost.setCurrentTab(0);
			}
		});
	    
	    
	    
	    scanButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {		  	  
				tabHost.setCurrentTab(1);
			}
		}); 


	    searchButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {	
				tabHost.setCurrentTab(2);
			}
			
			protected void showDialog(Context ctx, String string, String string2) {
				AlertDialog.Builder builderW = new AlertDialog.Builder(ctx);
		        builderW.setTitle(string);
		        builderW.setMessage(string2)
				       .setCancelable(false)
				       .setNegativeButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       }).create().show();
				
			}
		});

		tabHost = (TabHost) findViewById(android.R.id.tabhost);	
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			   public void onTabChanged(String arg0) {
			    switch (tabHost.getCurrentTab()) {
				case 0:
					trackPageView(Constants.PAGEVIEW_BROWSE_TOP_LEVEL);
					browseButton.setImageResource(R.drawable.browseon);
					scanButton.setImageResource(R.drawable.scanoff);
					searchButton.setImageResource(R.drawable.searchoff);
					break;
				case 1:
					trackPageView(Constants.PAGEVIEW_SCAN_HOME);
					scanButton.setImageResource(R.drawable.scanon);
					browseButton.setImageResource(R.drawable.browseoff);
					searchButton.setImageResource(R.drawable.searchoff);
					break;
				case 2:
					trackPageView(Constants.PAGEVIEW_SEARCH_TOP_LEVEL);
					scanButton.setImageResource(R.drawable.scanoff);
					browseButton.setImageResource(R.drawable.browseoff);
					searchButton.setImageResource(R.drawable.searchon);
					
					if((Constants.getRecentSearchProductResults() == null || Constants.getRecentSearchProductResults().size() <= 0)
						|| (Constants.getRecentSearchBrandResults() == null && Constants.getRecentSearchBrandResults().size() <= 0))
						onSearchRequested();
					
					break;
				default:
					break;
				}
			   }     
			        });
		
		Intent intent = new Intent().setClass(this, ProductsGroup.class);
		setupTab(new TextView(this), "Products",R.drawable.tabbariconhome, intent);
		
		intent = new Intent().setClass(this, Scan.class);
		setupTab(new TextView(this), "Scan",R.drawable.tabbariconscan, intent);
		
		intent = new Intent().setClass(this, SearchGroup.class);
		setupTab(new TextView(this), "Search",R.drawable.tabbariconscan, intent);
	    	
	    switch (Constants.lastActivity) {
		case 0:
			browseButtonClicked();
			break;
		case 1:
			scanButtonClicked();
			break;
		case 2:
			searchButtonClicked();
			break;
		default:
			break;
		}
	    
	}
	
	private void setupTab(final View view, final String tag, final int tabIcon, final Intent intent) {
		View tabview = createTabView(tabHost.getContext(), tag, tabIcon);
	    TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
	    tabHost.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text, final int tabIcon) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
	
    
	@Override
    protected void onRestart() {
		super.onRestart();
		trackPageView(Constants.PAGEVIEW_APP_FOREGROUND);		
	}
	
	@Override
	public void onStop() {
		super.onStop();
		trackPageView(Constants.PAGEVIEW_APP_BACKGROUND);
		saveLastActivity(getTabHost().getCurrentTab());
		
		//close db
		if(Constants.ds != null)
			Constants.ds.close();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		trackPageView(Constants.PAGEVIEW_APP_CLOSED);
	}
}