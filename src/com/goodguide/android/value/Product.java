package com.goodguide.android.value;

import java.io.Serializable;

public class Product implements Serializable{
	private int objectId;
	private String name;
	private float overallRating;
	private float environmentalRating;
	private float healthRating;
	private float socialRating;
	private String s3ImageURL;
	private String imageName;
	private int numberOfProductsInBrand;
	private String behindTheRatingDescriptions;
	private String behindTheRatingZScores;
	private String parentBrandName;
	private String parentCompanyName;
	private int parentCompanyId;
	private int includeInBrowse;
	private String buyNowURL;
	private String baseCategoryName;
	private int baseCategoryId;
	private String nutritionChartURL;
	private String nutritionComparisonChartURL;
	private String averageUserRating;
	private String healthyOptionsAvailable;
	private String ingredientScores;
	private String ingredients;
	private String nutritionThresholds;
	private String nutritionPercentages;
	private String nutritionGrams;
	private String nutritionLabels;
	private String vitaminLabels;
	private String vitaminPercentages;
	private boolean isBrand;
	private int parentBrandId;
	private String upc;
	
	//non db fields
	private String alternativesCategory;
	private int alternativesCategoryId;
	
	public int getObjectId() {
		return objectId;
	}
	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getOverallRating() {
		return overallRating;
	}
	public void setOverallRating(float overallRating) {
		this.overallRating = overallRating;
	}
	public float getEnvironmentalRating() {
		return environmentalRating;
	}
	public void setEnvironmentalRating(float environmentalRating) {
		this.environmentalRating = environmentalRating;
	}
	public float getHealthRating() {
		return healthRating;
	}
	public void setHealthRating(float healthRating) {
		this.healthRating = healthRating;
	}
	public float getSocialRating() {
		return socialRating;
	}
	public void setSocialRating(float socialRating) {
		this.socialRating = socialRating;
	}
	public String getS3ImageURL() {
		return s3ImageURL;
	}
	public void setS3ImageURL(String s3ImageURL) {
		this.s3ImageURL = s3ImageURL;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public int getNumberOfProductsInBrand() {
		return numberOfProductsInBrand;
	}
	public void setNumberOfProductsInBrand(int numberOfProductsInBrand) {
		this.numberOfProductsInBrand = numberOfProductsInBrand;
	}
	public String getBehindTheRatingDescriptions() {
		return behindTheRatingDescriptions;
	}
	public void setBehindTheRatingDescriptions(String behindTheRatingDescriptions) {
		this.behindTheRatingDescriptions = behindTheRatingDescriptions;
	}
	public String getBehindTheRatingZScores() {
		return behindTheRatingZScores;
	}
	public void setBehindTheRatingZScores(String behindTheRatingZScores) {
		this.behindTheRatingZScores = behindTheRatingZScores;
	}
	public String getParentBrandName() {
		return parentBrandName;
	}
	public void setParentBrandName(String parentBrandName) {
		this.parentBrandName = parentBrandName;
	}
	public String getParentCompanyName() {
		return parentCompanyName;
	}
	public void setParentCompanyName(String parentCompanyName) {
		this.parentCompanyName = parentCompanyName;
	}
	public int getParentCompanyId() {
		return parentCompanyId;
	}
	public void setParentCompanyId(int parentCompanyId) {
		this.parentCompanyId = parentCompanyId;
	}
	public int getIncludeInBrowse() {
		return includeInBrowse;
	}
	public void setIncludeInBrowse(int includeInBrowse) {
		this.includeInBrowse = includeInBrowse;
	}
	public String getBuyNowURL() {
		return buyNowURL;
	}
	public void setBuyNowURL(String buyNowURL) {
		this.buyNowURL = buyNowURL;
	}
	public String getBaseCategoryName() {
		return baseCategoryName;
	}
	public void setBaseCategoryName(String baseCategoryName) {
		this.baseCategoryName = baseCategoryName;
	}
	public int getBaseCategoryId() {
		return baseCategoryId;
	}
	public void setBaseCategoryId(int baseCategoryId) {
		this.baseCategoryId = baseCategoryId;
	}
	public String getNutritionChartURL() {
		return nutritionChartURL;
	}
	public void setNutritionChartURL(String nutritionChartURL) {
		this.nutritionChartURL = nutritionChartURL;
	}
	public String getNutritionComparisonChartURL() {
		return nutritionComparisonChartURL;
	}
	public void setNutritionComparisonChartURL(String nutritionComparisonChartURL) {
		this.nutritionComparisonChartURL = nutritionComparisonChartURL;
	}
	public String getAverageUserRating() {
		return averageUserRating;
	}
	public void setAverageUserRating(String averageUserRating) {
		this.averageUserRating = averageUserRating;
	}
	public String getHealthyOptionsAvailable() {
		return healthyOptionsAvailable;
	}
	public void setHealthyOptionsAvailable(String healthyOptionsAvailable) {
		this.healthyOptionsAvailable = healthyOptionsAvailable;
	}
	public String getIngredientScores() {
		return ingredientScores;
	}
	public void setIngredientScores(String ingredientScores) {
		this.ingredientScores = ingredientScores;
	}
	public String getIngredients() {
		return ingredients;
	}
	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}
	public String getNutritionThresholds() {
		return nutritionThresholds;
	}
	public void setNutritionThresholds(String nutritionThresholds) {
		this.nutritionThresholds = nutritionThresholds;
	}
	public String getNutritionPercentages() {
		return nutritionPercentages;
	}
	public void setNutritionPercentages(String nutritionPercentages) {
		this.nutritionPercentages = nutritionPercentages;
	}
	public String getNutritionGrams() {
		return nutritionGrams;
	}
	public void setNutritionGrams(String nutritionGrams) {
		this.nutritionGrams = nutritionGrams;
	}
	public String getNutritionLabels() {
		return nutritionLabels;
	}
	public void setNutritionLabels(String nutritionLabels) {
		this.nutritionLabels = nutritionLabels;
	}
	public String getVitaminLabels() {
		return vitaminLabels;
	}
	public void setVitaminLabels(String vitaminLabels) {
		this.vitaminLabels = vitaminLabels;
	}
	public String getVitaminPercentages() {
		return vitaminPercentages;
	}
	public void setVitaminPercentages(String vitaminPercentages) {
		this.vitaminPercentages = vitaminPercentages;
	}
	public int getParentBrandId() {
		return parentBrandId;
	}
	public void setParentBrandId(int parentBrandId) {
		this.parentBrandId = parentBrandId;
	}
	@Override
	public String toString() {
		return "Product [alternativesCategory=" + alternativesCategory
				+ ", alternativesCategoryId=" + alternativesCategoryId
				+ ", averageUserRating=" + averageUserRating
				+ ", baseCategoryId=" + baseCategoryId + ", baseCategoryName="
				+ baseCategoryName + ", behindTheRatingDescriptions="
				+ behindTheRatingDescriptions + ", behindTheRatingZScores="
				+ behindTheRatingZScores + ", buyNowURL=" + buyNowURL
				+ ", environmentalRating=" + environmentalRating
				+ ", healthRating=" + healthRating
				+ ", healthyOptionsAvailable=" + healthyOptionsAvailable
				+ ", imageName=" + imageName + ", includeInBrowse="
				+ includeInBrowse + ", ingredientScores=" + ingredientScores
				+ ", ingredients=" + ingredients + ", isBrand=" + isBrand
				+ ", name=" + name + ", numberOfProductsInBrand="
				+ numberOfProductsInBrand + ", nutritionChartURL="
				+ nutritionChartURL + ", nutritionComparisonChartURL="
				+ nutritionComparisonChartURL + ", nutritionGrams="
				+ nutritionGrams + ", nutritionLabels=" + nutritionLabels
				+ ", nutritionPercentages=" + nutritionPercentages
				+ ", nutritionThresholds=" + nutritionThresholds
				+ ", objectId=" + objectId + ", overallRating=" + overallRating
				+ ", parentBrandId=" + parentBrandId + ", parentBrandName="
				+ parentBrandName + ", parentCompanyId=" + parentCompanyId
				+ ", parentCompanyName=" + parentCompanyName + ", s3ImageURL="
				+ s3ImageURL + ", socialRating=" + socialRating + ", upc="
				+ upc + ", vitaminLabels=" + vitaminLabels
				+ ", vitaminPercentages=" + vitaminPercentages + "]";
	}
	public boolean isBrand() {
		return isBrand;
	}
	public void setBrand(boolean isBrand) {
		this.isBrand = isBrand;
	}
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	public String getAlternativesCategory() {
		return alternativesCategory;
	}
	public void setAlternativesCategory(String alternativesCategory) {
		this.alternativesCategory = alternativesCategory;
	}
	public int getAlternativesCategoryId() {
		return alternativesCategoryId;
	}
	public void setAlternativesCategoryId(int alternativesCategoryId) {
		this.alternativesCategoryId = alternativesCategoryId;
	}
}
