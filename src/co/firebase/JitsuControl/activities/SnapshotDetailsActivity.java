package co.firebase.JitsuControl.activities;

import com.flurry.android.FlurryAgent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.firebase.JitsuControl.ActivateSnapshotParams;
import co.firebase.JitsuControl.ApplicationInfo;
import co.firebase.JitsuControl.JItsuControlApplication;
import co.firebase.JitsuControl.R;
import co.firebase.JitsuControl.SnapshotInfo;
import co.firebase.JitsuControl.tasks.ActivateSnapshot;
import co.firebase.tasks.FireTask;
import co.firebase.tasks.FireTaskUIHandler;
import co.firebase.tasks.GenericFireTaskResult;
import co.firebase.tasks.UnbindedUIException;
import co.firebase.tasks.ui.EnableHandler;
import co.firebase.tasks.ui.ToastHandler;
import co.firebase.tasks.ui.VisibilityHandler;

public class SnapshotDetailsActivity extends JitsuControlAsyncActivity {
	public final static String APPLICATION = "APPLICATION";
	public final static String SNAPSHOT = "SNAPSHOT";
	private ApplicationInfo app;
	private SnapshotInfo snapshot;
	private Button activateButton;
	private ProgressBar progressBar1;
	public final static int CHANGED = 1000;
	
	@Override
	public FireTaskUIHandler<?, ?, ?> onBindPersistentTaskUI(
			FireTask<?, ?, ?> task) {
		if(task instanceof ActivateSnapshot) {
			return ((ActivateSnapshot)task).new UIHandler() {
				@Override
				protected void onCollectChildren() {
					super.onCollectChildren();
					this.children().add(new VisibilityHandler<ActivateSnapshotParams, Void, GenericFireTaskResult<Boolean>>(
							progressBar1));
					this.children().add(new ToastHandler<ActivateSnapshotParams, Void, GenericFireTaskResult<Boolean>>());
					this.children().add(new EnableHandler<ActivateSnapshotParams, Void, GenericFireTaskResult<Boolean>>(activateButton));
				}

				@Override
				public void onPostExecute(GenericFireTaskResult<Boolean> result) {
					super.onPostExecute(result);
					if(result.isSuccess()) {
						snapshot.setActive(true);
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
		this.setContentView(R.layout.snapshot);
		this.progressBar1 = (ProgressBar)this.findViewById(R.id.progressBar1);
		this.app = (ApplicationInfo)this.getIntent().getSerializableExtra(APPLICATION);
		this.snapshot = (SnapshotInfo)this.getIntent().getSerializableExtra(SNAPSHOT);
		this.setTitle(this.app.getId());
		this.activateButton = (Button)this.findViewById(R.id.activate);
		this.updateUI();
		
		FlurryAgent.logEvent("Snapshots details");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
	}

	private void updateUI() {
		TextView idLabel = (TextView)this.findViewById(R.id.id);
		idLabel.setText(this.snapshot.getId());
		
		TextView filenameLabel = (TextView)this.findViewById(R.id.filename);
		filenameLabel.setText(this.snapshot.getFilename());
		
		TextView activeLabel = (TextView)this.findViewById(R.id.active);
		activeLabel.setText(this.snapshot.isActive() ? "Yes": "No");
		
		TextView md5Label = (TextView)this.findViewById(R.id.md5);
		md5Label.setText(this.snapshot.getMd5());
		
		TextView dateLabel = (TextView)this.findViewById(R.id.creation_date);
		dateLabel.setText(this.snapshot.getCreationTime().toLocaleString());
		this.activateButton.setEnabled(this.snapshot.isActive() ? false : true);
	}
	
	public void activateAction(View view) {
		ActivateSnapshot currentTask = null;
		try {
			currentTask = getAsyncTaskManager().issuePersistentTask(new ActivateSnapshot());
		} catch (UnbindedUIException e) {
			return;
		}
		currentTask.setInProgressDescription("Activating Snapshot");
		ActivateSnapshotParams params = new ActivateSnapshotParams();
		params.setCredentials(((JItsuControlApplication)this.getApplication()).getCredentials());
		params.setApplicationName(this.app.getName());
		params.setSnapshotId(this.snapshot.getId());
		currentTask.execute(params);
		
		FlurryAgent.logEvent("Activate snapshot");
	}
}