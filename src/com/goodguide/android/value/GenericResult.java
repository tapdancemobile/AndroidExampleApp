package com.goodguide.android.value;

import java.io.Serializable;
import java.util.List;

public class GenericResult implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String Body; 
    public String CategoryID;
    public String CategoryName; 
    public String ItemID;
    public String Keywords;
    public String  MP3;
    public String ShortURL; 
    public String Teaser;
    public String Thumbnail;
    public String Title;
    public String Type;
    public String Image;
    public String URL;
    public List<AdInfo> Advertisement;
    
	public String getBody() {
		return Body;
	}
	public void setBody(String body) {
		Body = body;
	}
	public String getCategoryID() {
		return CategoryID;
	}
	public void setCategoryID(String categoryID) {
		CategoryID = categoryID;
	}
	public String getCategoryName() {
		return CategoryName;
	}
	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}
	public String getItemID() {
		return ItemID;
	}
	public void setItemID(String itemID) {
		ItemID = itemID;
	}
	public String getKeywords() {
		return Keywords;
	}
	public void setKeywords(String keywords) {
		Keywords = keywords;
	}
	public String getMP3() {
		return MP3;
	}
	public void setMP3(String mP3) {
		MP3 = mP3;
	}
	public String getShortURL() {
		return ShortURL;
	}
	public void setShortURL(String shortURL) {
		ShortURL = shortURL;
	}
	public String getTeaser() {
		return Teaser;
	}
	public void setTeaser(String teaser) {
		Teaser = teaser;
	}
	public String getThumbnail() {
		return Thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		Thumbnail = thumbnail;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	@Override
	public String toString() {
		return "GenericResult [Advertisement=" + Advertisement + ", Body="
				+ Body + ", CategoryID=" + CategoryID + ", CategoryName="
				+ CategoryName + ", Image=" + Image + ", ItemID=" + ItemID
				+ ", Keywords=" + Keywords + ", MP3=" + MP3 + ", ShortURL="
				+ ShortURL + ", Teaser=" + Teaser + ", Thumbnail=" + Thumbnail
				+ ", Title=" + Title + ", Type=" + Type + ", URL=" + URL + "]";
	}
	public String getImage() {
		return Image;
	}
	public void setImage(String image) {
		Image = image;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public List<AdInfo> getAdvertisement() {
		return Advertisement;
	}
	public void setAdvertisement(List<AdInfo> advertisement) {
		Advertisement = advertisement;
	}

}
