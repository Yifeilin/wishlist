package com.wish.wishlist.facebook.model;

import com.wish.wishlist.facebook.model.WishGraphObject;
import com.facebook.model.OpenGraphAction;

    /**
     * Interface representing the Make action.
     */
	public interface MakeAction extends OpenGraphAction {
		// The wish object
		public WishGraphObject getWish();
		public void setWish(WishGraphObject wish);
	}
