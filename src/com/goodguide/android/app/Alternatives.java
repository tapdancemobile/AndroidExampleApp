package com.goodguide.android.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonsware.cwac.thumbnail.ThumbnailAdapter;
import com.commonsware.cwac.thumbnail.ThumbnailBus;
import com.goodguide.android.app.R;
import com.goodguide.android.business.GoodGuideBusiness;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.MergeAdapter;
import com.goodguide.android.value.Product;

public class Alternatives extends GenericListActivity {
	private static final int[] IMAGE_IDS={R.id.thumbnail};
	private static final String TAG = null;
	private ThumbnailAdapter thumbs=null;
	private List<Product> model=null;
	private int pageCount;
	private int categoryId;
	private String title;
	private String subTitle;
	private String ingredientsToAvoid;
	private boolean showAll;
	private ProgressDialog myProgressDialog;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			thumbs.notifyDataSetChanged();
			refreshList();
			getListView().setSelection(model.size()-18);
			myProgressDialog.dismiss();
		}
	};
	
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.genericlist); 
		
		final Bundle extras = getIntent().getExtras();
		Product product = (Product)extras.getSerializable(Constants.PRODUCT);
		
        //set title of screen
        categoryId = extras.getInt(Constants.CATEGORY_ID);
        
        //retrieve categoryId from db if not populated(search and scan don't populate)
//        GoodGuideBusiness business = new GoodGuideBusiness();
//        business.getCategoryIdByProduct();
        
        subTitle = "Highest rated products by brand";
     
        //wrapper method in super class in order to log the page view
        trackPageView(Constants.PAGEVIEW_BROWSE_CATEGORY+title);
	    
		
		if (model==null) {
			model=new ArrayList<Product>();
	    	
	    	GoodGuideBusiness.getProductsBySubcategory(categoryId, showAll);
	    	List<Product> results = Constants.cachedProductList;
	    	
	    	results = results == null?new ArrayList<Product>():results;
	    	
			Iterator<Product> iter = results.iterator();
			
			while(iter.hasNext()) {
				Product info = iter.next();
				model.add(info);			
			}
			
		}
    	
		refreshList();
		
	}
	
	private void refreshList() {
		
		MergeAdapter adapter = new MergeAdapter();
		
		adapter.addView(getHeaderView(subTitle));
		adapter.addAdapter(new ProductAdapter());
	
		thumbs=new ThumbnailAdapter(this, adapter,((Application)getApplication()).getCache(adapter.getCount()),IMAGE_IDS);																			
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
	
	
	
	public void onListItemClick(ListView parent, View v, int position, long id) {
		Intent i = new Intent(Alternatives.this, ProductTabs.class);
		
		ThumbnailAdapter adapter = (ThumbnailAdapter) parent.getAdapter();
        Product product = (Product) adapter.getItem(position);
        
      if(Constants.showLog) Log.v(TAG, "Product is: "+product);
        
        Bundle extras = new Bundle();
        extras.putSerializable(Constants.PRODUCT, product);
        extras.putString(Constants.SUB_HEADER_TEXT, "Highest rated products by brand");
        extras.putInt(Constants.CATEGORY_ID, categoryId);
        i.putExtras(extras);
        
		
        // Create the view using FirstGroup's LocalActivityManager
        View view = ProductsGroup.group.getLocalActivityManager()
        .startActivity("productdetail", i
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        .getDecorView();

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
		return(model);
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
	
	
	class ProductAdapter extends ArrayAdapter<Product> {
		 ProductAdapter() {
			 
			 super(Alternatives.this, R.layout.row, model);
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
	
	class ProductWrapper {
		private TextView title=null;
		private TextView rating=null;
		private TextView more=null;
		private TextView moreInBrand=null;
		private ImageView image=null;
		private View row=null;
		
		ProductWrapper(View row) {
			this.row=row;
		}
		
		void populateFrom(Product info) {
			getTitle().setText(info.getName());
			getRating().setText(info.getOverallRating()+"");
			getMoreInBrand().setText(info.getNumberOfProductsInBrand()<=1?"":info.getNumberOfProductsInBrand()+" more in brand");
			
			if (info.getS3ImageURL()!=null) {
				try {

					  if(Constants.showLog) Log.v("image url is:", info.getS3ImageURL());
		            	getImage().setTag(info.getS3ImageURL());
						
				}
				catch (Throwable t) {
					goBlooey(t);
				}
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
			if (image==null) {
				image=(ImageView)row.findViewById(R.id.thumbnail);
			}
			
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
}
