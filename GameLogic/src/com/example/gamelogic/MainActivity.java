package com.example.gamelogic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	public static int clickTotal;
	public static boolean isAcceptingPresses;
	private String comp[];
	private CustomButton b11,b12,b13,b21,b22,b23,b31,b32,b33,b41,b42,b43,b51,b52,b53,b61,b62,b63;
	private CustomButton buttonArray[];
	private boolean clickableArray[][];
	private int totalMatchesRequired = 9;
	private int currentMatches = 0;
	Context context;
	Handler handler;
	static Dialog dialog;
	private static boolean doRestart;
	private TextView moves;
	private static int movesTotal;
	private String[] tempArray;
	long timeStart = 0L;
	long timeInMillisec = 0L;
	long timeUpdate = 0L;
	long timeSwapBuff = 0L;
	private TextView timeView;	
	private Handler timeHandle;
	int timeBonus = 600;
	static int finalScore = 0;
	int mCount = 0;
	int sCount = 0;
	private static String playerName = "";
	private static String serverOut = "";

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
		timeView = (TextView) findViewById(R.id.timer);
		timeHandle = new Handler();
		b11 = (CustomButton) findViewById(R.id.button11); b12 = (CustomButton) findViewById(R.id.button12); b13 = (CustomButton) findViewById(R.id.button13);
		b21 = (CustomButton) findViewById(R.id.button21); b22 = (CustomButton) findViewById(R.id.button22); b23 = (CustomButton) findViewById(R.id.button23);
		b31 = (CustomButton) findViewById(R.id.button31); b32 = (CustomButton) findViewById(R.id.button32); b33 = (CustomButton) findViewById(R.id.button33); 
		b41 = (CustomButton) findViewById(R.id.button41); b42 = (CustomButton) findViewById(R.id.button42); b43 = (CustomButton) findViewById(R.id.button43);
		b51 = (CustomButton) findViewById(R.id.button51); b52 = (CustomButton) findViewById(R.id.button52); b53 = (CustomButton) findViewById(R.id.button53); 
		b61 = (CustomButton) findViewById(R.id.button61); b62 = (CustomButton) findViewById(R.id.button62); b63 = (CustomButton) findViewById(R.id.button63);
		//This is placeholder text for each button for the purposes of testing
		/*
		b11.setTitle("A"); b12.setTitle("A"); b13.setTitle("B");
		b21.setTitle("B"); b22.setTitle("C"); b23.setTitle("C");
		b31.setTitle("D"); b32.setTitle("D"); b33.setTitle("E");
		b41.setTitle("E"); b42.setTitle("F"); b43.setTitle("F");
		b51.setTitle("G"); b52.setTitle("G"); b53.setTitle("H");
		b61.setTitle("H"); b62.setTitle("I"); b63.setTitle("I");
		*/
		comp = new String[2];
		comp[0] = "";
		comp[1] = "";
		clickTotal = 0;
		isAcceptingPresses = true;
		buttonArray  = new CustomButton[] {b11,b12,b13,b21,b22,b23,b31,b32,b33,b41,b42,b43,b51,b52,b53,b61,b62,b63};
		clickableArray = new boolean[][]{{true, true},{true, true},{true, true},
										 {true, true},{true, true},{true, true},
										 {true, true},{true, true},{true, true},
										 {true, true},{true, true},{true, true},
										 {true, true},{true, true},{true, true},
										 {true, true},{true, true},{true, true}
										};

		//tempArray = new String[] {"A","B","C","D","E","F","G","H","I","A","B","C","D","E","F","G","H","I"};
		
		String newWords = "";

		new Thread(new Runnable() {
			public void run()
			{
				Socket socket;
				DataOutputStream outStream;
				DataInputStream inStream;
				
				try {
					socket = new Socket("sslab24.cs.purdue.edu", 5000);
					outStream = new DataOutputStream(socket.getOutputStream());
					inStream =  new DataInputStream(socket.getInputStream());

					outStream.writeUTF("GET-WORDS");

					newWords = inStream.readUTF();
				}
				catch(Exception e)
				{
					Log.d(null, e.toString());
				}
			}
		}).start();

		String [] args = str.split("\\|");
		for(int i = 0; i < 9; i++)
		{
			tempArray[i] = args[i];
		}

		randomize(tempArray, buttonArray);
		
		timeStart = SystemClock.uptimeMillis();
		timeHandle.postDelayed(timer, 0);
		
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
							buttonArray[firstPressed].post(new clearText(firstPressed, buttonArray));
							buttonArray[secondPressed].post(new clearText(secondPressed, buttonArray));
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
		
		
		//loop that constantly checks if the game has been won
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
					//Log.d("Win Conditions", currentMatches + " : " + totalMatchesRequired);
					if (currentMatches == totalMatchesRequired) {
						currentMatches = 0;
						
						String finalTime = (String) timeView.getText();
						int split = finalTime.indexOf(':');
						String min = finalTime.substring(0, split);
						String sec = finalTime.substring(split+1);
						int minConvert = Integer.parseInt(min);
						if (sec.startsWith("0")) {
							sec = sec.substring(1);
						}
						int secConvert = Integer.parseInt(sec);
						int secTotal = (60 * minConvert) + secConvert;
						Log.d("Score", "Seconds : " + secTotal);
						timeBonus = timeBonus - secTotal;
						Log.d("Score", "Time Bonus :" + timeBonus);
						
						int movesBonus = 3000;
						int moveDif = movesTotal - totalMatchesRequired;
						movesBonus = movesBonus - (10 * moveDif);
						Log.d("Score", "Moves Bonus : " + movesBonus);
						finalScore = movesBonus + timeBonus;
						
						handler.post(new enterName());
						
						/* moved to be after name screen
						//Game is over
						handler.post(new buildDialog());
						timeSwapBuff += timeInMillisec;
						timeHandle.removeCallbacks(timer);
						*/
						
						
						
						//wait till program exits or restarts
						while (doRestart == false) {
						}
						doRestart = false;
						for (int i = 0; i < buttonArray.length; i++) {
							buttonArray[i].post(new clearText(i, buttonArray));
							buttonArray[i].post(new clearTitle(i, buttonArray));
							clickableArray[i][0] = true;
							clickableArray[i][1] = true;
							buttonArray[i].setClickable(true);
							movesTotal = 0;
							moves.post(new changeMoves(moves));
							buttonArray[i].post(new changeColor(buttonArray[i], Color.BLACK));
							
						}
						randomize(tempArray, buttonArray);
						timeSwapBuff = 0L;
						timeStart = SystemClock.uptimeMillis();
						timeHandle.postDelayed(timer, 0);
						
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
	
	public void randomize(String[] list, CustomButton[] buttonArray) {
		//code to randomly assign words to buttons
		//Really inefficient, and would technically have the potential to take a long time to complete
		//so if anyone has a better algorithm feel free to replace it
		int j = 0;
		int r;
		Random random = new Random();
		do {
			r = random.nextInt(buttonArray.length);
			if (buttonArray[r].getTitle() == null || buttonArray[r].getTitle() == "") {
				buttonArray[r].setTitle(list[j]);
				j++;
			}
		} while (j < buttonArray.length);
	}
	
	//enter name for highscore
	private class enterName implements Runnable {
		
		Button ok;
		
		EditText userName = (EditText)dialog.findViewById(R.id.userName);
		
		@Override
		public void run() {
			dialog.setContentView(R.layout.namescreen);
			dialog.setTitle("Enter Name:");
			
			timeSwapBuff += timeInMillisec;
			timeHandle.removeCallbacks(timer);
			
			ok = (Button) dialog.findViewById(R.id.ok);
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					//Game is over
					playerName = ((EditText)((View) view.getParent()).findViewById(R.id.userName)).getText().toString();
					Log.d(null, playerName);
					handler.post(new buildDialog());
					/*timeSwapBuff += timeInMillisec;
					timeHandle.removeCallbacks(timer);*/
				}
			
			});
			
			dialog.show();
		}
	}

	//creates win screen dialog
	private static class buildDialog implements Runnable {
		
		Button exit;
		Button restart;
		
		TextView nameScores, highScores;
		
		@Override
		public void run() {
			dialog.setContentView(R.layout.winscreen);
			dialog.setTitle("Final Score = " + finalScore);
			
			 nameScores = (TextView)dialog.findViewById(R.id.nameScores);
			 highScores = (TextView)dialog.findViewById(R.id.highScores);
			
			
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
			
			//ProcessBuilder pb = new ProcessBuilder("connect.sh");
			
			new Thread(new Runnable() {
				public void run()
				{
					Socket socket;
					DataOutputStream outStream;
					DataInputStream inStream;
					
					try
					{
						//Process p = Runtime.getRuntime().exec("PRINT-SCORES");
						
						//pb.start();
						/*
						SSHClient ssh = new SSHClient();
						ssh.loadKnownHosts();
						ssh.connect("sslab01.cs.purdue.edu");
						ssh.authPublicKey("zlawson");
						Session session = ssh.startSession();
						Command cmd = session.exec("echo \"PRINT-SCORES\" | telnet sslab24.cs.purdue.edu 5000");
						System.out.println(cmd.getOutputAsString());
						session.close();
						ssh.disconnect();
						*/
						
						socket = new Socket("sslab24.cs.purdue.edu", 5000);
						outStream = new DataOutputStream(socket.getOutputStream());
						inStream =  new DataInputStream(socket.getInputStream());
						
						//outStream.writeUTF("echo \"PRINT-SCORES\" | telnet sslab24.cs.purdue.edu 5000");
						outStream.writeUTF("INSERT-SCORE|" + playerName + "|" + finalScore + "|10");
						
						/*while((serverOut = inStream.readUTF()) != null)
						{
							
						}*/
						
						serverOut = String.format("%3s %-25s %-5s\n", "#", "Name", "Score");
						serverOut += inStream.readUTF();
						
						/*String[] splitString = serverOut.split("\\|");
						String names = String.format("%3s %-25s\n", "#", "Name");
						String scores = String.format("%-5s\n", "Score");
						
						for(int i = 0; i < splitString.length; i++)
						{
							if(i % 2 == 0)
							{
								names += splitString[i];
							}
							else
							{
								scores += splitString[i];
							}
						}*/
						
						
						
						/*Log.d("Name", names);
						Log.d("Score", scores);
						nameScores.setText(names);
						highScores.setText(scores);*/
						nameScores.setText(serverOut);
						
						//outStream.close();
						//inStream.close();
						//socket.close();
					}
					catch(Exception e)
					{
						Log.d(null, e.toString());
					}
				}
			}).start();
			//nameScores.setText();
			
			dialog.show();
		}
	}

	//runnable to update the number of moves tally
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
	//runnable to reset the text of the button
	private static class clearText implements Runnable {
		private final int loc;
		private final CustomButton[] arr;
		clearText(int loc, CustomButton[] arr) {
			this.loc = loc;
			this.arr = arr;
		}
		
		@Override
		public void run() {
			arr[loc].setText("");
		}
		
	}
	
	private static class clearTitle implements Runnable {
		private final int loc;
		private final CustomButton[] arr;
		clearTitle(int loc, CustomButton[] arr) {
			this.loc = loc;
			this.arr = arr;
		}
		
		@Override
		public void run() {
			arr[loc].setTitle("");
		}
	}
	//toast to show success or failure with selections
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
	//runnable to change the color of the button text
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
	
	private Runnable timer = new Runnable() {

		@Override
		public void run() {
			timeInMillisec = SystemClock.uptimeMillis() - timeStart;
			timeUpdate = timeSwapBuff + timeInMillisec;
			int secs = (int) (timeUpdate / 1000);
			int mins = secs / 60;
			secs = secs % 60;
			//Log.d("Timer", "" + mins + ":" + secs);
			timeView.setText("" + mins + ":" + String.format("%02d", secs));
			timeHandle.postDelayed(this, 0);
		}
		
	};
	
}
