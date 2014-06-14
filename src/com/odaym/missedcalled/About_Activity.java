package com.odaym.missedcalled;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class About_Activity extends Activity {
	private TextView aboutTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.about_activity_title);
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.act_about);

		aboutTV = (TextView) findViewById(R.id.aboutTV);
		aboutTV.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
