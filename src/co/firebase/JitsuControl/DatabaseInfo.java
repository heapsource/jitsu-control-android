package co.firebase.JitsuControl;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name, type, id;
	private Date creationDate, changedDate;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public static DatabaseInfo parse(JSONObject json) throws JSONException {
		DatabaseInfo app = new DatabaseInfo();
		app.setId(json.getString("_id"));
		app.setName(json.getString("name"));
		app.setType(json.getString("type"));
		app.setCreationDate(new Date(json.getLong("ctime")));
		app.setChangedDate(new Date(json.getLong("mtime")));
		return app;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getChangedDate() {
		return changedDate;
	}
	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}
}
