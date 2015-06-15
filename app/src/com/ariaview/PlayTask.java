package com.ariaview;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

public class PlayTask extends AsyncTask<String, String, String> {

	Button mButton;
	
	@Override
	protected String doInBackground(String... params) {

		/*	try {
				if (!Thread.interrupted())
					Thread.sleep(2000);*/
				publishProgress("+1"); // call onProgressUpdate
			/*}
			catch (InterruptedException e) {
				Log.e("T", "InterruptedException", e);
			}*/
			
			

			return "END";
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		mButton.performClick();
		
	}

	public void setLabel(Button button) {
		mButton = button;
	}
	
}
