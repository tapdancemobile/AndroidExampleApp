package com.goodguide.android.app;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater.Factory;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.goodguide.android.app.R;
import com.goodguide.android.business.GoodGuideBusiness;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.ImageDisplayer;
import com.goodguide.android.utilities.ImageReceivedCallback;
import com.goodguide.android.value.Product;

public class ProductTabs extends GenericTabActivity implements ImageReceivedCallback {
	
	private TabHost mTabHost;
	private Activity mContext;
	private Product product;
	private RelativeLayout photoGroup;
	private int categoryId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.producttab);
		
		Bundle extras = getIntent().getExtras();
		product = (Product)extras.getSerializable(Constants.PRODUCT);
		
		//set title of screen
        categoryId = extras.getInt(Constants.CATEGORY_ID);
        
      if(Constants.showLog) Log.v("\n****alternatives info:***\n", "\ncategoryId:"+categoryId+ "\nalternativesCategoryId:"+product.getAlternativesCategoryId()+"\nalternativesCategory:"+product.getAlternativesCategory());
        
        //scan only populates categoryId if in db and categoryName if not, search populates categoryName
        extras.remove(Constants.CATEGORY_ID);

        if(categoryId <= 0 && product.getAlternativesCategoryId() == 0) {
        	categoryId = new GoodGuideBusiness().getCategoryIdByName(product.getAlternativesCategory());
        } else if(categoryId <= 0 && product.getAlternativesCategoryId() != 0) {
        	categoryId = product.getAlternativesCategoryId();
        }
        
        //put new or existing value in
        extras.putInt(Constants.CATEGORY_ID, categoryId);
		
		mTabHost = getTabHost();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			   public void onTabChanged(String arg0) {
			    switch (mTabHost.getCurrentTab()) {
				case 0:
					trackPageView(Constants.PAGEVIEW_VIEW_PRODUCT_DETAILS+product.getName());
					break;
				case 1:
					trackPageView(Constants.PAGEVIEW_VIEW_PRODUCT_ALTERNATIVES+product.getName());
					break;
				default:
					break;
				}
			   }     
			        });
		
		Intent i = new Intent(ProductTabs.this, Alternatives.class);
        i.putExtras(extras);
		
        //hide alternatives list if it is a brand
		Product product = (Product)extras.getSerializable(Constants.PRODUCT);
        
		setupTab(new TextView(this), "Details", R.id.detailview);
		
		if(!product.isBrand())
			setupTab(new TextView(this), "Alternatives", i);
		
		populateDetailsView();
	}
	
	//ImageReceivedCallback function to display icon
	public void onImageReceived(ImageDisplayer displayer)
    {
		if(displayer.bmp == null) {
			displayer.bmp = BitmapFactory.decodeResource(getResources(), R.drawable.image_not_available);
			photoGroup.setClickable(false);
		}
		
        // run the ImageDisplayer on the UI thread
        this.runOnUiThread(displayer);
    }
	
	
	private void populateDetailsView() {
		mContext = this.getParent();
		
		TextView productTitle = (TextView) findViewById(R.id.detailtitletext);	
		productTitle.setText(product.getName());	
		
		//set the image from the url location
		final String imageURL = product.getS3ImageURL();
    	ImageView productImage = (ImageView) findViewById(R.id.productimage);

		// get the instance of the LayoutInflater
		LayoutInflater inflater = (LayoutInflater) getParent().getParent().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflate our view from the corresponding XML file
		final View layout = inflater.inflate(R.layout.product_image_dialog, (ViewGroup)findViewById(R.id.fs_product_image_root));
		// create a  popup window
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth()-50;
		final PopupWindow pw = new PopupWindow(layout, width, width*3/2, true);
		// set actions to buttons we have in our popup
		final ImageView fullsizeImage = (ImageView) layout.findViewById(R.id.fsproductimage);
		
		ImageButton closeButton = (ImageButton)layout.findViewById(R.id.closebutton);
		closeButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {		  	  
				pw.dismiss();
			}
		});
  	  
	  	  
    	photoGroup = (RelativeLayout) findViewById(R.id.photogroup);
    	photoGroup.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				// will start downloading/rendering the fullsize image
		        new ImageReceiver(imageURL, ProductTabs.this,  fullsizeImage);
		        
				// finally show the popup in the center of the window
				pw.showAtLocation(layout, Gravity.CENTER, 0, 0);
				trackPageView(Constants.PAGEVIEW_VIEW_PRODUCT_IMAGE+product.getName());
			}
		});
    	
    	// will start downloading/rendering the regular image
        new ImageReceiver(imageURL, this,  productImage);
    	
    	//set rating summary
    	ImageView overallImage = (ImageView) findViewById(R.id.ratingiconbg);
    	setIconBGForRating(product.getOverallRating(), overallImage);
    	
    	TextView overallScore = (TextView) findViewById(R.id.ratingicontext);	
    	float rating = product.getOverallRating();
    	String overallRating = rating==-1?"NA":rating+"";
    	overallScore.setText(overallRating);
    	
    	TextView healthScore = (TextView) findViewById(R.id.healthtext);	
    	setTextColorForRating(product.getHealthRating(), healthScore);
    	
    	TextView environmentScore = (TextView) findViewById(R.id.environmenttext);	
    	setTextColorForRating(product.getEnvironmentalRating(), environmentScore);
    	
    	TextView societyScore = (TextView) findViewById(R.id.societytext);	
    	setTextColorForRating(product.getSocialRating(), societyScore);
		
    	
    	String[] behindTheRatingsDescriptions = product.getBehindTheRatingDescriptions().split(":-\\)");
    	String[] behindTheRatingZScores = product.getBehindTheRatingZScores().split(":-\\)");
    	
    	for(int i=0; i<behindTheRatingsDescriptions.length; i++) {
    		
    		String scoreString = behindTheRatingZScores[i];
    		if("NA".equalsIgnoreCase(behindTheRatingZScores[i]) || "".equalsIgnoreCase(behindTheRatingZScores[i].trim())) {
    			scoreString = "0";
    		}
    		
    		int score = Integer.parseInt(scoreString);
    		String description = behindTheRatingsDescriptions[i];
    		
    		switch (i) {
			case 0:
				TextView healthText = (TextView) findViewById(R.id.btrhealthtext);
				ImageView healthImage = (ImageView) findViewById(R.id.btrhealthiconbg);
				healthText.setText(description);
				setBTRIcon(score, healthImage);
				break;
			case 1:
				TextView envText = (TextView) findViewById(R.id.btrenvironmentaltext);
				ImageView envImage = (ImageView) findViewById(R.id.btrenviromentaliconbg);
				envText.setText(description);
				setBTRIcon(score, envImage);
				break;
			case 2:
				TextView societyText = (TextView) findViewById(R.id.btrsocietytext);
				ImageView societyImage = (ImageView) findViewById(R.id.btrsocietyiconbg);
				societyText.setText(description);
				setBTRIcon(score, societyImage);
				break;

			default:
				break;
			}
    	}
    	
    	
    	//hide ingredients unless personal product or household chemicals
    	String categoryName = new GoodGuideBusiness().getCategoryNameById(categoryId);
    	
    	int foundIndex = categoryName==null?-1:Arrays.binarySearch(Constants.allPersonalCareTitles, categoryName);
    	if(foundIndex < 0)
    		foundIndex = categoryName==null?-1:Arrays.binarySearch(Constants.allHouseholdCleanersTitles, categoryName);
    	if(foundIndex < 0) 
    		foundIndex = categoryName==null?-1:Arrays.binarySearch(Constants.allFoodTitles, categoryName);
    	
    	//make sure that ingredients AND scores are present - some household items such as 'writing pad' do not have
		//ingredients	
		if(product.getIngredients() == null || product.getIngredients().length() <= 0
				|| product.getIngredientScores() == null || product.getIngredientScores().length() <= 0) 
			foundIndex = -1;
		
    	if(foundIndex >= 0)
    	{   
    	
	    	String[] ingredients = product.getIngredients().split(":-\\)");
	    	String[] ingredientScores = product.getIngredientScores().split(":-\\)");
	    	
	    	WebView ingredientsText = (WebView) findViewById(R.id.ingredientstext);
	    	String ingredientsHtml = "";
	    	
	    	for(int i=0; i<ingredients.length; i++) {  		
	    		String score = ingredientScores[i];
	    		String ingredient = ingredients[i];
	    		String ingredientHtml = "";
	    		
	    		if(Constants.LOW_CONCERN.equalsIgnoreCase(score))
	    			ingredientHtml += "<b style='color:#7676FE; font-size:.8em'>"+ingredient+"</b>";
	    		else if(Constants.MED_CONCERN.equalsIgnoreCase(score))
	    			ingredientHtml += "<b style='color:#FEAC5E; font-size:.8em'>"+ingredient+"</b>";
	    		else if(Constants.HIGH_CONCERN.equalsIgnoreCase(score))
	    			ingredientHtml += "<b style='color:#D45555; font-size:.8em'>"+ingredient+"</b>";
	    		else if(!Constants.NO_INGREDIENTS_FOUND.equalsIgnoreCase(ingredient))
	    			ingredientHtml += "<b style='color:black; font-size:.8em'>"+ingredient+"</b>";
	    		else
	    			ingredientHtml += "<b style='color:black; font-size:.8em'>"+Constants.NO_INGREDIENTS_MSG+"</b>";
	    			
	    		ingredientHtml += i==ingredients.length-1?"":", "; 
	    		ingredientsHtml += ingredientHtml;
	    	}
	    	
		    String styledContent = Constants.INGREDIENTS_CSS + "<html><body>" + ingredientsHtml + "</body></html>";
		  if(Constants.showLog) Log.v("ingredientsHTML:", styledContent);
//		  styledContent = "<html><body<h1>Hello</h1></body></html>";
		  ingredientsText.loadData(styledContent, "text/html", "utf-8");
		    
    	} else {
    		findViewById(R.id.ingredientsheadertext).setVisibility(View.GONE);
    		findViewById(R.id.ingredientsgroup).setVisibility(View.GONE);
    	}
	    
	    //force scroll to top
	    ScrollView scrollView = (ScrollView) findViewById(R.id.scroller);
	    scrollView.smoothScrollTo(0, 0);
	}
	
	private void setupTab(final View view, final String tag, final int viewId) {
		View tabview = createTabView(mTabHost.getContext(), tag);
	    TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(viewId);
		mTabHost.addTab(setContent);
	}
	
	private void setupTab(final View view, final String tag, final Intent intent) {
		View tabview = createTabView(mTabHost.getContext(), tag);
	    TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		mTabHost.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_detail_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
	
	
	private void setIconBGForRating(float rating, ImageView iconBg) {
		
		float ratingFloat = rating/2;
		int switchInt = (int)Math.ceil(ratingFloat);
		
		switch (switchInt) {
		case 1:
			iconBg.setImageResource(R.drawable.terrible_px100);
			break;
		case 2:
			iconBg.setImageResource(R.drawable.poor_px100);
			break;
		case 3:
			iconBg.setImageResource(R.drawable.fair_px100);
			break;
		case 4:
			iconBg.setImageResource(R.drawable.good_px100);
			break;
		case 5:
			iconBg.setImageResource(R.drawable.excellent_px100);
			break;
		default:
			iconBg.setImageResource(R.drawable.no_px100);
			break;
		}
	}
	
	private void setBTRIcon(int rating, ImageView iconBg) {
		
		if(rating < 0)
			iconBg.setImageResource(R.drawable.behind_the_rating_poor);
		else if(rating == 0)
			iconBg.setImageResource(R.drawable.behind_the_rating_fair);
		else if(rating > 0)
			iconBg.setImageResource(R.drawable.behind_the_rating_good);
		
		
	}
	
private void setTextColorForRating(float rating, TextView textView) {
		
		float ratingFloat = rating/2;
		int switchInt = (int)Math.ceil(ratingFloat);
		boolean isValidRating = rating >= 0 && rating <= 10?true:false;
		String text = !isValidRating?"NA":rating+"";
		Log.v("rating", "rating:"+rating);
		textView.setText(text);
		
		switch (switchInt) {
		case 1:
			textView.setTextColor(getResources().getColor(R.color.terriblecolor));
			break;
		case 2:
			textView.setTextColor(getResources().getColor(R.color.poorcolor));
			break;
		case 3:
			textView.setTextColor(getResources().getColor(R.color.faircolor));
			break;
		case 4:
			textView.setTextColor(getResources().getColor(R.color.goodcolor));
			break;
		case 5:
			textView.setTextColor(getResources().getColor(R.color.excellentcolor));
			break;
		default:
			textView.setTextColor(getResources().getColor(R.color.darkgrey));
			break;
		}
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

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	
	Log.v("", "back pressed");
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && ProductsGroup.group != null) {
        ProductsGroup.group.back();
        return true;
    }
    return super.onKeyDown(keyCode, event);
}

/* Creates the menu items */
public boolean onCreateOptionsMenu(Menu menu) {
    
	// is activity withing a tabactivity
    if (getParent() != null) 
    {
    	Log.v(TAG, "is tab");
        return getParent().onCreateOptionsMenu(menu);
    } else {
    	return false;
    }
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
