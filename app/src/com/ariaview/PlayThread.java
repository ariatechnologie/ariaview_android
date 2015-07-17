/*
 * @author Oneltone Da Silva
 * @version 1 
 */

package com.ariaview;

import android.widget.Button;

//Execute PlayTask
public class PlayThread extends Thread{

	private Button mButton;
	
	public PlayThread(Button button) {
		mButton = button;
	}
	
	 public void run() {
		 PlayTask pt = new PlayTask();
		 pt.setLabel(mButton);
		 pt.execute();
	 }
	 
}
