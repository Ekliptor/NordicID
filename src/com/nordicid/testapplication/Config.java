package com.nordicid.testapplication;

import java.io.File;
import java.util.prefs.Preferences;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

public class Config {
	protected static Config instance = null;
	
	protected String configFile = "config.ini"; // relative to working dir
	protected Preferences prefs = null;
	
	public static Config getInstance() {
		if (Config.instance == null)
			Config.instance = new Config();
		return Config.instance;
	}
	
	private Config() {
		try {
			Ini ini = new Ini(new File(this.configFile));
			this.prefs = new IniPreferences(ini);
			//System.out.println("grumpy/homePage: " + prefs.node("app").get("foo", null));
		}
		catch (Exception e) { // TODO catch FileNotFound exception and create defaultt file? we ship the default file already
			System.err.println("Error reading config file");
			System.err.println(e);
		}
	}
	
	public Preferences getPrefs() {
		return this.prefs;
	}
}
