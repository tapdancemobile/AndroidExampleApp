package com.goodguide.android.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.goodguide.android.shared.Constants;
import com.goodguide.android.value.Category;
import com.goodguide.android.value.Product;

public class GoodGuideDataSource {
 
	private static final String PRODUCTS_TABLE = "PRODUCTS";
	private static final String TAG = "GoodGuideDataSource";

	private DataBaseHelper myDbHelper;
	private SQLiteDatabase readDB;
	private SQLiteDatabase writeDB;

	public GoodGuideDataSource(Context context) {
		initDB(context);
	}

	public void initDB(Context context) {
		myDbHelper = new DataBaseHelper(context);
		
		try {
			myDbHelper.createDataBase();
		}
		catch (IOException e) {
			
		}
		
		readDB = myDbHelper.getReadableDatabase();
		writeDB = myDbHelper.getWritableDatabase();

	}

	public void close() {
		myDbHelper.close();
	}
	
	public void openIfClosed() {
		if(!myDbHelper.isDBOpen()) {
			Log.v(TAG, "DB is closed - opening...");
			 myDbHelper.OpenDB();
			 this.readDB = myDbHelper.getReadableDatabase();
		}
	}
	
	public boolean testDBImportWorked() {
	
		//check to see if db is closed and open
		openIfClosed();
		
	String sql = "select count(*) from products";
	
	if(Constants.showLog) Log.v(TAG, "what to do sql:"+sql);
	
	Cursor cursor = this.readDB.rawQuery(sql, null);
	
	//set db as being initialized and save to preferences
	if(cursor != null && cursor.getCount()>0) 
		Constants.setInitialized(true);
	 else
		 Constants.setInitialized(false);
	
	if (cursor.moveToFirst()) {
		do {
			if(Constants.showLog) Log.v(TAG, "products count:" + cursor.getInt(0));
		} while (cursor.moveToNext());
	}
	cursor.close();
	
	return Constants.isInitialized();
}

	public List<Category> getCategoriesWithParent(String parentCategory) {
		
		//check to see if db is closed and open
		openIfClosed();
		
		List<Category> categories = new ArrayList<Category>();
		parentCategory = parentCategory.replaceAll("'", "''");
		//do this weird order by to get the <product category>(All) at the top of the list
		String sql = "select * from categories where parentCategoryName = '"+parentCategory+"' order by parentCategoryName, length (ingredientsToAvoid) desc";
		
		if(Constants.showLog) Log.v(TAG, "categories with parent category sql:"+sql);
		
		Cursor cursor = this.readDB.rawQuery(sql, null);
		
		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();
				populateCategory(category,cursor);
				categories.add(category);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return categories;
	}
	
	private void populateCategory(Category category, Cursor cursor) {
		category.setParentCategoryName(cursor.getString(0));
		category.setName(cursor.getString(1));
		category.setObjectId(cursor.getInt(2));
		category.setImageName(cursor.getString(3));
		category.setIngredientsToAvoid(cursor.getString(4));
	}
	
	private void populateProduct(Product product, Cursor cursor) {
		product.setObjectId(cursor.getInt(0)); /*private int objectId;*/
		product.setName(cursor.getString(1));//private String name;
		product.setOverallRating(cursor.getFloat(2));//private float overallRating;
		product.setEnvironmentalRating(cursor.getFloat(3));//private float environmentalRating;
		product.setHealthRating(cursor.getFloat(4));//private float healthRating;
		product.setSocialRating(cursor.getFloat(5));//private float socialRating;
		product.setS3ImageURL(cursor.getString(6));//private String s3ImageURL;
		product.setImageName(cursor.getString(7));//private String imageName;
		product.setNumberOfProductsInBrand(cursor.getInt(8));//private int numberOfProductsInBrand;
		product.setBehindTheRatingDescriptions(cursor.getString(9));//private String behindTheRatingDescriptions;
		product.setBehindTheRatingZScores(cursor.getString(10));//private String behindTheRatingZScores;
		product.setParentBrandName(cursor.getString(11));//private String parentBrandName;
		product.setParentCompanyName(cursor.getString(12));//private String parentCompanyName;
		product.setParentCompanyId(cursor.getInt(13));//private int parentCompanyId;
		product.setIncludeInBrowse(cursor.getInt(14));//private int includeInBrowse;
		product.setBuyNowURL(cursor.getString(15));//private String buyNowURL;
		product.setBaseCategoryName(cursor.getString(16));//private String baseCategoryName;
		product.setBaseCategoryId(cursor.getInt(17));//private int baseCategoryId;
		product.setNutritionChartURL(cursor.getString(18));//private String nutritionChartURL;
		product.setNutritionComparisonChartURL(cursor.getString(19));//private String nutritionComparisonChartURL;
		product.setAverageUserRating(cursor.getString(20));//private String averageUserRating;
		product.setHealthyOptionsAvailable(cursor.getString(21));//private String healthyOptionsAvailable;
		product.setIngredientScores(cursor.getString(22));//private String ingredientScores;
		product.setIngredients(cursor.getString(23));//private String ingredients;
		product.setNutritionThresholds(cursor.getString(24));//private String nutritionThresholds;
		product.setNutritionPercentages(cursor.getString(25));//private String nutritionPercentages;
		product.setNutritionGrams(cursor.getString(26));//private String nutritionGrams;
		product.setNutritionLabels(cursor.getString(27));//private String nutritionLabels;
		product.setVitaminLabels(cursor.getString(28));//private String vitaminLabels;
		product.setVitaminPercentages(cursor.getString(29));//private String vitaminPercentages;
		product.setParentBrandId(cursor.getInt(30));//private int parentBrandId;*/
	}

	public List<Product> getProductsBySubcategory(int categoryId,
			boolean showAll) {
		
		//check to see if db is closed and open
		openIfClosed();
		
		//show all in db by default - the 'show all' makes a webservice call
		//overriding value
		showAll = true;
		
		List<Product> products = new ArrayList<Product>();
			
		String sql = "SELECT * from products INNER JOIN categories_products ON products.objectId = categories_products.childId and categories_products.parentId = "
			+categoryId+" WHERE (includeInBrowse = 'YES') order by overallRating desc";
		
		if(Constants.showLog) Log.v(TAG, "products with category sql:"+sql);
		
		Cursor cursor = this.readDB.rawQuery(sql, null);
		
		if (cursor.moveToFirst()) {
			do {
				Product product = new Product();
				populateProduct(product,cursor);
				products.add(product);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return products;
	}

	public Product getProductWithUPC(String upc) {
		
		//check to see if db is closed and open
		openIfClosed();
		
		Product product = new Product();
		
		String sql = "SELECT p.* FROM products p, upcs u where p.objectId = u.objectId and u.upc = '"+upc+"'";
		
		if(Constants.showLog) Log.v(TAG, "product with upc sql:"+sql);
		
		Cursor cursor = this.readDB.rawQuery(sql, null);
		
		if (cursor.moveToFirst()) {
			do {
				populateProduct(product,cursor);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return cursor.getCount()==0?null:product;
	}

	public int getCategoryIdByName(String categoryName) {
		//check to see if db is closed and open
		openIfClosed();
		
		int categoryId = 0;
		
		String sql = "SELECT objectId FROM categories  where name = '"+categoryName+"'";
		
		if(Constants.showLog) Log.v(TAG, "categoryId with productId sql:"+sql);
		
		Cursor cursor = this.readDB.rawQuery(sql, null);
		
		if (cursor.moveToFirst()) {
			do {
				categoryId = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return categoryId;
	}
	
	public int getCategoryIdByProductId(int productId) {
		//check to see if db is closed and open
		openIfClosed();
		
		int categoryId = 0;
		
		String sql = "SELECT parentId FROM categories_products  where childId = "+productId+"";
		
		if(Constants.showLog) Log.v(TAG, "categoryId with productId sql:"+sql);
		
		Cursor cursor = this.readDB.rawQuery(sql, null);
		
		if (cursor.moveToFirst()) {
			do {
				categoryId = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return categoryId;
	}

	public String getCategoryNameById(int categoryId) {
		//check to see if db is closed and open
		openIfClosed();
		
		String categoryName = null;
		
		String sql = "SELECT parentCategoryName FROM categories  where objectId = "+categoryId+"";
		
		if(Constants.showLog) Log.v(TAG, "categoryName with categoryId sql:"+sql);
		
		Cursor cursor = this.readDB.rawQuery(sql, null);
		
		if (cursor.moveToFirst()) {
			do {
				categoryName = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return categoryName;
	}

}
