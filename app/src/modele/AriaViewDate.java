package modele;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//Main Model 
public class AriaViewDate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double north;
	private double south;
	private double east;
	private double west;
	private ArrayList<String> listPolluant;
	private ArrayList<String> listDate;
	private String[] sitesTabString;
	private String legendPath;
	private String hostPath;
	private String middlePath;
	private String login;
	private String password;
	private ArrayList<AriaViewDateTerm> listAriaViewDateTerm;
	private int currentAriaViewDateTerm;
	private int currentPolluant;
	private int currentDate;
	private int currentSite;
	

	private Document document;
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	
	public AriaViewDate(){
		this.north = 0;
		this.south = 0;
		this.east = 0;
		this.west = 0;
		this.listPolluant = new ArrayList<String>();
		this.legendPath = "";
		this.hostPath = "";
		this.listAriaViewDateTerm = new ArrayList<AriaViewDateTerm>();
		this.currentAriaViewDateTerm = 0;
		this.currentPolluant = 0;
	}
	
	public AriaViewDate(String hostPath, String middlePath, int currentDate,int currentSite, ArrayList<String> listDate, String[] sitesTabString, String login, String password){
		this.hostPath = hostPath;
		this.middlePath = middlePath;
		this.currentDate = currentDate;
		this.currentSite = currentSite;
		this.listDate = listDate;
		this.sitesTabString = sitesTabString;
		this.login = login;
		this.password = password;
	}
	
	public AriaViewDate(double north, double south, double east, double west, String hostPath, String legendPath) {
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.listPolluant = new ArrayList<String>();
		this.legendPath = legendPath;
		this.hostPath = hostPath;
		this.listAriaViewDateTerm = new ArrayList<AriaViewDateTerm>();
		this.listDate = new ArrayList<String>();
		this.currentAriaViewDateTerm = 0;
		this.currentPolluant = 0;
	}
	
	public List<String> getBeginTimeSpanList(){
		List<String> beginTimeSpanList = new ArrayList<String>();
		
		for(AriaViewDateTerm ariaViewDateTerm: listAriaViewDateTerm){
			 beginTimeSpanList.add(ariaViewDateTerm.getBeginTimeSpan());
		}
		
		return beginTimeSpanList;
	}
	
	public String getAllPath(){
		return hostPath+
				sitesTabString[currentSite]+
				middlePath+
				listDate.get(currentDate)+"/";
	}
	
	public String getHostPath() {
		return hostPath;
	}

	public void setHostPath(String hostPath) {
		this.hostPath = hostPath;
	}

	public int getCurrentAriaViewDateTerm() {
		return currentAriaViewDateTerm;
	}

	public void setCurrentAriaViewDateTerm(int currentAriaViewDateTerm) {
		this.currentAriaViewDateTerm = currentAriaViewDateTerm;
	}

	public int getCurrentPolluant() {
		return currentPolluant;
	}

	public void setCurrentPolluant(int currentPolluant) {
		this.currentPolluant = currentPolluant;
	}

	public void addPolluant(String polluant){
		listPolluant.add(polluant);
	}
	
	public void addAriaViewDateTerm(AriaViewDateTerm ariaViewDateTerm){
		listAriaViewDateTerm.add(ariaViewDateTerm);
	}
	
	public double getNorth() {
		return north;
	}
	public void setNorth(double north) {
		this.north = north;
	}
	public double getSouth() {
		return south;
	}
	public void setSouth(double south) {
		this.south = south;
	}
	public double getEast() {
		return east;
	}
	public void setEast(double east) {
		this.east = east;
	}
	public double getWest() {
		return west;
	}
	public void setWest(double west) {
		this.west = west;
	}
	public ArrayList<String> getListPolluant() {
		return listPolluant;
	}
	public void setListPolluant(ArrayList<String> polluant) {
		this.listPolluant = polluant;
	}
	public String getLegendPath() {
		return legendPath;
	}
	public void setLegendPath(String legendPath) {
		this.legendPath = legendPath;
	}
	public ArrayList<AriaViewDateTerm> getListAriaViewDateTerm() {
		return listAriaViewDateTerm;
	}
	public void setListAriaViewDateTerm(ArrayList<AriaViewDateTerm> ariaViewDateTerm) {
		this.listAriaViewDateTerm = ariaViewDateTerm;
	}
	
	public ArrayList<String> getListDate() {
		return listDate;
	}

	public void setListDate(ArrayList<String> listDate) {
		this.listDate = listDate;
	}

	public String getMiddlePath() {
		return middlePath;
	}

	public void setMiddlePath(String middlePath) {
		this.middlePath = middlePath;
	}

	public int getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(int currentDate) {
		this.currentDate = currentDate;
	}

	public String[] getSitesTabString() {
		return sitesTabString;
	}

	public void setSitesTabString(String[] sitesTabString) {
		this.sitesTabString = sitesTabString;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getCurrentSite() {
		return currentSite;
	}

	public void setCurrentSite(int currentSite) {
		this.currentSite = currentSite;
	}

	@Override
	public String toString() {
		return "AriaViewDate [north=" + north + ", south=" + south + ", east="
				+ east + ", west=" + west + ", polluant=" + listPolluant
				+ ", legendPath=" + legendPath + ", ariaViewDateTerm="
				+ listAriaViewDateTerm + "]";
	}
	
	
	public void fillAriaViewDate(File fileKML){

		try {
			
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(fileKML);

			Double north  = Double.parseDouble(document.getElementsByTagName("north").item(0)
					.getTextContent());
			Double south = Double.parseDouble(document.getElementsByTagName("south").item(0)
					.getTextContent());
			Double east = Double.parseDouble(document.getElementsByTagName("east").item(0)
					.getTextContent());
			Double west = Double.parseDouble(document.getElementsByTagName("west").item(0)
					.getTextContent());
			String legendPath = URLEncoder.encode(document.getElementsByTagName("href").item(0)
					.getTextContent(), "UTF-8")
					.replaceAll("\\+", "%20");
			
			NodeList beginTimeNodeList = document.getElementsByTagName("begin");
			NodeList endTimeNodeList = document.getElementsByTagName("end");
			NodeList iconPathNodeList = document.getElementsByTagName("href");
			
			ArrayList<AriaViewDateTerm> listAriaViewDateTerm = new ArrayList<AriaViewDateTerm>();
			
			for (int i = 0; i < beginTimeNodeList.getLength(); i++) {
	            String beginTimeSpan = ((Element) beginTimeNodeList.item(i)).getTextContent();
	            String endTimeSpan = ((Element) endTimeNodeList.item(i)).getTextContent();
	            String iconPath = URLEncoder.encode(((Element) iconPathNodeList.item(i + 1))
						.getTextContent(), "UTF-8")
						.replaceAll("\\+", "%20");
	            listAriaViewDateTerm.add(new AriaViewDateTerm(beginTimeSpan, endTimeSpan, iconPath, ""));   
	        }
			
			setNorth(north);
			setSouth(south);
			setEast(east);
			setWest(west);
			setLegendPath(legendPath);
			setListAriaViewDateTerm(listAriaViewDateTerm);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
}
