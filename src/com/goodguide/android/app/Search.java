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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater.Factory;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.thumbnail.ThumbnailAdapter;
import com.commonsware.cwac.thumbnail.ThumbnailBus;
import com.goodguide.android.app.R;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.GoodGuideWebFeed;
import com.goodguide.android.utilities.MergeAdapter;
import com.goodguide.android.value.AdInfo;
import com.goodguide.android.value.Product;

public class Search extends GenericListActivity {
	private static final int[] IMAGE_IDS = { R.id.thumbnail };
	private static final String TAG = null;
	private ThumbnailAdapter thumbs = null;
	private ArrayList<Product> productsModel = null;
	private ArrayList<Product> brandsModel = null;
	private Integer appCategoryID;
	private int pageCount = 1;
	private int resultsCount;
	private int pagingSize = 50; 
	private ProgressDialog myProgressDialog;

	private Handler moreHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(searchThread != null && !searchThread.isInterrupted()) {
				thumbs.notifyDataSetChanged();
				refreshList();
				//paging size is 50 records
				getListView().setSelection(productsModel.size() - pagingSize);
				myProgressDialog.dismiss();
			}
		}
	};

	private String query;
	private int categoryId;
	private Thread searchThread;

	@Override
	public void onStop() {
		super.onStop();
		saveList(productsModel, Constants.RECENT_SEARCH_PRODUCTS);
		saveList(brandsModel, Constants.RECENT_SEARCH_BRANDS);
		saveString(query, Constants.RECENT_QUERY);
	}
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.search);
		
		Bundle extras = getIntent().getExtras();
		
		//if no query parameters passed via intent and either of the the products/brands lists are populated
		if(extras == null 
				&& 
				((Constants.getRecentSearchProductResults() != null && Constants.getRecentSearchProductResults().size() > 0)
				|| (Constants.getRecentSearchBrandResults() != null && Constants.getRecentSearchBrandResults().size() > 0))) {
			productsModel = Constants.getRecentSearchProductResults();
			brandsModel = Constants.getRecentSearchBrandResults();
			query = Constants.getRecentQuery();
			Log.v("", "\n\ncached search\n\n");
		}
		//if there are query parameters then this was a user initiated search
		else if(extras != null) {
			productsModel = (ArrayList<Product>) extras.getSerializable(Constants.SEARCH_PRODUCT_RESULTS);
			brandsModel = (ArrayList<Product>) extras.getSerializable(Constants.SEARCH_BRANDS_RESULTS);
			query = extras.getString(Constants.QUERY);
			Log.v("", "\n\nsearch initiated from user\n\n");
		//there are no products or brands populated and the user has not initiated a search
		} 
		refreshList();
		
		findViewById(R.id.newsearchbutton).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Log.v("", "new search");
				onSearchRequested();
			}
		});
		
	}

	private void performSearch(String query) {

		query = URLEncoder.encode(query);
		Log.v(TAG, "in performSearch:" + query);

		HashMap<String, List<Product>> resultsMap = new GoodGuideWebFeed()
				.search(query,-1, pageCount);

		List<Product> productResults = resultsMap.get(Constants.PRODUCT);
		List<Product> brandResults = resultsMap.get(Constants.BRAND);

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

		// get brand results if they exist
		brandResults = brandResults == null ? new ArrayList<Product>()
				: brandResults;
		Log.v(TAG, "results:" + brandResults.size());
		Iterator<Product> iter2 = brandResults.iterator();
		resultsCount = iter2 != null ? brandResults.size() : 0;

		while (iter2.hasNext()) {
			Product info = iter2.next();
			brandsModel.add(info);
		}
	}
	


	private void refreshList() {

		//cache values
		Constants.setRecentQuery(query);
		Constants.setRecentSearchProductResults(productsModel);
		Constants.setRecentSearchBrandResults(brandsModel);
		
		
		MergeAdapter adapter = new MergeAdapter();

		if(productsModel != null && productsModel.size() > 0 
				|| brandsModel != null && brandsModel.size() > 0) {
			
			// add products list
			adapter.addView(getHeaderView("Top Results for '" + query + "'"));
			adapter.addAdapter(new ProductAdapter());
			adapter.addView(getMoreView());
	
			if (brandsModel != null && brandsModel.size() > 0) {
				// add brands list
				adapter.addView(getHeaderView("Company Results"));
				adapter.addAdapter(new BrandAdapter());
			}
		} else
		{
			adapter.addView(getHeaderView("No Search Results"));
		}
		thumbs = new ThumbnailAdapter(this, adapter,
				((Application) getApplication()).getCache(adapter.getCount()), IMAGE_IDS);
		setListAdapter(thumbs);
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

	private View getMoreView() {

		TextView textView = new TextView(getApplicationContext());
		textView.setBackgroundResource(R.anim.cellbg_states);
		textView.setClickable(true);
		textView.setId(2);
		textView.setText("Load more results for '"+query+"'");
		textView.setTextSize(20);
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(getResources().getColor(R.color.rowtitle));
		textView.setPadding(15, 20, 15, 20);

		textView.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {

				String progressDialogMessage = "Loading more results for '"+query+"'...";

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
				
				searchThread = new Thread(new Runnable() {
				public void run() {
					if(!searchThread.isInterrupted()){
						pageCount++;
						performSearch(query);
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
		Intent i = new Intent(Search.this, ProductTabs.class);

		ThumbnailAdapter adapter = (ThumbnailAdapter) parent.getAdapter();
		Product product = (Product) adapter.getItem(position);

		Log.v(TAG, "Product is: " + product);

		Bundle extras = new Bundle();
		extras.putSerializable(Constants.PRODUCT, product);
		 extras.putInt(Constants.CATEGORY_ID, categoryId);
		i.putExtras(extras);

		// Create the view using ProductsGroup's LocalActivityManager
		View view = ProductsGroup.group.getLocalActivityManager()
				.startActivity("productdetailsearch",
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();

		view.setTag(Constants.PAGEVIEW_VIEW_PRODUCT_DETAILS+product.getName());
		
		// Again, replace the view
		ProductsGroup.group.replaceView(view);
		GoodGuide.browseButtonClicked();
		
	}

	class ProductAdapter extends ArrayAdapter<Product> {
		ProductAdapter() {

			super(Search.this, R.layout.row, productsModel);
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

	class BrandAdapter extends ArrayAdapter<Product> {
		BrandAdapter() {

			super(Search.this, R.layout.row, brandsModel);
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
		private TextView title = null;
		private TextView rating = null;
		private TextView more = null;
		private TextView moreInBrand = null;
		private ImageView image = null;
		private ImageView ratingIcon=null;
		private View row = null;

		ProductWrapper(View row) {
			this.row = row;
		}

		void populateFrom(Product info) {
			getTitle().setText(info.getName());
			getRating().setText(info.getOverallRating()==-1?"Partial data":info.getOverallRating()+"");
			
			if(info.getOverallRating()==-1)
				row.findViewById(R.id.ratingOutOf10).setVisibility(View.INVISIBLE);
				
			setRatingIconForScore(getRatingIcon(), info.getOverallRating());
			getMoreInBrand().setText(
					info.getNumberOfProductsInBrand() <= 1 ? "" : info
							.getNumberOfProductsInBrand()
							+ " more in brand");

			getImage().setImageResource(R.drawable.image_not_available);

			//The thumbnail cache is a data map and the key is the url. If the url is null, then there is 
			//no unique identifier for the image. I am just putting the objectId in as the key
			//naturally, this won't make the image appear, but it will keep the indexing correct
			getImage().setTag(info.getS3ImageURL()==null?info.getObjectId():info.getS3ImageURL());
		}

		TextView getTitle() {
			if (title == null) {
				title = (TextView) row.findViewById(R.id.titletext);
			}

			return (title);
		}

		TextView getRating() {
			if (rating == null) {
				rating = (TextView) row.findViewById(R.id.ratingValue);
			}

			return (rating);
		}
		
		ImageView getRatingIcon() {
			if (ratingIcon==null) {
				ratingIcon=(ImageView)row.findViewById(R.id.ratingIcon);
			}
			
			return(ratingIcon);
		}

		TextView getMoreInBrand() {
			if (moreInBrand == null) {
				moreInBrand = (TextView) row.findViewById(R.id.productsInBrand);
			}

			return (moreInBrand);
		}

		TextView getMore() {
			if (more == null) {
				more = (TextView) row.findViewById(R.id.more);
			}

			return (more);
		}

		ImageView getImage() {
			if (image == null) {
				image = (ImageView) row.findViewById(R.id.thumbnail);
			}

			return (image);
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(thumbs != null)
			thumbs.close();
		
		//way to stop a thread according to Oracle
		if(searchThread != null) {
		Thread tempThread = new Thread();
		tempThread = searchThread;
		searchThread = null;
		tempThread.interrupt();
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return (productsModel);
	}

	private ThumbnailBus getBus() {
		return (((Application) getApplication()).getBus());
	}

	private void goBlooey(Throwable t) {
		Log.e("CacheDemo", "Exception!", t);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.exception).setMessage(t.toString())
				.setPositiveButton(R.string.ok, null).show();
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
    public void setMenuBackground(){

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