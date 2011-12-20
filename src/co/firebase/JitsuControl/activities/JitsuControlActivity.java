package co.firebase.JitsuControl.activities;

import co.firebase.JitsuControl.JItsuControlApplication;
import co.firebase.JitsuControl.R;
import co.firebase.tasks.FireTask;
import co.firebase.tasks.FireTaskUIHandler;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JitsuControlActivity extends JitsuControlAsyncActivity {
	JItsuControlApplication app = null;
	Button gotoDashboardButton = null, loginButton = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (JItsuControlApplication)this.getApplication();
        setContentView(R.layout.main);
        gotoDashboardButton = (Button)this.findViewById(R.id.dashboard);
        loginButton = (Button)this.findViewById(R.id.login);
        
        if(app.loggedIn()) {
        	showDashboard();
        }
    }

	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
	}

	public void updateUI() {
		app = (JItsuControlApplication)this.getApplication();
		
		if(app.loggedIn()) {
			loginButton.setText(R.string.logout);
			gotoDashboardButton.setVisibility(View.VISIBLE);
		} else {
			loginButton.setText(R.string.login);
			gotoDashboardButton.setVisibility(View.GONE);
		}
	}
	
    public void loginAction(View sender) {
    	app = (JItsuControlApplication)this.getApplication();
		if(app.loggedIn()) {
			app.logout();
		} else {
	        showLogin();
		}
		this.updateUI();
    }

	public void showLogin() {
		this.startActivity(new Intent(this, LoginActivity.class));
	}
	public void showDashboard() {
		this.startActivity(new Intent(this, DashboardActivity.class));
	}
	public void dashboardAction(View sender) {
		showDashboard();
	}

	@Override
	public FireTaskUIHandler<?, ?, ?> onBindPersistentTaskUI(
			FireTask<?, ?, ?> task) {
		// TODO Auto-generated method stub
		return null;
	}
}