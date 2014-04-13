package ch.uzh.ifi.seal.ase.group3.client;

import java.util.List;

import ch.uzh.ifi.seal.ase.group3.db.model.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>StoredTermService</code>.
 */
public interface StoredTermServiceAsync {

	void getStoredTerms(AsyncCallback<List<Result>> callback);

	void clearAllStoredTerms(AsyncCallback<Void> callback);

	void waitForDBChange(AsyncCallback<Void> callback);


}
