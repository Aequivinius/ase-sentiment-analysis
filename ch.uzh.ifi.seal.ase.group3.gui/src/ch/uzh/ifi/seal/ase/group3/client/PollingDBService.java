package ch.uzh.ifi.seal.ase.group3.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("pollservice")
public interface PollingDBService extends RemoteService {
	List<String> startPoll();
}
