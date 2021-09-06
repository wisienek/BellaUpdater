package main.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Utils {
	public GUI gui;
	
	private static String user = System.getProperty("user.home");
	private static String sep = System.getProperty("file.separator");
	
	private List<Mod> lista = new ArrayList<Mod>();
	
	public Utils(GUI _gui) {
		this.gui = _gui;
	}
	
	@SuppressWarnings("resource")
	public static void init() throws IOException {
		
		File versionFile = new File( user + sep + "belaVersion.txt" );
		
		if( versionFile.exists() == true ) {
			BufferedReader br = new BufferedReader( new FileReader(versionFile) );
		    String line;
		    while( (line = br.readLine()) != null ) {
		       if( line.contains("version=") )
		    	   GUI.myVersion = line.replace("version=", "");
		       if( line.contains("path=") )
		    	   GUI.modpackFolder = line.replace("path=", "");
		    }
		} else {
			GUI.myVersion = "0.0.0";
			GUI.modpackFolder = "";
			
			saveVFile();
		}
	}
	public static void saveVFile() throws IOException {
		File versionFile = new File( user + sep + "belaVersion.txt" );
		versionFile.createNewFile();
		
	    BufferedWriter writer = new BufferedWriter(new FileWriter(versionFile));
	    writer.write( "version=" + GUI.myVersion + "\n" );
	    writer.write( "path=" + GUI.modpackFolder + "\n" );
	    writer.close();
	}

	public void getVersion() throws IOException {
		gui.label.setText("Szukam updatów...");
		
        URL versionTXT = new URL( "https://drive.google.com/uc?export=download&id=" + GUI.versionID );
        URLConnection yc = versionTXT.openConnection();
        BufferedReader in = new BufferedReader( new InputStreamReader( yc.getInputStream() ) );
        
        String tIn;
        while ( (tIn = in.readLine()) != null ) {
            if( tIn.contains("version=") ) {
            	GUI.latestVersion = tIn.replace("version=", "");
            } else {
            	if( tIn.contains(", ") == false || tIn.contains(".jar") == false ) continue;
            	String[] args = tIn.split(", ");
            	System.out.println(tIn);
            	
            	if( args == null || args.length < 3 ) {
            	    JOptionPane.showMessageDialog( new JFrame(), "Złe formatowanie wersji moda!", "Dialog", JOptionPane.ERROR_MESSAGE );
            	    return;
            	}
            	
            	String name = args[0];
            	String id = args[1];
            	Boolean install = Boolean.valueOf(args[2]);
            	String version = args[3];
            	
            	if( version.equals(GUI.latestVersion) || GUI.latestVersion == null ) {
                	Mod mod = new Mod(name, id, install, version);
                	
                	lista.add(mod);
            	}
            }
        }

        
        if( GUI.latestVersion == null || GUI.latestVersion.equals("0.0.0") == true || GUI.latestVersion.equals(GUI.myVersion) == true ) {
        	gui.label.setText( "Brak updatów!" );
        } else {
        	gui.label.setText( "Pobierz update" );
        	
        	gui.versionLabel.setText( GUI.myVersion + " -> " + GUI.latestVersion );
        }
        System.out.println( GUI.latestVersion );
       
        in.close();
	}
	
	public void downloadLatest() throws IOException, GeneralSecurityException {
		if( GUI.modpackFolder == null || GUI.modpackFolder.length() == 0 ) {
			gui.label.setText("Brak modpack folderu!");
			return;
		}
		if( new File( GUI.modpackFolder ).exists() == false ) {
			gui.label.setText("Folder modpacku nie istnieje!");
			return;
		}
		if( lista.size() == 0 ) {
			gui.label.setText( "Brak modów..." );
			return;
		}
			
		gui.label.setText( "Aktualizuję mody" );
		
		File file = new File( GUI.modpackFolder + sep + "mods" );
		if( file.exists() == false ) file.createNewFile();
		
		gui.jProgressBar.setVisible(true);
		
		int step = 100000 / lista.size();
		
		// installuj
    	for( Mod mod : lista) {
    		if( mod.version.equals(GUI.latestVersion) == false ) continue;
    		
    		File modFile = new File( file.getAbsolutePath() + sep + mod.name );
    		
        	if ( mod.install == false ) {
        		if( modFile.exists() == true ) {
        			Boolean del = modFile.delete();
        			if( del == false ) {
        				JOptionPane.showMessageDialog( new JFrame(), "Nie udało się usunąć moda: " + mod.name, "Dialog", JOptionPane.ERROR_MESSAGE );
        				continue;
        			}
        		}
        			
        		int val = gui.jProgressBar.getValue();
        		gui.jProgressBar.setValue( val + step );
        			
        		if( val + step >= 99990 ) gui.label.setText( "Pobrano pliki!" );
        	} else {
        		if( modFile.exists() == false ) {
        			Thread t = new Thread( new DownloadMod( mod, step ) );
        			t.start();
        		} else {
        			gui.jProgressBar.setValue( gui.jProgressBar.getValue() + step );
        		}
        	}
    	}
    	
    	
    	gui.button.setEnabled( false );
    	
    	
    	GUI.myVersion = GUI.latestVersion;
    	gui.versionLabel.setText( GUI.myVersion );
    	saveVFile();
	}
	
    class DownloadMod implements Runnable {
    	Mod mod;
        int step;
        
        DownloadMod( Mod _mod, int _step ) { 
        	mod = _mod;
        	step = _step;
        }
        
		public void run() {
            try {
                URL url = new URL( "https://drive.google.com/uc?export=download&id=" + mod.id );
                
                InputStream in = url.openStream();
                Files.copy(in, Paths.get( GUI.modpackFolder + sep + "mods" + sep + mod.name ), StandardCopyOption.REPLACE_EXISTING);

                int val = gui.jProgressBar.getValue();
                gui.jProgressBar.setValue( val + step );
                System.out.println(val+step);
                if( val + step >= 99990 ) gui.label.setText( "Pobrano pliki!" );
                
            } catch (IOException e) {}
        }
    }

	public void compareFiles() {
		try {
			getVersion();
			
			File mods = new File( GUI.modpackFolder + sep + "mods" );
			if( mods.exists() == false || mods.isDirectory() == false ) {
				JOptionPane.showMessageDialog( new JFrame(), "Folder z modami nie istnieje.", "Dialog", JOptionPane.ERROR_MESSAGE );
				return;
			}
			
			
			for( Mod mod : lista ) {
				if( new File( mods.getAbsolutePath() + sep + mod.name ).exists() == false ) {
					GUI.myVersion = "0.0.0";
					gui.versionLabel.setText( GUI.myVersion + " -> " + GUI.latestVersion );
					gui.label.setText("Pobierz update");
					return;
				}
			}
		} catch (IOException e) {}
		
	}
	
}
