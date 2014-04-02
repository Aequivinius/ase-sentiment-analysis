package ch.uzh.ifi.seal.ase.group3.client;


import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>QueueManagerService</code>.
 */
public interface QueueManagerServiceAsync {
	void addNewSearchTerm(String term, AsyncCallback<Void> callback) throws IllegalArgumentException;
}
