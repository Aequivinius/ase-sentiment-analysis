package ch.uzh.ifi.seal.ase.group3.client;

import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.seal.ase.group3.db.model.Result;

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
public class Charts {

	private AbstractDataTable dataTableVis;
	private Table chartTableVis;
	private List<Result> termList;
	private List<String> termListSelected = new ArrayList<String>();

	/**
	 * Displays a Google Charts Visualization of stored search terms
	 */
	public void displayData(final List<Result> termList) {

		this.termList = termList;

		// Create a callback to be called when the visualization API
		// has been loaded.
		final Runnable onLoadCallback = new Runnable() {
			public void run() {
				Panel panel = RootPanel.get("visualizationContent");

				dataTableVis = createTableStoredTerms();
				chartTableVis = new Table(dataTableVis, createOptionsTable(20, 0, true));
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
		data.addColumn(ColumnType.NUMBER, "# Tweets");
		data.addColumn(ColumnType.DATE, "Start Date");
		data.addColumn(ColumnType.DATE, "End Date");
		data.addColumn(ColumnType.DATE, "Computed At");
		data.addColumn(ColumnType.NUMBER, "Calculation Time (s)");
		// data.addColumn(ColumnType.NUMBER, "User ID");

		data.addRows(termList.size());

		int currentRow = 0;

		for (Result g : termList) {
			data.setValue(currentRow, 0, g.getQuery());
			data.setValue(currentRow, 1, round(g.getSentiment()));
			data.setValue(currentRow, 2, g.getNumTweets());
			data.setValue(currentRow, 3, g.getStartDate());
			data.setValue(currentRow, 4, g.getEndDate());
			data.setValue(currentRow, 5, g.getComputedAt());
			data.setValue(currentRow, 6, round(g.getCalculationTime() / 1000.0));
			// data.setValue(currentRow, 7, g.getUserId());

			++currentRow;
		}

		// Data view -- read only
		DataView result = DataView.create(data);
		return result;
	}

	/**
	 * Rounds a value to two decimals
	 * 
	 * @param value
	 * @return
	 */
	private String round(double value) {
		return String.valueOf(((double) Math.round(value * 100)) / 100);
	}

	/**
	 * Manage selected items in table visualization
	 */
	private SelectHandler createSelectHandlerComments(final Table chart, final List<Result> termList) {
		return new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {

				// clear List containing previous selection
				if (!termListSelected.isEmpty()) {
					termListSelected.clear();
				}

				// May be multiple selections.
				JsArray<Selection> selections = chart.getSelections();

				for (int i = 0; i < selections.length(); i++) {
					// add ID of each selection to list
					Selection selection = selections.get(i);
					if (selection.isRow()) {
						int row = selection.getRow();
						// add all IDs of selected terms to list (retrievable using getSelectedTerms() )
						termListSelected.add(termList.get(row).getQuery());
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
	public List<String> getSelectedTerms() {
		return termListSelected;
	}

	/**
	 * Clear current selection
	 */
	public void clearTermsSelection() {
		termListSelected.clear();
	}
}