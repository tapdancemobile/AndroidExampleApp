package com.goodguide.android.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.goodguide.android.utilities.GoodGuideDataSource;
import com.goodguide.android.utilities.MyLocation;
import com.goodguide.android.value.Category;
import com.goodguide.android.value.GenericResult;
import com.goodguide.android.value.Product;

public class Constants {

	public static final int LOGIN_DIALOG_ID = 42;
	public static final int PUBLISH_DIALOG_ID = 43;
	public static final String MOST_RECENT = "mostRecent";
	public static final String CATEGORIES = "Verticals";
	public static final String RANDOM = "Random";
	public static final String SEARCH = "Search";
	public static final String INFO = "Scan";
	
	
	/**Show Log**/
	public static final boolean showLog = false;
	
	public static final String APP_CATEGORY = "APP_CATEGORY";
	public static final int MOST_RECENT_CATEGORY_ID = 265;
	public static int DEFAULT_RECORD_FETCH_COUNT = 20;
	public static int DEFAULT_FORMAT = 0;
	public static String INITIALIZED = "INITIALIZED";
	private static Hashtable<Integer, List<GenericResult>> feedData;
	public static final String TYPE_AD = "AD";
	public static final String TYPE_MORE = "More";
	public static final boolean SHOW_ADS = true;
	public static final boolean NO_ADS = false;
	public static final int IS_PHONE = 1;
	
	public static final int TAB_PRODUCTS = 0;
	public static final int TAB_SCAN = 1;
	
	public static final int MENU_BROWSE = 0;
	public static final int MENU_SCAN = 1;
	public static final int MENU_SEARCH = 2;
	
	public static final String ENTRY_INFO = "ENTRY_INFO";
	public static final String CSS = 
	"<style type=\"text/css\">" +
		"body {background:#e7ecef;font-family:Arial,Helvetica,sans-serif;}"+
		"div.contentContainer h1 {border-bottom: 1px solid #c9d1d1;padding:10px 0 5px 0;margin:0 0 10px 0;}"+
		"strong {color:#009eba;}"+
	"</style>";
	public static final String SCRUB_BAR = "scrubbar";
	public static final String VOLUME_BAR = "volumebar";
	public static final String NO_SEARCH_RESULTS = "No Search Results";
	public static final String TITLE = "title";
	public static final String MOST_RECENT_JSON = "mostRecent";
	public static final String RANDOM_JSON = "random";
	public static final String FAVORITES = "favorites";
	public static final String LAST_ACTIVITY = "lastActivity";
	public static final int MENU_ENTRY = 6;
	public static final int MENU_CATEGORY = 7;
	public static final String CATEGORY_TITLE = "categoryList";

	public static final int DENSITY_MEDIUM = 160;
	public static final int DENSITY_HIGH = 240;
	
	//Google Analytics variables
	public static final String GOOGLE_ANALYTICS_PROFILE_UA = "UA-7045112-7";//replace this with value from profile
	public static final int GOOGLE_ANALYTICS_DISPATCH_INTERVAL = 10;//in seconds
	public static final String PAGEVIEW_BROWSE_TOP_LEVEL = "Browse - Top Level";
	public static final String PAGEVIEW_BROWSE_CATEGORY = "Browse Category - ";
	public static final String PAGEVIEW_BROWSE_SUBCATEGORY = "Browse Subcategory - ";
	public static final String PAGEVIEW_BROWSE_ALL_SUBCATEGORY = "Browse All Subcategory - ";
	public static final String PAGEVIEW_VIEW_PRODUCT_DETAILS = "View Product Details - ";
	public static final String PAGEVIEW_VIEW_PRODUCT_ALTERNATIVES = "View Product Alternatives - ";
	public static final String PAGEVIEW_VIEW_PRODUCT_IMAGE = "View Product Image - ";
	public static final String PAGEVIEW_SCAN_HOME = "View Scan Home";
	public static final String PAGEVIEW_SCAN_CAMERA = "View Scan Camera View";
	public static final String PAGEVIEW_SEARCH_TOP_LEVEL = "Search - Top Level ";
	public static final String PAGEVIEW_SEARCH = "Search GoodGuide - ";
	public static final String PAGEVIEW_APP_LAUNCHED = "App Launched";
	public static final String PAGEVIEW_APP_FOREGROUND = "App Foreground";
	public static final String PAGEVIEW_APP_BACKGROUND = "App Background";
	public static final String PAGEVIEW_APP_CLOSED = "App Closed";

	
	//Vertical counts
	public static final String ALL_PRODUCTS_COUNT = "92,000";
	public static final String ALL_APPLIANCES_COUNT = "2,500";
	public static final String ALL_APPAREL_COUNT = "100";
	public static final String ALL_BABY_AND_KIDS_COUNT = "3,000";
	public static final String ALL_ELECTRONICS_COUNT = "500";
	public static final String ALL_FOOD_COUNT = "22,000";
	public static final String ALL_HOUSEHOLD_COUNT = "4,000";
	public static final String ALL_LIGHTING_COUNT = "7,000";
	public static final String ALL_PERSONALCARE_COUNT = "52,000";
	public static final String ALL_PETFOOD_COUNT = "1,500";
	
	//Category Names
	public static final String[] allProductTitles = new String[]{"Air Conditioners", "Action Figures, Dolls, & Stuffed Animals", "Activewear", "Air Conditioners", "Air Fresheners", "Arts & Music", "Baby & Toddler Toys", "Baby Care", "Baby Food", "Bath, Shower & Soap", "Breads & Baked Goods", "Breakfast", "Candy", "Canned Foods", "Cat Food", "Cell Phones", "Coffee", "Condiments, Dips, & Salad Dressings", "Dairy & Dairy Substitutes", "Deodorants & Antiperspirants", "Dishwashers", "Dishwashing", "Dog Food", "Dresses", "Drinks", "Eye & Ear Care", "Feminine Hygiene", "Fluorescent", "Foot & Nail Care", "Fragrance & Perfumes", "Fresh Meat & Seafood", "Fresh Produce", "Frozen Foods", "Games & Puzzles", "General Toys & Children's Products", "Hair Care", "Home Paper Products", "Household Cleaners", "Incandescent", "Jeans", "LED Lighting", "Laundry", "Learning", "Makeup", "Medicine Cabinet", "Men's Grooming", "Office Paper Products", "Oral Care", "Packaged Grains", "Packaged Meals and Sides", "Pants, Shorts", "Pretend Play & Dress-Up", "Refrigerators and Freezers", "Shirts, Blouses, Polos, T-Shirts, Tanks", "Skin Care", "Skirts", "Snacks", "Sports & Outdoor", "Sun Care", "Sweaters, Hoodies, Sleep & Lounge", "Teas", "Trains, Vehicles, & Construction Toys", "Washers and Dryers"};	
	public static final String[] allAppliancesTitles = new String[]{"Air Conditioners", "Dishwashers", "Refrigerators and Freezers", "Washers and Dryers"};
	public static final String[] allApparelTitles = new String[]{"Activewear", "Dresses", "Jeans", "Pants, Shorts", "Shirts, Blouses, Polos, T-Shirts, Tanks", "Skirts", "Sweaters, Hoodies, Sleep & Lounge"};
	public static final String[] allBabyAndKidsTitles = new String[]{"Action Figures, Dolls, & Stuffed Animals", "Arts & Music", "Baby Care", "Baby Food", "Baby & Toddler Toys", "Games & Puzzles", "General Toys & Children's Products", "Learning", "Pretend Play & Dress-Up", "Sports & Outdoor", "Trains, Vehicles, & Construction Toys"};
	public static final String[] allElectronicsTitles = new String[]{"Cell Phones"};
	public static final String[] allFoodTitles = new String[]{"Breads & Baked Goods", "Breakfast", "Candy", "Canned Foods", "Coffee", "Condiments, Dips, & Salad Dressings", "Dairy & Dairy Substitutes", "Drinks", "Fresh Meat & Seafood", "Fresh Produce", "Frozen Foods", "Packaged Grains", "Packaged Meals and Sides", "Snacks", "Teas"};
	public static final String[] allHouseholdCleanersTitles = new String[]{"Air Fresheners", "Dishwashing", "Home Paper Products", "Household Cleaners", "Laundry", "Office Paper Products"};
	public static final String[] allLightingTitles = new String[]{"Fluorescent", "Incandescent", "LED Lighting"};
	public static final String[] allPersonalCareTitles = new String[]{"Bath, Shower & Soap", "Deodorants & Antiperspirants", "Eye & Ear Care", "Feminine Hygiene", "Foot & Nail Care", "Fragrance & Perfumes", "Hair Care", "Makeup", "Medicine Cabinet", "Men's Grooming", "Oral Care", "Skin Care", "Sun Care"};
	public static final String[] allPetFoodTitles = new String[]{"Dog Food", "Cat Food"};
	
	//Exclude ingredients list
	public static final String[] excludeIngredientsCategories = new String[]{"Home Paper Products", "Office Paper Products"};
	
	public static final String VERSION_CODE_CACHED = "Version_Code_Cached";
	
	public static final String PRODUCTS_VERTICAL_TYPE = "All Products";
	public static final String APPLIANCES_VERTICAL_TYPE = "Appliances";
	public static final String APPAREL_VERTICAL_TYPE = "Apparel";
	public static final String BABIES_AND_KIDS_VERTICAL_TYPE = "Babies and Kids";
	public static final String ELECTRONICS_VERTICAL_TYPE = "Electronics";
	public static final String FOOD_VERTICAL_TYPE = "Food";
	public static final String HOUSEHOLD_CLEANERS_VERTICAL_TYPE = "Household";
	public static final String LIGHTING_VERTICAL_TYPE = "Lighting";
	public static final String PERSONAL_CARE_VERTICAL_TYPE = "Personal Care";
	public static final String PET_FOOD_VERTICAL_TYPE = "Pet Food";
	
	
	public static final String HEADER_TEXT = "HEADER_TEXT";
	public static final String SUB_HEADER_TEXT = "SUB_HEADER_TEXT";
	public static final String CATEGORY_ID = "CATEGORY_ID";
	public static final String SHOW_ALL = "SHOW_ALL";
	public static final float OVERALL_FEATURED_PRODUCTS_THRESHHOLD = 7.1f;
	public static final int FEATURED_PRODUCTS_COUNT_LIMIT = 25;
	public static final String INGREDIENTS_TO_AVOID = "IngredientsToAvoid";
	public static final String PRODUCT = "PRODUCT";
	public static final String BRAND = "BRAND";
	public static final String API_KEY = "28jhts3w5whg4sqnd85thxz7";
	public static final String ANDROID_HASH = "504a4636aa5cafd7e7fb3b266063c7a9e39f7c89";
	
	public static final String SEARCH_PRODUCT_RESULTS = "SEARCH_PRODUCT_RESULTS";
	public static final String SEARCH_BRANDS_RESULTS = "SEARCH_BRAND_RESULTS";
	public static final String QUERY = "query";
	
	public static int randomBetween1and100 = 0;
	private static List<GoodGuideEntry> mostRecentList;
	private static ArrayList<GoodGuideEntry> randomList;
	private static boolean isInitialized;
	private static ArrayList<GenericResult> favorites;
	private static ArrayList<GenericResult> mostRecent;
	private static ArrayList<GenericResult> random;
	private static int selectedMenuItem;
	private static int deviceResolution;
	public static boolean register = true;
	public static boolean connected = false;
	public static int versionCode = 1;
	public static int lastActivity;
	public static int screenSize;
	public static GoodGuideDataSource ds;
	public static HashMap<String,List<HashMap<String,List<Category>>>> cachedParentCategoryMap;
	public static List<Product> cachedProductList;
	public static String SUBCATEGORY = "SubCategory";
	public static final String SEARCH_NETWORK_DOWN_MESSAGE = "'Search' requires an active connection to the internet. Please connect to a network and try again.";
	public static final String BROWSE_NETWORK_DOWN_MESSAGE = "'Browse' requires an active connection to the internet. Please connect to a network and try again.";
	public static final String LOW_CONCERN = "LI";
	public static final String MED_CONCERN = "MI";
	public static final String HIGH_CONCERN = "HI";
	public static final String INGREDIENTS_CSS = 
		"<style type=\"text/css\">" +
			"low {color:#7676FE;}"+
			"med {color:#FEAC5E;}"+
			"high {color:#D45555;}"+
			"black {color:black;}"+
		"</style>";
	public static final String NO_INGREDIENTS_FOUND = "NO_INGREDIENTS_FOUND";
	public static final String NO_INGREDIENTS_MSG = "GoodGuide is a work-in-progress, and we do not yet have ingredients for this product";
	public static final String SEARCH_BARCODE_RESULT = "search";
	public static final String RECENTLY_SCANNED = "recently_scanned";
	public static List<Product> recentlyScanned = new ArrayList<Product>();
	public static Product scannedProduct;
	public static Location location;
	public static DisplayMetrics metrics;
	
	public static final String RECENT_SEARCH_PRODUCTS = "recent_search_products";
	public static final String RECENT_SEARCH_BRANDS = "recent_search_brands";
	public static final String RECENT_QUERY = "recent_query";
	public static ArrayList<Product> recentSearchProductResults = new ArrayList<Product>();
	public static ArrayList<Product> recentSearchBrandResults = new ArrayList<Product>();
	public static String recentQuery = null;
	
	public static final int SCAN_HISTORY_LIMIT = 10;
	public static final int SCAN_PRODUCTS_MATCH_LIMIT = 2;
	public static final String EXISTS = "EXISTS";
	public static final String ACTION_SCANBUTTON = "Scan Button";
	public static final String ACTION_PRODUCTSCANNED = "Product Scanned";
	public static final String CATEGORY_SCAN = "Scanner";
	public static final String LABEL_SCAN = "Pressed";
	public static final String LABEL_NO_UPC = "No UPC found for scan data";
	public static final String LABEL_NO_RATING = "No rating found for upc:";
	public static final String RESULT = "Result";
	public static final String UPC = "upc";
	public static final String PAGEVIEW_NEW_INSTALLATION = "New Installation of App";
	public static final String PAGEVIEW_UPGRADE = "Upgrade of App";
	public static final String OBJECT_ID = "objectId";
	

	
	public static Hashtable<Integer, List<GenericResult>> getFeedData() {
		return feedData;
	}

	public static void setFeedData(Hashtable<Integer, List<GenericResult>> mFeedData) {
		feedData = mFeedData;
	}
	
	public static int getRandomBetween1and100(){
		java.util.Random randomGenerator = new java.util.Random();
		return randomGenerator.nextInt(100);
	}
	
	public static String formatForFB(String description, int maxlength) {
		String descriptionLengthResult = description.length()>=maxlength?description.substring(0, maxlength-3)+"...":description;
		String descriptionReplace1 = descriptionLengthResult.replaceAll("\r\n", " ");
		String descriptionReplace2 = descriptionReplace1.replaceAll("\n", " ");
		String descriptionReplace3 = descriptionReplace2.replaceAll("\r", " ");
		String descriptionReplace4 = descriptionReplace3.replaceAll("\t", " ");
		String descriptionReplace5 = descriptionReplace4.replaceAll(";", ",");
		String descriptionReplace6 = descriptionReplace5.replaceAll("-", " ");
		String descriptionReplace7 = descriptionReplace6.replaceAll("~", " ");
		String descriptionReplace8 = descriptionReplace7.replaceAll("\"", "\\\"");
		String descriptionReplace9 = descriptionReplace8.replaceAll("#", " ");
		String descriptionReplace10 = descriptionReplace9.replaceAll(",", " ");
		String descriptionReplace11 = descriptionReplace10.replaceAll("&", " ");
		String descriptionReplace12 = descriptionReplace11.replaceAll("&nbsp;", " ");
		String descriptionReplace13 = descriptionReplace12.replaceAll("&NBSP;", " ");
		
		return descriptionReplace13;
	}


	public static void setMostRecentList(ArrayList<GoodGuideEntry> model) {
		mostRecentList = model;
	}

	public static List<GoodGuideEntry> getMostRecentList() {
		return mostRecentList;
	}
	
	public static void setRandomList(ArrayList<GoodGuideEntry> model) {
		randomList = model;
	}

	public static List<GoodGuideEntry> getRandomList() {
		return randomList;
	}

	public static boolean isInitialized() {
		return isInitialized;
	}

	public static void setInitialized(boolean isInitialized) {
		Constants.isInitialized = isInitialized;
	}

	public static ArrayList<GenericResult> getFavorites() {
		return favorites;
	}

	public static void setFavorites(ArrayList<GenericResult> favorites) {
		Constants.favorites = favorites;
	}

	public static ArrayList<GenericResult> getMostRecent() {
		return mostRecent;
	}

	public static void setMostRecent(ArrayList<GenericResult> mostRecent) {
		Constants.mostRecent = mostRecent;
	}

	public static ArrayList<GenericResult> getRandom() {
		return random;
	}

	public static void setRandom(ArrayList<GenericResult> random) {
		Constants.random = random;
	}

	public static int getSelectedMenuItem() {
		return selectedMenuItem;
	}

	public static void setSelectedMenuItem(int id) {
		Constants.selectedMenuItem = id;
	}

	public static int getDeviceResolution() {
		return deviceResolution==Constants.DENSITY_HIGH?2:1;
	}

	public static void setDeviceResolution(int deviceResolution) {
		Constants.deviceResolution = deviceResolution;
	}

	public static List<Product> getRecentlyScanned() {
		return recentlyScanned;
	}

	public static void setRecentlyScanned(List<Product> recentlyScanned) {
		Constants.recentlyScanned = recentlyScanned;
	}

	@Override
	public String toString() {
		return "Constants []";
	}

	public static ArrayList<Product> getRecentSearchProductResults() {
		return recentSearchProductResults;
	}

	public static void setRecentSearchProductResults(
			ArrayList<Product> recentSearchProductResults) {
		Constants.recentSearchProductResults = recentSearchProductResults;
	}

	public static ArrayList<Product> getRecentSearchBrandResults() {
		return recentSearchBrandResults;
	}

	public static void setRecentSearchBrandResults(
			ArrayList<Product> recentSearchBrandResults) {
		Constants.recentSearchBrandResults = recentSearchBrandResults;
	}

	public static String getRecentQuery() {
		return recentQuery;
	}

	public static void setRecentQuery(String recentQuery) {
		Constants.recentQuery = recentQuery;
	}



}
