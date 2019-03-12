package com.raffertynh.a3server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Configuration {

	public static String ROOT_FOLDER = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();

	public String installDir = "";
	public String workshopDir = "";
	public String profileDir = "";
	public JSONArray modsEnabled = new JSONArray();
	
	private File CFG_FILE;
	private File CFG_LOC;
	public Configuration() {
		System.out.println("Operating System: " + System.getProperty("os.name"));
		//root folder for saving the configuration file
		if(System.getProperty("os.name").toLowerCase().contains("linux")) {
			//linux: /home/<user>/config.json
			ROOT_FOLDER = System.getProperty("user.home");
			CFG_FILE = new File(ROOT_FOLDER + "/config.json");
			CFG_LOC = new File(ROOT_FOLDER + "/");
		} else {
			//windows: Documents/BJ Pandora/config.json
			CFG_FILE = new File(ROOT_FOLDER + "\\StinchinStein A3 Server\\config.json");
			CFG_LOC = new File(ROOT_FOLDER + "\\StinchinStein A3 Server\\");
		}
		
		if(!CFG_LOC.exists()) CFG_LOC.mkdirs(); 
		//default settings
		installDir = "";
		updateConfig();
		
		if(CFG_FILE.exists()) {
			load(); //load file
		} else {
			save(); //save all values to default in new file.
		}
		//load if file exists, else? Save default to file.
	}
	
	
	public void updateConfig() {
		//completely unused as far as I know...
		
		//DEPRECATED
		
		//saves to existing file, but sets values to the default if they don't exist
		//pretty much for when we add new values in future updates, it won't overwrite user settings.
		try {
			FileReader configurationFile = new FileReader(CFG_FILE);
			JSONObject obj = (JSONObject) new JSONParser().parse(configurationFile);
			configurationFile.close();
			if(obj.get("installDir") == null) obj.put("installDir", installDir);
			if(obj.get("workshopDir") == null) obj.put("workshopDir", workshopDir);
			if(obj.get("profileDir") == null) obj.put("profileDir", profileDir);
			if(obj.get("modsEnabled") == null) obj.put("modsEnabled", modsEnabled.toJSONString());
			FileOutputStream writer = new FileOutputStream(CFG_FILE);
			writer.write(obj.toJSONString().getBytes());
			writer.close();
			System.out.println("[Configuration] Saved Existing File!");
		} catch(Exception e) {}
	}
	
	public void save() {
		try {
			FileOutputStream writer = new FileOutputStream(CFG_FILE);
			JSONObject obj = new JSONObject();
			obj.put("installDir", installDir);
			obj.put("workshopDir", workshopDir);
			obj.put("modsEnabled", modsEnabled);
			obj.put("profileDir", profileDir);
			writer.write(obj.toJSONString().getBytes());
			writer.close();
			System.out.println("[Configuration] Saved New File!");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	//load file values to this class for use in the program
	public void load() {
        System.out.println("[Configuration] Loading...");
        
		try {
        	FileReader configurationFile = new FileReader(CFG_FILE);
	        JSONObject obj = (JSONObject) new JSONParser().parse(configurationFile);
	        installDir = obj.get("installDir").toString();
	        workshopDir = obj.get("workshopDir").toString();
	        profileDir = obj.get("profileDir").toString();
	        modsEnabled = (JSONArray) new JSONParser().parse(obj.get("modsEnabled").toString());
	        System.out.println("[Configuration] Loaded!");
        } catch(Exception e) {
	        System.out.println("[Configuration] Failed!");
	        e.printStackTrace();
        }
	}
	
	//from an old game my friend and I worked on
	
	/*public void apply(PandoraAPI pandora) {
		//apply configuration to game values
		pandora.user = WIDTH;
		pandora.HEIGHT = HEIGHT;
		pandora.display = fullscreenDisplay;
	}*/
}
