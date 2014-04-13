package ch.uzh.ifi.seal.ase.group3.client;


import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>QueueManagerService</code>.
 */
public interface PollingDBServiceAsync {

	void startPoll(AsyncCallback<List<String>> callback);
}
