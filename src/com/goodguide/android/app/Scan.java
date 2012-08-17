package com.goodguide.android.app;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.LayoutInflater.Factory;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.commonsware.cwac.thumbnail.ThumbnailAdapter;
import com.goodguide.android.app.Scan;
import com.goodguide.android.app.R;
import com.goodguide.android.business.GoodGuideBusiness;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.GoodGuideWebFeed;
import com.goodguide.android.utilities.ImageDisplayer;
import com.goodguide.android.utilities.ImageReceivedCallback;
import com.goodguide.android.utilities.MergeAdapter;
import com.goodguide.android.utilities.ScanDB;
import com.goodguide.android.value.Product;

public class Scan extends GenericListActivity implements ImageReceivedCallback  {
	
	private MergeAdapter adapter;
	private LayoutInflater inflater;
	private View layout;
	private PopupWindow pw;
	private ProgressDialog myProgressDialog = null; 
	
	
	private Handler searchHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			GoodGuideWebFeed webFeed = new GoodGuideWebFeed();
			Bundle bundle = msg.getData();
			boolean exists = bundle.getBoolean(Constants.EXISTS);
			String productName = bundle.getString(Constants.PRODUCT);
			String upc = bundle.getString(Constants.UPC);
			int objectId = bundle.getInt(Constants.OBJECT_ID);
			
			Log.v("", "exists:"+exists+ " productName:"+productName+" upc:"+upc);
			
			loadRecentScansOrInstructions(null);
			myProgressDialog.dismiss();
			
			if(!exists && productName == null) {
				noUpcDialog.show();
				trackEvent(Constants.CATEGORY_SCAN, Constants.ACTION_PRODUCTSCANNED, Constants.LABEL_NO_UPC, 0);
				webFeed.callGoodguideAnalytics(0, "Product", upc, "Scan", "");
			} else if(!exists && productName != null) {
				noRatingDialog = new AlertDialog.Builder(Scan.this)
		    	.setTitle("UPC not found")
		    	.setMessage("GoodGuide wasn't able to match this barcode with a product we've rated.")
		    	.setNegativeButton("OK",
		    			new DialogInterface.OnClickListener() {
		    				public void onClick(DialogInterface dialog,
		    						int whichButton) {
		    					// ignore, just dismiss
		    				}
		    			});
				noRatingDialog.show();
				trackEvent(Constants.CATEGORY_SCAN, Constants.ACTION_PRODUCTSCANNED, Constants.LABEL_NO_RATING+upc, 0);
				webFeed.callGoodguideAnalytics(0, "Product", upc, "Scan", "");
			} else {
				trackEvent(Constants.CATEGORY_SCAN, Constants.ACTION_PRODUCTSCANNED, upc, 0);
				webFeed.callGoodguideAnalytics(objectId, "Product", upc, "Scan", "");
			}
				
			
		}
	};
	private Builder noUpcDialog;
	private Builder noRatingDialog;
	private int categoryId;
	private Thread searchThread;

	//ImageReceivedCallback function to display icon
	public void onImageReceived(ImageDisplayer displayer)
    {
		if(displayer.bmp == null) 
			displayer.bmp = BitmapFactory.decodeResource(getResources(), R.drawable.image_not_available);
		
        // run the ImageDisplayer on the UI thread
        this.runOnUiThread(displayer);
    }
	
	
	@Override
	public void onStop() {
		super.onStop();
		saveList(Constants.getRecentlyScanned(), Constants.RECENTLY_SCANNED);
	}
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);
        
        noUpcDialog = new AlertDialog.Builder(this)
    	.setTitle("UPC not found")
    	.setMessage("GoodGuide wasn't able to find this product's UPC code. Please try to find it by searching or browsing.")
    	.setNegativeButton("OK",
    			new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog,
    						int whichButton) {
    					// ignore, just dismiss
    				}
    			});
              
        loadRecentScansOrInstructions(null);
        
        Button scanButton = (Button)findViewById(R.id.scanbutton);
        scanButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
			Intent scanIntent = new Intent(Scan.this,
					RedLaserSDK.class);
					startActivityForResult(scanIntent, 1);
					trackEvent(Constants.CATEGORY_SCAN, Constants.ACTION_SCANBUTTON, Constants.LABEL_SCAN, 0);
			}
		});
		
		
		findViewById(R.id.clearbutton).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
			Constants.getRecentlyScanned().clear();
			loadRecentScansOrInstructions(null);
			}
		});
		
    }

	private void loadRecentScansOrInstructions(String barcode) {
		if(Constants.getRecentlyScanned().size() > 0) {
			
			//set latest scan info up top
			
			//show clear button
			findViewById(R.id.clearbutton).setVisibility(View.VISIBLE);
			
			//set product image
			final Product mostRecentScan = Constants.getRecentlyScanned().get(Constants.getRecentlyScanned().size()-1);
			String upc = barcode==null?mostRecentScan.getUpc():barcode;
			mostRecentScan.setUpc(upc);
			ImageView productImage = (ImageView)findViewById(R.id.productimage);
			// will start downloading/rendering the image
	        new ImageReceiver(mostRecentScan.getS3ImageURL(), this,  productImage);
	        
	        TextView titleText = (TextView)findViewById(R.id.producttext);
	        titleText.setText(mostRecentScan.getName());
	        
	        TextView upcText = (TextView)findViewById(R.id.upctext);
	        upcText.setText("UPC: "+mostRecentScan.getUpc());
			
	        TextView overallText = (TextView)findViewById(R.id.overallrating);
	        TextView healthText = (TextView)findViewById(R.id.healthrating);
	        TextView environmentText = (TextView)findViewById(R.id.environmentrating);
	        TextView societyText = (TextView)findViewById(R.id.societyrating);
	        
	        ImageView overallImage = (ImageView)findViewById(R.id.overallimage);
	        ImageView healthImage = (ImageView)findViewById(R.id.healthimage);
	        ImageView environmentImage = (ImageView)findViewById(R.id.environmentimage);
	        ImageView societyImage = (ImageView)findViewById(R.id.societyimage);
	        
	        setTextAndImageForRating(mostRecentScan.getOverallRating(), overallText, overallImage);
	        setTextAndImageForRating(mostRecentScan.getHealthRating(), healthText, healthImage);
	        setTextAndImageForRating(mostRecentScan.getEnvironmentalRating(), environmentText, environmentImage);
	        setTextAndImageForRating(mostRecentScan.getSocialRating(), societyText, societyImage);
	        
	        //set onclick behaviour for 'more about this product' button
	        Button aboutProductButton = (Button)findViewById(R.id.morebutton);
	        aboutProductButton.setOnClickListener(new OnClickListener() {
    			
    			public void onClick(View v) {
    				Intent i = new Intent(Scan.this, ProductTabs.class);

    				Bundle extras = new Bundle();
    				extras.putSerializable(Constants.PRODUCT, mostRecentScan);
    				extras.putString(Constants.SUB_HEADER_TEXT,
    						"Highest rated products by brand");
    				i.putExtras(extras);

    				// Create the view using FirstGroup's LocalActivityManager
    				View view = ProductsGroup.group.getLocalActivityManager()
    						.startActivity("productdetail",
    								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    						.getDecorView();

    				// Again, replace the view
    				ProductsGroup.group.replaceView(view);
    				GoodGuide.browseButtonClicked();				
    			}
    		});
			
	        //use adapter as a placeholder for now since i need to reverse the order of the items as it is
	        // since the new view is placed at the bottom and I need it at the top
			adapter = new MergeAdapter();
        	findViewById(R.id.scanlogo).setVisibility(View.GONE);
        	findViewById(R.id.upc_root).setVisibility(View.VISIBLE);
        	getListView().setVisibility(View.VISIBLE);
        	inflater=getLayoutInflater();
       	
        	Iterator<Product> iter = Constants.getRecentlyScanned().iterator();
        	
        	while(iter.hasNext()) {
        	final Product info = iter.next();
        	View row = inflater.inflate(R.layout.row, null);
        	row.setOnClickListener(new OnClickListener() {
    			
    			public void onClick(View v) {
    				Intent i = new Intent(Scan.this, ProductTabs.class);

    				Bundle extras = new Bundle();
    				extras.putSerializable(Constants.PRODUCT, info);
    				extras.putString(Constants.SUB_HEADER_TEXT,
    						"Highest rated products by brand");
    				i.putExtras(extras);

    				// Create the view using FirstGroup's LocalActivityManager
    				View view = ProductsGroup.group.getLocalActivityManager()
    						.startActivity("productdetail",
    								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    						.getDecorView();

    				// Again, replace the view
    				ProductsGroup.group.replaceView(view);
    				GoodGuide.browseButtonClicked();				
    			}
    		});
        	
        	
        	TextView name = (TextView)row.findViewById(R.id.titletext);
        	name.setText(info.getName());
        	
        	TextView rating = (TextView)row.findViewById(R.id.ratingValue);
        	rating.setText(info.getOverallRating() == -1?"NA":info.getOverallRating()+"");
        	
        	if(info.getOverallRating() == -1)
        		row.findViewById(R.id.ratingOutOf10).setVisibility(View.INVISIBLE);
        	
        	TextView brand = (TextView)row.findViewById(R.id.productsInBrand);
        	brand.setText(info.getNumberOfProductsInBrand()<=1?"":info.getNumberOfProductsInBrand()+" more in brand");
        	
        	ImageView ratingIcon = (ImageView)row.findViewById(R.id.ratingIcon);
        	setRatingIconForScore(ratingIcon, info.getOverallRating());
        	
        	ImageView imageView = (ImageView)row.findViewById(R.id.thumbnail);
        	// will start downloading/rendering the image
	        new ImageReceiver(info.getS3ImageURL(), this,  imageView);
			adapter.addView(row);
        	}
        	
        //reverse order of items in the listview to make the most recent scan first
        //limit results
        int counter = 1;
       	List<View> reverseOrderViewList = new ArrayList<View>();
        	for(int i = adapter.getCount()-1; i >= 0; i--) {
        		View view = (View)adapter.getItem(i);
        		
        		if(counter <= Constants.SCAN_HISTORY_LIMIT)
        			reverseOrderViewList.add(view);
        		
        		counter++;
        	}
        	
        	adapter = new MergeAdapter();
        	adapter.addView(getHeaderView("Recently scanned items..."));
        	adapter.addViews(reverseOrderViewList);      	
        	setListAdapter(adapter);
        	
        } else {
        	//hide clear button if no recently scanned products
        	findViewById(R.id.clearbutton).setVisibility(View.INVISIBLE);
        	findViewById(R.id.scanlogo).setVisibility(View.VISIBLE);
        	findViewById(R.id.upc_root).setVisibility(View.GONE);
        	getListView().setVisibility(View.GONE);
        	Log.v("", "does not contain scanned");
        }
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//we came from a scanning activity
		//need to do something with it
		if (resultCode == RESULT_OK) {
			
			// activity indicator
			// set busy dialog
			myProgressDialog = new ProgressDialog(getParent());
			myProgressDialog.setTitle("Please Wait");
			myProgressDialog.setMessage("Searching GoodGuide for UPC");
			myProgressDialog.setIndeterminate(true);
			myProgressDialog.setCancelable(true);
			
			myProgressDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface arg0) {				    	
					    searchThread.interrupt();	
					    myProgressDialog.dismiss();
				}
	        });
			
			myProgressDialog.show();
			
			final String barcode = data.getAction();
			new ScanDB(searchHandler).execute(barcode);
		} 
	}


	private View getHeaderView(String title) {
		
		TextView textView = new TextView(getApplicationContext());
		textView.setBackgroundColor(getResources().getColor(R.color.menuheader));
		textView.setId(2);
		textView.setText(title);
		textView.setTextSize(17);
		textView.setTextColor(getResources().getColor(R.color.darkgrey));
		textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		textView.setPadding(5, 5, 0, 5);
		
		return textView;
	}

	public int getIcon(String iconName) {
		int resID = getResources().getIdentifier(iconName, "drawable", "com.goodguide.android.app");
		return resID;
	}
	
	
private void setTextAndImageForRating(float rating, TextView textView, ImageView imageView) {
		
		float ratingFloat = rating/2;
		int switchInt = (int)Math.ceil(ratingFloat);
		String text = rating==-1?"NA":rating+"";
		textView.setText(text);
		
		switch (switchInt) {
		case 1:
			imageView.setImageResource(R.drawable.oval_terrible);
			break;
		case 2:
			imageView.setImageResource(R.drawable.oval_poor);
			break;
		case 3:
			imageView.setImageResource(R.drawable.oval_fair);
			break;
		case 4:
			imageView.setImageResource(R.drawable.oval_good);
			break;
		case 5:
			imageView.setImageResource(R.drawable.oval_excellent);
			break;
		default:
			imageView.setImageResource(R.drawable.oval_norating);
			break;
		}
	}
	

private void setRatingIconForScore(ImageView mRatingIcon,
		float mOverallRating) {
	
	int switchInt = (int)Math.ceil(mOverallRating/2);
	
	switch (switchInt) {
	case 1:
		mRatingIcon.setImageResource(R.drawable.img_1_16);
		break;
	case 2:
		mRatingIcon.setImageResource(R.drawable.img_2_16);
		break;
	case 3:
		mRatingIcon.setImageResource(R.drawable.img_3_16);
		break;
	case 4:
		mRatingIcon.setImageResource(R.drawable.img_4_16);
		break;
	case 5:
		mRatingIcon.setImageResource(R.drawable.img_5_16);
		break;
	default:
		mRatingIcon.setImageResource(R.drawable.img_0_16);
		break;
	}
	
}

public void onBackPressed() {  
    GoodGuide.browseButtonClicked(); 
    return;  
} 

/* Creates the menu items */
public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
   
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
