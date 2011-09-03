package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.FileState;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

/**
 * Table model for data view table.
 *
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class DataTableModel extends AbstractTableModel implements ILanguage {


	private static final long serialVersionUID = -6966275553679729950L;
	private ResourceBundle resource;
	private String resourceBundlePath;
	private final List<DataRowModel> data;
	private List<String> columnNames;
	private final int UNIT = 1024;
	/**
	 * Index of action column.
	 */
	public static final int ACTION_COLUMN = 0;
	/**
	 * Index of name column.
	 */
	public static final int NAME_COLUMN = 1;
	/**
	 * Index of MIME type column.
	 */
	public static final int MIME_COLUMN = 2;
	/**
	 * Index of file size column.
	 */
	public static final int SIZE_COLUMN = 3;
	/**
	 * Index of download state column.
	 */
	public static final int DOWNLOADED_COLUMN = 4;

	/**
	 * Constructor of data table model.
	 */
	public DataTableModel() {
		super();
		LanguageObservable.getInstance().attach(this);
		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");
		initColumns();
		data = new LinkedList<DataRowModel>();
	}

	/**
	 * Getter of column class type. Necessary for first column in order to be a
	 * check box.
	 *
	 * @param columnIndex column index
	 * @return class
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return Boolean.class;
		}
		else {
			return String.class;
		}
	}

	/**
	 * Init. method - adding columns with proper names.
	 */
	private void initColumns() {
		columnNames = new LinkedList<String>();
		columnNames.add("table.ededb.datatable.action");
		columnNames.add("table.ededb.datatable.filename");
		columnNames.add("table.ededb.datatable.mime");
		columnNames.add("table.ededb.datatable.size");
		columnNames.add("table.ededb.datatable.localcopy");
	}

	/**
	 * Getter of row count.
	 *
	 * @return integer - row count
	 */
	@Override
	public int getRowCount() {
		return data.size();
	}

	/**
	 * Getter of column count.
	 *
	 * @return integer - column count
	 */
	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	/**
	 * Getter of column name.
	 *
	 * @param columnIndex index of column
	 * @return column name
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return resource.getString(columnNames.get(columnIndex));
	}

	/**
	 * Getter of value in table.
	 *
	 * @param rowIndex row index
	 * @param columnIndex column index
	 * @return Object (type depends of column index : 0 - boolean, 1 - String, 2
	 *         - String, 3 - long, 4 - FileState, else - false)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		if (rowIndex < 0 || rowIndex > data.size()) {
			return null;
		}

		switch (columnIndex) {
		case 0:
			return data.get(rowIndex).isSelected();
		case 1:
			return data.get(rowIndex).getFileInfo().getFilename();
		case 2:
			return data.get(rowIndex).getFileInfo().getMimeType();
		case 3:
			return countFileSize(data.get(rowIndex).getFileInfo().getLength());
		case 4:
			return data.get(rowIndex).getState();
		default:
			return false;
		}
	}

	/**
	 * Setter of table cell value
	 *
	 * @param object input value
	 * @param rowIndex row index
	 * @param columnIndex column index
	 */
	@Override
	public void setValueAt(Object object, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			try {
				boolean tmp = (Boolean) object;
				data.get(rowIndex).setSelected(tmp);
			}
			catch (Exception e) {
				System.err.println("DataTableModel set value: " + e);
			}
		}
		else
			if (columnIndex == getColumnCount() - 1) {
				try {
					data.get(rowIndex).setState((FileState) object);
				}
				catch (Exception e) {
					System.err.println("DataTableModel set value: " + e);
				}
			}

	}

	/**
	 * Getter whether is cell modifiable.
	 *
	 * @param rowIndex Row index
	 * @param columnIndex column index
	 * @return true/false
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex == 0);
	}

	/**
	 * Method for adding row in table.
	 *
	 * @param fileInfo File information class
	 * @param state DataRowModel.VALUE
	 * @param location experiment download folder path
	 */
	public void addRow(DataFileInfo fileInfo, FileState state, String location) {

		if (fileInfo != null && location != null
				&& (state == FileState.NO_COPY || state == FileState.HAS_COPY || state == FileState.DOWNLOADING)
				|| state == FileState.CORRUPTED) {
			data.add(new DataRowModel(fileInfo, state, location));
		}
		else {
			System.err.println("Row was not added - invalid data format!");
		}
		this.fireTableDataChanged();
	}

	/**
	 * Getter of data List<>
	 *
	 * @return List<DataRowModel> data
	 */
	public List<DataRowModel> getData() {
		return data;
	}

	/**
	 * Method clearing table.
	 */
	public void clear() {
		data.clear();
		this.fireTableDataChanged();
	}

	/**
	 * Setter of localization resource bundle path
	 *
	 * @param path path to localization source file.
	 */
	@Override
	public void setLocalizedResourceBundle(String path) {
		this.resourceBundlePath = path;
		resource = ResourceBundle.getBundle(path);
	}

	/**
	 * Getter of path to resource bundle.
	 *
	 * @return path to localization file.
	 */
	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	/**
	 * Setter of resource bundle key.
	 *
	 * @param string key
	 */
	@Override
	public void setResourceBundleKey(String string) {
		throw new UnsupportedOperationException("Method is not implemented yet...");
	}

	/**
	 * Method invoked by change of LanguageObservable.
	 *
	 * @throws JUIGLELangException
	 */
	@Override
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				fireTableStructureChanged();
			}
		});

	}

	/**
	 * Method for counting file size from long value
	 *
	 * @param length long value of file
	 * @return number Ki/Mi/Gi/Ti/Pi/Ei b
	 */
	private String countFileSize(long length) {

		if (length < UNIT) {
			return length + " B";
		}
		int exp = (int) (Math.log(length) / Math.log(UNIT));
		String pre = "KMGTPE".charAt(exp - 1) + "i";

		return String.format("%.1f %sB", length / Math.pow(UNIT, exp), pre);

	}
}
