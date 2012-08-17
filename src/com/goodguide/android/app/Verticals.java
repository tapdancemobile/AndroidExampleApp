package com.goodguide.android.app;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.LayoutInflater.Factory;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonsware.cwac.thumbnail.ThumbnailAdapter;
import com.goodguide.android.app.R;
import com.goodguide.android.business.GoodGuideBusiness;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.shared.GoodGuideEntry;
import com.goodguide.android.utilities.GoodGuideDataSource;
import com.goodguide.android.utilities.MergeAdapter;
import com.goodguide.android.value.GenericResult;

public class Verticals extends GenericListActivity {
	
	//both of these are used to display the activity indicator
	//for initializing the db upon the user's first interaction
	private ProgressDialog myProgressDialog;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			myProgressDialog.dismiss();
			
			//only set new version code if the new db was processed
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Verticals.this);
	        SharedPreferences.Editor editor = settings.edit();
			editor.putInt(Constants.VERSION_CODE_CACHED, Constants.versionCode);
        	editor.commit();
		}
	};
	private MergeAdapter adapter;
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genericlist);   
        
      //set context for dialog, search, menu
		ProductsGroup.contextForMenuAndDialogs = Verticals.this;
        
        adapter = new MergeAdapter();
        
		adapter.addView(getRowView("babiesandkids",Constants.BABIES_AND_KIDS_VERTICAL_TYPE, Constants.ALL_BABY_AND_KIDS_COUNT+"+ Products"));
		adapter.addView(getRowView("food",Constants.FOOD_VERTICAL_TYPE, Constants.ALL_FOOD_COUNT+"+ Products"));
		adapter.addView(getRowView("household",Constants.HOUSEHOLD_CLEANERS_VERTICAL_TYPE, Constants.ALL_HOUSEHOLD_COUNT+"+ Products"));
		adapter.addView(getRowView("personalcare",Constants.PERSONAL_CARE_VERTICAL_TYPE, Constants.ALL_PERSONALCARE_COUNT+"+ Products"));
		adapter.addView(getRowView("petfood",Constants.PET_FOOD_VERTICAL_TYPE, Constants.ALL_PETFOOD_COUNT+"+ Products"));
		adapter.addView(getRowView("appliances",Constants.APPLIANCES_VERTICAL_TYPE, Constants.ALL_APPLIANCES_COUNT+"+ Products"));
		adapter.addView(getRowView("apparel",Constants.APPAREL_VERTICAL_TYPE, Constants.ALL_APPAREL_COUNT+"+ Brands"));
		adapter.addView(getRowView("electronics",Constants.ELECTRONICS_VERTICAL_TYPE, Constants.ALL_ELECTRONICS_COUNT+"+ Products"));
		adapter.addView(getRowView("lighting",Constants.LIGHTING_VERTICAL_TYPE, Constants.ALL_LIGHTING_COUNT+"+ Products"));
		adapter.addView(getRowView("allproducts",Constants.PRODUCTS_VERTICAL_TYPE, Constants.ALL_PRODUCTS_COUNT+"+ Products"));
		
		setListAdapter(adapter);
		
	//only initialize if has not been done	
	if(Constants.isInitialized() == false) {
		
		Log.v("**\nIn Verticals: initialized:**", Constants.isInitialized()+"");
		
		// activity indicator
		// set busy dialog
		myProgressDialog = new ProgressDialog(getParent()){
            @Override
            public boolean onSearchRequested() {
                    return false;
            }
		}; 
		myProgressDialog.setTitle("Completing Installation");
		myProgressDialog.setMessage("Please be patient while we load our database of over "+Constants.ALL_PRODUCTS_COUNT+" products.");
		myProgressDialog.setIndeterminate(true);
		myProgressDialog.setCancelable(false);
		myProgressDialog.show();
		
			new Thread(new Runnable() {
				public void run() {
					initializeDB();
					handler.sendEmptyMessage(0);
				}
			}).start();
		}
	
    }
 

	private void initializeDB() {
		//start db copy if need be
		Constants.ds = new GoodGuideDataSource(this);
		 setInitialized(Constants.ds.testDBImportWorked());
	}
    

    
	private View getRowView(String icon, final String text, final String subText) {
		
		RelativeLayout view = new RelativeLayout(this);
		view.setBackgroundResource(R.anim.cellbg_states);
		view.setPadding(0, 0, 10, 0);
		
		
		ImageView image = new ImageView(this);
		image.setId(1);
		image.setImageResource(getIcon(icon));
		image.setPadding(0, 5, 0, 5);
		view.addView(image);
		
		TextView textView = new TextView(getApplicationContext());
		textView.setId(2);
		textView.setText(text);
		textView.setTextSize(20);
		textView.setTextColor(getResources().getColor(R.color.rowtitle));
		textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setPadding(10, 20, 0, 0);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.FILL_PARENT);
		lp.addRule(RelativeLayout.RIGHT_OF, image.getId());
		
		view.addView(textView, lp);
		
		TextView textView2 = new TextView(getApplicationContext());
		textView2.setId(3);
		textView2.setText(subText);
		textView2.setTextSize(14);
		textView2.setTextColor(getResources().getColor(R.color.rowtitle));
		textView2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		textView2.setGravity(Gravity.CENTER_VERTICAL);
		textView2.setPadding(10, 0, 0, 0);
		
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.FILL_PARENT);
		lp2.addRule(RelativeLayout.BELOW, textView.getId());
		lp2.addRule(RelativeLayout.RIGHT_OF, image.getId());
		
		view.addView(textView2, lp2);
		
		view.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {				

		        Intent i = new Intent(Verticals.this, SubProducts.class);
		        i.putExtra(Constants.HEADER_TEXT, text);

		        // Create the view using FirstGroup's LocalActivityManager
		        View view = ProductsGroup.group.getLocalActivityManager()
		        .startActivity("subproducts", i
		        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
		        .getDecorView();

		        view.setTag(Constants.PAGEVIEW_BROWSE_CATEGORY+text);
		        
		        // Again, replace the view
		        ProductsGroup.group.replaceView(view);
			}
		});
		
		return view;
	}


	
	public int getIcon(String iconName) {
		int resID = getResources().getIdentifier(iconName, "drawable", "com.goodguide.android.app");
		return resID;
	}
	
	//catch this in case we are not connected
	//search is only network
	//need to call getParent because we are in a tab
	@Override
	public boolean onSearchRequested(){
		if(Constants.connected) {
	            return getParent().onSearchRequested();
		}
		else {
		showDialog(getParent(), "Network Error", Constants.SEARCH_NETWORK_DOWN_MESSAGE);
		return false;
		}
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
