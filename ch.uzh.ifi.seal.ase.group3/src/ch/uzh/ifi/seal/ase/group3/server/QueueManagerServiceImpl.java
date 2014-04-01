package ch.uzh.ifi.seal.ase.group3.server;

import ch.uzh.ifi.seal.ase.group3.client.QueueManagerService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class QueueManagerServiceImpl extends RemoteServiceServlet implements QueueManagerService {

	@Override
	public void addNewSearchTerm(String term) throws IllegalArgumentException {
		// TODO: add term to processing queue
		
	}
}
