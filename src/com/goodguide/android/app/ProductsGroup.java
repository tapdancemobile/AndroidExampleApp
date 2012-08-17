package com.goodguide.android.app;

import java.util.ArrayList;
import java.util.Iterator;

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

public class ProductsGroup extends ActivityGroup {

        private static final String TAG = "ProductsGroup";

	// Keep this in a static variable to make it accessible for all the nesten activities, lets them manipulate the view
	public static ProductsGroup group;
	public static Context contextForMenuAndDialogs;

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
	      View view = getLocalActivityManager().startActivity("Verticals", new
	    	      							Intent(this,Verticals.class)
	    	      							.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
	    	                                .getDecorView();

	      view.setTag(Constants.PAGEVIEW_BROWSE_TOP_LEVEL);
              // Replace the view of this ActivityGroup
	      replaceView(view);
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

	public void home() {
		
		int historySize = history.size();
		if(historySize > 1) {
			View rootView = history.get(0);
			history.clear();
			history.add(rootView);
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
			rootView.startAnimation(animation);
			setContentView(rootView);
			trackPageView((String)rootView.getTag());
		}
	}
	
	public void back() {
		
		Log.v("", "history is:"+history.size());
		
		if(history.size() > 1) {
			history.remove(history.size()-1);
			View v = history.get(history.size()-1);
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
			v.startAnimation(animation);
			setContentView(v);
			trackPageView((String)v.getTag());
		}else {
			finish();
		}
	}
  
	     public void onBackPressed() {  
	         ProductsGroup.group.back();  
	         return;  
	    } 
	     
	     public void trackPageView(String pageView) {
	 		tracker.trackPageView(pageView);
	 		Log.v(TAG,"Tracking Page View:"+pageView);
	 	}

	 	/* Creates the menu items */
	 	public boolean onCreateOptionsMenu(Menu menu) {
	 		
	 	    MenuInflater inflater = new MenuInflater(getApplicationContext());
	 	    inflater.inflate(R.layout.menu, menu);
	 	    setMenuBackground();
	 	    return true;
	 	}
	 	

	 	/*IconMenuItemView is the class that creates and controls the options menu 
	 	 * which is derived from basic View class. So We can use a LayoutInflater 
	 	 * object to create a view and apply the background.
	 	 */
	 	protected void setMenuBackground(){

	 	  if(Constants.showLog) Log.d(TAG, "Enterting setMenuBackGround");
	 	    
	 	    if(getLayoutInflater().getFactory() == null) {
	 	    	 Log.d(TAG, "Enterting setMenuBackGround");
	 		    getLayoutInflater().setFactory( new Factory() {
	 		
	 				public View onCreateView(String name, Context context,
	 						AttributeSet attrs) {
	 		
	 		            if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {
	 		
	 		            	 Log.d(TAG, "Enterting onCreateView");
	 		            	
	 		                try { // Ask our inflater to create the view
	 		                    LayoutInflater f = getLayoutInflater();
	 		                    final View view = f.createView( name, null, attrs );
	 		                    /* 
	 		                     * The background gets refreshed each time a new item is added the options menu. 
	 		                     * So each time Android applies the default background we need to set our own 
	 		                     * background. This is done using a thread giving the background change as runnable
	 		                     * object
	 		                     */
	 		                    new Handler().post( new Runnable() {
	 		                        public void run () {
	 		                            view.setBackgroundResource( R.color.titlebar);
	 		                        }
	 		                    } );
	 		                    return view;
	 		                }
	 		                catch ( InflateException e ) {}
	 		                catch ( ClassNotFoundException e ) {}
	 		            }
	 		            return null;
	 				}
	 		    });
	 	    }
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
}
