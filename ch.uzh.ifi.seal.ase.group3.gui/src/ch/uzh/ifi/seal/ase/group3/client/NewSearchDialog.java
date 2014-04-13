package ch.uzh.ifi.seal.ase.group3.client;

import java.util.List;

import ch.uzh.ifi.seal.ase.group3.db.model.Result;
import ch.uzh.ifi.seal.ase.group3.shared.Constants;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NewSearchDialog extends DialogBox implements ClickHandler{

	private GWTMain env;
	Button addButton = new Button("Add new term");
	Button cancelButton = new Button("Cancel");
	TextBox searchTermTextBox = new TextBox();

	public NewSearchDialog(GWTMain env) {

		this.env = env;

		// Set the dialog box's caption.
		setText("Add a search term");
		// Enable animation.
		setAnimationEnabled(true);
		// Enable glass background.
		setGlassEnabled(true);

		// Main Panel
		VerticalPanel mainDialogPanel = new VerticalPanel();
		setWidget(mainDialogPanel);

		// Search term details
		Label textLabel = new Label("Search Term:");
		searchTermTextBox.setWidth("150px");
		mainDialogPanel.add(textLabel);
		mainDialogPanel.add(searchTermTextBox);


		// is required to setFocus to textbox successfully. Reason: Widget takes
		// a certain time to load.
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				searchTermTextBox.setFocus(true);
			}
		});

		// Setting up buttons
		HorizontalPanel buttonPanel = new HorizontalPanel();

		buttonPanel.add(addButton);
		buttonPanel.add(cancelButton);
		mainDialogPanel.add(buttonPanel);

		addButton.addClickHandler(this);
		cancelButton.addClickHandler(this);
	};

	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();

		if (sender == cancelButton) {
			searchTermTextBox.setText("");
			NewSearchDialog.this.hide();
		} 
		else if (sender == addButton) {

			final String newTerm = searchTermTextBox.getValue();

			// check for empty search term
			if(newTerm.isEmpty()) {
				Window.alert(Constants.EMPTY_NEW_STRING);
			}
			else {	

				// do asynchronous saving 
				AsyncCallback<Void> callback = new AsyncCallback<Void>(){
					public void onFailure(Throwable caught) {
						// Do something with errors.
						Window.alert (Constants.ADD_NEW_TERM_FAILED);
					}
					public void onSuccess(Void result){
						
						// start polling service: notifies GUI when new data can be displayed.
						AsyncCallback<List<String>> callbackRefresh = new AsyncCallback<List<String>>(){

							@Override
							public void onFailure(Throwable caught) {

								Window.alert ("Polling Service failed");
							}

							@Override
							public void onSuccess(List<String> result) {
								if (result.isEmpty()){
									Window.alert("polling onSuccess returned zero-sized Array!");
								}
								else{
									
									env.refreshDisplay();			
								}
								
								
							}
							
						};
						env.getPollingService().startPoll(callbackRefresh);
						
						
						
						// show Alert that term was saved
						Window.alert(Constants.ADD_NEW_TERM_SUCCESS);						
					}
				};

				// add search term to message queue system
				env.getQueueManager().addNewSearchTerm(newTerm, callback);

			}
			// hide dialog
			NewSearchDialog.this.hide();

		}

	}
}


