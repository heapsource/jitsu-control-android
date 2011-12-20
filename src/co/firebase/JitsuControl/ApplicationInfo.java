package co.firebase.JitsuControl;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class ApplicationInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id, name, subdomain, state;
	private SnapshotInfo activeSnapshot;
	private Date creationDate, changedDate;

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getChangedDate() {
		return changedDate;
	}

	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public boolean started() {
		return "started".equalsIgnoreCase(this.state);
	}

	public SnapshotInfo getActiveSnapshot() {
		return activeSnapshot;
	}

	public void setActiveSnapshot(SnapshotInfo activeSnapshot) {
		this.activeSnapshot = activeSnapshot;
	}
	public static ApplicationInfo parse(JSONObject json) throws JSONException {
		ApplicationInfo app = new ApplicationInfo();
		app.setId(json.getString("_id"));
		app.setName(json.getString("name"));
		app.setSubdomain(json.getString("subdomain"));
		app.setState(json.getString("state"));
		app.setActiveSnapshot(SnapshotInfo.parse(json.getJSONObject("active")));
		app.setCreationDate(new Date(json.getLong("ctime")));
		app.setChangedDate(new Date(json.getLong("mtime")));
		return app;
	}
}
