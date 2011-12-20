package co.firebase.JitsuControl.activities;

import com.flurry.android.FlurryAgent;

import co.firebase.JitsuControl.R;
import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about);
		FlurryAgent.logEvent("About");
	}
	
}
