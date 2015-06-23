package com.ariaview;

import android.os.AsyncTask;
import android.widget.Button;

//Perform Click on button
public class PlayTask extends AsyncTask<String, String, String> {

	Button mButton;

	@Override
	protected String doInBackground(String... params) {
		publishProgress("+1");
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
