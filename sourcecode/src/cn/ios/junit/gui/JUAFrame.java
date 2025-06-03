package cn.ios.junit.gui;

import cn.ios.junit.API;
import cn.ios.junit.config.Config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.Font;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class JUAFrame extends JFrame {

	public static void main(String[] args) {
		JUAFrame frame = new JUAFrame();
		frame.setVisible(true);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField classUnderTestPathField = null;
	private JTextField dependencyPathField = null;
	private JTextField outputField = null;
	private JTextField minSizeOfSetText = null;
	private JTextField maxSizeOfSetText = null;
	private JTextField maxLengthOfString = null;
	private JTextField maxSizeOfCaseText = null;
	private JTextField kObjectSensitiveAssertionText = null;
	
	public static JTextArea textArea = null;

	private JFileChooser fileChooser = null;

	private boolean Flag = false;
	private boolean isOK = false;

	private String classUnderTestPath = "";
	private String outputPath = "";
	private List<String> dependencies = null;

	/**
	 * Create the frame.
	 */
	public JUAFrame() {
		setTitle("JUA: An Extensible Unit Test Generation Tool for Java Based on Annotation");
		setIconImage(Toolkit.getDefaultToolkit().getImage("F:\\Junit Generation\\plugin-project\\icons\\icon.png"));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 640);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		fileChooser = newJFileChooser();

		Font font = new Font("Times New Roman", Font.BOLD, 24);

		int x = 60, y = 30, width = 200, width_s = 40, height = 30, dx = 200, dy = 50;
		// contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		addLable(x, y, width + 300, height, "JUA Options", font);

		Font fontSmall = new Font("Times New Roman", Font.PLAIN, 16);

		// Class Under Test
		y = y + dy;
		addLable(x, y, width, height, "Class Under Test", fontSmall);
		classUnderTestPathField = addTextField(x + dx, y, width + 200, height, "/Users/cbq/Downloads/ChromeDownloads/AuctionApp/target/classes", fontSmall);
		addButton(x + dx + width + 5 + 200, y, height, height, "...", fontSmall, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> list = openFiles("Class Under Test Path(s): Byte code dictionaries or jar files", false,
							JFileChooser.FILES_AND_DIRECTORIES);
				if(!list.isEmpty()) {
					classUnderTestPath = list.get(0);
					classUnderTestPathField.setText(classUnderTestPath);
				}
			}
		});

		// dependencyPath
		y = y + dy;
		addLable(x, y, width, height, "Dependencies", fontSmall);
		dependencyPathField = addTextField(x + dx, y, width + 200, height, "", fontSmall);
		addButton(x + dx + width + 5 + 200, y, height, height, "...", fontSmall, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dependencies = openFiles("Dependency Path(s): Byte code dictionaries or jar files", true,
						JFileChooser.FILES_AND_DIRECTORIES);
				dependencyPathField.setText(dependencies.toString());
			}
		});

		// output
		y = y + dy;
		addLable(x, y, width, height, "Output", fontSmall);
		outputField = addTextField(x + dx, y, width + 200, height, "", fontSmall);
		addButton(x + dx + width + 5 + 200, y, height, height, "...", fontSmall, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> list = openFiles("Output dictionary", false, JFileChooser.DIRECTORIES_ONLY);
				if(!list.isEmpty()) {
					outputPath = list.get(0);
					outputField.setText(outputPath);
				}
			}
		});

		// MinSizeOfSet
		y = y + dy/2;

		addLable(x + dx + width_s + 20, y, 60, height, "Logs", fontSmall);
		textArea = addTextArea(x + dx + width_s + 20, y + 30, 400, 280, "", fontSmall);

		y = y + dy;
		addLable(x, y, width, height, "Min Size of Set(Array):", fontSmall);
		minSizeOfSetText = addTextField(x + dx, y, width_s, height, "0", fontSmall);

		// MaxSizeOfSet
		y = y + dy;
		addLable(x, y, width, height, "Max Size of Set(Array):", fontSmall);
		maxSizeOfSetText = addTextField(x + dx, y, width_s, height, "5", fontSmall);

		// MaxSizeOfSet
		y = y + dy;
		addLable(x, y, width, height, "Max Length of String:", fontSmall);
		maxLengthOfString = addTextField(x + dx, y, width_s, height, "20", fontSmall);

		// MaxSizeOfCase
		y = y + dy;
		addLable(x, y, width, height, "Max Size of Test Case:", fontSmall);
		maxSizeOfCaseText = addTextField(x + dx, y, width_s, height, "10", fontSmall);

		// K-Object-Sensitive-Assertion
		y = y + dy;
		addLable(x, y, width, height, "K-Object-Sensitive-Assertion:", fontSmall);
		kObjectSensitiveAssertionText = addTextField(x + dx, y, width_s, height, "1", fontSmall);

		// OK
		addButton(500, 550, 80, 30, "Run", fontSmall, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setOK(true);
				// JUA Invoke
				// Config
				int minSizeOfSet = Integer.parseInt(minSizeOfSetText.getText());
				int maxSizeOfSet = Integer.parseInt(maxSizeOfSetText.getText());
				int maxSizeOfCase = Integer.parseInt(maxSizeOfCaseText.getText());
				int maxLengthOfStr = Integer.parseInt(maxLengthOfString.getText());
				int kObjectSensitiveAssertion = Integer.parseInt(kObjectSensitiveAssertionText.getText());

				Config.REF_ARRAY_MIN_SIZE = minSizeOfSet;
				Config.REF_ARRAY_MAX_SIZE = maxSizeOfSet;
				Config.MAX_UNIT_METHOD = maxSizeOfCase;
				Config.STRING_MAX_LENGTH = maxLengthOfStr;
				Config.MAX_NESTED_OBJECT = kObjectSensitiveAssertion;

				String target = classUnderTestPathField.getText();
				String dependency = dependencyPathField.getText();
				String output = outputField.getText() + "/";


				API.generateClassList(target,dependency,output,null,maxSizeOfCase);
			}
		});

		// Exit
		addButton(600, 550, 80, 30, "Exit", fontSmall, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// Default Setting
		addButton(360, 550, 120, 30, "Default Setting", fontSmall, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				minSizeOfSetText.setText("0");
				maxSizeOfSetText.setText("5");
				maxSizeOfCaseText.setText("10");
				kObjectSensitiveAssertionText.setText("1");
			}
		});

	}
	private List<String> openFiles(String text, boolean multi, int selectionMode) {
		fileChooser.setDialogTitle(text);
		fileChooser.setMultiSelectionEnabled(multi);
		fileChooser.setFileSelectionMode(selectionMode);
		List<String> files = new ArrayList<>();
		int result = fileChooser.showDialog(getParent(), "OK");
		if (result == JFileChooser.APPROVE_OPTION) {
			if (multi) {
				File[] selectedFiles = fileChooser.getSelectedFiles();
				if (selectedFiles != null && selectedFiles.length > 0) {
					for (File file : selectedFiles) {
						files.add(file.getAbsolutePath());
					}
				}
			} else {
				files.add(fileChooser.getSelectedFile().getAbsolutePath());
			}
		} else if (result == JFileChooser.ERROR) {
//			System.out.println("errrrrrrrrrr");
		}
		return files;
	}

	private JFileChooser newJFileChooser() {
		JFileChooser fileChooser = new JFileChooser("/Users/cbq/Downloads/ChromeDownloads/AuctionApp");
		return fileChooser;
	}

	private JLabel addLable(int x, int y, int width, int height, String text, Font font) {
		JLabel jLabel = new JLabel(text);
		jLabel.setFont(font);
		jLabel.setBounds(x, y, width, height);
		contentPane.add(jLabel);
		return jLabel;
	}

	private JTextField addTextField(int x, int y, int width, int height, String text, Font font) {
		JTextField textField = new JTextField(text);
		textField.setBounds(x, y, width, height);
		textField.setFont(font);
		contentPane.add(textField);
//		contentPane.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		return textField;
	}

	private JTextArea addTextArea(int x, int y, int width, int height, String text, Font font) {
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(x, y, width, height);
		contentPane.add(scrollPane_1);


		JTextArea textArea = new JTextArea(text);
		textArea.setBounds(x, y, width, height);
		textArea.setFont(font);
//		contentPane.add(textArea);
		textArea.setEditable(false);
		textArea.setAutoscrolls(true);
		scrollPane_1.setViewportView(textArea);
//		contentPane.add(textField, BorderLayout.CENTER);
//		textArea.setRows(rows);
		return textArea;
	}

	private JButton addButton(int x, int y, int width, int height, String text, Font font, ActionListener listener) {
		JButton btn = new JButton(text);
		btn.addActionListener(listener);
//		btn.addActionListener(listener);
		btn.setBounds(x, y, width, height);
		contentPane.add(btn);
		return btn;
	}

	public boolean isFlag() {
		return Flag;
	}

	public void setFlag(boolean flag) {
		Flag = flag;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}

}
