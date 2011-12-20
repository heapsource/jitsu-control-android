package co.firebase.JitsuControl.activities;

import com.flurry.android.FlurryAgent;

import android.os.Bundle;
import android.widget.TextView;
import co.firebase.JitsuControl.DatabaseInfo;
import co.firebase.JitsuControl.R;
import co.firebase.tasks.FireTask;
import co.firebase.tasks.FireTaskUIHandler;

public class DatabaseDetailsActivity  extends JitsuControlAsyncActivity {
	public final static String DATABASE = "DATABASE";
	private DatabaseInfo database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.database);
		this.database = (DatabaseInfo)this.getIntent().getSerializableExtra(DATABASE);
		this.setTitle(this.database.getId());
		TextView idLabel = (TextView)this.findViewById(R.id.id);
		idLabel.setText(this.database.getId());
		
		TextView typeLabel = (TextView)this.findViewById(R.id.type);
		typeLabel.setText(this.database.getType().toUpperCase());
		
		TextView changedDateLabel = (TextView)this.findViewById(R.id.changed_date);
		changedDateLabel.setText(this.database.getChangedDate().toLocaleString());
		
		TextView creationDateLabel = (TextView)this.findViewById(R.id.creation_date);
		creationDateLabel.setText(this.database.getCreationDate().toLocaleString());
		
		FlurryAgent.logEvent("Database details");
	}

	@Override
	public FireTaskUIHandler<?, ?, ?> onBindPersistentTaskUI(
			FireTask<?, ?, ?> task) {
		// TODO Auto-generated method stub
		return null;
	}
}
