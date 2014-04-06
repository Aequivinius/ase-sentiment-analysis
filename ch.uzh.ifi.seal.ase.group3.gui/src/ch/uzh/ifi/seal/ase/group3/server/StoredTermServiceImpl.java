package ch.uzh.ifi.seal.ase.group3.server;

import java.util.ArrayList;

import ch.uzh.ifi.seal.ase.group3.client.StoredTermService;
import ch.uzh.ifi.seal.ase.group3.shared.SearchTerm;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StoredTermServiceImpl extends RemoteServiceServlet implements
		StoredTermService {

	@Override
	public void storeTerm(SearchTerm term) throws IllegalArgumentException {
		// TODO: store new term in DB
		
	}

	@Override
	public ArrayList<SearchTerm> getStoredTerms() {
		// TODO: get all stored terms with sentiments
		ArrayList<SearchTerm> tempList = new ArrayList<SearchTerm>();
		return tempList;
	}

	@Override
	public void clearAllStoredTerms() {
		// TODO: clear all routine
		
	}




}
