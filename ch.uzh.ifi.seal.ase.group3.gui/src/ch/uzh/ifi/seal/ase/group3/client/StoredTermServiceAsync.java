package ch.uzh.ifi.seal.ase.group3.client;

import java.util.List;

import ch.uzh.ifi.seal.ase.group3.db.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>StoredTermService</code>.
 */
public interface StoredTermServiceAsync {
	void storeTerm(Result term, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void getStoredTerms(AsyncCallback<List<Result>> callback);

	void clearAllStoredTerms(AsyncCallback<Void> callback);
}
