package modele;

import java.io.Serializable;

//Model Term clouds releases
public class AriaViewDateTerm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String beginTimeSpan;
	private String endTimeSpan;
	private String iconPath;
	private String polluant;
	private String legendPath;

	public AriaViewDateTerm() {

	}

	public AriaViewDateTerm(String beginTimeSpan, String endTimeSpan,
			String iconPath, String polluant, String legendPath) {
		this.beginTimeSpan = beginTimeSpan;
		this.endTimeSpan = endTimeSpan;
		this.iconPath = iconPath;
		this.polluant = polluant;
		this.legendPath = legendPath;
	}

	public String getPolluant() {
		return polluant;
	}

	public void setPolluant(String polluant) {
		this.polluant = polluant;
	}

	public String getBeginTimeSpan() {
		return beginTimeSpan;
	}

	public void setBeginTimeSpan(String beginTimeSpan) {
		this.beginTimeSpan = beginTimeSpan;
	}

	public String getEndTimeSpan() {
		return endTimeSpan;
	}

	public void setEndTimeSpan(String endTimeSpan) {
		this.endTimeSpan = endTimeSpan;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getLegendPath() {
		return legendPath;
	}

	public void setLegendPath(String legendPath) {
		this.legendPath = legendPath;
	}

	@Override
	public String toString() {
		return "AriaViewDateTerm [beginTimeSpan=" + beginTimeSpan
				+ ", endTimeSpan=" + endTimeSpan + ", iconPath=" + iconPath
				+ ", polluant=" + polluant + ", legendPath=" + legendPath + "]";
	}

}
