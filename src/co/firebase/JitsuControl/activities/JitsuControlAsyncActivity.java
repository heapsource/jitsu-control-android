package co.firebase.JitsuControl.activities;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import co.firebase.JitsuControl.JItsuControlApplication;
import co.firebase.tasks.AsyncActivity;

public abstract class JitsuControlAsyncActivity extends AsyncActivity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		createMenu(this, menu);
		return true;
	}
	
	public static boolean createMenu(Activity activity, Menu menu) {
		menu.add("About").setIntent(new Intent(activity, AboutActivity.class));
		
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
