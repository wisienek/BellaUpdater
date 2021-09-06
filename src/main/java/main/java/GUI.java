package main.java;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class GUI {
	
	public static String versionID = "1dmP4kJpA6Nn5fG_94CxCTtFaG75Zrz7Q";
	public static String modpackFolder;
	
	public static String myVersion;
	public static String latestVersion;
	
	public JLabel label;
	public JLabel versionLabel;
	public MyFrame frame;
	public JPanel mainPanel;
	public JProgressBar jProgressBar;
	public JButton button;
	public JButton chbutton;
	
	private int width = 240;
	private int height = 280;
	
	private int clicks = 0;
	
	private static Utils utils;

	public GUI() throws IOException {
		// mainframe
		frame = new MyFrame();
		
		jProgressBar = new JProgressBar();
		jProgressBar.setBounds( width/2 - 100, height/2 + 20, 200, 10 );
		jProgressBar.setVisible( false );
		jProgressBar.setMaximum( 100000 );
		
		chbutton = new JButton( "..." );
		chbutton.setBounds( width - 50, 5, 40, 20);
		chbutton.addActionListener( e -> {
			String sep = System.getProperty("file.separator");
			String home = System.getProperty("user.home");
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory( new File( (modpackFolder != null) ? modpackFolder : home + sep + "AppData" + sep + "Roaming" ) );
			fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			
			int resp = fileChooser.showOpenDialog( null );
			
			if( resp == JFileChooser.APPROVE_OPTION ) {
				File file = new File( fileChooser.getSelectedFile().getAbsolutePath() );
				
				modpackFolder = file.getAbsolutePath();
				
				try {
					Utils.saveVFile();
				} catch (IOException e1) { e1.printStackTrace(); }
			}
			
		});
		
		Image photo = new ImageIcon(this.getClass().getResource("/button.png")).getImage();
		
		button = new JButton(  );
		button.setBounds( (width/2) - 80, height - 100, 160, 50 );
		button.setFocusable( false );
		button.setFocusPainted(true);
		button.setIcon(new ImageIcon(photo));
		button.setContentAreaFilled(false);
		button.setMargin(new Insets(0,0,0,0));
		button.addActionListener(e -> {
			try {
				// zamienić jakoś fajnie + forsowany
				
				if( this.label.getText().equals("Pobierz update") )
					utils.downloadLatest();
				else if ( this.label.getText().equals("Sprawdź aktualizacje") )
					utils.getVersion();
				else if ( this.label.getText().equals("Forsuj update") )
					utils.downloadLatest();
				else {
					if (clicks == 10) 
						this.label.setText("Forsuj update");
					else 
						clicks++;
				}
				
				
			} catch (IOException | GeneralSecurityException e1) {
				e1.printStackTrace();
			}
			
		});
		
		versionLabel = new JLabel( myVersion + "" );
		versionLabel.setBounds( 10, 10, 120, 30);
		
		label = new JLabel( "Sprawdź aktualizacje" );
		label.setBounds( (width/2) - 80, height - 90, 160, 30);
		label.setForeground( Color.decode("#b4ecb4") );
		label.setVerticalAlignment( JLabel.CENTER );
		label.setVerticalTextPosition( JLabel.CENTER );
		label.setHorizontalAlignment( JLabel.CENTER );
		label.setHorizontalTextPosition( JLabel.CENTER );
		

		mainPanel = new JPanel() {
			private static final long serialVersionUID = -6607531112181264060L;

			@Override
			  protected void paintComponent(Graphics g) {

			    super.paintComponent(g);
			        g.drawImage(new ImageIcon(this.getClass().getResource("/bg.png")).getImage(), 0, 0, null);
			}
		};
		mainPanel.setPreferredSize( new Dimension(width, height) );
		mainPanel.setBorder( BorderFactory.createEmptyBorder( 30, 30, 10, 30) );
		mainPanel.setLayout( null );
		
		mainPanel.add( label );
		mainPanel.add( versionLabel );
		mainPanel.add( button );
		mainPanel.add( chbutton );
		mainPanel.add( jProgressBar );
		
		frame.add(mainPanel, BorderLayout.CENTER);
		frame.pack();
		
	}
	
	
	public static void main(String[] args) throws IOException {
		Utils.init();
		
		GUI gui = new GUI();
		utils = new Utils( gui );
		
		utils.compareFiles();
	}

}
