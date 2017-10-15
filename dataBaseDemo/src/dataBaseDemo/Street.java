package dataBaseDemo;
import java.io.*;
import java.util.*;

public class Street {
	public Scanner file;
	public String inLat;
	public String endLat;
	public String inLon;
	public String endLon;
	public String ava;
	public String element;
	
	public void openFile() {
		try {
			file = new Scanner(new File("C:\\Users\\HgMeza\\Documents\\streetDB.txt"));
		}
		catch(Exception e) {
			System.out.println("Unable to open file");
		}
		
	}
	private void setElement() {
		element = file.next();
	}
	public String getElement() {
		setElement();
		return element;
	}
	
	public void closeFile() {
		file.close();
	}
	
}