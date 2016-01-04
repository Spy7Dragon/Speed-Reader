import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.JCheckBox;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class ReaderForm{

	private JFrame frame;
	private final JPanel panel = new JPanel();
	private File selectedFile = null;
	private static int speed = 600;
	static Timer timer = null;
	Scanner stream = null;
	String fileData = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReaderForm window = new ReaderForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ReaderForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));

		frame = new JFrame();
		frame.setBounds(100, 100, 450, 436);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		final JLabel lblFileName = new JLabel("File Name");
		lblFileName.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		lblFileName.setBounds(10, 11, 414, 14);
		frame.getContentPane().add(lblFileName);

		final JLabel lblText = new JLabel("Text");
		lblText.setHorizontalAlignment(SwingConstants.CENTER);
		lblText.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblText.setBounds(10, 56, 414, 29);
		frame.getContentPane().add(lblText);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.start();
			}
		});
		btnStart.setFont(new Font("Times New Roman", Font.BOLD, 12));
		btnStart.setBounds(10, 116, 89, 23);
		frame.getContentPane().add(btnStart);

		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				timer.stop();
			}
		});
		btnStop.setFont(new Font("Times New Roman", Font.BOLD, 12));
		btnStop.setBounds(335, 116, 89, 23);
		frame.getContentPane().add(btnStop);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(10, 168, 414, 163);
		frame.getContentPane().add(textArea);

		
		JButton btnNewButton = new JButton("Clear");
		btnNewButton.setBounds(10, 342, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JCheckBox chckbxClear = new JCheckBox("Use Copied Text");
		chckbxClear.setBounds(301, 342, 123, 23);
		frame.getContentPane().add(chckbxClear);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpenFile = new JMenuItem("Open File");
		mntmOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Create a file chooser
				int result = fc.showOpenDialog(panel);

				if (result == JFileChooser.APPROVE_OPTION)
				{
					if (stream != null){
						stream.close(); //close the current file
					}
					selectedFile = fc.getSelectedFile();
					lblFileName.setText(selectedFile.toString());
					
					String fileType = FilenameUtils.getExtension(selectedFile.getPath());
					if (fileType.equals("docx")){
						
						try {
							FileInputStream fis = new FileInputStream(selectedFile.getAbsolutePath());
							XWPFDocument document = new XWPFDocument(fis);
							XWPFWordExtractor extractor = new XWPFWordExtractor(document);
							fileData = extractor.getText();
							stream = new Scanner(fileData);
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(fileType.equals("doc"))
					{
						try {
							InputStream fis = new FileInputStream(selectedFile.getAbsoluteFile());
							HWPFDocument document = new HWPFDocument(fis);
							WordExtractor extractor = new WordExtractor(document);
							fileData = extractor.getText();
							stream = new Scanner(fileData);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	
					}
					else{
						try {
							stream = new Scanner(selectedFile);
						} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
					}
					}
				}
			}
		});
		mnFile.add(mntmOpenFile);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);

		JMenu mnSpeed = new JMenu("Speed");
		menuBar.add(mnSpeed);

		JMenuItem mntmVeryFast = new JMenuItem("Very Fast");
		mntmVeryFast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 200;
				timer.setDelay(speed);
			}
		});
		mnSpeed.add(mntmVeryFast);

		JMenuItem mntmFast = new JMenuItem("Fast");
		mntmFast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 400;
				timer.setDelay(speed);
			}
		});
		mnSpeed.add(mntmFast);

		JMenuItem mntmMedium = new JMenuItem("Medium");
		mntmMedium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 600;
				timer.setDelay(speed);
			}
		});
		mnSpeed.add(mntmMedium);

		JMenuItem mntmSlow = new JMenuItem("Slow");
		mntmSlow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 800;
				timer.setDelay(speed);
			}
		});
		mnSpeed.add(mntmSlow);

		JMenuItem mntmVerySlow = new JMenuItem("Very Slow");
		mntmVerySlow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 1000;
				timer.setDelay(speed);
			}
		});
		mnSpeed.add(mntmVerySlow);
		
		ActionListener timerListener = new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{	
				if (stream != null)
				{
					if (stream.hasNext()){
						lblText.setText(stream.next());
					}
				}
			}
		};
		timer = new Timer(speed, timerListener);
	}
}
