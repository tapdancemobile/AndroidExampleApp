package com.goodguide.android.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.goodguide.android.shared.Constants;
import com.goodguide.android.utilities.GoodGuideDataSource;
import com.goodguide.android.utilities.GoodGuideWebFeed;
import com.goodguide.android.value.Category;
import com.goodguide.android.value.Product;

public class GoodGuideBusiness {
	
	private static final String TAG = null;

	//This method takes a string array of parent category names and return a List of hashmaps that contain the detail 
	//categories for the parent category
	//EX(single parent category): 
	//			Drinks
	//			Juices
	// Drinks =>Soda
	//			Sports Drinks
	//
	public static List<HashMap<String,List<Category>>> getCategoriesByVerticalType(String verticalType) {
		
		List<HashMap<String,List<Category>>> categoriesList = new ArrayList<HashMap<String,List<Category>>>();
		
		if(Constants.PRODUCTS_VERTICAL_TYPE.equalsIgnoreCase(verticalType))
			categoriesList = getCategoriesWithParentCategoryNames(verticalType, Constants.allProductTitles,categoriesList);
		else if(Constants.APPLIANCES_VERTICAL_TYPE.equalsIgnoreCase(verticalType))
			categoriesList = getCategoriesWithParentCategoryNames(verticalType, Constants.allAppliancesTitles,categoriesList);
		else if(Constants.APPAREL_VERTICAL_TYPE.equalsIgnoreCase(verticalType))	
			categoriesList = getCategoriesWithParentCategoryNames(verticalType, Constants.allApparelTitles,categoriesList);
		else if(Constants.BABIES_AND_KIDS_VERTICAL_TYPE.equalsIgnoreCase(verticalType))	
			categoriesList = getCategoriesWithParentCategoryNames(verticalType, Constants.allBabyAndKidsTitles,categoriesList);
		else if(Constants.ELECTRONICS_VERTICAL_TYPE.equalsIgnoreCase(verticalType))	
			categoriesList = getCategoriesWithParentCategoryNames(verticalType, Constants.allElectronicsTitles,categoriesList);
		else if(Constants.FOOD_VERTICAL_TYPE.equalsIgnoreCase(verticalType))
			categoriesList = getCategoriesWithParentCategoryNames(verticalType, Constants.allFoodTitles,categoriesList);
		else if(Constants.PERSONAL_CARE_VERTICAL_TYPE.equalsIgnoreCase(verticalType))
			categoriesList = getCategoriesWithParentCategoryNames(verticalType, Constants.allPersonalCareTitles,categoriesList);
		else if(Constants.HOUSEHOLD_CLEANERS_VERTICAL_TYPE.equalsIgnoreCase(verticalType))	
			categoriesList = getCategoriesWithParentCategoryNames(verticalType, Constants.allHouseholdCleanersTitles,categoriesList);
		else if(Constants.PET_FOOD_VERTICAL_TYPE.equalsIgnoreCase(verticalType))	
			categoriesList = getCategoriesWithParentCategoryNames(verticalType, Constants.allPetFoodTitles,categoriesList);
		else if(Constants.LIGHTING_VERTICAL_TYPE.equalsIgnoreCase(verticalType))	
			categoriesList = getCategoriesWithParentCategoryNames(verticalType, Constants.allLightingTitles,categoriesList);
		
		return categoriesList;
	}

	private static List<HashMap<String,List<Category>>> getCategoriesWithParentCategoryNames(String verticalCategoryName, String[] parentCategories, List<HashMap<String,List<Category>>> categoriesListReference) {
		
		//we check our cached values rather than hit the db again
		if(Constants.cachedParentCategoryMap !=null && Constants.cachedParentCategoryMap.containsKey(verticalCategoryName)) {
			Log.v(TAG, " Found Cached Parent Category Map");
			return Constants.cachedParentCategoryMap.get(verticalCategoryName);
		}
		
		//Loop through parentCategories and populate if not cached
		for(int i=0; i<parentCategories.length; i++) {
			
			String parentCategory = (String)parentCategories[i];
			HashMap<String,List<Category>> categoryHashMap = new HashMap<String,List<Category>>();
			Log.v(TAG, "ds is null:"+Constants.ds == null?"true":"false");
			List<Category> categories = Constants.ds.getCategoriesWithParent(parentCategory);
			categoryHashMap.put(parentCategory, categories);
			categoriesListReference.add(categoryHashMap);
		}
			
		//cache the value to retrieve later in this session if need be
		Log.v(TAG, " Creating New Parent Category Map");
		if(Constants.cachedParentCategoryMap ==null) {
			Constants.cachedParentCategoryMap = new HashMap<String, List<HashMap<String,List<Category>>>>();
		}
		
		Constants.cachedParentCategoryMap.put(verticalCategoryName, categoriesListReference);
		
		return categoriesListReference;
	}

	public static void getProductsBySubcategory(int categoryId, boolean showAll) {
		Constants.cachedProductList = categoryId==0?null:Constants.ds.getProductsBySubcategory(categoryId, showAll);
	}

	public Product lookForProductInDB(String upc) {
		return Constants.ds.getProductWithUPC(upc);
	}

	public int getCategoryIdByName(String categoryName) {
		return Constants.ds.getCategoryIdByName(categoryName);
	}
	
	public int getCategoryIdByProductId(int productId) {
		return Constants.ds.getCategoryIdByProductId(productId);
	}

	public String getCategoryNameById(int categoryId) {
		return Constants.ds.getCategoryNameById(categoryId);
	}

}
