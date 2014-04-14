package ch.uzh.ifi.seal.ase.group3.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class OnDBChange {

	private GWTMain env;

	private AsyncCallback<Void> callback;

	public OnDBChange(GWTMain env) {

		this.env = env;
	}

	public void waitForDB() {
		callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Void result) {

				env.refreshDisplay(); // refresh that GUI!
				env.getStoredTermService().waitForDBChange(callback); // restart polling for DB changes

			}

		};

		env.getStoredTermService().waitForDBChange(callback);
	}
}
