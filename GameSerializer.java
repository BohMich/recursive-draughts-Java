package Draughts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.xml.transform.Templates;

//class deals with saving and loading games from .ser files
//
public class GameSerializer {
	private String directory = null;
	private File currentDirFile;
	public GameSerializer() {
		//get the program directory
		currentDirFile = new File(".");
		String helper = currentDirFile.getAbsolutePath();
		String currentDir = helper.substring(0, helper.length() - 1);
		directory = currentDir;
	}
	
	public boolean saveGame(MoveHistory game, String nameOfGame) {
		try {
			
	         FileOutputStream fileOut = new FileOutputStream(directory + nameOfGame + ".ser");         
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(game);
	         out.close();
	         fileOut.close();
	         return true;
	      } catch (IOException i) {
	         return false;
	      }
	}
	
	public Game loadGame() {
		String control = null;
		File[] files = currentDirFile.listFiles();
		ArrayList<String> serializableFiles = new ArrayList<>();
		int choice;
		int count = 0;
		
		for (File file : files) {
			if (file.isDirectory()) {
				continue;
			} else {
				if(file.getName().endsWith(".ser")) {
					System.out.print(count + "| ");
					System.out.println(file.getName());
					serializableFiles.add(file.getName());
					count++;
				}
				
			}
			
		}
		while(!serializableFiles.isEmpty()) {
			System.out.println("Choose file to serialize");
			try {
				
				int file = Draughts.scanner.nextInt();					
				
				if(file>serializableFiles.size()-1)continue;
				
				FileInputStream fileIn = new FileInputStream(serializableFiles.get(file));
		         ObjectInputStream in = new ObjectInputStream(fileIn);
		         MoveHistory temp = (MoveHistory) in.readObject();
		         in.close();
		         fileIn.close();
		         
		         Game g = new Game();
		 			g._MOVEHISTORY = temp;
		 			g._MOVEHISTORY.loadMoves(g);
		 		
		         return g;
			}
			catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		
		return null;
	}
	
	
}
