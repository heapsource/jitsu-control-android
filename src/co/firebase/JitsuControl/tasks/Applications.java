package co.firebase.JitsuControl.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.firebase.JitsuControl.ApplicationInfo;
import co.firebase.JitsuControl.Credentials;
import co.firebase.tasks.GenericFireTaskResult;
import co.firebase.tasks.specialized.OuterTask;
import co.firebase.tasks.specialized.WebRequestParams;
import co.firebase.tasks.specialized.http.HttpStringResponseTask;
import co.firebase.tasks.specialized.http.HttpTask;
import co.firebase.tasks.specialized.json.JSONResult;
import co.firebase.tasks.specialized.json.JSONTask;

public class Applications extends OuterTask<Credentials, Void, GenericFireTaskResult<List<ApplicationInfo>>, WebRequestParams, Integer, JSONResult> {
	GenericFireTaskResult<List<ApplicationInfo>> results;

	public Applications() {
		super(new JSONTask<WebRequestParams,Integer>(new HttpStringResponseTask<WebRequestParams,Integer>(new HttpTask())));
	}
	
	@Override
	protected GenericFireTaskResult<List<ApplicationInfo>> doInBackground(Credentials... arg0) {
		Credentials credentials = arg0[0];
		
		String url = String.format("http://api.nodejitsu.com/apps/%s", credentials.getUsername());
		ArrayList<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();
		WebRequestParams webRequest = new WebRequestParams(new HttpGet(url));
		webRequest.setCredentials(new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPassword()));
		JSONResult jresult = this.executeInner(webRequest);
		if(!jresult.isSuccess()) {
			GenericFireTaskResult<List<ApplicationInfo>> result = new GenericFireTaskResult<List<ApplicationInfo>>(apps);
			result.setInnerResult(jresult);
			return result;
		}
		JSONObject json = jresult.getValue();
		if(json == null)  {
			GenericFireTaskResult<List<ApplicationInfo>> errorRes = new GenericFireTaskResult<List<ApplicationInfo>>(apps, new Exception("Invalid response"));
			errorRes.setInnerResult(jresult);
			errorRes.setErrorMessage("It seems like you don't have any apps in there");
			return errorRes;
		}
		GenericFireTaskResult<List<ApplicationInfo>> result = new GenericFireTaskResult<List<ApplicationInfo>>(apps);
		result.setInnerResult(jresult);
		try {
			JSONArray applicationsResult = (JSONArray) json.get("apps");
			if (applicationsResult != null) {
				for (int i = 0; i < applicationsResult.length(); i++) {
					JSONObject jsonObject = applicationsResult.getJSONObject(i);
					apps.add(ApplicationInfo.parse(jsonObject));
				}
			}
		} catch (JSONException e) {
			GenericFireTaskResult<List<ApplicationInfo>> errorResult = new GenericFireTaskResult<List<ApplicationInfo>>(null, e);
			errorResult.setInnerResult(jresult);
			errorResult.setErrorMessage(":( sorry, an error has occurred. Try again later or with a different query");
			return errorResult;
		}
		int totalResults = apps.size();
		if(totalResults == 0) {
			result.setSuccessMessage(String.format("You don't have any application", totalResults));
		} else if(totalResults == 1) {
			result.setSuccessMessage(String.format("One application found", totalResults));
		} else {
			result.setSuccessMessage(String.format("%d applications found", totalResults));
		}
		return result;
	}


}
