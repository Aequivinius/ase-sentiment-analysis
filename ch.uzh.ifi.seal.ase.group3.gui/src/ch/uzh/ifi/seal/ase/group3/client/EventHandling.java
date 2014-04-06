package ch.uzh.ifi.seal.ase.group3.client;

import ch.uzh.ifi.seal.ase.group3.shared.Constants;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * @General Info: Implement any actions you want the keys to perform under ...action() at the bottom
 * 
 * @challenge The big challenge with the current shortcuts implementation is, that every shortcut fires multiple times.
 * Be sure to handle this appropriately. See Votevisualiser.prepareVisualisation() for Tills solution.
 * 
 */
public class EventHandling implements NativePreviewHandler, ClickHandler {

	HandlerRegistration shortcutHandler = Event.addNativePreviewHandler(this);
	private final GWTMain env;

	public EventHandling(final GWTMain env){
		this.env = env;
	}

	/** Handling for button clicks */ 	
	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();

		String buttonName = sender.getElement().getInnerText();

		if (buttonName.equalsIgnoreCase("Add New")) {
			//Window.alert("Add New clicked.");
			addNewAction();
		} 
		else if (buttonName.equalsIgnoreCase("Clear All")) {
			//Window.alert("Clear All clicked.");
			clearAllAction();
		} 
		else if (buttonName.equalsIgnoreCase("Test Load")) {
			 testLoadAction();
		}		
		else {
			// should not occur
			Window.alert("Error in ClickEvent! What the fuck did you click?");
		}
	}

	/** Handling for keyboard shortcuts */ 	
	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		NativeEvent ne = event.getNativeEvent();

		if (ne.getCtrlKey() && ne.getAltKey()){	// CTRL + ALT + ...

			switch (ne.getKeyCode()){
			case 'n':
			case 'N':
				//Window.alert("Add New Shortcut");
				addNewAction();
				break;

			case 't':
			case 'T':
				
				testLoadAction();
				break;

			case 'c':
			case 'C':
				//Window.alert("Clear All Shortcut");
				 clearAllAction();
				break;

			default:
				//Window.alert("Unknown shortcut.");
				break;
			}
		}
	}

	/** 
	 * Initiate an artificial test load
	 */
	private void testLoadAction(){
		// TODO: implement test load (use QueueManager to add a large amount of strings to the queue)	
		Window.alert("Test Load clicked.");
	}

	/** 
	 * Add new search term to queue
	 */
	private void addNewAction(){
		
		final String newTerm = env.getNewTerm();

		Boolean save = Window.confirm(Constants.ADD_NEW_TERM_CONFIRM);
		if (save) {

			// do asynchronous saving 
			AsyncCallback<Void> callback = new AsyncCallback<Void>(){
				public void onFailure(Throwable caught) {
					// Do something with errors.
					Window.alert (Constants.ADD_NEW_TERM_FAILED);
				}
				public void onSuccess(Void result){
					// show Alert that comment was saved
					Window.alert(Constants.ADD_NEW_TERM_SUCCESS);						
				}
			};

			// add search term to message queue system
			env.getQueueManager().addNewSearchTerm(newTerm, callback);
		}
	}		

	/** 
	 * Clear all stored search terms & results
	 */
	private void clearAllAction(){
		
		Boolean remove = Window.confirm(Constants.CLEAR_ALL_CONFIRM);
		if (remove) {
			// do asynchronous removal of stored terms
			AsyncCallback<Void> callback = new AsyncCallback<Void>(){
				public void onFailure(Throwable caught) {
					// Do something with errors.
					Window.alert (Constants.CLEAR_ALL_FAIL);
				}
				public void onSuccess(Void result){
					// show Alert that comment was saved
					Window.alert(Constants.CLEAR_ALL_SUCCESS);
					// refresh display
					env.refreshDisplay();
				}
			};	
			
			
			// add search term to message queue system
			env.getQueueManager().removeSearchTerms(callback);			
			env.getStoredTermService().clearAllStoredTerms(callback);
		}
	}	
}


