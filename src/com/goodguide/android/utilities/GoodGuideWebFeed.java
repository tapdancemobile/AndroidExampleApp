package com.goodguide.android.utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.json.JSONException;
import org.xml.json.XML;

import android.util.Log;

import com.goodguide.android.app.Products;
import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.RestClient.RequestMethod;
import com.goodguide.android.value.GenericResult;
import com.goodguide.android.value.Product;
import com.google.gson.Gson;

public class GoodGuideWebFeed {
	
private static final String TAG = "GoodGuideWebFeed";

public GoodGuideWebFeed(){
	super();	
}


public HashMap<String,List<Product>> search(String searchTerm, int categoryId, int page) {
	
	HashMap<String,List<Product>> searchResults = new HashMap<String, List<Product>>();
	List<Product> productsList = new ArrayList<Product>();
	List<Product> brandsList = new ArrayList<Product>();
	
	//	//http://iphone.goodguide.com/search?api_key=axz8424wzhthgvx4ygx6df9q&&iphone_hash=504a4636aa5cafd7e7fb3b266063c7a9e39f7c89&api_format=extended&page=1&format=xml&api_version=1.0&q=tea&adym=1&count=10000&sort_by=rating

	
	RestClient client = new RestClient("http://iphone.goodguide.com/search");    
    client.AddParam("api_key", Constants.API_KEY);
    client.AddParam("iphone_hash", Constants.ANDROID_HASH);
    client.AddParam("api_format", "extended");
    client.AddParam("page", page+"");
    client.AddParam("format", "xml");
    client.AddParam("api_version", "1.0");
    client.AddParam("q", searchTerm);
    if(categoryId > 0)
    	client.AddParam("category_id", categoryId+"");
    client.AddParam("adym", "1");
    client.AddParam("count", "10000");
    client.AddParam("sort_by", "rating");
//    
//  if(Constants.showLog) Log.v(TAG, "intSkip:"+skipCount);
    
    try {
        client.Execute(RequestMethod.GET);
    } catch (Exception e) {
        e.printStackTrace();
    }

    String response = client.getResponse();
    org.xml.json.JSONObject jobj;
     
    try 
    {
        jobj = XML.toJSONObject(response);
        org.xml.json.JSONObject  data     = jobj.getJSONObject("goodguide-response");
        
        org.xml.json.JSONArray  entities = null;
        org.xml.json.JSONObject entity = null;
        try {
        	entities     = data.getJSONArray("entities");
        } catch (org.xml.json.JSONException e) {
        	entity     = data.getJSONObject("entities");
		}
        
        //products is first in entities list
        org.xml.json.JSONObject productData = entities==null?entity:entities.getJSONObject(0);
        populateProductsFromJSON(productsList, productData, false);
        searchResults.put(Constants.PRODUCT, productsList);
        
        //if brands are available retrieve them as well
        if(entities != null) {
        	populateProductsFromJSON(brandsList, entities.getJSONObject(1), true);
        	searchResults.put(Constants.BRAND, brandsList);
        }
        
        
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    
    return searchResults;
}

public HashMap<String,List<Product>> searchFromScannerResults(String searchTerm) {
	
	HashMap<String,List<Product>> searchResults = new HashMap<String, List<Product>>();
	List<Product> productsList = new ArrayList<Product>();
	List<Product> brandsList = new ArrayList<Product>();
	
	//	//http://iphone.goodguide.com/search?api_key=axz8424wzhthgvx4ygx6df9q&&iphone_hash=504a4636aa5cafd7e7fb3b266063c7a9e39f7c89&api_format=extended&page=1&format=xml&api_version=1.0&q=tea&adym=1&count=10000&sort_by=rating

	
	RestClient client = new RestClient("http://iphone.goodguide.com/search");    
    client.AddParam("api_key", Constants.API_KEY);
    client.AddParam("iphone_hash", Constants.ANDROID_HASH);
    client.AddParam("api_format", "simple,ecobonus");
    client.AddParam("page", "1");
    client.AddParam("format", "xml");
    client.AddParam("api_version", "1.0");
    client.AddParam("q", searchTerm);
    client.AddParam("adym", "1");
    client.AddParam("count", "20");

//    
//  if(Constants.showLog) Log.v(TAG, "intSkip:"+skipCount);
    
    try {
        client.Execute(RequestMethod.GET);
    } catch (Exception e) {
        e.printStackTrace();
    }

    String response = client.getResponse();
    org.xml.json.JSONObject jobj;
     
    try 
    {
        jobj = XML.toJSONObject(response);
        org.xml.json.JSONObject  data     = jobj.getJSONObject("goodguide-response");
        
        org.xml.json.JSONArray  entities = null;
        org.xml.json.JSONObject entity = null;
        try {
        	entities     = data.getJSONArray("entities");
        } catch (org.xml.json.JSONException e) {
        	entity     = data.getJSONObject("entities");
		}
        
        //products is first in entities list
        org.xml.json.JSONObject productData = entities==null?entity:entities.getJSONObject(0);
        populateProductsFromJSON(productsList, productData, false);
        searchResults.put(Constants.PRODUCT, productsList);
        
        //if brands are available retrieve them as well
        if(entities != null) {
        	populateProductsFromJSON(brandsList, entities.getJSONObject(1), true);
        	searchResults.put(Constants.BRAND, brandsList);
        }
        
        
    }
    catch(Exception e)
    {
        e.printStackTrace();
        return null;
    }
    
    return searchResults;
}

private void populateProductsFromJSON(List<Product> productsList,
		org.xml.json.JSONObject productData, boolean isBrand) throws JSONException {
	
	org.xml.json.JSONArray  products = null;   
	
	if(productData.has("entity"))
		products  = productData.getJSONArray("entity");
	else
		return;

	if(Constants.showLog) Log.d(TAG, "***All Data from Feed***");
	if(Constants.showLog) Log.d(TAG, "Products:"+products.length());
	  
	for(int i=0; i<products.length();i++){
		org.xml.json.JSONObject productJson = products.getJSONObject(i);
		Product info = new Product();
		
		//manually set info instead of gson - xml has too many nested elements
		info.setBrand(isBrand);
		info.setObjectId(productJson.has("id")?productJson.getInt("id"):-1);
		info.setName(productJson.has("name")?productJson.getString("name"):"");
		info.setS3ImageURL(productJson.has("image-url")?productJson.getString("image-url"):"");
		info.setBaseCategoryId(productJson.has("base-category-id")?productJson.getInt("base-category-id"):-1);
		info.setBaseCategoryName(productJson.has("base-category-name")?productJson.getString("base-category-name"):"");
		info.setIngredients(productJson.has("ingredients")?productJson.getString("ingredients"):"");
		info.setIngredientScores(productJson.has("ingredient-scores")?productJson.getString("ingredient-scores"):"");
		info.setBehindTheRatingDescriptions(productJson.has("ingredient-scores")?productJson.getString("behind-the-rating-descriptions"):"");
		info.setBehindTheRatingZScores(productJson.has("behind-the-rating-z-scores")?productJson.getString("behind-the-rating-z-scores"):"");
		info.setNutritionChartURL(productJson.has("nutrition-chart-url")?productJson.getString("nutrition-chart-url"):"");
		info.setNutritionComparisonChartURL(productJson.has("nutrition-comparison-chart-url")?productJson.getString("nutrition-comparison-chart-url"):"");
		info.setNutritionLabels(productJson.has("nutrition-labels")?productJson.getString("nutrition-labels"):"");
		info.setNutritionGrams(productJson.has("nutrition-grams")?productJson.getString("nutrition-grams"):"");
		info.setNutritionPercentages(productJson.has("nutrition-percent-daily-values")?productJson.getString("nutrition-percent-daily-values"):"");
		info.setNutritionThresholds(productJson.has("nutrition-threshholds")?productJson.getString("nutrition-threshholds"):"");
		info.setVitaminLabels(productJson.has("vitamin-labels")?productJson.getString("vitamin-labels"):"");
		info.setVitaminPercentages(productJson.has("vitamin-percentages")?productJson.getString("vitamin-percentages"):"");
		info.setHealthyOptionsAvailable(productJson.has("healthy-options-available")?productJson.getString("healthy-options-available"):"");
		
		//go into nested elements here
		
		//This is the parent json to get parent category name to help with alternatives list
		org.xml.json.JSONArray  parentsJsonArray = null;
        org.xml.json.JSONObject parentsJsonObject = null;
        try {
        	parentsJsonArray     = productJson.getJSONArray("parents");
        } catch (org.xml.json.JSONException e) {
        	parentsJsonObject     = productJson.getJSONObject("parents");
		}
		
		//For multiple ratings available
	    if(parentsJsonArray !=null) {
	    	
	        for(int j=0; j<parentsJsonArray.length(); j++) {
	        	org.xml.json.JSONObject json = parentsJsonArray.getJSONObject(j);
	        	
	        	String type = json.getString("entity_type");
	        	org.xml.json.JSONObject entityJSON = json.getJSONObject("entity");
	        	String name = entityJSON.getString("name");
	        	
	        	if("category".equalsIgnoreCase(type))
	        		info.setAlternativesCategory(name);	        	
	        	}	
	        } else {
	        	String type = parentsJsonObject.getString("entity_type");
	        	org.xml.json.JSONObject entityJSON = parentsJsonObject.getJSONObject("entity");
	        	String name = entityJSON.getString("name");
	        	
	        	if("category".equalsIgnoreCase(type))
	        		info.setAlternativesCategory(name);        	
	        	}

		
		//ratings json
		org.xml.json.JSONObject ratingJson = productJson.getJSONObject("rating");
		info.setOverallRating(getNumberOrNegativeOne(ratingJson, "value"));
		
		org.xml.json.JSONObject subRatingJson = ratingJson.getJSONObject("sub-ratings");
		
		//There could not be multiple ratings...
		org.xml.json.JSONArray  ratingJsonArray = null;
	    org.xml.json.JSONObject ratingJsonObject = null;
	    try {
	    	ratingJsonArray     = subRatingJson.getJSONArray("rating");
	    } catch (org.xml.json.JSONException e) {
	    	ratingJsonObject     = subRatingJson.getJSONObject("rating");
		}
		
	    //For multiple ratings available
	    if(ratingJsonArray !=null) {
	    	
	        for(int j=0; j<ratingJsonArray.length(); j++) {
	        	org.xml.json.JSONObject json = ratingJsonArray.getJSONObject(j);
	        	
	        	String name = json.getString("name");
	        	float value = getNumberOrNegativeOne(json, "value");
	        	
	        	if("environmental".equalsIgnoreCase(name))
	        		info.setEnvironmentalRating(value);
	        	if("social".equalsIgnoreCase(name))
	        		info.setSocialRating(value);
	        	if("health-and-safety".equalsIgnoreCase(name))
	        		info.setHealthRating(value);
	        }
	        //For a single rating
	    } else {
	    	String name = ratingJsonObject.getString("name");
	    	float value = getNumberOrNegativeOne(ratingJsonObject, "value");
	    	
	    	if("environmental".equalsIgnoreCase(name))
	    		info.setEnvironmentalRating(value);
	    	if("social".equalsIgnoreCase(name))
	    		info.setSocialRating(value);
	    	if("health-and-safety".equalsIgnoreCase(name))
	    		info.setHealthRating(value);
	    }
		
		productsList.add(info);
		
//        	if(Constants.showLog) Log.v(TAG, "***Product "+i+"***");
//        	if(Constants.showLog) Log.v(TAG, info.toString());
//        	if(Constants.showLog) Log.v(TAG, "***********");
	}
}

private void populateProductsFromTheFindJSON(List<Product> productsList,
		org.xml.json.JSONObject productData) throws JSONException {
	org.xml.json.JSONArray  products     = productData.getJSONArray("item");

	if(Constants.showLog) Log.d(TAG, "***All Data from Feed***");
	if(Constants.showLog) Log.d(TAG, "Products:"+products.length());
	  
	for(int i=0; i<products.length();i++){
		org.xml.json.JSONObject productJson = products.getJSONObject(i);
		Product info = new Product();
		
		//manually set info instead of gson - xml has too many nested elements
		info.setName(productJson.has("fattitle")?productJson.getString("fattitle"):"");		
		productsList.add(info);
		
        	if(Constants.showLog) Log.v(TAG, "***Product "+i+"***");
        	if(Constants.showLog) Log.v(TAG, info.toString());
        	if(Constants.showLog) Log.v(TAG, "***********");
	}
}

private void populateProductsFromGoogleJSON(List<Product> productsList,
		org.xml.json.JSONObject productData) throws JSONException {
	
	org.xml.json.JSONArray  products = null;
	org.xml.json.JSONObject  product = null;
	
	if(productData.has("item")) {
		try {
			products  = productData.getJSONArray("item");
	    } catch (org.xml.json.JSONException e) {
	    	product  = productData.getJSONObject("item");
		}
	}
	else
		return;

	if(Constants.showLog) Log.d(TAG, "***All Data from Feed***");
	if(Constants.showLog) Log.d(TAG, "Products:"+(products==null?0:products.length()));
	  
	
	if(products == null) {
		products = new org.xml.json.JSONArray();
		products.put(product);
	}
	
	for(int i=0; i<products.length();i++){
		org.xml.json.JSONObject productJson = products.getJSONObject(i);
		Product info = new Product();
		
		//manually set info instead of gson - xml has too many nested elements
		info.setName(productJson.has("title")?productJson.getString("title"):"");		
		productsList.add(info);
		
        	if(Constants.showLog) Log.v(TAG, "***Product "+i+"***");
        	if(Constants.showLog) Log.v(TAG, info.toString());
        	if(Constants.showLog) Log.v(TAG, "***********");
	}
}

public float getNumberOrNegativeOne(org.xml.json.JSONObject jsonString, String key) {			
	float value = -1;
	if(jsonString.has(key)) {
		try {
			value = (float)jsonString.getDouble(key);
		} catch(org.xml.json.JSONException e) {
			Log.v("", "Json is not a number - setting to -1");
		}
	}
		return value;
	}


public List<Product> lookForProductOnTheFind(String barcode) {
		
		List<Product> productsList = new ArrayList<Product>();
		
		//http://www.thefind.com/query.php?xmlapi=1&query=070501092002
		
		RestClient client = new RestClient("http://www.thefind.com/query.php");    
	    client.AddParam("xmlapi", "1");
	    client.AddParam("query", barcode);
	    
	    try {
	        client.Execute(RequestMethod.GET);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    String response = client.getResponse();
	    org.xml.json.JSONObject jobj;
	     
	    try 
	    {
	        jobj = XML.toJSONObject(response);
	        org.xml.json.JSONObject  res     = jobj.getJSONObject("response");
	        org.xml.json.JSONObject  data     = res.getJSONObject("results");
	        
	        org.xml.json.JSONArray  entities = null;
	        org.xml.json.JSONObject entity = null;
	        try {
	        	entities     = data.getJSONArray("itemlist");
	        } catch (org.xml.json.JSONException e) {
	        	try{
	        		entity     = data.getJSONObject("itemlist");
	        	}catch (org.xml.json.JSONException f) {
	        		//return null if no results
	        		return null;
	        	}
			}
	        
	        //products is first in entities list
	        org.xml.json.JSONObject productData = entities==null?entity:entities.getJSONObject(0);
	        populateProductsFromTheFindJSON(productsList, productData);
	        
	        
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	    
	    return productsList.size()==0?null:productsList;
}


public List<Product> lookForProductOnGoogle(String barcode) {
		
		List<Product> productsList = new ArrayList<Product>();
		
		//http://www.thefind.com/query.php?xmlapi=1&query=070501092002
		
		RestClient client = new RestClient("http://www.google.com/base/feeds/snippets");    
	    client.AddParam("alt", "rss");
	    client.AddParam("bq", barcode);
	    
	    try {
	        client.Execute(RequestMethod.GET);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    String response = client.getResponse();
	    org.xml.json.JSONObject jobj;
	     
	    try 
	    {
	        jobj = XML.toJSONObject(response);
	        org.xml.json.JSONObject  data     = jobj.getJSONObject("rss");
	        
	        org.xml.json.JSONArray  entities = null;
	        org.xml.json.JSONObject entity = null;
	        try {
	        	entities     = data.getJSONArray("channel");
	        } catch (org.xml.json.JSONException e) {
	        	try{
	        		entity     = data.getJSONObject("channel");
	        	}catch (org.xml.json.JSONException f) {
	        		//return null if no results
	        		return null;
	        	}
			}
	        
	        //products is first in entities list
	        org.xml.json.JSONObject productData = entities==null?entity:entities.getJSONObject(0);
	        populateProductsFromGoogleJSON(productsList, productData);
	        
	        
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	    
	    return productsList.size()==0?null:productsList;
}


public void callGoodguideAnalytics(int entity, String type, String upc, String event, String room) {
	//break from method if not connected
	if(!Constants.connected)
		return;
	
	double lat;
	double lon;

	lat = Constants.location == null?0:Constants.location.getLatitude();
	lon = Constants.location == null?0:Constants.location.getLongitude();
	
	HttpURLConnection connection = null;
	DataOutputStream outputStream = null;

	String urlServer = "http://packrat.goodguide.com/logging/views";
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary =  "0xKhTmLbOuNdArY";

	try
	{

	URL url = new URL(urlServer);
	connection = (HttpURLConnection) url.openConnection();

	// Allow Inputs & Outputs
	connection.setDoInput(true);
	connection.setDoOutput(true);
	connection.setUseCaches(false);

	// Enable POST method
	connection.setRequestMethod("POST");

	connection.setRequestProperty("Connection", "Keep-Alive");
	connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

	outputStream = new DataOutputStream( connection.getOutputStream() );
	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"medium\"\r\n\r\n");
	outputStream.writeBytes("android");
	outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"entity_type\"\r\n\r\n");
	outputStream.writeBytes(type);
	outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"upc\"\r\n\r\n");
	outputStream.writeBytes(upc);
	outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"id\"\r\n\r\n");
	if(entity==0)
		outputStream.writeBytes("");
	else
		outputStream.writeBytes(""+entity);
	outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"event\"\r\n\r\n");
	outputStream.writeBytes(event);
	outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"store\"\r\n\r\n");
	outputStream.writeBytes("");
	outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"zip\"\r\n\r\n");
	outputStream.writeBytes("");
	outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"lat\"\r\n\r\n");
	if(lat==0)
		outputStream.writeBytes("");
	else
		outputStream.writeBytes(""+lat);
	outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"lon\"\r\n\r\n");
	if(lon==0)
		outputStream.writeBytes("");
	else
		outputStream.writeBytes(""+lon);
	outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"room\"\r\n\r\n");
	outputStream.writeBytes(room);
	outputStream.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
	
	outputStream.writeBytes("Content-Disposition: form-data; name=\"publish\"\r\n\r\n");
	outputStream.writeBytes("1");
	outputStream.writeBytes(lineEnd);
	outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

	Log.v("\n****OutputStream\n:", outputStream.toString());
	
	// Responses from the server (code and message)
	int serverResponseCode = connection.getResponseCode();
	String serverResponseMessage = connection.getResponseMessage();
	
	Log.v("\n****Analytics Input Parameters: entity:", entity + ", type:" + type + " upc:" + upc + " event:" + event + " room: " + room + "\n");
	Log.v("\n****Analytics ResponseCode:", serverResponseCode+"\n");
	Log.v("\n****Analytics ResponseMessage:", serverResponseMessage+"\n");
	
	outputStream.flush();
	outputStream.close();
	}
	catch (Exception ex)
	{
		Log.v("\n****Exception:",ex.getMessage());
	}
}

}
