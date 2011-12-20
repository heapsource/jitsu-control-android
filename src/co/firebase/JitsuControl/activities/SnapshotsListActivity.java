package co.firebase.JitsuControl.activities;

import java.util.List;

import com.flurry.android.FlurryAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import co.firebase.JitsuControl.ApplicationInfo;
import co.firebase.JitsuControl.JItsuControlApplication;
import co.firebase.JitsuControl.R;
import co.firebase.JitsuControl.SnapshotInfo;
import co.firebase.JitsuControl.SnapshotsParams;
import co.firebase.JitsuControl.tasks.Snapshots;
import co.firebase.tasks.FireTask;
import co.firebase.tasks.FireTaskUIHandler;
import co.firebase.tasks.GenericFireTaskResult;
import co.firebase.tasks.UnbindedUIException;
import co.firebase.tasks.ui.ToastHandler;
import co.firebase.tasks.ui.VisibilityHandler;

public class SnapshotsListActivity extends JitsuControlAsyncListActivity {
	public final static String APPLICATION = "APPLICATION";
	private ProgressBar progressBar = null;
	private ApplicationInfo app = null;
	private final static int DETAILS_REQUEST_CODE = 20302;
	public final static int CHANGED = 1000;
	
	@Override
	public FireTaskUIHandler<?, ?, ?> onBindPersistentTaskUI(
			FireTask<?, ?, ?> task) {
		if (task instanceof Snapshots) {
			return ((Snapshots) task).new UIHandler() {

				@Override
				protected void onCollectChildren() {
					super.onCollectChildren();
					this.children().add(new VisibilityHandler<SnapshotsParams, Void, GenericFireTaskResult<List<SnapshotInfo>>>(
									progressBar));
					this.children().add(new ToastHandler<SnapshotsParams, Void, GenericFireTaskResult<List<SnapshotInfo>>>());
				}

				@Override
				public void onPostExecute(GenericFireTaskResult<List<SnapshotInfo>> result) {
					super.onPostExecute(result);
					if(result.isSuccess()) {
						showList(result.getValue());
					}
				}

			};
		}
		return null;
	}
	
	private void showList(List<SnapshotInfo> list) {
		this.setListAdapter(new ArrayAdapter<SnapshotInfo>(this, R.layout.two_line_image_row, list) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;
		 
				if (null == convertView) {
					row = SnapshotsListActivity.this.getLayoutInflater().inflate(R.layout.two_line_image_row, parent, false);
				} else {
					row = convertView;
				}
				SnapshotInfo item = getItem(position);
				TextView tv = (TextView) row.findViewById(android.R.id.text1);
				tv.setText(item.getFilename() + (item.isActive() ? " <Active>": ""));
				
				TextView tv2 = (TextView) row.findViewById(android.R.id.text2);
				tv2.setText(item.getCreationTime().toLocaleString());
				
				ImageView iv = (ImageView) row.findViewById(R.id.icon);
				iv.setImageResource(R.drawable.archive);
				return row;
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.app = (ApplicationInfo)this.getIntent().getSerializableExtra(APPLICATION);
		this.setContentView(R.layout.snapshots);
		this.progressBar = (ProgressBar)this.findViewById(R.id.progressBar1);
		this.setTitle(String.format(this.getString(R.string.snapshots_title), this.app.getId()));
		
		this.refreshList();
		
		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SnapshotInfo snapshot = (SnapshotInfo)arg0.getItemAtPosition(arg2);
				Intent detailsIntent = new Intent(SnapshotsListActivity.this, SnapshotDetailsActivity.class);
				detailsIntent.putExtra(SnapshotDetailsActivity.APPLICATION, app);
				detailsIntent.putExtra(SnapshotDetailsActivity.SNAPSHOT, snapshot);
				SnapshotsListActivity.this.startActivityForResult(detailsIntent, DETAILS_REQUEST_CODE);
			}
			
		});
		FlurryAgent.logEvent("Snapshots list");
	}
	
	private void refreshList() {
		Snapshots currentTask = null;
		try {
			currentTask = getAsyncTaskManager().issuePersistentTask(new Snapshots());
		} catch (UnbindedUIException e) {
			return;
		}
		currentTask.setInProgressDescription(this.getString(R.string.snapshots_progress));
		SnapshotsParams params = new SnapshotsParams();
		params.setCredentials(((JItsuControlApplication)this.getApplication()).getCredentials());
		params.setApplicationName(this.app.getName());
		currentTask.execute(params);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == DETAILS_REQUEST_CODE && resultCode == SnapshotDetailsActivity.CHANGED) {
			this.setResult(CHANGED);
			this.refreshList();
		}
	}
	
}
