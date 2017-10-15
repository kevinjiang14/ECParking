package dataBaseDemo;

import java.io.File;
import java.util.Scanner;

public class StreetsSize {
	private Scanner file;
	private int size;
	
	public void openFile() {
		try {
			file = new Scanner(new File("C:\\Users\\HgMeza\\Documents\\streetDB.txt"));
		}
		catch(Exception e) {
			System.out.println("Unable to open file");
		}
		
	}
	
	public void setSize() {
		int count = 0;
		
		while(file.hasNext()) {
			count++;
			file.next();
		}
		size = count / 5;
	}
	
	public void closeFile() {
		file.close();
	}
	
	public int getSize() { return size; }
}
