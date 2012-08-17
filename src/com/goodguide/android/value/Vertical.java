package com.goodguide.android.value;

public class Vertical {
	
	public Vertical(String mTitle, String mSubtitle, int mResId) {
		this.title = mTitle;
		this.subTitle = mSubtitle;
		this.imageResId = mResId;
	}
	
	private String title;
	private String subTitle;
	private int imageResId;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public int getImageResId() {
		return imageResId;
	}
	public void setImageResId(int imageResId) {
		this.imageResId = imageResId;
	}
	@Override
	public String toString() {
		return "Vertical [imageResId=" + imageResId + ", subTitle=" + subTitle
				+ ", title=" + title + "]";
	}
}
