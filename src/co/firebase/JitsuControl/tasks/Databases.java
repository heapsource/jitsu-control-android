package co.firebase.JitsuControl.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.firebase.JitsuControl.DatabaseInfo;
import co.firebase.JitsuControl.Credentials;
import co.firebase.tasks.GenericFireTaskResult;
import co.firebase.tasks.specialized.OuterTask;
import co.firebase.tasks.specialized.WebRequestParams;
import co.firebase.tasks.specialized.http.HttpStringResponseTask;
import co.firebase.tasks.specialized.http.HttpTask;
import co.firebase.tasks.specialized.json.JSONResult;
import co.firebase.tasks.specialized.json.JSONTask;

public class Databases  extends OuterTask<Credentials, Void, GenericFireTaskResult<List<DatabaseInfo>>, WebRequestParams, Integer, JSONResult> {
	GenericFireTaskResult<List<DatabaseInfo>> results;

	public Databases() {
		super(new JSONTask<WebRequestParams,Integer>(new HttpStringResponseTask<WebRequestParams,Integer>(new HttpTask())));
	}
	
	@Override
	protected GenericFireTaskResult<List<DatabaseInfo>> doInBackground(Credentials... arg0) {
		Credentials credentials = arg0[0];
		
		String url = String.format("http://api.nodejitsu.com/databases/%s", credentials.getUsername());
		ArrayList<DatabaseInfo> apps = new ArrayList<DatabaseInfo>();
		WebRequestParams webRequest = new WebRequestParams(new HttpGet(url));
		webRequest.setCredentials(new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPassword()));
		JSONResult jresult = this.executeInner(webRequest);
		if(!jresult.isSuccess()) {
			GenericFireTaskResult<List<DatabaseInfo>> result = new GenericFireTaskResult<List<DatabaseInfo>>(apps);
			result.setInnerResult(jresult);
			return result;
		}
		JSONArray applicationsResult = jresult.getArrayValue();
		GenericFireTaskResult<List<DatabaseInfo>> result = new GenericFireTaskResult<List<DatabaseInfo>>(apps);
		result.setInnerResult(jresult);
		try {
			if (applicationsResult != null) {
				for (int i = 0; i < applicationsResult.length(); i++) {
					JSONObject jsonObject = applicationsResult.getJSONObject(i);
					apps.add(DatabaseInfo.parse(jsonObject));
				}
			}
		} catch (JSONException e) {
			GenericFireTaskResult<List<DatabaseInfo>> errorResult = new GenericFireTaskResult<List<DatabaseInfo>>(null, e);
			errorResult.setInnerResult(jresult);
			errorResult.setErrorMessage(":( sorry, an error has occurred. Try again later or with a different query");
			return errorResult;
		}
		int totalResults = apps.size();
		if(totalResults == 0) {
			result.setSuccessMessage(String.format("You don't have any database", totalResults));
		} else if(totalResults == 1) {
			result.setSuccessMessage(String.format("One database found", totalResults));
		} else {
			result.setSuccessMessage(String.format("%d databases found", totalResults));
		}
		return result;
	}


}