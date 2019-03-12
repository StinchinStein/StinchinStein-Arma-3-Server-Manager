package com.raffertynh.a3server;

import java.io.File;
import java.io.FileInputStream;

import org.json.simple.JSONObject;

public class ArmaCFGParser {
	
	public ArmaCFGParser() {
	}
	
	public static JSONObject parse(String input) {
		JSONObject obj = new JSONObject();

		String[] lines = input.split("\n");
		for(String line : lines) {
			if(line.contains("=")) { //key, value type
				String key = line.substring(0, line.indexOf("=")).trim();
				String val = line.substring(line.indexOf("=")+1, line.length()).trim().replaceAll("\"", "").replaceAll(";", "");
				obj.put(key, val);
				//System.out.println("KEY: " + key + " VAL: " + val);
			}
		}
		return obj;
	}

	public static JSONObject parse(File file) {
		byte[] data = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
		} catch(Exception e) {}
		return parse(new String(data));
	}
}
