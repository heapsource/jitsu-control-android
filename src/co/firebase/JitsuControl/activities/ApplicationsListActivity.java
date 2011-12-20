package co.firebase.JitsuControl.activities;

import java.util.HashMap;
import java.util.List;

import com.flurry.android.FlurryAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import co.firebase.JitsuControl.ApplicationInfo;
import co.firebase.JitsuControl.Credentials;
import co.firebase.JitsuControl.JItsuControlApplication;
import co.firebase.JitsuControl.R;
import co.firebase.JitsuControl.tasks.Applications;
import co.firebase.tasks.FireTask;
import co.firebase.tasks.FireTaskUIHandler;
import co.firebase.tasks.GenericFireTaskResult;
import co.firebase.tasks.UnbindedUIException;
import co.firebase.tasks.ui.EnableHandler;
import co.firebase.tasks.ui.ToastHandler;
import co.firebase.tasks.ui.VisibilityHandler;

public class ApplicationsListActivity extends JitsuControlAsyncListActivity {
	ProgressBar progressBar = null;
	private final static int DETAILS_REQUEST_CODE = 20302;
	
	@Override
	public FireTaskUIHandler<?, ?, ?> onBindPersistentTaskUI(
			FireTask<?, ?, ?> task) {
		if (task instanceof Applications) {
			return ((Applications) task).new UIHandler() {

				@Override
				protected void onCollectChildren() {
					super.onCollectChildren();
					this.children().add(new VisibilityHandler<Credentials, Void, GenericFireTaskResult<List<ApplicationInfo>>>(
									progressBar));
					this.children().add(new ToastHandler<Credentials, Void, GenericFireTaskResult<List<ApplicationInfo>>>());
					this.children().add(new EnableHandler<Credentials, Void, GenericFireTaskResult<List<ApplicationInfo>>>(getListView()));
				}

				@Override
				public void onPostExecute(GenericFireTaskResult<List<ApplicationInfo>> result) {
					super.onPostExecute(result);
					if(result.isSuccess()) {
						showList(result.getValue());
					}
				}

			};
		}
		return null;
	}
	
	private void showList(List<ApplicationInfo> list) {
		this.setListAdapter(new ArrayAdapter<ApplicationInfo>(this, R.layout.two_line_image_row, list) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;
		 
				if (null == convertView) {
					row = ApplicationsListActivity.this.getLayoutInflater().inflate(R.layout.two_line_image_row, parent, false);
				} else {
					row = convertView;
				}
		 
				ApplicationInfo app = getItem(position);
				TextView tv = (TextView) row.findViewById(android.R.id.text1);
				tv.setText(app.getName());
				
				TextView tv2 = (TextView) row.findViewById(android.R.id.text2);
				tv2.setText(app.getState().toUpperCase());
				
				ImageView iv = (ImageView) row.findViewById(R.id.icon);
				iv.setImageResource(R.drawable.application);
				return row;
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.applications);
		this.progressBar = (ProgressBar)this.findViewById(R.id.progressBar1);
		String userName = ((JItsuControlApplication)this.getApplication()).getCredentials().getUsername();
		this.setTitle(String.format(this.getString(R.string.applications_title), userName));
		
		refreshList();
		
		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ApplicationInfo app = (ApplicationInfo)arg0.getItemAtPosition(arg2);
				Intent detailsIntent = new Intent(ApplicationsListActivity.this, ApplicationDetailsActivity.class);
				detailsIntent.putExtra(ApplicationDetailsActivity.APPLICATION, app);
				ApplicationsListActivity.this.startActivityForResult(detailsIntent, DETAILS_REQUEST_CODE);
			}
			
		});
		
		FlurryAgent.logEvent("Applications list");
	}

	public void refreshList() {
		Applications currentTask = null;
		try {
			currentTask = getAsyncTaskManager().issuePersistentTask(new Applications());
		} catch (UnbindedUIException e) {
			return;
		}
		currentTask.setInProgressDescription(this.getString(R.string.applications_progress));
		currentTask.execute(((JItsuControlApplication)this.getApplication()).getCredentials());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == DETAILS_REQUEST_CODE && resultCode == ApplicationDetailsActivity.CHANGED) {
			this.refreshList();
		}
	}
	
	
}
