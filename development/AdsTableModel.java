
public class AdsTableModel extends javax.swing.table.AbstractTableModel
{
	private Ads ads;

	public AdsTableModel(Ads ads)
	{
		this.ads = ads;
	}

	public void setData(Ads ads)
	{
		this.ads = ads;
		this.fireTableStructureChanged();
	}

	public int getRowCount()
	{
		if (this.ads == null) {
			return 0;
		}
		return this.ads.numInstances();
	}

	public int getColumnCount()
	{
		if (this.ads == null) {
			return 0;
		}
		return this.ads.numAttributes();
	}

	public String getColumnName(int col) {
		if (this.ads == null) {
			return "";
		}
		return this.ads.attribute(col).name();
	}

	public Object getValueAt(int row, int column)
	{
		if (this.ads == null) {
			return null;
		}
		if (this.ads.attribute(column).isNumeric()) {
			return String.format("%.6f%%", this.ads.get(row).value(column)*100);
		} else {
			return this.ads.get(row).stringValue(column);
		}
	}

}