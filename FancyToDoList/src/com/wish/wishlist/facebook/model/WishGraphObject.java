package com.wish.wishlist.facebook.model;
import com.facebook.model.GraphObject;

    /**
     * Interface representing the Wish Open Graph object.
     */
	public interface WishGraphObject extends GraphObject {
		// A URL
		public String getUrl();
		public void setUrl(String url);
		// An ID
		public String getId();
		public void setId(String id);
	}
