package com.android.wishlist.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.android.wishlist.R;

public class Splash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Handler x = new Handler();
		x.postDelayed(new splashhandler(), 2000);
	}
	
	class splashhandler implements Runnable{

        public void run() {
            startActivity(new Intent(getApplication(),DashBoard.class));
            Splash.this.finish();
        }
	}
}
