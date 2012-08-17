package com.goodguide.android.app;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.GoodGuideDataSource;
import com.goodguide.android.utilities.MyLocation;
import com.goodguide.android.utilities.ObjectSerializer;
import com.goodguide.android.utilities.MyLocation.LocationResult;
import com.goodguide.android.value.GenericResult;
import com.goodguide.android.value.Product;

public class Splash extends GenericActivity {

	protected static final String TAG = "Splash";
	private static final long SPLASH_DISPLAY_TIME = 2000;
	private Handler handler;
	protected Handler handler2;
	private GenericResult entry;
	private ArrayList<GenericResult> category;
	private int categoryId;
	private String title;
	GoodGuideDataSource ds;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splash);
	    
	  //get current location if we can
	    getLocation();
	    
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {		    	
		    	
			    Intent mIntent = null;
			    mIntent = new Intent(Splash.this, GoodGuide.class);
				startActivityForResult(mIntent, 0);	
				Splash.this.finish();
			}
		};
		
		 /* Create a new handler with which to start the main activity
	    and close this splash activity after SPLASH_DISPLAY_TIME has
	    elapsed. */
		
		//a quick and dirty way to disable app 
//		String DATE_FORMAT = "yyyy-MM-dd";
//	    java.text.SimpleDateFormat sdf = 
//	          new java.text.SimpleDateFormat(DATE_FORMAT);
//	    Calendar now = Calendar.getInstance(); 
//	    Calendar expiration = Calendar.getInstance(); 
//	    expiration.set(2011, 2 , 17); 
//		
//		if(!now.after(expiration)) {
//			Log.v("", "before expiration");
			
	new Handler().postDelayed(new Runnable() {
		public void run() {
			
			/* Create an intent that will start the main activity. */
			Intent mainIntent = new Intent(Splash.this,
				GoodGuide.class);		
			
			initialize();
			
			Splash.this.startActivity(mainIntent);
				
			/* Finish splash activity so user cant go back to it. */
			Splash.this.finish();
			
			/* Apply our splash exit (fade out) and main
			   entry (fade in) animation transitions. */
			try {
				
			    Method method = Activity.class.getMethod("overridePendingTransition", new Class[]{int.class, int.class});
			    method.invoke(Splash.this, R.anim.mainfadein, R.anim.splashfadeout);
			} catch (Exception e) {
			    // Can't change animation, so do nothing
			}

		}
	}, SPLASH_DISPLAY_TIME);
//		} else {
//			showDialog(Splash.this, "Expired", "This app is expired...");
//		}
}

	
	public boolean isOnline() {
		 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo nInfo = cm.getActiveNetworkInfo();
		 return nInfo==null?false:nInfo.isConnectedOrConnecting();
		}


	protected void initialize() {
    	
    	// Restore user selections from android generated file
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        
        Constants.lastActivity = settings.getInt(Constants.LAST_ACTIVITY, 0);
        Constants.setInitialized(settings.getBoolean(Constants.INITIALIZED, false));
        int versionCodeFromSettings = settings.getInt(Constants.VERSION_CODE_CACHED, 0);  
        int versionCodeFromManifest = 0;
        
        List<Product> recentlyScanned;
        PackageManager pm = getPackageManager();
        try {
            //---get the package info---
            PackageInfo pi =  pm.getPackageInfo("com.goodguide.android.app", 0);
            //---display the versioncode---
            versionCodeFromManifest = pi.versionCode;
            
          //track whether it is an upgrade or new installations
            if(versionCodeFromSettings == 0)
            	trackPageView(Constants.PAGEVIEW_NEW_INSTALLATION);
            else if(versionCodeFromSettings != 0 && versionCodeFromSettings != versionCodeFromManifest) {
            	trackPageView(Constants.PAGEVIEW_UPGRADE);
            	Constants.setInitialized(false);
            	Constants.versionCode = pi.versionCode;
            }
            
            
            //see if recently scanned from previous session
            recentlyScanned = (ArrayList<Product>) ObjectSerializer.deserialize(settings.getString(Constants.RECENTLY_SCANNED, ObjectSerializer.serialize(new ArrayList<Product>())));
            Constants.setRecentlyScanned(recentlyScanned);
            
          //see if recent searches from previous session
            Constants.setRecentSearchProductResults((ArrayList<Product>) ObjectSerializer.deserialize(settings.getString(Constants.RECENT_SEARCH_PRODUCTS, null)));
            Constants.setRecentSearchBrandResults((ArrayList<Product>) ObjectSerializer.deserialize(settings.getString(Constants.RECENT_SEARCH_BRANDS, null)));
            Constants.setRecentQuery(settings.getString(Constants.RECENT_QUERY, null));
            
        } catch (Exception e) {
        	 Log.v(TAG, "Error: " + e.getMessage());
        }

        
      //set resolution of device
		Constants.setDeviceResolution(getResolution());
		
	
	   //set DisplayMetrics
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Constants.metrics = metrics;
		
	    //see if we have connectivity
	    Constants.connected = isOnline();
	    
        if(Constants.isInitialized())
        	initializeDBIfDBExists();
	    
      if(Constants.showLog) Log.v(TAG, "/**** GoodGuide Initialization Summary ****/");
      if(Constants.showLog) Log.v(TAG, "VersionCode from Manifest: " +versionCodeFromManifest); 
      if(Constants.showLog) Log.v(TAG, "VersionCode from Settings: " +versionCodeFromSettings);
      if(Constants.showLog) Log.v(TAG, "initialized:"+Constants.isInitialized());
      if(Constants.showLog) Log.v(TAG, "lastActivity:"+Constants.lastActivity);
      if(Constants.showLog) Log.v(TAG, "connected:"+isOnline());
      if(Constants.showLog) Log.v(TAG, "device resolution:"+Constants.getDeviceResolution());
      if(Constants.showLog) Log.v(TAG, "/******************************************/");
	}
	
	private void initializeDBIfDBExists() {
		Constants.ds = new GoodGuideDataSource(this);
	}
	
	private void getLocation() {
		LocationResult locationResult = new LocationResult(){
			@Override
			public void gotLocation(Location location) {
			
				 
		if(location != null) {			
				//set location name
				location.setProvider("My Location");
				if(Constants.showLog) Log.v(TAG, "**************Location:"+location.getProvider()+"lon:"+location.getLongitude()+" lat:"+location.getLatitude());
				Constants.location = location;
				} 
			}
		};
   
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);
		//stop get location
	}

    private int getResolution(){
    	DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        boolean is15 = false;
        try {
        	is15 = dm.getClass().getDeclaredField("densityDpi")==null?true:false;
        	if(is15) {
        		return Constants.DENSITY_MEDIUM;
        	} else {
        		Field densityDpi = dm.getClass().getDeclaredField("densityDpi");
        		return densityDpi.getInt(dm);
        	}
        } catch(Exception e) {
        	return Constants.DENSITY_MEDIUM;
        }
    }

    
}