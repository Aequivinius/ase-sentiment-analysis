package ch.uzh.ifi.seal.ase.group3.client;

import java.util.List;

import ch.uzh.ifi.seal.ase.group3.db.Result;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("storedterm")
public interface StoredTermService extends RemoteService {

	void storeTerm(Result term) throws IllegalArgumentException;

	void clearAllStoredTerms();

	List<Result> getStoredTerms();
}
