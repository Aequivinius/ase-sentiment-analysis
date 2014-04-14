package ch.uzh.ifi.seal.ase.group3.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>QueueManagerService</code>.
 */
public interface QueueManagerServiceAsync {

	void addNewSearchTerm(String term, Date startDate, Date endDate, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void removeSearchTerms(AsyncCallback<Void> callback);
}
