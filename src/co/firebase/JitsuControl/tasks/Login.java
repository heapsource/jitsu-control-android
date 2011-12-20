package co.firebase.JitsuControl.tasks;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;

import co.firebase.JitsuControl.Credentials;
import co.firebase.JitsuControl.LoginResult;
import co.firebase.tasks.specialized.OuterTask;
import co.firebase.tasks.specialized.WebRequestParams;
import co.firebase.tasks.specialized.WebRequestResult;
import co.firebase.tasks.specialized.http.HttpTask;

public class Login extends OuterTask<Credentials, Void, LoginResult, WebRequestParams, Integer, WebRequestResult> {
	
	public Login() {
		super(new HttpTask());
	}
	
	@Override
	protected LoginResult doInBackground(Credentials... params) {
		Credentials credentials = params[0];
		String url = String.format("http://api.nodejitsu.com/auth");
		WebRequestParams webRequest = new WebRequestParams(new HttpGet(url));
		UsernamePasswordCredentials cred = new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPassword());
		webRequest.setCredentials(cred);
		WebRequestResult result = this.executeInner(webRequest);
		if(result.isSuccess()) {
			return (LoginResult) result.asInnerOf(new LoginResult(result.isSuccess(), null)).setSuccessMessage("Successfully logged in");
		} else {
			int statusCode = result.getValue().getStatusLine().getStatusCode();
			if(statusCode == 403) {
				return (LoginResult) result.asInnerOf(new LoginResult(result.isSuccess(), null)).setErrorMessage("Oops... you can't login. Be sure your account was activated and the user and password are correct.");
			} else {
				return (LoginResult) result.asInnerOf(new LoginResult(result.isSuccess(), null)).setErrorMessage("Failed to login. Check your internet connection and try later.");
			}
		}
	}
}
