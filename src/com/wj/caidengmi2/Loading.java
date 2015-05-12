package com.wj.caidengmi2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Loading extends Activity {
	private MyHandler mHandler = new MyHandler();

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.loading);
		this.mHandler.postDelayed(new Runnable() {
			public void run() {
				Loading.this.mHandler.sendEmptyMessage(0);
			}
		}
				, 2000L);
	}

	class MyHandler extends Handler {
		MyHandler() {
		}
		public void handleMessage(Message paramMessage) {
			super.handleMessage(paramMessage);
			Intent localIntent = new Intent(Loading.this, Main.class);
			Loading.this.startActivity(localIntent);
			Loading.this.finish();
		}
	}
}