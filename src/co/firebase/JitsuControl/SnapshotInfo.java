package co.firebase.JitsuControl;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class SnapshotInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	private String id, filename, md5;
	private boolean active;
	private Date creationTime;
	
	public static SnapshotInfo parse(JSONObject json) throws JSONException {
		SnapshotInfo snapshot = new SnapshotInfo();
		snapshot.setId(json.getString("id"));
		snapshot.setMd5(json.getString("md5"));
		snapshot.setFilename(json.getString("filename"));
		snapshot.setActive(json.getBoolean("active"));
		snapshot.setCreationTime(new Date(json.getLong("ctime")));
		return snapshot;
	}
}
