package com.example.test_activityorientation;

import android.app.Activity;
import android.os.Bundle;

public class PortraitActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_portrait);
	}
}
