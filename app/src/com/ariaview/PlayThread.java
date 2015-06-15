package com.ariaview;

import android.widget.Button;

public class PlayThread extends Thread{

	private Button mButton;
	
	public PlayThread(Button button) {
		mButton = button;
	}
	
	 public void run() {
		// mButton.performClick();
		 PlayTask pt = new PlayTask();
		 pt.setLabel(mButton);
		 pt.execute();
	 }
	 
}
