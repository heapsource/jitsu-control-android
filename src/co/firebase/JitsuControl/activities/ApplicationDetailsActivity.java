package co.firebase.JitsuControl.activities;

import java.util.HashMap;

import com.flurry.android.FlurryAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.firebase.JitsuControl.ApplicationInfo;
import co.firebase.JitsuControl.ChangeStateParams;
import co.firebase.JitsuControl.GetApplicationParams;
import co.firebase.JitsuControl.JItsuControlApplication;
import co.firebase.JitsuControl.R;
import co.firebase.JitsuControl.tasks.ChangeState;
import co.firebase.JitsuControl.tasks.GetApplication;
import co.firebase.tasks.FireTask;
import co.firebase.tasks.FireTaskUIHandler;
import co.firebase.tasks.GenericFireTaskResult;
import co.firebase.tasks.UnbindedUIException;
import co.firebase.tasks.ui.EnableHandler;
import co.firebase.tasks.ui.ToastHandler;
import co.firebase.tasks.ui.VisibilityHandler;

public class ApplicationDetailsActivity extends JitsuControlAsyncActivity {
	public final static String APPLICATION = "APPLICATION";
	private ApplicationInfo app;
	private Button changeStateButton, versionButton;
	private ProgressBar progressBar1;
	public final static int CHANGED = 1000;
	private final static int SNAPSHOTS_REQUEST_CODE = 255546, START_STOP_MENU = 5010, CHANGE_SNAPSHOT = 5011;
	
	@Override
	public FireTaskUIHandler<?, ?, ?> onBindPersistentTaskUI(
			FireTask<?, ?, ?> task) {
		if(task instanceof ChangeState) {
			return ((ChangeState)task).new UIHandler() {
				@Override
				protected void onCollectChildren() {
					super.onCollectChildren();
					this.children().add(new VisibilityHandler<ChangeStateParams, Void, GenericFireTaskResult<Boolean>>(
							progressBar1));
					this.children().add(new ToastHandler<ChangeStateParams, Void, GenericFireTaskResult<Boolean>>());
					this.children().add(new EnableHandler<ChangeStateParams, Void, GenericFireTaskResult<Boolean>>(changeStateButton));
					this.children().add(new EnableHandler<ChangeStateParams, Void, GenericFireTaskResult<Boolean>>(versionButton));
				}

				@Override
				public void onPostExecute(GenericFireTaskResult<Boolean> result) {
					super.onPostExecute(result);
					if(result.isSuccess()) {
						app.setState(app.started() ? "stopped" : "started");
						updateUI();
						setResult(CHANGED);
					}
				}
			};
		} else if(task instanceof GetApplication) {
			return ((GetApplication)task).new UIHandler() {
				@Override
				protected void onCollectChildren() {
					super.onCollectChildren();
					this.children().add(new VisibilityHandler<GetApplicationParams, Void, GenericFireTaskResult<ApplicationInfo>>(
							progressBar1));
					this.children().add(new ToastHandler<GetApplicationParams, Void, GenericFireTaskResult<ApplicationInfo>>());
				}

				@Override
				public void onPostExecute(GenericFireTaskResult<ApplicationInfo> result) {
					super.onPostExecute(result);
					if(result.isSuccess()) {
						app = result.getValue();
						updateUI();
						setResult(CHANGED);
					}
				}
			};
		}
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.application);
		this.progressBar1 = (ProgressBar)this.findViewById(R.id.progressBar1);
		this.app = (ApplicationInfo)this.getIntent().getSerializableExtra(APPLICATION);
		this.setTitle(this.app.getId());
		this.changeStateButton = (Button)this.findViewById(R.id.startStop);
		this.versionButton = (Button)this.findViewById(R.id.version);
		this.updateUI();
		
		FlurryAgent.logEvent("Application details");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
	}

	private void updateUI() {
		TextView idLabel = (TextView)this.findViewById(R.id.id);
		idLabel.setText(this.app.getId());
		
		TextView subdomainLabel = (TextView)this.findViewById(R.id.subdomain);
		subdomainLabel.setText(String.format("http://%s.nodejitsu.com", this.app.getSubdomain()));
		
		TextView stateLabel = (TextView)this.findViewById(R.id.state);
		stateLabel.setText(this.app.getState().toUpperCase());
		
		TextView changedDateLabel = (TextView)this.findViewById(R.id.changed_date);
		changedDateLabel.setText(this.app.getChangedDate().toLocaleString());
		
		TextView creationDateLabel = (TextView)this.findViewById(R.id.creation_date);
		creationDateLabel.setText(this.app.getCreationDate().toLocaleString());
		
		
		versionButton.setText(this.app.getActiveSnapshot().getId());
		
		this.changeStateButton.setText(this.app.started() ? "Stop": "Start");
	}
	
	public void changeStateAction(View view) {
		changeState();
	}

	public void changeState() {
		ChangeState currentTask = null;
		try {
			currentTask = getAsyncTaskManager().issuePersistentTask(new ChangeState());
		} catch (UnbindedUIException e) {
			return;
		}
		if(this.app.started()) {
			FlurryAgent.logEvent("Stop application");
		} else {
			FlurryAgent.logEvent("Start application");
		}
		currentTask.setInProgressDescription(this.app.started() ? "Stopping application": "Starting application");
		ChangeStateParams params = new ChangeStateParams();
		params.setStop(this.app.started());
		params.setCredentials(((JItsuControlApplication)this.getApplication()).getCredentials());
		params.setApplicationName(this.app.getName());
		currentTask.execute(params);
	}
	
	public void snapshotsAction(View view) {
		changeSnapshot();
	}

	public void changeSnapshot() {
		Intent intent = new Intent(this, SnapshotsListActivity.class);
		intent.putExtra(SnapshotsListActivity.APPLICATION, this.app);
		this.startActivityForResult(intent, SNAPSHOTS_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == SNAPSHOTS_REQUEST_CODE && resultCode == SnapshotsListActivity.CHANGED) {
			this.downloadAppInfo();
			setResult(CHANGED);
		}
	}
	private void downloadAppInfo() {
		GetApplication currentTask = null;
		try {
			currentTask = getAsyncTaskManager().issuePersistentTask(new GetApplication());
		} catch (UnbindedUIException e) {
			return;
		}
		currentTask.setInProgressDescription(this.app.started() ? "Stopping application": "Starting application");
		GetApplicationParams params = new GetApplicationParams();
		params.setCredentials(((JItsuControlApplication)this.getApplication()).getCredentials());
		params.setApplicationName(this.app.getName());
		currentTask.execute(params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, START_STOP_MENU, Menu.NONE, "");
		menu.add(Menu.NONE, CHANGE_SNAPSHOT, Menu.NONE, "Change Snapshot");
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean isExecutingChangeState = this.getAsyncTaskManager().isExecutingTask(ChangeState.class);
		boolean canChangeState = !isExecutingChangeState;
		menu.findItem(START_STOP_MENU).setTitle(this.app.started() ? "Stop": "Start").setEnabled(canChangeState);
		
		menu.findItem(CHANGE_SNAPSHOT).setEnabled(canChangeState);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case START_STOP_MENU:
				changeState();
				return true;
			case CHANGE_SNAPSHOT:
				changeSnapshot();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}