package com.raffertynh.renderer;

import javax.swing.JList;

public class ArmaMod {

	public String localizedName, folderName;
	public boolean isSelected = false;
	public String fileSize = "N/A";
	public ArmaMod(String name, String name2) {
		this.localizedName = name2;
		this.folderName = name;
	}


	public void setSelected(boolean b) {
		this.isSelected = b;
	}
	public boolean isSelected() {
		return this.isSelected;
	}
	
	@Override
	public String toString() {
		return this.folderName;
	}
}
