package co.firebase.JitsuControl;

public class ActivateSnapshotParams {
	private Credentials credentials;
	private String applicationName, snapshotId;
	
	public Credentials getCredentials() {
		return credentials;
	}
	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getSnapshotId() {
		return snapshotId;
	}
	public void setSnapshotId(String snapshotId) {
		this.snapshotId = snapshotId;
	}
}
