package ch.uzh.ifi.seal.ase.group3.shared;

public final class Constants {

	/**
	 * Class containing constants
	 */
	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	public static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
		
	/**
	 * The title displayed to the user when fetching stored search terms
	 */
	public static final String VISUALIZATION_TITLE = "Stored search terms:";
	
	/**
	 * The message displayed to the user when fetching stored search terms
	 */
	public static final String WAIT_WHILE_FETCHING = "Please wait while already "
			+ "stored search terms and their results are being fetched from cache.";
	
	/**
	 * The message displayed to the user when no stored search terms are available
	 */
	public static final String NO_STORED_TERMS = "No stored search terms available yet!";
	
	/**
	 * The message displayed to the user when a new term should be stored (confirmation)
	 */
	public static final String ADD_NEW_TERM_CONFIRM = "Do you want to add the new search term to the processing queue?";
	
	/**
	 * The message displayed to the user when a new term should be stored but failed
	 */
	public static final String ADD_NEW_TERM_FAILED = "There was an error in adding the new search term. Please check your internet connection and retry.";
	
	/**
	 * The message displayed to the user when a new term was stored successfully
	 */
	public static final String ADD_NEW_TERM_SUCCESS = "Search term has been added to queue, will be processed shortly!";
	
	/**
	 * The message displayed to the user when all stored search terms are to be removed (confirmation)
	 */
	public static final String CLEAR_ALL_CONFIRM = "Do you want to clear all currently active search terms?";
	
	/**
	 * The message displayed to the user when all stored search terms are to be removed but removal failed
	 */
	public static final String CLEAR_ALL_FAIL = "There was an error while clearing the search terms. Please check your internet connection and retry.";
	
	/**
	 * The message displayed to the user when all stored search terms are removed successfully
	 */
	public static final String CLEAR_ALL_SUCCESS = "All stored search terms have been cleared successfully!";
	
	/**
	 * The message displayed to the user when an empty string is submitted for addition
	 */
	public static final String EMPTY_NEW_STRING = "The search term and/or date(s) cannot be empty. Please try again.";
	
	/**
	 * The message displayed to the user when results are refreshed.
	 */
	public static final String RESULTS_REFRESHED = "Results were refreshed.";
	
	/**
	 * The date format used for DateBox and string-to-date parsing
	 */
	public static final String DATE_FORMAT = "yyyy MMM dd";
}
