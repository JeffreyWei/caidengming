package com.wj.caidengmi2;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.game.zhongqiuguess.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Main extends Activity
		implements View.OnClickListener {
	public static boolean firstFlg = false;
	private ImageView mBtnChallenge;
	private ImageView mBtnPractice;
	private ImageView mBtnExit;
	private ImageView mLanternImg;
	private ImageView mBtnShop;

	private void findViews() {
		this.mBtnChallenge = ((ImageView) findViewById(R.id.btn_challenge));
		this.mBtnPractice = ((ImageView) findViewById(R.id.btn_practice));
		this.mBtnExit = ((ImageView) findViewById(R.id.btn_exit));
		this.mLanternImg = ((ImageView) findViewById(R.id.index_anim));
		this.mBtnShop = ((ImageView) findViewById(R.id.btn_shop));
	}

	private void setListener() {
		this.mBtnChallenge.setOnClickListener(this);
		this.mBtnPractice.setOnClickListener(this);
		this.mBtnExit.setOnClickListener(this);
		this.mBtnShop.setOnClickListener(this);
	}

	public void onClick(View view) {
		if (mBtnChallenge == view) {
			startActivity(new Intent(this, CPractice.class));
		} else if (mBtnPractice == view) {
			startActivity(new Intent(this, Practice.class));
		} else if (mBtnShop == view) {
			startActivity(new Intent(this, Shop.class));
		} else if (mBtnExit == view) {
			ExitApplication.getInstance().exit();
		}

	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.main);
		//YjfSDK.getInstance(this, null).initInstance("13885", "EM2VRHMVQ485XJG68PXJVL360DERF0SEY3", "16728", "163");
		//YjfSDK.getInstance(this, null).initInstance("14146", "EM3USM4JG4TIM3QR8TVPJK13A6W5FF9TAH", "16906", "360");
		findViews();
		setListener();
		Button plaque_button = (Button) findViewById(R.id.button);
		// 显示插屏广告
//		plaque_button.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				PlaqueSDK.getInstance(Main.this, null).getPlaque(v, YjfSDK.ADIMAGE_FLAG_PLAQUE_VERTICAL);
//			}
//		});
// 插屏广告---------------------------------------------------------------------------------------------------------------------------------


		//用于退出整个应用
		ExitApplication.getInstance().addActivity(this);

		File dir = new File("data/data/com.game.zhongqiuguess/databases");

		if (!dir.exists())
			dir.mkdir();
		if (!(new File(SqlHelper.DB_NAME)).exists()) {
			firstFlg = true;  //判断是第一次登录该应用
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(SqlHelper.DB_NAME);
				byte[] buffer = new byte[8192];
				int count = 0;
				InputStream is = getResources().openRawResource(R.raw.score);
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(SqlHelper.DB_NAME, null);
			database.execSQL("CREATE TABLE score( id text not null, type text not null, score text not null)");

			String sql1 = "insert into score values('1','norm','0')";
			String sql2 = "insert into score values('2','challenge','0')";
			database.execSQL(sql1);
			database.execSQL(sql2);
		}


	}
  
 /* *//**
	 * 用于监听插屏广告的显示与关闭
	 *//*
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Dialog dialog = AppConnect.getInstance(this).getPopAdDialog();
		if(dialog != null){
			if(dialog.isShowing()){
				// 插屏广告正在显示
			}
			dialog.setOnCancelListener(new OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog) {
					// 监听插屏广告关闭事件
				}
			});
		}
	}*/


}
