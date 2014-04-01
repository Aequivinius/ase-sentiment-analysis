package ch.uzh.ifi.seal.ase.group3.client;

import java.util.ArrayList;
import ch.uzh.ifi.seal.ase.group3.shared.SearchTerm;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("storedterm")
public interface StoredTermService extends RemoteService {
	void storeTerm(SearchTerm term) throws IllegalArgumentException;
	void clearAllStoredTerms();
	ArrayList<SearchTerm> getStoredTerms();
}
