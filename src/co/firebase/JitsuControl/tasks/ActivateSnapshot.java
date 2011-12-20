package co.firebase.JitsuControl.tasks;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;

import co.firebase.JitsuControl.ActivateSnapshotParams;
import co.firebase.JitsuControl.Credentials;
import co.firebase.tasks.GenericFireTaskResult;
import co.firebase.tasks.specialized.OuterTask;
import co.firebase.tasks.specialized.WebRequestParams;
import co.firebase.tasks.specialized.WebRequestResult;
import co.firebase.tasks.specialized.http.HttpTask;

public class ActivateSnapshot extends OuterTask<ActivateSnapshotParams, Void, GenericFireTaskResult<Boolean>, WebRequestParams, Integer, WebRequestResult> {
	
	public ActivateSnapshot() {
		super(new HttpTask());
	}
	
	@Override
	protected GenericFireTaskResult<Boolean> doInBackground(ActivateSnapshotParams... params) {
		ActivateSnapshotParams param = params[0];
		Credentials credentials = param.getCredentials();
		String url = String.format("http://api.nodejitsu.com/apps/%s/%s/snapshots/%s/activate", param.getCredentials().getUsername(), param.getApplicationName(), param.getSnapshotId());
		WebRequestParams webRequest = new WebRequestParams(new HttpPost(url));
		UsernamePasswordCredentials cred = new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPassword());
		webRequest.setCredentials(cred);
		WebRequestResult result = this.executeInner(webRequest);
		if(result.isSuccess()) {
			return (GenericFireTaskResult<Boolean>) result.asInnerOf(new GenericFireTaskResult<Boolean>(result.isSuccess())).setSuccessMessage("Snapshot successfully activated");
		} else {
			return (GenericFireTaskResult<Boolean>) result.asInnerOf(new GenericFireTaskResult<Boolean>(result.isSuccess())).setErrorMessage("Snapshot can not be activated at this time");
		}
	}
}
