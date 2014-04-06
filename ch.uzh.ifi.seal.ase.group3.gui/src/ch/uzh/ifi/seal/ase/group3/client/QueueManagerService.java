package ch.uzh.ifi.seal.ase.group3.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("queuemanager")
public interface QueueManagerService extends RemoteService {
	void addNewSearchTerm(String term) throws IllegalArgumentException;
	void removeSearchTerms();
}
