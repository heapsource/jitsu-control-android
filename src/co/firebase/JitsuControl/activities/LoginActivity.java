package co.firebase.JitsuControl.activities;

import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import co.firebase.JitsuControl.Credentials;
import co.firebase.JitsuControl.JItsuControlApplication;
import co.firebase.JitsuControl.LoginResult;
import co.firebase.JitsuControl.R;
import co.firebase.JitsuControl.tasks.Login;
import co.firebase.tasks.FireTask;
import co.firebase.tasks.FireTaskUIHandler;
import co.firebase.tasks.UnbindedUIException;
import co.firebase.tasks.ui.ToastHandler;

public class LoginActivity extends JitsuControlAsyncActivity {
	private EditText userNameText, passwordText;
	private JItsuControlApplication app = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		this.userNameText = (EditText)this.findViewById(R.id.username);
		this.passwordText = (EditText)this.findViewById(R.id.password);
		
		app = (JItsuControlApplication)this.getApplication();
		
		this.userNameText.setText(app.getCredentials().getUsername());
		this.passwordText.setText(app.getCredentials().getPassword());
	}
	
	private void saveCredentials() {
		Credentials credentials = app.getCredentials();
		credentials.setUsername(this.userNameText.getText().toString());
		credentials.setPassword(this.passwordText.getText().toString());
		app.setCredentials(credentials);
	}
	
	@Override
	public FireTaskUIHandler<?, ?, ?> onBindPersistentTaskUI(
			FireTask<?, ?, ?> task) {
		if (task instanceof Login) {
			return ((Login) task).new UIHandler() {

				@Override
				protected void onCollectChildren() {
					super.onCollectChildren();
					this.children().add(new ToastHandler<Credentials, Void, LoginResult>());
				}

				@Override
				public void onPostExecute(LoginResult result) {
					super.onPostExecute(result);
					if(result.isSuccess()) {
						saveCredentials();
						
						HashMap<String, String> eventParams = new HashMap<String, String>();
						eventParams.put("username", userNameText.getText().toString());
						FlurryAgent.logEvent("Login",  eventParams);
						
						LoginActivity.this.startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
						finish();
					}
				}

			};
		}
		return null;
	}
	
	public void loginAction(View sender) {
		Login currentLoginTask = null;
		try {
			currentLoginTask = getAsyncTaskManager().issuePersistentTask(new Login());
		} catch (UnbindedUIException e) {
			return;
		}
		currentLoginTask.setInProgressDescription(this.getString(R.string.login_progress));
		Credentials credentials = new Credentials();
		credentials.setUsername(this.userNameText.getText().toString());
		credentials.setPassword(this.passwordText.getText().toString());
		currentLoginTask.execute(credentials);
	}
}
