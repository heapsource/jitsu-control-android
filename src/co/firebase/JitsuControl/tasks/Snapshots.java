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
import co.firebase.JitsuControl.SnapshotInfo;
import co.firebase.JitsuControl.SnapshotsParams;
import co.firebase.tasks.GenericFireTaskResult;
import co.firebase.tasks.specialized.OuterTask;
import co.firebase.tasks.specialized.WebRequestParams;
import co.firebase.tasks.specialized.http.HttpStringResponseTask;
import co.firebase.tasks.specialized.http.HttpTask;
import co.firebase.tasks.specialized.json.JSONResult;
import co.firebase.tasks.specialized.json.JSONTask;

public class Snapshots extends OuterTask<SnapshotsParams, Void, GenericFireTaskResult<List<SnapshotInfo>>, WebRequestParams, Integer, JSONResult> {
	GenericFireTaskResult<List<ApplicationInfo>> results;

	public Snapshots() {
		super(new JSONTask<WebRequestParams,Integer>(new HttpStringResponseTask<WebRequestParams,Integer>(new HttpTask())));
	}
	
	@Override
	protected GenericFireTaskResult<List<SnapshotInfo>> doInBackground(SnapshotsParams... arg0) {
		SnapshotsParams param = arg0[0];
		Credentials credentials = param.getCredentials();
		
		String url = String.format("http://api.nodejitsu.com/apps/%s/%s/snapshots", credentials.getUsername(), param.getApplicationName());
		ArrayList<SnapshotInfo> apps = new ArrayList<SnapshotInfo>();
		WebRequestParams webRequest = new WebRequestParams(new HttpGet(url));
		webRequest.setCredentials(new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPassword()));
		JSONResult jresult = this.executeInner(webRequest);
		if(!jresult.isSuccess()) {
			GenericFireTaskResult<List<SnapshotInfo>> result = new GenericFireTaskResult<List<SnapshotInfo>>(apps);
			result.setInnerResult(jresult);
			return result;
		}
		JSONObject json = jresult.getValue();
		if(json == null)  {
			GenericFireTaskResult<List<SnapshotInfo>> errorRes = new GenericFireTaskResult<List<SnapshotInfo>>(apps, new Exception("Invalid response"));
			errorRes.setInnerResult(jresult);
			errorRes.setErrorMessage("It seems like you don't have any snapshots in there");
			return errorRes;
		}
		GenericFireTaskResult<List<SnapshotInfo>> result = new GenericFireTaskResult<List<SnapshotInfo>>(apps);
		result.setInnerResult(jresult);
		try {
			JSONArray applicationsResult = (JSONArray) json.get("snapshots");
			if (applicationsResult != null) {
				for (int i = 0; i < applicationsResult.length(); i++) {
					JSONObject jsonItem = applicationsResult.getJSONObject(i);
					SnapshotInfo app = SnapshotInfo.parse(jsonItem);
					apps.add(app);
				}
			}
		} catch (JSONException e) {
			GenericFireTaskResult<List<SnapshotInfo>> errorResult = new GenericFireTaskResult<List<SnapshotInfo>>(null, e);
			errorResult.setInnerResult(jresult);
			errorResult.setErrorMessage(":( sorry, an error has occurred. Try again later or with a different query");
			return errorResult;
		}
		int totalResults = apps.size();
		if(totalResults == 0) {
			result.setSuccessMessage(String.format("You don't have any snapshots", totalResults));
		} else if(totalResults == 1) {
			result.setSuccessMessage(String.format("One snapshot found", totalResults));
		} else {
			result.setSuccessMessage(String.format("%d snapshots found", totalResults));
		}
		return result;
	}


}