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

public class GUI extends JFrame
{
	private AdsTableModel tableModel;
	private DataManager dataManager;
	private AdFactory adFactory;

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
		JPanel pnlTarget = new JPanel(new MigLayout("ins 5"));
		pnlTarget.add(new JLabel("Target:"));
		String[] targetsGender = {"Men", "Women"};
		JComboBox cmbGender = new JComboBox(targetsGender);
		pnlTarget.add(cmbGender);
		String[] targetsAge = {"19-23","24-30","31-35"};
		JComboBox cmbAge = new JComboBox(targetsAge);
		pnlTarget.add(cmbAge);
		JButton btnSubmit = new JButton("Show suggestions");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
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
		JTable table = new JTable(this.tableModel);
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