import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import java.util.Enumeration;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.omt.epubreader.domain.Book;
import com.omt.epubreader.domain.Epub;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class ReaderForm{

	private JFrame frame;
	private final JPanel panel = new JPanel();
	private File selectedFile = null;
	private static int speed = 300;
	static Timer timer = null;
	Scanner stream = null;
	String fileData = null;
	boolean copiedText = false;
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
		
		final JTextArea textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				if (copiedText)
				{
					fileData = textArea.getText();
					stream = new Scanner(fileData);
				}
			}

			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				if (copiedText)
				{
					fileData = textArea.getText();
					stream = new Scanner(fileData);
				}
			}

			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				if (copiedText)
				{
					fileData = textArea.getText();
					stream = new Scanner(fileData);
				}
			}
		});
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setBounds(10, 168, 414, 163);
		frame.getContentPane().add(areaScrollPane);
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});
		btnClear.setBounds(10, 342, 89, 23);
		frame.getContentPane().add(btnClear);
		
		final JCheckBox chckbxCopiedText = new JCheckBox("Use Copied Text");
		chckbxCopiedText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxCopiedText.isSelected())
				{
					copiedText = true;
					fileData = textArea.getText();
					stream = new Scanner(fileData);
				}
				else
				{
					copiedText = false;
				}
			}
		});
		chckbxCopiedText.setBounds(301, 342, 123, 23);
		frame.getContentPane().add(chckbxCopiedText);

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
							fis.close();
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
							FileInputStream fis = new FileInputStream(selectedFile.getAbsolutePath());
							HWPFDocument document = new HWPFDocument(fis);
							WordExtractor extractor = new WordExtractor(document);
							fileData = extractor.getText();
							stream = new Scanner(fileData);
							fis.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	
					}
					else if (fileType.equals("pdf"))
					{
						try{
							PDDocument document = PDDocument.load(selectedFile);
							document.getClass();
							if (!document.isEncrypted())
							{
								PDFTextStripperByArea stripper = new PDFTextStripperByArea();
								stripper.setSortByPosition(true);
								PDFTextStripper Tstripper = new PDFTextStripper();
								stream = new Scanner(Tstripper.getText(document));
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					else if (fileType.equals("epub"))
					{
						try {
							Epub epubReader = new Epub();
							Book book = epubReader.getBook(new FileInputStream(selectedFile.getAbsoluteFile()));
							Enumeration e = book.getChaptersData().elements();
							String theHTML = "";
							while (e.hasMoreElements()){
								theHTML += (String) e.nextElement();
							}
							Document doc = Jsoup.parse(theHTML);
							fileData = doc.text();
							stream = new Scanner(fileData);
						}
						catch (FileNotFoundException e)
						{
							e.printStackTrace();
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
				speed = 100;
				timer.setDelay(speed);
			}
		});
		mnSpeed.add(mntmVeryFast);

		JMenuItem mntmFast = new JMenuItem("Fast");
		mntmFast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 200;
				timer.setDelay(speed);
			}
		});
		mnSpeed.add(mntmFast);

		JMenuItem mntmMedium = new JMenuItem("Medium");
		mntmMedium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 300;
				timer.setDelay(speed);
			}
		});
		mnSpeed.add(mntmMedium);

		JMenuItem mntmSlow = new JMenuItem("Slow");
		mntmSlow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 400;
				timer.setDelay(speed);
			}
		});
		mnSpeed.add(mntmSlow);

		JMenuItem mntmVerySlow = new JMenuItem("Very Slow");
		mntmVerySlow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speed = 500;
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
