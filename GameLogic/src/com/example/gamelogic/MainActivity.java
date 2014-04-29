package com.example.gamelogic;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	
	public static int clickTotal;
	public static boolean isAcceptingPresses;
	private String comp[];
	private CustomButton b11,b12,b13,b21,b22,b23,b31,b32,b33,b41,b42,b43;
	private CustomButton buttonArray[];
	private boolean clickableArray[][];
	private int totalMatchesRequired = 6;
	private int currentMatches = 0;
	Context context;
	Handler handler;
	static Dialog dialog;
	private static boolean doRestart;
	private TextView moves;
	private static int movesTotal;
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		context = this;
		handler = new Handler();
		dialog = new Dialog(this);
		doRestart = false;
		moves = (TextView) findViewById(R.id.movetotal);
		movesTotal = 0;
		b11 = (CustomButton) findViewById(R.id.button11);
		b12 = (CustomButton) findViewById(R.id.button12);
		b13 = (CustomButton) findViewById(R.id.button13);
		b21 = (CustomButton) findViewById(R.id.button21);
		b22 = (CustomButton) findViewById(R.id.button22);
		b23 = (CustomButton) findViewById(R.id.button23);
		b31 = (CustomButton) findViewById(R.id.button31);
		b32 = (CustomButton) findViewById(R.id.button32);
		b33 = (CustomButton) findViewById(R.id.button33);
		b41 = (CustomButton) findViewById(R.id.button41);
		b42 = (CustomButton) findViewById(R.id.button42);
		b43 = (CustomButton) findViewById(R.id.button43);
		b11.setTitle("A"); b12.setTitle("A"); b13.setTitle("B");
		b21.setTitle("B"); b22.setTitle("C"); b23.setTitle("C");
		b31.setTitle("D"); b32.setTitle("D"); b33.setTitle("E");
		b41.setTitle("E"); b42.setTitle("F"); b43.setTitle("F");
		comp = new String[2];
		comp[0] = "";
		comp[1] = "";
		clickTotal = 0;
		isAcceptingPresses = true;
		buttonArray  = new CustomButton[] {b11,b12,b13,b21,b22,b23,b31,b32,b33,b41,b42,b43};
		clickableArray = new boolean[][]{{true, true},{true, true},{true, true},
										 {true, true},{true, true},{true, true},
										 {true, true},{true, true},{true, true},
										 {true, true},{true, true},{true, true}
										};
		
		
		new Thread(new Runnable() {
			//if two buttons have been clicked this code will compare the words in each
			public void run() {
				while(true) {
					while(clickTotal < 2) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					isAcceptingPresses = false;
					//Update boolean array with 2 new presses
					for (int i = 0; i < buttonArray.length; i++) {
						
						if (!buttonArray[i].isClickable()) {
							clickableArray[i][0] = false;
						}
					}
					int firstPressed = -1;
					int secondPressed = -1;
						//this for loop sets the two strings to be compared
						//this will be altered once the database gets implemented
						for (int i = 0; i < buttonArray.length; i++) {
							if (!clickableArray[i][0] && clickableArray[i][1]) {
								//button still in the game
								if (comp[0] != "") {
									Log.d(null, "Changing second!");
									secondPressed = i;
									comp[1] = buttonArray[i].getTitle();
								}
								else {
									Log.d(null, "Changing first!");
									firstPressed = i;
									comp[0] = buttonArray[i].getTitle();
								}
							}
						}
						Log.d("Comp Thread", "Comparing : " + comp[0] + " and " + comp[1]);
						if (comp[0].equals(comp[1])) {
							handler.post(new toast("Match Found!", context));
							buttonArray[firstPressed].post(new changeColor(buttonArray[firstPressed], Color.BLUE));
							buttonArray[secondPressed].post(new changeColor(buttonArray[secondPressed], Color.BLUE));
							clickableArray[firstPressed][1] = false;
							clickableArray[secondPressed][1] = false;
							buttonArray[firstPressed].setClickable(false);
							buttonArray[secondPressed].setClickable(false);
							comp[0] = "";
							comp[1] = "";
							clickTotal = 0;
							currentMatches++;
						}
						else {
							handler.post(new toast("Try Again", context));
							buttonArray[firstPressed].post(new changeText(firstPressed, buttonArray));
							buttonArray[secondPressed].post(new changeText(secondPressed, buttonArray));
							comp[0] = "";
							comp[1] = "";
							buttonArray[firstPressed].setClickable(true);
							buttonArray[secondPressed].setClickable(true);
							clickableArray[firstPressed][0] = true;
							clickableArray[secondPressed][0] = true;
						}
						clickTotal = 0;
						movesTotal++;
						moves.post(new changeMoves(moves));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		/*
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		*/
		
		//Track status of game
		new Thread(new Runnable() {
			Button exit;
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.d("Win Conditions", currentMatches + " : " + totalMatchesRequired);
					if (currentMatches == totalMatchesRequired) {
						currentMatches = 0;
						//Game is over
						handler.post(new buildDialog());
						//wait till program exits or restarts
						while (doRestart == false) {
							
						}
						for (int i = 0; i < buttonArray.length; i++) {
							buttonArray[i].post(new changeText(i, buttonArray));
							clickableArray[i][0] = true;
							clickableArray[i][1] = true;
							buttonArray[i].setClickable(true);
							movesTotal = 0;
							moves.post(new changeMoves(moves));
							
						}
						
					}
				}
			}
			
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	private static class buildDialog implements Runnable {
		
		Button exit;
		Button restart;
		@Override
		public void run() {
			dialog.setContentView(R.layout.winscreen);
			dialog.setTitle("Congratulations!");
			
			
			exit = (Button) dialog.findViewById(R.id.exit);
			exit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					System.exit(0);
				}
			
			});
			restart = (Button) dialog.findViewById(R.id.reboot);
			restart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					doRestart = true;
					dialog.cancel();
				}
				
			});
			dialog.show();
		}
		
	}
	
	private static class changeMoves implements Runnable {
		private final TextView tv;
		changeMoves(TextView tv) {
			this.tv = tv;
		}
		
		@Override
		public void run() {
			tv.setText(String.valueOf(movesTotal));
		}
		
	}
	
	private static class changeText implements Runnable {
		private final int loc;
		private final CustomButton[] arr;
		changeText(int loc, CustomButton[] arr) {
			this.loc = loc;
			this.arr = arr;
		}
		
		@Override
		public void run() {
			arr[loc].setText("");
		}
		
	}
	
	private static class toast implements Runnable {

		String msg;
		Context context;
		toast(String msg, Context context) {
			this.msg = msg;
			this.context = context;
		}
		
		@Override
		public void run() {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private static class changeColor implements Runnable {

		int color;
		Button button;
		
		changeColor(Button button, int color) {
			this.color = color;
			this.button = button;
		}
		
		@Override
		public void run() {
			button.setTextColor(color);
		}
		
	}
	
}