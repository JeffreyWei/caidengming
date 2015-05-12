package com.wj.caidengmi2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.emar.escore.recommendwall.RecommendSDK;
import com.emar.escore.scorewall.ScoreWallSDK;
import com.emar.escore.sdk.YjfSDK;
import com.emar.escore.sdk.widget.UpdateScordNotifier;
import com.game.zhongqiuguess.R;

@SuppressLint("HandlerLeak")
public class Shop extends Activity implements View.OnClickListener, UpdateScordNotifier {
	private ImageView mBtGetJiFen;
	private int pointTotal = 0;
	private TextView totalText;
	
	 private void findViews(){
		 this.mBtGetJiFen = ((ImageView)findViewById(R.id.bt_get_jifen));
		 this.totalText = ((TextView)findViewById(R.id.text_now_total));
	 }
	 
	 private void setListener(){
	    this.mBtGetJiFen.setOnClickListener(this);
	    this.totalText.setOnClickListener(this);
	 }
	 
	 public void onClick(View view){
		 ScoreWallSDK.getInstance(this,this).getScore();// 回调接口中返回积分
		 if(mBtGetJiFen == view){
			 RecommendSDK.getInstance(Shop.this).showAdlist();
		 }
	 }
	 
	 public void onCreate(Bundle paramBundle){
		    super.onCreate(paramBundle);
		    setContentView(R.layout.shop);
		    findViews();
		    setListener();
		 	YjfSDK.getInstance(this, null).initInstance("14147", "EMDEZCOM7CSNHM2RYQABGC7GCG29DLAL8D", "16908", "360");
//		 	YjfSDK.getInstance(this, null).initInstance("14149", "EMJYMJ9OHGLS5PRBH1TRGD7QZ4IMNWD62J", "16906", "360");
		    ScoreWallSDK.getInstance(Shop.this,Shop.this).getScore();// 回调接口中返回积分
			//用于退出整个应用
			ExitApplication.getInstance().addActivity(this);
			
	 }
	 
	 protected void onDestroy() {
		 YjfSDK.getInstance(this,null).recordAppClose();//释放内存
		super.onDestroy();
	 }
	
	class MyHandler2 extends Handler {
		public void handleMessage(Message paramMessage) {
			super.handleMessage(paramMessage);
			Shop.this.totalText.setText("您当前积分："+pointTotal);
		}
	}


	@Override
	public void updateScoreFailed(int arg0, int arg1, String arg2) {
		
		
	}



	@Override
	public void updateScoreSuccess(int type, int currentScore, int changeScore,String unit) {
		this.pointTotal = currentScore;
		
	} 
}