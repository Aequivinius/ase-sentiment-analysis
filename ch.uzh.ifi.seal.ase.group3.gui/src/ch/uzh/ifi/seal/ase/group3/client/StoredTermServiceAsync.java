package ch.uzh.ifi.seal.ase.group3.client;

import java.util.ArrayList;
import ch.uzh.ifi.seal.ase.group3.shared.SearchTerm;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>StoredTermService</code>.
 */
public interface StoredTermServiceAsync {
	void storeTerm(SearchTerm term, AsyncCallback<Void> callback) throws IllegalArgumentException;
	void getStoredTerms(AsyncCallback<ArrayList<SearchTerm>> callback);
	void clearAllStoredTerms(AsyncCallback<Void> callback);
}
