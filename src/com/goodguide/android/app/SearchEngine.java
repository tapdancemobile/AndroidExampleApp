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

public class SearchEngine extends GenericListActivity {

	private ArrayList<Product> productsModel = null;
	private ArrayList<Product> brandsModel = null;
	private Integer appCategoryID;
	private int pageCount = 1;
	private ProgressDialog myProgressDialog;

	private Handler searchHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(searchThread != null && !searchThread.isInterrupted()) {
				myProgressDialog.dismiss();
				
				Intent i = new Intent(SearchEngine.this, Search.class);

				Bundle extras = new Bundle();
				extras.putSerializable(Constants.SEARCH_PRODUCT_RESULTS, productsModel);
				extras.putSerializable(Constants.SEARCH_BRANDS_RESULTS, brandsModel);
				extras.putString(Constants.QUERY, query);
				i.putExtras(extras);

				// Create the view using FirstGroup's LocalActivityManager
				View view = SearchGroup.group.getLocalActivityManager()
						.startActivity("search",
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();

				// Again, replace the view
				SearchGroup.group.replaceView(view);
				GoodGuide.searchButtonClicked();
				
				SearchEngine.this.finish();
			}
		}
	};

	private String query;
	private Thread searchThread;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.search);
		
			Bundle extras = getIntent().getExtras();
	
			// hide empty view during initial load - looks bad
			SearchEngine.this.setVisible(false);
	
			productsModel = new ArrayList<Product>();
			brandsModel = new ArrayList<Product>();
	
			Intent intent = getIntent();
	
			if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
				query = intent.getStringExtra(SearchManager.QUERY);
				
				trackPageView(Constants.PAGEVIEW_SEARCH+query+" - page:"+pageCount);
	
				// activity indicator
				// set busy dialog
				myProgressDialog = new ProgressDialog(SearchEngine.this);
				myProgressDialog.setTitle("Please Wait");
				myProgressDialog.setMessage("Searching GoodGuide for '"+query+"'");
				myProgressDialog.setIndeterminate(true);
				myProgressDialog.setCancelable(true);
				
				myProgressDialog.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface arg0) {
							searchThread.interrupt();
							SearchEngine.this.finish();
					}
		        });
	
				myProgressDialog.show();
	
				searchThread = new Thread(new Runnable() {
					public void run() {
						if(!searchThread.isInterrupted()){
							performSearch(query);
							searchHandler.sendEmptyMessage(0);
						}
					}
				});
				searchThread.start();
	
			} 
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

		while (iter.hasNext()) {
			Product info = iter.next();
			productsModel.add(info);
		}

		// get brand results if they exist
		brandResults = brandResults == null ? new ArrayList<Product>()
				: brandResults;
		Log.v(TAG, "results:" + brandResults.size());
		Iterator<Product> iter2 = brandResults.iterator();

		while (iter2.hasNext()) {
			Product info = iter2.next();
			brandsModel.add(info);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		//way to stop a thread according to Oracle
		if(searchThread != null) {
		Thread tempThread = new Thread();
		tempThread = searchThread;
		searchThread = null;
		tempThread.interrupt();
		}
	}
	
}