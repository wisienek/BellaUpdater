package main.java;

import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class MyFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public MyFrame() throws IOException {
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setTitle( "Updater" );

		// this.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("src/main/resources/bg.png")))));
		
		this.setResizable( false );
		this.setVisible( true );
		
		ImageIcon image = new ImageIcon(this.getClass().getResource("/logo.png"));
		this.setIconImage( image.getImage() );
		
	}
	
	
}
