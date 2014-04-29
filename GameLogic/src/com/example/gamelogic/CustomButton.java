package com.example.gamelogic;

import android.content.Context;
import android.widget.Button;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class CustomButton extends Button implements OnClickListener {
	private boolean clickable;
	private String title;
	
	public CustomButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}
	
	public CustomButton(Context context) {
		super(context);
		setOnClickListener(this);
	}
	
	public boolean isClickable() {
		return clickable;
	}
	
	public void setClickable(boolean status) {
		clickable = status;
	}
	
	
	

	@Override
	public void onClick(View v) {
		if (clickable && MainActivity.clickTotal < 2) {
			Log.d("Custom Button", "Button clicked");
			this.setText(title);
			MainActivity.clickTotal++;
			setClickable(false);
			/*
			if (MainActivity.clickTotal > 2) {
				setClickable(false);
				return;
			}
			else{
				MainActivity.clickTotal++;
				setClickable(false);
			}
			*/
		}
		else {
			Log.d("Custom Button", "Button Press Not Accepted At This Time");
			return;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
