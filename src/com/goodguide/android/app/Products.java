package com.goodguide.android.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater.Factory;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonsware.cwac.thumbnail.ThumbnailAdapter;
import com.commonsware.cwac.thumbnail.ThumbnailBus;
import com.goodguide.android.app.R;
import com.goodguide.android.business.GoodGuideBusiness;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.GoodGuideWebFeed;
import com.goodguide.android.utilities.MergeAdapter;
import com.goodguide.android.value.Product;

public class Products extends GenericListActivity{
	private static final int[] IMAGE_IDS={R.id.thumbnail};
	private static final String TAG = null;
	private ThumbnailAdapter thumbs=null;
	private List<Product> productsModel=null;
	private ArrayList<Product> brandsModel = null;

	private int pageCount = 1;
	
	private int categoryId;
	private int resultsCount;
	private String title;
	private String subTitle;
	private String ingredientsToAvoid;
	private boolean showAll;
	private ProgressDialog myProgressDialog;
	private Bundle extras;
	private String categoryName;
	private Thread searchThread;
	private int pagingSize = 50;
	
	private Handler moreHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(searchThread != null && !searchThread.isInterrupted()) {
				thumbs.notifyDataSetChanged();
				refreshList();
				//paging size is 50 records
				getListView().setSelection(productsModel.size() - pagingSize);
				myProgressDialog.dismiss();
				Products.this.setVisible(true);
			}
		}
	}; 
	
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.genericlist);
		
		extras = getIntent().getExtras();
		
        //set title of screen
        title = extras.getString(Constants.HEADER_TEXT);
        subTitle = extras.getString(Constants.SUB_HEADER_TEXT);
        ingredientsToAvoid = extras.getString(Constants.INGREDIENTS_TO_AVOID);
        categoryId = extras.getInt(Constants.CATEGORY_ID);
        categoryName = extras.getString(Constants.CATEGORY_TITLE);
        
        //set this to indicate whether we are retrieving more from the webservice
        showAll = false;
	    
		Log.v("**************categoryName", categoryName);
        
		if (productsModel==null) {
			productsModel=new ArrayList<Product>();
	    	
	    	GoodGuideBusiness.getProductsBySubcategory(categoryId, showAll);
	    	List<Product> results = Constants.cachedProductList;
	    	
	    	results = results == null?new ArrayList<Product>():results;
	    	
			Iterator<Product> iter = results.iterator();
			
			while(iter.hasNext()) {
				Product info = iter.next();
				productsModel.add(info);			
			}
			
		}
    	
		refreshList();
		
	}
	
	
	private void refreshList() {
		
		//hide ingredients unless personal product or household chemicals
    	String parentCategoryName = new GoodGuideBusiness().getCategoryNameById(categoryId);
		
    	int foundIndex = Arrays.binarySearch(Constants.allPersonalCareTitles, parentCategoryName);
    	if(foundIndex < 0) 
    		foundIndex = Arrays.binarySearch(Constants.allHouseholdCleanersTitles, parentCategoryName);
    	
    	if(Arrays.binarySearch(Constants.excludeIngredientsCategories, parentCategoryName) >= 0)
    		foundIndex = -1;
    	
    	Log.v("***\ndelete me***", "categoryName:"+parentCategoryName + " foundIndex:"+foundIndex + " ingredients:"+ingredientsToAvoid);
    	
		MergeAdapter adapter = new MergeAdapter();
		
		if(!"".equalsIgnoreCase(ingredientsToAvoid) && foundIndex >= 0) {
			adapter.addView(getHeaderView("Ingredients to watch for"));
			adapter.addView(getIngredientsView());
		}
		
		adapter.addView(getHeaderView(subTitle));
		adapter.addAdapter(new ProductAdapter());
		
		if(!showAll)
			adapter.addView(getMoreInitialView());
		else
			adapter.addView(getMoreSecondaryView());
		
		if (brandsModel != null && brandsModel.size() > 0) {
			// add brands list
			adapter.addView(getHeaderView("Company Results"));
			adapter.addAdapter(new BrandAdapter());
		}
	
		thumbs=new ThumbnailAdapter(this, adapter,((Application)getApplication()).getCache(productsModel.size()),IMAGE_IDS);																			
		setListAdapter(thumbs);
	}
	
	private View getIngredientsView() {
		RelativeLayout view = new RelativeLayout(this);
		view.setTag(categoryId);
		view.setBackgroundResource(R.anim.cellbg_states);
		view.setPadding(20, 20, 20, 20);
		
		ImageView image = new ImageView(this);
		image.setId(1);
		image.setImageResource(R.drawable.ingredientstoavoidicon);
		
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.FILL_PARENT);
		lp1.addRule(RelativeLayout.CENTER_VERTICAL, view.getId());
		
		view.addView(image, lp1);
		
		TextView textView = new TextView(getApplicationContext());
		textView.setId(2);
		textView.setText(ingredientsToAvoid);
		textView.setTextSize(14);
		textView.setTextColor(getResources().getColor(R.color.rowtitle));
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setPadding(20, 0, 0, 0);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.FILL_PARENT);
		lp.addRule(RelativeLayout.RIGHT_OF, image.getId());
		
		view.addView(textView, lp);
		
		return view;
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

	private View getMoreSecondaryView() {

		TextView textView = new TextView(getApplicationContext());
		textView.setBackgroundResource(R.anim.cellbg_states);
		textView.setClickable(true);
		textView.setId(2);
		textView.setText("Load more results for category '"+categoryName+"'");
		textView.setTextSize(20);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(getResources().getColor(R.color.rowtitle));
		textView.setPadding(15, 20, 15, 20);

		textView.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
		        
				String progressDialogMessage = "Retrieving more products in category:'"+categoryName+"'";

				// activity indicator
				// set busy dialog
				myProgressDialog = new ProgressDialog(getParent().getParent());
				myProgressDialog.setTitle("Please Wait");
				myProgressDialog.setMessage(progressDialogMessage);
				myProgressDialog.setIndeterminate(true);
				myProgressDialog.setCancelable(true);
				
				myProgressDialog.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface arg0) {
						    searchThread.interrupt();					    	
					}
		        });
				
				myProgressDialog.show();
				
				
				new Thread(new Runnable() {
					public void run() {

						pageCount++;
						getAllInCategory(categoryId);
						moreHandler.sendEmptyMessage(0);

					}
				}).start();
			}
		});

		return textView;
	}
	
	private View getMoreInitialView() {
		TextView textView = new TextView(getApplicationContext());
		textView.setBackgroundResource(R.anim.cellbg_states);
		textView.setClickable(true);
		textView.setId(2);
		textView.setText("Tap to browse all products...");
		textView.setTextSize(20);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(getResources().getColor(R.color.rowtitle));
		textView.setPadding(15, 20, 15, 20);
		
		textView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				//hide list since we are clearing it to put only webservice values in. looks weird otherwise
				Products.this.setVisible(false);
				
				//set this to indicate whether we are retrieving more from the webservice
		        showAll = true;
				
				//clear initial results from db
				productsModel.clear();
				
				// activity indicator
				// set busy dialog
				myProgressDialog = new ProgressDialog(getParent().getParent());
				myProgressDialog.setTitle("Please Wait");
				myProgressDialog.setMessage("Retrieving more products in category:'"+categoryName+"'");
				myProgressDialog.setIndeterminate(true);
				myProgressDialog.setCancelable(true);
				
				myProgressDialog.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface arg0) {	
						searchThread.interrupt();					    	
					}
		        });
				
				myProgressDialog.show();
	
				searchThread = new Thread(new Runnable() {
					public void run() {
						if(!searchThread.isInterrupted()){
							getAllInCategory(categoryId);
							moreHandler.sendEmptyMessage(0);
						}
					}
				});
				searchThread.start();
				
			}
		});
		
		return textView;

	}
	
	public void onListItemClick(ListView parent, View v, int position, long id) {
		
//		showProgressDialog(this);
		Intent i = new Intent(Products.this, ProductTabs.class);
		
		ThumbnailAdapter adapter = (ThumbnailAdapter) parent.getAdapter();
        Product product = (Product) adapter.getItem(position);
        
      if(Constants.showLog) Log.v(TAG, "Product is: "+product);
        
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.PRODUCT, product);
        extras.putInt(Constants.CATEGORY_ID, categoryId);
        i.putExtras(extras);
        
		
        // Create the view using FirstGroup's LocalActivityManager
        View view = ProductsGroup.group.getLocalActivityManager()
        .startActivity("productdetail", i
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        .getDecorView();
        
        view.setTag(Constants.PAGEVIEW_VIEW_PRODUCT_DETAILS+product.getName());
        
        // Again, replace the view
        ProductsGroup.group.replaceView(view);
	}
	

	@Override
	public void onDestroy() {
		super.onDestroy();	
		thumbs.close();
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		return(productsModel);
	}
	
	private ThumbnailBus getBus() {
		return(((Application)getApplication()).getBus());		
	}
	
	private void goBlooey(Throwable t) {
		Log.e("CacheDemo", "Exception!", t);
		
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		
		builder
			.setTitle(R.string.exception)
			.setMessage(t.toString())
			.setPositiveButton(R.string.ok, null)
			.show();
	}
	
	
	private void getAllInCategory(int mCategoryId) {

		Log.v(TAG, "in getAllInCategory:" + mCategoryId);

		HashMap<String, List<Product>> resultsMap = new GoodGuideWebFeed()
				.search("",categoryId, pageCount);

		List<Product> productResults = resultsMap.get(Constants.PRODUCT);

		// get product results if they exist
		productResults = productResults == null ? new ArrayList<Product>()
				: productResults;
		Log.v(TAG, "results:" + productResults.size());
		Iterator<Product> iter = productResults.iterator();
		resultsCount = iter != null ? productResults.size() : 0;

		while (iter.hasNext()) {
			Product info = iter.next();
			productsModel.add(info);
		}
	}
	
	class ProductAdapter extends ArrayAdapter<Product> {
		 ProductAdapter() {
			 
			 super(Products.this, R.layout.row, productsModel);
		}
		
		public View getView(int position, View convertView,
												ViewGroup parent) {
			View row=convertView;
			ProductWrapper wrapper=null;
			
			if (row==null) {													
				LayoutInflater inflater=getLayoutInflater();
				
				row=inflater.inflate(R.layout.row, null);
				wrapper=new ProductWrapper(row);
				row.setTag(wrapper);
			}
			else {
				wrapper=(ProductWrapper)row.getTag();
			}
			
			wrapper.populateFrom(getItem(position));
			
			return(row);
		}
	}
	
	class BrandAdapter extends ArrayAdapter<Product> {
		BrandAdapter() {

			super(Products.this, R.layout.row, brandsModel);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ProductWrapper wrapper = null;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();

				row = inflater.inflate(R.layout.row, null);
				wrapper = new ProductWrapper(row);
				row.setTag(wrapper);
			} else {
				wrapper = (ProductWrapper) row.getTag();
			}

			wrapper.populateFrom(getItem(position));

			return (row);
		}
	}
	
	class ProductWrapper {
		private TextView title=null;
		private TextView rating=null;
		private ImageView ratingIcon=null;
		private TextView more=null;
		private TextView moreInBrand=null;
		private ImageView image=null;
		private View row=null;
		
		ProductWrapper(View row) {
			this.row=row;
		}
		
		void populateFrom(Product info) {
			getTitle().setText(info.getName());
			getRating().setText(info.getOverallRating()==-1?"Partial data":info.getOverallRating()+"");
			setRatingIconForScore(getRatingIcon(), info.getOverallRating());
			getMoreInBrand().setText(info.getNumberOfProductsInBrand()<=1?"":info.getNumberOfProductsInBrand()+" more in brand");
			
			Log.v("","product is:"+info.getName()+", image url is:" + info.getS3ImageURL());
			getImage().setImageResource(R.drawable.image_not_available);

			//The thumbnail cache is a data map and the key is the url. If the url is null, then there is 
			//no unique identifier for the image. I am just putting the objectId in as the key
			//naturally, this won't make the image appear, but it will keep the indexing correct
			getImage().setTag(info.getS3ImageURL()==null?info.getObjectId():info.getS3ImageURL());
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

		TextView getTitle() {
			if (title==null) {
				title=(TextView)row.findViewById(R.id.titletext);
			}
			
			return(title);
		}
		
		TextView getRating() {
			if (rating==null) {
				rating=(TextView)row.findViewById(R.id.ratingValue);
			}
			
			return(rating);
		}
		
		ImageView getRatingIcon() {
			if (ratingIcon==null) {
				ratingIcon=(ImageView)row.findViewById(R.id.ratingIcon);
			}
			
			return(ratingIcon);
		}
		
		TextView getMoreInBrand() {
			if (moreInBrand==null) {
				moreInBrand=(TextView)row.findViewById(R.id.productsInBrand);
			}
			
			return(moreInBrand);
		}
		
		TextView getMore() {
			if (more==null) {
				more=(TextView)row.findViewById(R.id.more);
			}
			
			return(more);
		}
		
		ImageView getImage() {
			image=(ImageView)row.findViewById(R.id.thumbnail);
			return(image);
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
