package com.goodguide.android.app;

import java.util.ArrayList;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.goodguide.android.app.R;
import com.goodguide.android.shared.Constants;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.LayoutInflater.Factory;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

public class SearchGroup extends ActivityGroup {

        private static final String TAG = "SearchGroup";

	// Keep this in a static variable to make it accessible for all the nesten activities, lets them manipulate the view
	public static SearchGroup group;

        // Need to keep track of the history if you want the back-button to work properly, don't use this if your activities requires a lot of memory.
	private ArrayList<View> history;
	private GoogleAnalyticsTracker tracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      this.history = new ArrayList<View>();
	      group = this;

	      //setup google analytics
	      tracker = GoogleAnalyticsTracker.getInstance();

		    // tracker started with a dispatch interval (in seconds).
		    tracker.start(Constants.GOOGLE_ANALYTICS_PROFILE_UA, Constants.GOOGLE_ANALYTICS_DISPATCH_INTERVAL, this);
	      
              // Start the root activity withing the group and get its view
	      View view = getLocalActivityManager().startActivity("Search", new
	    	      							Intent(this,Search.class)
	    	      							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
	    	                                .getDecorView();

	      view.setTag(Constants.PAGEVIEW_SEARCH_TOP_LEVEL);
	      
          // Replace the view of this ActivityGroup
	      replaceViewCompletely(view);
	   }

	public void replaceView(View v) {
                // Adds the old one to history
		history.add(v);
                // Changes this Groups View to the new View.
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		v.startAnimation(animation);
		setContentView(v);
		trackPageView((String)v.getTag());
	}
	
	public void replaceViewCompletely(View v) {
        // Changes this Groups View to the new View.
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		v.startAnimation(animation);
		setContentView(v);
		trackPageView((String)v.getTag());
	}

//	public void back() {
//		
//		Log.v("", "history is:"+history.size());
//		
//		if(history.size() > 1) {
//			history.remove(history.size()-1);
//			View v = history.get(history.size()-1);
//			Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
//			v.startAnimation(animation);
//			setContentView(v);
//			trackPageView((String)v.getTag());
//		}else {
//			GoodGuide.browseButtonClicked();
//		}
//	}
  
//	     public void onBackPressed() {  
//	         GoodGuide.browseButtonClicked();  
//	         return;  
//	    } 
	     
	     public void trackPageView(String pageView) {
	 		tracker.trackPageView(pageView);
	 		Log.v(TAG,"Tracking Page View:"+pageView);
	 	}
}
