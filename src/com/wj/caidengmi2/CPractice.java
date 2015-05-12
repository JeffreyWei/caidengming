package com.wj.caidengmi2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.csvreader.CsvReader;
import com.emar.escore.scorewall.ScoreWallSDK;
import com.emar.escore.sdk.YjfSDK;
import com.emar.escore.sdk.widget.UpdateScordNotifier;
import com.game.zhongqiuguess.R;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressLint("HandlerLeak")
public class CPractice extends Activity implements View.OnClickListener,UpdateScordNotifier {
	private Intent intent = new Intent("com.angel.Android.MUSIC");
	private boolean startMusicFlag = true;
	private MyCount mc;


	private TextView[] mAnswerTextArr = new TextView[3];
	private MyHandler mHandler = new MyHandler();
	private MyHandler2 h = new MyHandler2();
	private TextView mQuestText;
	private TextView remindText;
	private TextView textView1;
	private TextView highestScoreText;
	private TextView showTime;
	private Random mRandom = new Random();
	private ImageView mReturnBtn;
	private Riddle mRiddle;
	private List<Riddle> mRiddleList = new ArrayList<Riddle>();
	private SoundPool mSp;
	private ImageView mStartBtn;
	private ImageView mgetAnswerBtn;
	private ImageView clostMusic;
	private ImageView btShare;

	private ImageView goToShop;
	private ImageView btReturn;
	private PopupWindow popWindow;
	// 获取自定义布局文件poplayout.xml的视图
	private View popview = null;
	private TextView textRemind;

	private int mStatus = 0;

	public static int highestScore = 0;
	public  int nowTotal = 0;



	public int pointTotal = 0;

	private void checkRiddle(int paramInt) {
		mc.cancel();
		if(-1 == paramInt){
			new AlertDialog.Builder(this)
					.setMessage("时间到，点击确定进入下一题.")
					.setPositiveButton(R.string.btn_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									CPractice.this.nextRiddle();
								}
							}).show();
		}else{

			if ((this.mStatus == 1) && (this.mRiddle != null)) {
				if (this.mRiddle.getAnswer() != paramInt) {
					new AlertDialog.Builder(this)
							.setMessage(R.string.wrong_message)
							.setPositiveButton(R.string.btn_ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface paramDialogInterface,
												int paramInt) {
											CPractice.this.nextRiddle();
										}
									}).show();


				} else {
					nowTotal++;

					remindText.setText("您目前得分："+nowTotal);
					if(nowTotal>highestScore){
						highestScore = nowTotal;
						highestScoreText.setText(getText(R.string.highest_score).toString()+highestScore);
					}
					//this.mIsAnswer = true;
					new AlertDialog.Builder(this)
							.setIcon(R.drawable.dialog_icon)
							.setTitle("结果")
							.setMessage(R.string.right_message)
							.setPositiveButton(R.string.btn_ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface paramDialogInterface,
												int paramInt) {
											CPractice.this.nextRiddle();
										}
									}).show();

				}
			}
		}

	}



	private void findViews() {
		//PopupWindow里的两个Button
		goToShop = (ImageView) popview.findViewById(R.id.bt_ok);
		btReturn = (ImageView) popview.findViewById(R.id.bt_cancel);
		this.mQuestText = ((TextView) findViewById(R.id.text_quest));
		this.remindText = ((TextView) findViewById(R.id.remind_text));
		this.showTime = ((TextView) findViewById(R.id.show_time));
		this.textView1 = ((TextView) findViewById(R.id.textView1));
		this.highestScoreText = ((TextView) findViewById(R.id.highest_score));
		this.mStartBtn = ((ImageView) findViewById(R.id.btn_start));
		this.mgetAnswerBtn = ((ImageView) findViewById(R.id.btn_getanswer));
		this.clostMusic = ((ImageView) findViewById(R.id.btn_close_music));
		this.mReturnBtn = ((ImageView) findViewById(R.id.btn_return));
		this.btShare = ((ImageView) findViewById(R.id.btn_share));
		this.mAnswerTextArr[0] = ((TextView) findViewById(R.id.text_answer_0));
		this.mAnswerTextArr[1] = ((TextView) findViewById(R.id.text_answer_1));
		this.mAnswerTextArr[2] = ((TextView) findViewById(R.id.text_answer_2));
	}

	private void setListener() {
		goToShop.setOnClickListener(this);
		btReturn.setOnClickListener(this);
		this.mStartBtn.setOnClickListener(this);
		this.mReturnBtn.setOnClickListener(this);
		this.mgetAnswerBtn.setOnClickListener(this);
		this.btShare.setOnClickListener(this);
		this.clostMusic.setOnClickListener(this);
		this.textView1.setOnClickListener(this);

		for (int i = 0;; i++) {
			if (i >= this.mAnswerTextArr.length)
				return;
			this.mAnswerTextArr[i].setOnClickListener(this);
		}
	}

	// 获取灯谜的集合 //
	private void genRiddles() {
		try {
			InputStream inputStream = getResources().openRawResource(R.raw.q);
			CsvReader reader = new CsvReader(inputStream,Charset.forName("UTF-8"));
			while (reader.readRecord()) {
				String riddle = reader.get(0);
				String answers[] = new String[3];
				answers[0] =  reader.get(1);
				answers[1] =  reader.get(2);
				answers[2] =  reader.get(3);

				int answer = Integer.valueOf(reader.get(4));
				Riddle r = new Riddle();
				r.setRiddle(riddle);
				r.setAnswers(answers);
				r.setAnswer(answer);
				mRiddleList.add(r);
			}

		} catch (Exception e) {

		}
	}



	// 获取练习模式历史最高分 //
	private void genNormHighestScore() {
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(SqlHelper.DB_NAME, null);
		try {
			Cursor c = database.query("score", new String[] { "id", "type","score"}, null, null, null, null, null);
			String score = "0";
			while(c.moveToNext()){
				String type = c.getString(c.getColumnIndex("type"));
				score = c.getString(c.getColumnIndex("score"));
				if("norm".equals(type)){
					break;
				}
			}
			c.close();
			highestScore = Integer.valueOf(score);
			highestScoreText.setText(getText(R.string.highest_score).toString()+highestScore);
		} catch (Exception e) {

		}
	}



	public  void nextRiddle() {
		if ((this.mRiddleList == null) || (this.mRiddleList.isEmpty()))
			genRiddles();
		this.mRiddle = ((Riddle) this.mRiddleList.remove(this.mRandom
				.nextInt(this.mRiddleList.size())));
		this.mQuestText.setText(this.mRiddle.getRiddle());
		for (int i = 0;i<3; i++) {
			this.mAnswerTextArr[i].setText(this.mRiddle.getAnswers()[i]);
		}
		mc = new MyCount(10000, 1000);
		mc.start();
	}

	private void playGame() {
		//this.mPdg = ProgressDialog.show(this, "", getText(R.string.CPractice));
		mgetAnswerBtn.setEnabled(true);
		CPractice.this.genRiddles();
		CPractice.this.mHandler.sendEmptyMessage(0);
	}


	/**
	 * 获取答案
	 */
	private void getAnswer(){
		if(pointTotal < 20){
			ScoreWallSDK.getInstance(this,this).consumeScore(5);// //回调接口中返回消费信息状态
		}

		int answerIndex = this.mRiddle.getAnswer();
		String answer = this.mRiddle.getAnswers()[answerIndex-1];

		new AlertDialog.Builder(this)
				.setMessage(getText(R.string.answer).toString()+answer+".点击确定进入下一题。")
				.setPositiveButton(R.string.btn_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								CPractice.this.nextRiddle();
							}
						}).show();
	}



	private void stopGame() {
		mc.cancel();
		this.mStatus = 0;
		this.mStartBtn.setImageResource(R.drawable.bt_start);
		this.mQuestText.setText(getText(R.string.challenge_title));
		this.mgetAnswerBtn.setEnabled(false);
		for (int i = 0;i < this.mAnswerTextArr.length; i++) {
			this.mAnswerTextArr[i].setText("");
		}

		YjfSDK.getInstance(this,null).recordAppClose();//释放内存
		stopService(intent);
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(SqlHelper.DB_NAME, null);
		String UPDATE_DATA="update score set score='"+highestScore+"' where type='norm'";
		database.execSQL(UPDATE_DATA);
	}


	private void closeMusic(){
		clostMusic.setImageResource(R.drawable.startmusic);
		startMusicFlag = false;
		stopService(intent);
	}

	private void starMusic(){
		clostMusic.setImageResource(R.drawable.closemusci);
		startMusicFlag = true;
		startService(intent);
	}

	public void onClick(View view) {
		//查詢用戶積分
		ScoreWallSDK.getInstance(CPractice.this,CPractice.this).getScore();

		if(mStartBtn == view){
			playGame();
		}else if(mReturnBtn == view){
			stopGame();
			startActivity(new Intent(this, Main.class));
			finish();
		}else if(mgetAnswerBtn == view){
			//奖励虚拟货币
			//AppConnect.getInstance(this).showAppOffers(this);
			mc.cancel();
			if(pointTotal<2){
				//规定弹窗的位置
				popWindow.showAtLocation(findViewById(R.id.main2), Gravity.BOTTOM,0, 0);

			}else{
				if(pointTotal < 20){
					new AlertDialog.Builder(this)
							.setMessage("获取答案需要花费两个积分，您确认要获取答案吗?点击确定即可获取答案.")
							.setPositiveButton(R.string.btn_ok,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface paramDialogInterface,
												int paramInt) {
											getAnswer();
										}
									}).setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();
				}else{
					getAnswer();
				}
			}
		}else if(clostMusic == view){
			if(startMusicFlag){
				closeMusic();
			}else{
				starMusic();
			}
		}else if(mAnswerTextArr[0] == view){
			checkRiddle(1);
		}else if(mAnswerTextArr[1] == view){
			checkRiddle(2);
		}else if(mAnswerTextArr[2] == view){
			checkRiddle(3);
		}else if(goToShop == view){
			startActivity(new Intent(this, Shop.class));
		}else if(btReturn == view){
			if (popWindow.isShowing()) {
				popWindow.dismiss();
			}
		}else if(btShare == view){
			if(null == mRiddle){
				new AlertDialog.Builder(this)
						.setMessage("请先点击左上角开始按钮进行游戏！")
						.setPositiveButton(R.string.btn_ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface paramDialogInterface,int paramInt) {
									}
								}).show();
			}else{
				Intent intent=new Intent(Intent.ACTION_SEND);
				intent.setType("image/*");
				intent.putExtra(Intent.EXTRA_SUBJECT, "灯谜大全");
				intent.putExtra(Intent.EXTRA_TEXT, "我正在玩灯谜大全，好玩又刺激！谜面："+mRiddle.getRiddle()+",猜猜谜底是什么？下载地址：http://bbs.mumayi.com/thread-2334747-1-1.html");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, getTitle()));
			}
		}

	}

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.cpractice);
		startService(intent);

		LayoutInflater layoutInflater = (LayoutInflater) (CPractice.this).getSystemService(LAYOUT_INFLATER_SERVICE);
		popview = layoutInflater.inflate(R.layout.popwindow, null);
		if (popWindow == null) {
			popWindow = new PopupWindow(popview,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}


		findViews();
		setListener();
		//YjfSDK.getInstance(this, null).initInstance("14146", "EM3USM4JG4TIM3QR8TVPJK13A6W5FF9TAH", "16906", "360");
		//YjfSDK.getInstance(this,null).initInstance("13866","EM6Z9IRY7EAS4E3IFLF9GMKMVUDS42BNDZ","16716","163");
		//ScoreWallSDK.getInstance(this,this).showAdlist();// 回调接口中返回激活信息		
		ScoreWallSDK.getInstance(this,this).getScore();// 回调接口中返回积分

		mc = new MyCount(10000, 1000);
		this.mgetAnswerBtn.setEnabled(false);
		genNormHighestScore();
		closeMusic();

		textRemind = (TextView) popview.findViewById(R.id.remindtext);
		textRemind.setText("获取答案需要2个积分，您当前积分不足，去商店免费赚取积分吧。");
		//用于退出整个应用
		ExitApplication.getInstance().addActivity(this);
	}

	protected void onDestroy() {
		stopGame();

		if (this.mSp != null) {
			this.mSp.release();
			this.mSp = null;
		}
		super.onDestroy();
	}



	class MyHandler extends Handler {
		public void handleMessage(Message paramMessage) {
			super.handleMessage(paramMessage);
			//CPractice.this.mPdg.dismiss();
			CPractice.this.mStartBtn.setImageResource(R.drawable.bt_next_task);
			CPractice.this.mStatus = 1;
			mc.cancel();
			CPractice.this.nextRiddle();
		}
	}


	class MyHandler2 extends Handler {
		public void handleMessage(Message paramMessage) {
			super.handleMessage(paramMessage);
			textView1.setText("当前余额："+CPractice.this.pointTotal);
		}
	}



	/**
	 * AppConnect.getPoints()方法的实现，必须实现
	 *
	 * @param currencyName
	 *            虚拟货币名称.
	 * @param pointTotal
	 *            虚拟货币余额.
	 */
	public void getUpdatePoints(String currencyName, int pointTotal) {
		this.pointTotal = pointTotal;
		h.sendEmptyMessageDelayed(0, pointTotal);
	}

	/**
	 * AppConnect.getPoints() 方法的实现，必须实现
	 *
	 * @param error
	 *            请求失败的错误信息
	 */
	public void getUpdatePointsFailed(String error) {

	}


	/*定义一个倒计时的内部类*/
	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		@Override
		public void onFinish() {
			CPractice.this.checkRiddle(-1);
		}
		@Override
		public void onTick(long millisUntilFinished) {
			showTime.setText("还剩(" + millisUntilFinished / 1000 + ")秒");
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