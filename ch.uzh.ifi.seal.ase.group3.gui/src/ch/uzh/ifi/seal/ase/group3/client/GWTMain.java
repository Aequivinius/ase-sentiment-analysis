package ch.uzh.ifi.seal.ase.group3.client;

import java.util.List;

import ch.uzh.ifi.seal.ase.group3.db.model.Result;
import ch.uzh.ifi.seal.ase.group3.shared.Constants;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTMain implements EntryPoint {

	/* GUI vars */
	// Main Menu
	final HorizontalPanel mainMenuPanel = new HorizontalPanel();
	final Button addNewButton = new Button("Add New");
	final Button clearAllButton = new Button("Clear All");
	final Button refreshButton = new Button("Refresh");
	final Button testLoadButton = new Button("Test Load");
	final Button loginButton = new Button("Login");
	// final TextBox searchTermField = new TextBox();

	// General Visualization widgets/objects
	private Label visTitleLabel = new Label(Constants.VISUALIZATION_TITLE);
	private final String visDefaultContentText = Constants.WAIT_WHILE_FETCHING;
	private Label visDefaultContentLabel = new Label(visDefaultContentText);
	private VerticalPanel visContentPanel = new VerticalPanel();

	/* Button & shortcuts interaction */
	EventHandling eventHandler = new EventHandling(this);

	/* Charts instance */
	private Charts charts = new Charts();

	/* ASync Services */
	private StoredTermServiceAsync storedTermSvc = GWT.create(StoredTermService.class);
	private QueueManagerServiceAsync queueMgrSvc = GWT.create(QueueManagerService.class);

	/** This is the entry point method. */
	public void onModuleLoad() {

		/* build initial dynamic GUI parts */
		buildGUI();

		/* build list of already present search terms */
		buildDataSet();

		// Focus the cursor on the add new search term field when the app loads
		// searchTermField.setFocus(true);
		// searchTermField.selectAll();
	}

	/**
	 * Build and assemble GUI
	 */
	private void buildGUI() {

		/* build dynamic GUI parts */

		// assemble main menu
		mainMenuPanel.addStyleName("mainMenu");
		mainMenuPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainMenuPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		// searchTermField.setWidth("150px");
		// mainMenuPanel.add(searchTermField);

		mainMenuPanel.add(addNewButton);
		mainMenuPanel.add(clearAllButton);
		mainMenuPanel.add(testLoadButton);
		mainMenuPanel.add(refreshButton);

		RootPanel.get("mainMenu").add(mainMenuPanel);

		// assemble visualization title
		visTitleLabel.setStyleName("visualizationTitle");
		RootPanel.get("visualizationTitle").add(visTitleLabel);

		// assemble visualization
		visContentPanel.setStyleName("visualizationContent");
		visContentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		visContentPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		visDefaultContentLabel.addStyleName("alignCenter");
		visContentPanel.add(visDefaultContentLabel);
		RootPanel.get("visualizationContent").add(visContentPanel);

		/* add event handlers for mouse & keyboard */
		addNewButton.addClickHandler(eventHandler);
		clearAllButton.addClickHandler(eventHandler);
		testLoadButton.addClickHandler(eventHandler);
		refreshButton.addClickHandler(eventHandler);
	}

	/**
	 * Build search terms chart (Google Charts table) using RPC call
	 */
	private void buildDataSet() {
		AsyncCallback<List<Result>> callback = new AsyncCallback<List<Result>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(Constants.SERVER_ERROR);
			}

			@Override
			public void onSuccess(List<Result> result) {

				RootPanel.get("visualizationContent").clear();
				
				Window.alert("Results fetched");

				if (result.isEmpty()) {
					// no stored terms found
					Label commentLabel = new Label(Constants.NO_STORED_TERMS);
					// commentLabel.addStyleName("commentTextSingle");
					RootPanel.get("visualizationContent").add(commentLabel);
				} else {
					// display results
					charts.displayData(result);
				}
			}
		};

		storedTermSvc.getStoredTerms(callback);
	}

	/**
	 * @return Stored Term Service instance
	 */
	public StoredTermServiceAsync getStoredTermService() {
		return this.storedTermSvc;
	}

	/**
	 * @return Currently entered search term
	 */
//	public String getNewTerm() {
//		return searchTermField.getValue();
//	}

	/**
	 * @return Queue Manager Service instance
	 */
	public QueueManagerServiceAsync getQueueManager() {
		return queueMgrSvc;
	}

	/**
	 * Refresh visualization of stored search terms
	 */
	public void refreshDisplay() {
		buildDataSet();
	}
}
