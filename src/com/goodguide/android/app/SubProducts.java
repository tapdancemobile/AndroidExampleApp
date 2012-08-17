package com.goodguide.android.app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import com.goodguide.android.app.Products.ProductWrapper;
import com.goodguide.android.app.R;
import com.goodguide.android.business.GoodGuideBusiness;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.GoodGuideDataSource;
import com.goodguide.android.utilities.MergeAdapter;
import com.goodguide.android.value.Category;
import com.goodguide.android.value.Product;

public class SubProducts extends GenericListActivity {
	
	GoodGuideDataSource ds;
	//both of these are used to display the activity indicator
	//for initializing the db upon the user's first interaction
	private ProgressDialog myProgressDialog;
	protected MergeAdapter adapter;
	protected ThumbnailAdapter thumbs;
	private static final int[] IMAGE_IDS={R.id.thumbnail};
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		       //We only retrieve the cached value
	        List<HashMap<String,List<Category>>> subCategories = Constants.cachedParentCategoryMap.get(msg.obj.toString());
	        
	        adapter = new MergeAdapter();
	        
	        //Create the list in an iterative fashion
	        Iterator<HashMap<String, List<Category>>> subCategoriesIter = subCategories.iterator();
	        
	        while(subCategoriesIter.hasNext()) {
	        	HashMap<String,List<Category>> info = subCategoriesIter.next();
	        	
	        	Iterator<String> keySetIter = info.keySet().iterator();
	        	while(keySetIter.hasNext()) {
	        		//get key and add category header
	        		String key = keySetIter.next();
//	        		Log.v("Key:", key);
	        		adapter.addView(getHeaderView(key));
	        		
				        		//get SubProducts and add them below category
				        		List<Category> subProducts = info.get(key);
				        		adapter.addAdapter(new CategoryAdapter(subProducts));
	        	}
	        }
	   
			
	        thumbs=new ThumbnailAdapter(SubProducts.this, adapter,((Application)getApplication()).getCache(adapter.getCount()),IMAGE_IDS);																			
			setListAdapter(thumbs);
			myProgressDialog.dismiss();

			
		}
	};
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genericlist);
        
        Bundle extras = getIntent().getExtras();
        
        //set title of screen
        final String title = extras.getString(Constants.HEADER_TEXT);
        		
        		// activity indicator
        		// set busy dialog
        		myProgressDialog = ProgressDialog.show(getParent(),
        				"Please Wait", "Retrieving category information", true);
        		
        			new Thread(new Runnable() {
        				public void run() {
        					GoodGuideBusiness.getCategoriesByVerticalType(title);
        			        Message message = new Message();
        			        message.obj = title;
        					handler.sendMessage(message);
        				}
        			}).start();
        		}
        
    
	private View getRowView(final Category category) {
		
		// get the instance of the LayoutInflater
		LayoutInflater inflater = (LayoutInflater) getParent().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.categories_row, null);
		
		TextView titleText = (TextView)view.findViewById(R.id.titletext);
		titleText.setText(category.getName());
		
		ImageView productImage = (ImageView)view.findViewById(R.id.thumbnail);
		String imageName = "img_"+category.getImageName().substring(0, category.getImageName().length()-4);
		productImage.setImageResource(getIcon(imageName));
	
		view.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(SubProducts.this, Products.class);
		        i.putExtra(Constants.HEADER_TEXT, category.getName());
		        i.putExtra(Constants.SUB_HEADER_TEXT, "Highest rated products by brand");
		        i.putExtra(Constants.CATEGORY_ID, category.getObjectId());
		        i.putExtra(Constants.CATEGORY_TITLE, category.getName());
		        i.putExtra(Constants.SHOW_ALL, false);
		        i.putExtra(Constants.INGREDIENTS_TO_AVOID, category.getIngredientsToAvoid());

		        // Create the view using FirstGroup's LocalActivityManager
		        View view = ProductsGroup.group.getLocalActivityManager()
		        .startActivity("products", i
		        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
		        .getDecorView();

		        view.setTag(Constants.PAGEVIEW_BROWSE_SUBCATEGORY+category.getName());
		        
		        // Again, replace the view
		        ProductsGroup.group.replaceView(view);
			}
		});
		
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

	@Override
	public void onDestroy() {
		super.onDestroy();	
		thumbs.close();
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
	
	public void onListItemClick(ListView parent, View v, int position, long id) {
		
		ThumbnailAdapter adapter = (ThumbnailAdapter) parent.getAdapter();
        Category category = (Category) adapter.getItem(position);
        
		Intent i = new Intent(SubProducts.this, Products.class);
        i.putExtra(Constants.HEADER_TEXT, category.getName());
        i.putExtra(Constants.SUB_HEADER_TEXT, "Highest rated products by brand");
        i.putExtra(Constants.CATEGORY_ID, category.getObjectId());
        i.putExtra(Constants.CATEGORY_TITLE, category.getName());
        i.putExtra(Constants.SHOW_ALL, false);
        i.putExtra(Constants.INGREDIENTS_TO_AVOID, category.getIngredientsToAvoid());

        // Create the view using FirstGroup's LocalActivityManager
        View view = ProductsGroup.group.getLocalActivityManager()
        .startActivity("products", i
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        .getDecorView();

        view.setTag(Constants.PAGEVIEW_BROWSE_SUBCATEGORY+category.getName());
        
        // Again, replace the view
        ProductsGroup.group.replaceView(view);
	}
	
	class CategoryAdapter extends ArrayAdapter<Category> {
		CategoryAdapter(List<Category> model) {
			 
			 super(SubProducts.this, R.layout.row, model);
		}
		
		public View getView(int position, View convertView,
												ViewGroup parent) {
			View row=convertView;
			CategoryWrapper wrapper=null;
			
			if (row==null) {													
				LayoutInflater inflater=getLayoutInflater();
				
				row=inflater.inflate(R.layout.categories_row, null);
				wrapper=new CategoryWrapper(row);
				row.setTag(wrapper);
			}
			else {
				wrapper=(CategoryWrapper)row.getTag();
			}
			
			wrapper.populateFrom(getItem(position));
			
			return(row);
		}
	}
	
	class CategoryWrapper {
		private TextView title=null;
		private ImageView image=null;
		private View row=null;
		
		CategoryWrapper(View row) {
			this.row=row;
		}
		
		void populateFrom(Category info) {
			getTitle().setText(info.getName());
			getImage(info);
		}
		
		TextView getTitle() {
			if (title==null) {
				title=(TextView)row.findViewById(R.id.titletext);
			}
			
			return(title);
		}
		
		
		ImageView getImage(Category info) {
			if (image==null) {
				image=(ImageView)row.findViewById(R.id.thumbnail);
			}
			
			String imageName = "img_"+info.getImageName().substring(0, info.getImageName().length()-4);
			image.setImageBitmap(getIcon(imageName,75));
			return image;
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
