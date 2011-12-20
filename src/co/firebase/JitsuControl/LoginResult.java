package co.firebase.JitsuControl;

import co.firebase.tasks.GenericFireTaskResult;

public final class LoginResult extends GenericFireTaskResult<Boolean> {
	public LoginResult(Boolean success, Exception error) {
		super(success, error);
	}
}
