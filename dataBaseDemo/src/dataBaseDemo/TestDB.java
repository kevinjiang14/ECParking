package dataBaseDemo;

public class TestDB{
	public static void main(String[] ags) {
		
		StreetsSize Size = new StreetsSize();
		Size.openFile();
		Size.setSize();
		Size.closeFile();
		
		
		int numOfStreets = Size.getSize();
		
		Street nextStreetInfo = new Street();
		nextStreetInfo.openFile();
		Street street[] = new Street[numOfStreets];
		
		for(int i = 0; i < numOfStreets; i++) {
			street[i] = new Street();
			street[i].inLat = nextStreetInfo.getElement();
			street[i].inLon = nextStreetInfo.getElement();
			street[i].endLat = nextStreetInfo.getElement();
			street[i].endLon = nextStreetInfo.getElement();
			street[i].ava = nextStreetInfo.getElement();
		}
		
		nextStreetInfo.closeFile();
		
		System.out.println(street[0].ava);
		
	}
}

