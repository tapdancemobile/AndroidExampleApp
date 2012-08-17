package com.goodguide.android.value;

public class Category {
	private String parentCategoryName;
	private String name;
	private int objectId;
	private String imageName;
	private String ingredientsToAvoid;
	
	public String getParentCategoryName() {
		return parentCategoryName;
	}
	public void setParentCategoryName(String parentCategoryName) {
		this.parentCategoryName = parentCategoryName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getObjectId() {
		return objectId;
	}
	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getIngredientsToAvoid() {
		return ingredientsToAvoid;
	}
	public void setIngredientsToAvoid(String ingredientsToAvoid) {
		this.ingredientsToAvoid = ingredientsToAvoid;
	}
	@Override
	public String toString() {
		return "Category [imageName=" + imageName + ", ingredientsToAvoid="
				+ ingredientsToAvoid + ", name=" + name + ", objectId="
				+ objectId + ", parentCategoryName=" + parentCategoryName + "]";
	}

}
