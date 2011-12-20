package co.firebase.JitsuControl;

public class ChangeStateParams {
	private Credentials credentials;
	private String applicationName;
	private Boolean stop;
	
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
	public Boolean getStop() {
		return stop;
	}
	public void setStop(Boolean stop) {
		this.stop = stop;
	}
	
}
