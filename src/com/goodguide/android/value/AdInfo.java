package com.goodguide.android.value;

import java.io.Serializable;

public class AdInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String Type;
	public String Image;
	public String Url;
	
	public String getImage() {
		return Image;
	}

	public void setImage(String imageURL) {
		this.Image = imageURL;
	}

	public String getURL() {
		return Url;
	}

	public void setURL(String uRL) {
		Url = uRL;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	@Override
	public String toString() {
		return "Advertisement [Image=" + Image + ", Type=" + Type + ", Url="
				+ Url + "]";
	}
}
