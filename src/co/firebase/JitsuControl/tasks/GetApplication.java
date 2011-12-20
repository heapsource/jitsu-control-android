package co.firebase.JitsuControl.tasks;

import java.util.List;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import co.firebase.JitsuControl.ApplicationInfo;
import co.firebase.JitsuControl.Credentials;
import co.firebase.JitsuControl.GetApplicationParams;
import co.firebase.tasks.GenericFireTaskResult;
import co.firebase.tasks.specialized.OuterTask;
import co.firebase.tasks.specialized.WebRequestParams;
import co.firebase.tasks.specialized.http.HttpStringResponseTask;
import co.firebase.tasks.specialized.http.HttpTask;
import co.firebase.tasks.specialized.json.JSONResult;
import co.firebase.tasks.specialized.json.JSONTask;

public class GetApplication extends OuterTask<GetApplicationParams, Void, GenericFireTaskResult<ApplicationInfo>, WebRequestParams, Integer, JSONResult> {
	GenericFireTaskResult<List<ApplicationInfo>> results;

	public GetApplication() {
		super(new JSONTask<WebRequestParams,Integer>(new HttpStringResponseTask<WebRequestParams,Integer>(new HttpTask())));
	}
	
	@Override
	protected GenericFireTaskResult<ApplicationInfo> doInBackground(GetApplicationParams... arg0) {
		GetApplicationParams param = arg0[0];
		Credentials credentials = param.getCredentials();
		
		String url = String.format("http://api.nodejitsu.com/apps/%s/%s", credentials.getUsername(), param.getApplicationName());
		WebRequestParams webRequest = new WebRequestParams(new HttpGet(url));
		webRequest.setCredentials(new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPassword()));
		JSONResult jresult = this.executeInner(webRequest);
		if(!jresult.isSuccess()) {
			GenericFireTaskResult<ApplicationInfo> result = new GenericFireTaskResult<ApplicationInfo>();
			result.setInnerResult(jresult);
			return result;
		}
		JSONObject json = jresult.getValue();
		if(json == null)  {
			GenericFireTaskResult<ApplicationInfo> errorRes = new GenericFireTaskResult<ApplicationInfo>(null, new Exception("Invalid response"));
			errorRes.setInnerResult(jresult);
			errorRes.setErrorMessage("Invalid application");
			return errorRes;
		}
		GenericFireTaskResult<ApplicationInfo> result;
		try {
			result = new GenericFireTaskResult<ApplicationInfo>(ApplicationInfo.parse(json.getJSONObject("app")));
			result.setInnerResult(jresult);
			return result;
		} catch (JSONException e) {
			GenericFireTaskResult<ApplicationInfo> errorRes = new GenericFireTaskResult<ApplicationInfo>(null, new Exception("Invalid response"));
			errorRes.setInnerResult(jresult);
			errorRes.setErrorMessage("Invalid application");
			return errorRes;
		}
	}


}
