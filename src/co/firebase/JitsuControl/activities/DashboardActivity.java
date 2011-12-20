package co.firebase.JitsuControl.activities;

import java.util.ArrayList;
import java.util.HashMap;

import com.flurry.android.FlurryAgent;

import co.firebase.JitsuControl.JItsuControlApplication;
import co.firebase.JitsuControl.R;
import co.firebase.tasks.FireTask;
import co.firebase.tasks.FireTaskUIHandler;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DashboardActivity extends JitsuControlAsyncListActivity {
	private static final int APPS = 0;
	private static final int DATABASES = 1;
	ArrayList<DashboardEntry> entries = new ArrayList<DashboardEntry>();
	
	class DashboardEntry {
		public String title, name;
		public int id;
		public int icon;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dashboard);
		this.setTitle(String.format(this.getString(R.string.dashboard_title), ((JItsuControlApplication)this.getApplication()).getCredentials().getUsername()));
		DashboardEntry entry = null;
		entry = new DashboardEntry();
		entry.id = APPS;
		entry.name = "applications";
		entry.title = this.getString(R.string.dashboard_apps);
		entry.icon = R.drawable.application;
		entries.add(entry);
		
		entry = new DashboardEntry();
		entry.id = DATABASES;
		entry.name = "databases";
		entry.title = this.getString(R.string.dashboard_databases);
		entry.icon = R.drawable.database;
		entries.add(entry);
		
		this.setListAdapter(new ArrayAdapter<DashboardEntry>(this, R.layout.single_line_image_row, entries) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;
		 
				if (null == convertView) {
					row = DashboardActivity.this.getLayoutInflater().inflate(R.layout.single_line_image_row, parent, false);
				} else {
					row = convertView;
				}
				DashboardEntry entry = getItem(position);
				TextView tv = (TextView) row.findViewById(android.R.id.text1);
				tv.setText(entry.title);
				
				ImageView iv = (ImageView) row.findViewById(R.id.icon);
				iv.setImageResource(entry.icon);
				return row;
			}
		});
		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long itemId) {
				DashboardEntry entry = (DashboardEntry)arg0.getItemAtPosition(index);
				
				HashMap<String, String> eventParams = new HashMap<String, String>();
				eventParams.put("name", entry.name);
				FlurryAgent.logEvent("Dashboard item clicked",  eventParams);
				
				switch(entry.id) {
					case APPS:
						DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, ApplicationsListActivity.class));
					break;
					case DATABASES:
						DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, DatabasesListActivity.class));
					break;
				}
			}
		});
	}
	@Override
	public FireTaskUIHandler<?, ?, ?> onBindPersistentTaskUI(
			FireTask<?, ?, ?> task) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
