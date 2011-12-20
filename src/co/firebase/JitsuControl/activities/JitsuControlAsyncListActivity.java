package co.firebase.JitsuControl.activities;

import com.flurry.android.FlurryAgent;

import android.view.Menu;
import co.firebase.JitsuControl.JItsuControlApplication;
import co.firebase.tasks.AsyncListActivity;

public abstract class JitsuControlAsyncListActivity extends AsyncListActivity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		JitsuControlAsyncActivity.createMenu(this, menu);
		return true;
	}
	
	public void onStart()
	{
	   super.onStart();
	   FlurryAgent.onStartSession(this, JItsuControlApplication.FLURRY_KEY);
	}
	public void onStop()
	{
	   super.onStop();
	   FlurryAgent.onEndSession(this);
	}
}
