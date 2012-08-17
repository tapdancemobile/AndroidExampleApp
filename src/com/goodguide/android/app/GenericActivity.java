package com.goodguide.android.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.DrawableManager;
import com.goodguide.android.utilities.ObjectSerializer;
import com.goodguide.android.value.AdInfo;
import com.goodguide.android.value.GenericResult;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.urbanairship.push.AirMail;

public class GenericActivity extends Activity{

	
	private static final String TAG = "GenericActivity";
	private AlertDialog networkDialog;
    GoogleAnalyticsTracker tracker;
    DrawableManager iconDownloadManager;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		tracker = GoogleAnalyticsTracker.getInstance();
		iconDownloadManager = new DrawableManager();

	    // tracker started with a dispatch interval (in seconds).
	    tracker.start(Constants.GOOGLE_ANALYTICS_PROFILE_UA, Constants.GOOGLE_ANALYTICS_DISPATCH_INTERVAL, this);

	}
	
	
	public void saveLastActivity(int lastActivityID) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.LAST_ACTIVITY, lastActivityID);
        editor.commit();
	}
	
	public void addFavorite(GenericResult favorite) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        ArrayList<GenericResult> favorites = Constants.getFavorites();
        boolean exists = false; 
        
        Iterator<GenericResult> iter = favorites.iterator();
        while(iter.hasNext()) {
        	GenericResult info = iter.next();
        	if(info.Title.equalsIgnoreCase(favorite.Title)){
        		exists = true;
        	}
        }
        
        if(!exists)
		favorites.add(favorite);
		
        Constants.setFavorites(favorites);
		try {
			editor.putString(Constants.FAVORITES, ObjectSerializer.serialize(favorites));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        editor.commit();
	}
	
	public void saveEntry(GenericResult entry) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        
		try {
			editor.putString(Constants.ENTRY_INFO, ObjectSerializer.serialize(entry));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        editor.commit();
	}
	
	public void saveList(ArrayList<GenericResult> list, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        try {
			editor.putString(key, ObjectSerializer.serialize(list));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        editor.commit();
	}
	

	public void openWeb(String url) {
		Uri uri = Uri.parse( url );
		startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
	}
	
	public void trackPageView(String pageView) {
		tracker.trackPageView(pageView);
		Log.v(TAG,"***\nTracking Page View:"+pageView+"\n***");
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
	
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    // Stop the tracker when it is no longer needed.
	    tracker.stop();
	  }
	
	public Drawable getDrawableFromURL(String iconURL) {
		return iconDownloadManager.fetchDrawable(iconURL);
	}
	
	public void fetchDrawableFromURLWithThread(String iconURL, ImageView imageView) {
		iconDownloadManager.fetchDrawableOnThread(iconURL, imageView);
	}

}
