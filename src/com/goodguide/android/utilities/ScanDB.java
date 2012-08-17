package com.goodguide.android.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.goodguide.android.business.GoodGuideBusiness;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.value.Product;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ScanDB extends AsyncTask<String, Void, Void> {


	private Handler dbSearchHandler;
	private Bundle  dbSearchResultsBundle;

	public ScanDB(Handler handler) {
		this.dbSearchHandler = handler;
	}
	
	
	@Override
	protected Void doInBackground(String... params) {
		dbSearchResultsBundle = searchForUPC(params[0]);
		return null;
	}

@Override
protected void onPostExecute( Void result )  {
	Message msg = new Message();
	msg.setData(dbSearchResultsBundle);
	dbSearchHandler.sendMessage(msg);
}


private Bundle searchForUPC(String barcode) {
		
		try {
			
		//get rid of first 0 to get upc format from ean
		barcode = barcode.replaceFirst("0", "");
		Log.d("RLSample","BARCODE: "+barcode);
		
		//search for barcode
		GoodGuideBusiness business = new GoodGuideBusiness();
		Product foundProduct = null;
		String productName = null;
		
		//look in db first for complete product information
		foundProduct = business.lookForProductInDB(barcode);
		
		//looking for a product name to associate with the upc in the following 2 calls
		GoodGuideWebFeed webFeed = new GoodGuideWebFeed();
		List<Product> productsList = null;
		
		if(foundProduct != null) {	
			foundProduct.setAlternativesCategoryId(new GoodGuideBusiness().getCategoryIdByProductId(foundProduct.getObjectId()));
		}
		else if(foundProduct == null) {
			Log.v("idiot check", "productsList:"+productsList+", foundProduct:"+foundProduct);
			productsList = webFeed.lookForProductOnTheFind(barcode);
		}
		if(foundProduct == null && productsList == null) {
			Log.v("idiot check", "productsList:"+productsList+", foundProduct:"+foundProduct);
			productsList = webFeed.lookForProductOnGoogle(barcode);
		}
		
		//finally hit the goodguide search api to find the product details
		if(foundProduct == null && productsList != null) {
			
			//create url search parameter with returned names
			productsList = productsList==null?new ArrayList<Product>():productsList;
			Iterator<Product> iter = productsList.iterator();
			
			//need the product name to put in the bundle in case can't find on goodguide
			Product firstProduct = (Product)productsList.get(0);
			productName = firstProduct.getName();
			
			String queryString = "";
			
			int i = 0;
			while(iter.hasNext() && i < Constants.SCAN_PRODUCTS_MATCH_LIMIT) {
				Product info = iter.next();
				
				//clean up name
				String name = info.getName().replaceAll(" ", "zazbzczd");
				name = name.replaceAll("[^A-Za-z]", "");
				name = name.replaceAll("zazbzczd", "%20");
				
				i++;
				
				queryString += "("+name+")";
				if(iter.hasNext() && i < Constants.SCAN_PRODUCTS_MATCH_LIMIT)
					queryString += "%20OR%20";
				
			}
			
			HashMap<String, List<Product>> foundProducts = webFeed.searchFromScannerResults(queryString);
			
			List<Product> products = foundProducts.get(Constants.PRODUCT);
			List<Product> brands = foundProducts.get(Constants.BRAND);
			
			//give preference to products found
			if(products != null && products.size() > 0)
				foundProduct = products.get(0);
			else if(brands != null && brands.size() > 0)
				foundProduct = brands.get(0);
			else
				foundProduct = null;
		}
		
		
		//if we found a product or brand we can add it to the recently scanned list 
		//which also populates the detail info at the top of the screen
		
		boolean exists = false;
		
		
		if(foundProduct != null) {
			foundProduct.setUpc(barcode.toString());
			Constants.getRecentlyScanned().add(foundProduct);
			exists = true;
		} 
		
		Bundle bundle = new Bundle();
		bundle.putBoolean(Constants.EXISTS, exists);
		bundle.putString(Constants.PRODUCT, productName);
		bundle.putString(Constants.UPC, barcode);
		bundle.putInt(Constants.CATEGORY_ID, foundProduct.getAlternativesCategoryId());
		bundle.putInt(Constants.OBJECT_ID,foundProduct != null?foundProduct.getObjectId():0);
		return bundle;
		
		} catch(Exception e) {
			Bundle bundle = new Bundle();
			bundle.putBoolean(Constants.EXISTS, false);
			bundle.putString(Constants.PRODUCT, "");
			bundle.putString(Constants.UPC, barcode);
			return bundle;
		}
	} 

}
