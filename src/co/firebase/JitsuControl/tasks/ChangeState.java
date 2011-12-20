package co.firebase.JitsuControl.tasks;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;

import co.firebase.JitsuControl.ChangeStateParams;
import co.firebase.JitsuControl.Credentials;
import co.firebase.tasks.GenericFireTaskResult;
import co.firebase.tasks.specialized.OuterTask;
import co.firebase.tasks.specialized.WebRequestParams;
import co.firebase.tasks.specialized.WebRequestResult;
import co.firebase.tasks.specialized.http.HttpTask;

public final class ChangeState extends OuterTask<ChangeStateParams, Void, GenericFireTaskResult<Boolean>, WebRequestParams, Integer, WebRequestResult> {
	
	public ChangeState() {
		super(new HttpTask());
	}
	
	@Override
	protected GenericFireTaskResult<Boolean> doInBackground(ChangeStateParams... params) {
		ChangeStateParams param = params[0];
		Credentials credentials = param.getCredentials();
		String apiAction = param.getStop() ? "stop": "start";
		String url = String.format("http://api.nodejitsu.com/apps/%s/%s/%s", param.getCredentials().getUsername(), param.getApplicationName(), apiAction);
		WebRequestParams webRequest = new WebRequestParams(new HttpPost(url));
		UsernamePasswordCredentials cred = new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPassword());
		webRequest.setCredentials(cred);
		WebRequestResult result = this.executeInner(webRequest);
		String completedAction = param.getStop() ? "stopped" : "started";
		if(result.isSuccess()) {
			return (GenericFireTaskResult<Boolean>) result.asInnerOf(new GenericFireTaskResult<Boolean>(result.isSuccess())).setSuccessMessage(String.format("Application successfully %s", completedAction));
		} else {
			return (GenericFireTaskResult<Boolean>) result.asInnerOf(new GenericFireTaskResult<Boolean>(result.isSuccess())).setErrorMessage(String.format("The application can not be %s", completedAction));
		}
	}
}
