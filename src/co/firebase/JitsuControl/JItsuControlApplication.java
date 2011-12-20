package co.firebase.JitsuControl;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class JItsuControlApplication extends Application{
	public static final String FLURRY_KEY = "JADP3G2TCZCFIAYGKMFT";
	private static final String USERNAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	private Credentials credentials = new Credentials();
	
	public Credentials getCredentials() {
		SharedPreferences e = this.getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE);
		this.credentials.setUsername(e.getString(USERNAME, null));
		this.credentials.setPassword(e.getString(PASSWORD, null));
		return this.credentials;
	}
	
	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
		Editor e = this.getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE).edit();
		e.putString(USERNAME, credentials.getUsername());
		e.putString(PASSWORD, credentials.getPassword());
		e.commit();
	}
	
	public void logout() {
		Editor e = this.getSharedPreferences(this.getPackageName(), Context.MODE_PRIVATE).edit();
		e.clear();
		e.commit();
	}
	public boolean loggedIn() {
		return this.getCredentials().getUsername() != null;
	}
}
