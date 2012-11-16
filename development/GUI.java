import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import net.miginfocom.layout.CC;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.Toolkit;
import javax.swing.filechooser.FileNameExtensionFilter;
import weka.core.*;
import java.util.*;
import weka.core.converters.DatabaseLoader;

public class GUI extends JFrame
{
	private AdsTableModel tableModel;
	private DataManager dataManager;
	private AdFactory adFactory;

	// GUI components
	private JTable table;
	private JComboBox cmbGender;
	private JComboBox cmbAge;

	public GUI()
	{
		super("Ad Estimator");
		if (System.getProperty("os.name").contains("Mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		this.tableModel = new AdsTableModel(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new MigLayout("fill", "", "[]10:10:10[grow]"));
		panel.setOpaque(true);
		this.setContentPane(panel);

		this.dataManager = new DatabaseManager("instances");

		// Menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenuItem menuItmReport = new JMenuItem("Add report");
		menuItmReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
				int returnVal = chooser.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					dataManager.add(new Ads(DuegoDataParser.parse(chooser.getSelectedFile())));
				}
			}
		});
		menuItmReport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuFile.add(menuItmReport);

		JMenuItem menuItmExport = new JMenuItem("Export selection");
		menuItmExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Get selected rows
				int[] selection = table.getSelectedRows();
				for (int i = 0; i < selection.length; i++) {
					selection[i] = table.convertRowIndexToModel(selection[i]);
				}
				if (selection.length == 0) {
					JOptionPane.showMessageDialog(GUI.this, "No rows selected.");
					return;
				}
				// Ask for campaign name
				String campaignName = JOptionPane.showInputDialog(GUI.this, "What is the name of the campaign?");
				// Create list with a map for each ad
				List<Map<String,String>> adList = new LinkedList<Map<String,String>>();
				for (int row : selection) {
					Map<String,String> adMap = new HashMap<String,String>();
					if (campaignName != null) {
						adMap.put("Campaign Name", campaignName);
					}
					adMap.put("Body", tableModel.getValueAt(row, tableModel.findColumn("Body")).toString());
					adMap.put("Image Hash", tableModel.getValueAt(row, tableModel.findColumn("Image_Hash")).toString());
					String val = (String)cmbGender.getSelectedItem();
					if (val.equalsIgnoreCase("All")) {
						val = "";
					}
					adMap.put("Gender", val);
					val = (String)cmbAge.getSelectedItem();
					if (!val.equalsIgnoreCase("All")) {
						adMap.put("Age Min", val.substring(0, val.indexOf("-")));
						adMap.put("Age Max", val.substring(val.indexOf("-")+1));
					}
					adList.add(adMap);
				}
				// Perform export
				Exporter exp = new Exporter("data/export-template.csv");
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
				int returnVal = chooser.showSaveDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					exp.export(chooser.getSelectedFile(), adList);
					JOptionPane.showMessageDialog(GUI.this, "All lines were exported successfully.");
				}
			}
		});
		menuItmExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
			Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuFile.add(menuItmExport);

		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menuFile.add(quit);

		menuBar.add(menuFile);
		this.setJMenuBar(menuBar);

		// Target
		DatabaseLoader loader = null;
		Instances inst = null;
		try {
			loader = new DatabaseLoader();
			loader.connectToDatabase();
			loader.setQuery("SELECT Gender, Age_Min, Age_Max FROM instances");
			inst = loader.getDataSet();
			loader.reset();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		JPanel pnlTarget = new JPanel(new MigLayout("ins 5"));
		pnlTarget.add(new JLabel("Target:"));
		List<String> genders = new LinkedList<String>();
		for (int i = 0; i < inst.attribute("Gender").numValues(); i++) {
			genders.add(inst.attribute("Gender").value(i));
		}
		genders.add("All");
		cmbGender = new JComboBox(genders.toArray(new String[0]));
		pnlTarget.add(cmbGender);

		String[] targetsAge = {"18-23","24-30","31-35", "All"};
		Set<String> ages = new TreeSet<String>();
		Attribute attMinAge = inst.attribute("Age_Min");
		Attribute attMaxAge = inst.attribute("Age_Max");
		for (Instance instance : inst) {
			ages.add(instance.value(attMinAge) + "-" + instance.value(attMaxAge));
		}
		ages.add("All");
		cmbAge = new JComboBox(ages.toArray(new String[0]));
		pnlTarget.add(cmbAge);
		JButton btnSubmit = new JButton("Show suggestions");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dataManager.resetWhere();

					String val = (String)cmbGender.getSelectedItem();
					if (!val.equalsIgnoreCase("All")) {
						dataManager.where("Gender", val);
					}
					val = (String)cmbAge.getSelectedItem();
					if (!val.equalsIgnoreCase("All")) {
						dataManager.where("Age_Min", val.substring(0, val.indexOf("-")));
					}

					Estimator est = Estimator.factory(dataManager.get(), "weka.classifiers.functions.Logistic", Arrays.asList("-R", "1000").toArray(new String[0]));
					adFactory = new CombinationAdFactory(dataManager);
					Instances ads = adFactory.all();
					ads.setClassIndex(ads.numAttributes()-1);
					for (Instance ad : ads) {
						ad.setClassValue(est.estimate(ad));
					}
					tableModel.setData(new Ads(ads));
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		pnlTarget.add(btnSubmit);
		this.add(pnlTarget, "wrap");

		// Table
		table = new JTable(this.tableModel);
		table.setAutoCreateRowSorter(true);
		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, "grow 100 100");

		// Show frame
		this.pack();
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new GUI();
	}

}