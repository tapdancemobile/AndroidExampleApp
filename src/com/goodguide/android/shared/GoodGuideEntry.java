package com.goodguide.android.shared;

import com.goodguide.android.value.Product;

public class GoodGuideEntry {
		public String title="";
		public String imageUrl=null;
		public Product info;
		
		public GoodGuideEntry(String mTitle, String mImageUrl, Product mInfo) {
			this.title=mTitle;
			this.imageUrl=mImageUrl;
			this.info = mInfo;
		}
	}