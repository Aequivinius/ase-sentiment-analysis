package ch.uzh.ifi.seal.ase.group3.client;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.seal.ase.group3.shared.SearchTerm;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.DataView;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.Table;

/**
 * Manages Google Charts Visualization of stored search terms
 * 
 */
public class Charts{

	private AbstractDataTable dataTableVis;	
	private Table chartTableVis;
	private ArrayList<SearchTerm> termList;
	private ArrayList<String> termListSelected;

	/**
	 * Displays a Google Charts Visualization of stored search terms
	 */
	public void displayData(final ArrayList<SearchTerm> termList) {
		
		this.termList = termList;

		// Create a callback to be called when the visualization API
		// has been loaded.
		final Runnable onLoadCallback = new Runnable() {
			public void run() {
				Panel panel = RootPanel.get("visualizationContent");				

				dataTableVis = createTableStoredTerms();
				chartTableVis = new Table(dataTableVis, createOptionsTable(10, 0, true));
				chartTableVis.addSelectHandler(createSelectHandlerComments(chartTableVis, termList));
				panel.clear(); // clears previous visualization or the loading animation
				panel.add(chartTableVis);
			}
		};		

		VisualizationUtils.loadVisualizationApi(onLoadCallback, Table.PACKAGE);
	}

	/**
	 * Create table options for visualization
	 */
	private Table.Options createOptionsTable(int numberVisibleRows, int sortColumn, Boolean sortAscending) {
		Table.Options options = Table.Options.create();
		// options.setWidth(400);
    	// options.setHeight(240);
		options.setPage(Table.Options.Policy.ENABLE);
		options.setPageSize(numberVisibleRows);
		options.setSortColumn(sortColumn);
		options.setSortAscending(sortAscending);

		return options;
	}
	
	/**
	 * Create Google Charts Visualization of stored search terms
	 */
	private AbstractDataTable createTableStoredTerms() {
		DataTable data = DataTable.create();

		data.addColumn(ColumnType.STRING, "Term");
		data.addColumn(ColumnType.NUMBER, "Result");

		data.addRows(termList.size());

		int currentRow = 0;

		for (SearchTerm g : termList) {

			data.setValue(currentRow, 0, g.getTerm());
			data.setValue(currentRow, 1, g.getSentiment());
			++currentRow;
		}

		// Data view -- read only
		DataView result = DataView.create(data);
		return result;	
	}

	/**
	 * Manage selected items in table visualization
	 */
	private SelectHandler createSelectHandlerComments(final Table chart, final List<SearchTerm> termList) {
		return new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {				

				// clear List containing previous selection
				termListSelected.clear();

				// May be multiple selections.
				JsArray<Selection> selections = chart.getSelections();

				for (int i = 0; i < selections.length(); i++) {
					// add ID of each selection to list
					Selection selection = selections.get(i);
					if (selection.isRow()) {									
						int row = selection.getRow();
						// add all IDs of selected terms to list (retrievable using getSelectedTerms() )
						termListSelected.add(termList.get(row).getTerm());
					} else {
						// unreachable, only rows should be selected
					}
				}
			}
		};

	}

	/**
	 * Return list of currently selected terms
	 */
	public ArrayList<String> getSelectedTerms(){
		return termListSelected;
	}

	/**
	 * Clear current selection
	 */
	public void clearTermsSelection() {
		termListSelected.clear();
	}
}