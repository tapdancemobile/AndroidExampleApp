/***
	Copyright (c) 2008-2009 CommonsWare, LLC
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.goodguide.android.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.goodguide.android.app.R;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.DrawableManager;
import com.goodguide.android.utilities.ObjectSerializer;
import com.goodguide.android.value.AdInfo;
import com.goodguide.android.value.GenericResult;
import com.goodguide.android.value.Product;
import com.urbanairship.push.AirMail;


public class GenericListActivity extends ListActivity {

	
	 protected static final String TAG = "GenericListActivity";
	 AlertDialog networkDialog;
	 GoogleAnalyticsTracker tracker;
	 DrawableManager iconDownloadManager;
	 
		private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressDialog.dismiss();
			}
		};
		
		private ProgressDialog progressDialog;

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
        if(Constants.showLog) Log.v(TAG,"Saving Last Activity as:"+lastActivityID);
	}
	
	public void saveCategoryId(int categoryId) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.APP_CATEGORY, categoryId);
        editor.commit();
	}
	
	public void saveTitle(String title) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.TITLE, title);
        editor.commit();
	}
	
	public void setInitialized(boolean initialized) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Constants.INITIALIZED, initialized);
        editor.commit();
	}
	
	public void saveString(String value, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
        editor.commit();
	}
	
	public void saveList(List<Product> list, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        try {
			editor.putString(key, ObjectSerializer.serialize((ArrayList<Product>)list));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        editor.commit();
	}
	
	public void removeFavorite(int favoriteIndex) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        ArrayList<GenericResult> favorites = Constants.getFavorites();
        
        favorites.remove(favoriteIndex);
		
        Constants.setFavorites(favorites);
		try {
			editor.putString(Constants.FAVORITES, ObjectSerializer.serialize(favorites));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        editor.commit();
	}

	 View getAdView(final AdInfo adInfo) {
		 
		 RelativeLayout relativeLayout = new RelativeLayout(this);
		 
		ImageView view = new ImageView(this);
		DrawableManager drawableManager = new DrawableManager();
		view.setImageDrawable(drawableManager.fetchDrawable(adInfo.getImage()));
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		
		relativeLayout.addView(view, lp);
		
		relativeLayout.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Log.v("test", "adInfo:"+adInfo.toString());
				openWeb(adInfo.Url);
			}
		});
		return relativeLayout;
	}
	
	public void openWeb(String url) {
		Uri uri = Uri.parse( url );
		startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
	}
	
	public void trackPageView(String pageView) {
		tracker.trackPageView(pageView);
		Log.v(TAG,"Tracking Page View:"+pageView);
	}
	
	public void trackEvent(String category, String action, String label, int value) {
		tracker.trackEvent(category, action, label, value);
		Log.v(TAG,"Tracking Event : category:"+category+", action:"+action+", label:"+label+", value:"+value);
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
	
	protected void showProgressDialog(Context ctx) {
	// activity indicator
	// set busy dialog
	progressDialog = ProgressDialog.show(ctx,
			"Please wait...", "", true);
	}
	
	public Bitmap getIcon(String iconName, int square) {
		int resID = getResources().getIdentifier(iconName, "drawable", "com.goodguide.android.app");
		
		// load the original BitMap
        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), resID==0?R.drawable.image_not_available:resID);
      
	    if(square != 0)   
	    {
        	int width = bitmapOrg.getWidth();
	        int height = bitmapOrg.getHeight();
			int staticWidth = square;
			
			
			int newWidth = width>staticWidth?staticWidth:width; 	
			int newHeight = (newWidth * height)/width;
	        
	        // calculate the scale - in this case = 0.4f
	        float scaleWidth = ((float) newWidth) / width;
	        float scaleHeight = ((float) newHeight) / height;
	        
	        // create a matrix for the manipulation
	        Matrix matrix = new Matrix();
	        // resize the bit map
	        matrix.postScale(scaleWidth, scaleHeight);
	
	        // recreate the new Bitmap
	        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapOrg, newWidth, newHeight, false);
			return resizedBitmap;
        } else {
        	return bitmapOrg;
        }
	}
	
	public int getIcon(String iconName) {
		return getResources().getIdentifier(iconName, "drawable", "com.goodguide.android.app");
	}
	
	public Bitmap getIconDrawable(Bitmap image) {
		
		//get the scale
		int originalWidth = image.getWidth();
		int originalHeight = image.getHeight();
		int staticWidth = 75;
		
		
		int newWidth = originalWidth>staticWidth?staticWidth:originalWidth; 	
		int newHeight = (newWidth * originalHeight)/originalWidth;
	
		Log.v("image resizing:", "original:("+originalWidth+","+originalHeight+")" +  "new:("+newWidth+","+newHeight+")");
	
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(image, newWidth, newHeight, false);
		return resizedBitmap;
	}
	
	public Drawable getDrawableFromURL(String iconURL) {
		return iconDownloadManager.fetchDrawable(iconURL);
	}
	
	@Override
	  protected void onDestroy() {
	    super.onDestroy();
	    // Stop the tracker when it is no longer needed.
	    tracker.stop();
	  }
}