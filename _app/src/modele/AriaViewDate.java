package modele;

import java.util.ArrayList;

public class AriaViewDate {

	private double north;
	private double south;
	private double east;
	private double west;
	private ArrayList<String> listPolluant;
	private String legendPath;
	private ArrayList<AriaViewDateTerm> listAriaViewDateTerm;
	private int currentAriaViewDateTerm;
	private int currentPolluant;
	
	public AriaViewDate() {
		this.north = 0;
		this.south = 0;
		this.east = 0;
		this.west = 0;
		this.listPolluant = new ArrayList<String>();
		this.legendPath = "";
		this.listAriaViewDateTerm = new ArrayList<AriaViewDateTerm>();
		this.currentAriaViewDateTerm = 0;
		this.currentPolluant = 0;
	}
	
	public AriaViewDate(double north, double south, double east, double west, String legendPath) {
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.listPolluant = new ArrayList<String>();
		this.legendPath = legendPath;
		this.listAriaViewDateTerm = new ArrayList<AriaViewDateTerm>();
		this.currentAriaViewDateTerm = 0;
		this.currentPolluant = 0;
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
	
	@Override
	public String toString() {
		return "AriaViewDate [north=" + north + ", south=" + south + ", east="
				+ east + ", west=" + west + ", polluant=" + listPolluant
				+ ", legendPath=" + legendPath + ", ariaViewDateTerm="
				+ listAriaViewDateTerm + "]";
	}
}
