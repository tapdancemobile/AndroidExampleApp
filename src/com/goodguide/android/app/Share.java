package com.goodguide.android.app;

import com.goodguide.android.app.R;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class Share extends GenericActivity{
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.share);
		
		Bundle extras = getIntent().getExtras();
		
        //set title of screen
//        title = extras.getString(Constants.HEADER_TEXT);
//        subTitle = extras.getString(Constants.SUB_HEADER_TEXT);
//        ingredientsToAvoid = extras.getString(Constants.INGREDIENTS_TO_AVOID);
//        categoryId = extras.getInt(Constants.CATEGORY_ID);
//        showAll = extras.getBoolean(Constants.SHOW_ALL);
     
        //wrapper method in super class in order to log the page view
//        trackPageView(Constants.PAGEVIEW_BROWSE_CATEGORY+title);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		Log.v("", "back pressed");
	    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && ProductsGroup.group != null) {
	        ProductsGroup.group.back();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

}
