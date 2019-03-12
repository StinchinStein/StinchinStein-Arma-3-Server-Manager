package com.raffertynh.a3server;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.raffertynh.renderer.ArmaMod;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class A3ServerLauncher extends JFrame {

	private A3ServerLauncher instance;
	private JPanel contentPane;
	
	public String INSTALL_DIR = "";
	public String WORKSHOP_MODS_DIR = "";
	public String MODS_PARAM = "";
	public String PROFILE_DIR = "";
	
	public JSONArray MODS_LIST = new JSONArray();
	public Configuration config;
	private JButton btnStartServer, btnMissionParams, btnProfileCfg;
	private Process processA3Server;
	public JTextArea steamCMDConsole;
	private JButton btnModManager;
	
	A3WorkshopWindow winWorkshop;
	
	public boolean SERVER_RUNNING = false;
	
	public ArrayList<ArmaMod> CACHE_WORKSHOP = new ArrayList<ArmaMod>();
	public ArrayList<ArmaMod> CACHE_MODLIST = new ArrayList<ArmaMod>();
	
	public static DecimalFormat df = new DecimalFormat("#,###.#");
	
	public boolean CACHE_FINISHED = false;
	
	public static void main(String[] args) {
		new A3ServerLauncher();
	}

	class ArmaThread extends Thread {
		
		public void run() {
			try {
				DiscordRichPresence rich = new DiscordRichPresence
						.Builder("Status: Running")
						.setDetails("StinchinStein's Arma 3 Server Manager")
						.build();
				DiscordRPC.discordUpdatePresence(rich);
				SERVER_RUNNING = true;
				processA3Server = Runtime.getRuntime().exec(INSTALL_DIR + "\\arma3server_x64.exe -name=A3Server -world=altis -config=CONFIG_server.cfg " + MODS_PARAM);
				//System.out.println(INSTALL_DIR + "\\arma3server.exe -profiles=F:\\SteamLibrary\\steamapps\\common\\Arma 3 Server\\Users -name=server -world=altis -config=CONFIG_server.cfg " + MODS_PARAM);
				while(processA3Server.isAlive()) {}
				steamCMDConsole.append("Stopped Arma 3 Server.\n");
				btnStartServer.setText("Start Server");
				btnMissionParams.setEnabled(true);
				btnProfileCfg.setEnabled(true);
				btnModManager.setEnabled(true);
	    		SERVER_RUNNING = false;
			} catch(Exception e) {
				btnMissionParams.setEnabled(true);
				btnProfileCfg.setEnabled(true);
				btnStartServer.setText("Start Server");
				btnModManager.setEnabled(true);
	    		SERVER_RUNNING = false;
			}
		}
	}
	public A3ServerLauncher() {
		instance = this;
		setResizable(false);
		config = new Configuration();
		
		//ArmaCFGParser cfg = new ArmaCFGParser();
		//cfg.parse(new File(PROFILE_DIR + "\\A3Server.Arma3Profile"));
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if(processA3Server != null && processA3Server.isAlive()) {
					processA3Server.destroy();
				}
			}
		});
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 416, 326);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnStartServer = new JButton("Start Server");
		btnStartServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!SERVER_RUNNING) {
					new Thread(new ArmaThread()).start();
					btnStartServer.setText("Stop Server");
					btnMissionParams.setEnabled(false);
					btnProfileCfg.setEnabled(false);
					btnModManager.setEnabled(false);
					steamCMDConsole.append("Started Arma 3 Server.\n");
				} else {
		    		processA3Server.destroy();
		    		DiscordRichPresence rich = new DiscordRichPresence
		    				.Builder("Status: Not Running")
		    				.setDetails("StinchinStein's Arma 3 Server Manager")
		    				.build();
		    		DiscordRPC.discordUpdatePresence(rich);
				}
			}
		});
		btnStartServer.setBounds(285, 267, 119, 23);
		contentPane.add(btnStartServer);
		DefaultListModel<ArmaMod> md = new DefaultListModel<>();
	    
	    JScrollPane scrollPane = new JScrollPane();
	    scrollPane.setBounds(6, 104, 398, 131);
	    contentPane.add(scrollPane);
	    
	    steamCMDConsole = new JTextArea();
	    steamCMDConsole.setEnabled(false);
	    scrollPane.setViewportView(steamCMDConsole);
	    
	    JLabel lblSteamcmdSettings = new JLabel("Server Console");
	    lblSteamcmdSettings.setHorizontalAlignment(SwingConstants.CENTER);
	    lblSteamcmdSettings.setBounds(6, 80, 92, 23);
	    contentPane.add(lblSteamcmdSettings);
	    
	    btnMissionParams = new JButton("CONFIG_server.cfg");
	    btnMissionParams.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		new A3ConfigEditor(instance, new File(INSTALL_DIR + "\\CONFIG_server.cfg"));
	    	}
	    });
	    btnMissionParams.setBounds(5, 240, 143, 23);
	    contentPane.add(btnMissionParams);
	    
	    btnProfileCfg = new JButton("A3Server.Arma3Profile");
	    btnProfileCfg.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		new A3ConfigEditor(instance, new File(PROFILE_DIR + "\\A3Server.Arma3Profile"));
	    	}
	    });
	    btnProfileCfg.setBounds(261, 80, 143, 23);
	    contentPane.add(btnProfileCfg);
	    
	    JLabel lblArmaServer_2 = new JLabel("Arma 3 Server Manager Created By BJ Rafferty");
	    lblArmaServer_2.setHorizontalAlignment(SwingConstants.RIGHT);
	    lblArmaServer_2.setBounds(175, 5, 229, 14);
	    contentPane.add(lblArmaServer_2);
	    
	    JButton btnLauncherSettings = new JButton("Launcher Settings");
	    btnLauncherSettings.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		new A3SettingsWindow(instance);
	    	}
	    });
	    btnLauncherSettings.setBounds(285, 240, 119, 23);
	    contentPane.add(btnLauncherSettings);
	    
	    JButton btnServerFolder = new JButton("Open Server Folder");
	    btnServerFolder.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		try {
					Desktop.getDesktop().open(new File(INSTALL_DIR));
				} catch (IOException e1) {}
	    	}
	    });
	    btnServerFolder.setBounds(151, 240, 130, 23);
	    contentPane.add(btnServerFolder);
	    
	    btnModManager = new JButton("Mod Manager");
	    btnModManager.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		A3ModManager modManager = new A3ModManager(instance, MODS_LIST);
	    		modManager.addWindowListener(new java.awt.event.WindowAdapter() {
	    		    @Override
	    		    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
	    		    	config.modsEnabled = MODS_LIST;
	    		    	config.save();
	    		    }
	    		});
	    	}
	    });
	    btnModManager.setBounds(6, 267, 143, 23);
	    contentPane.add(btnModManager);

		config.load();
	    //load config
		if(config.installDir.length() > 0) {
			INSTALL_DIR = config.installDir;
			MODS_LIST = config.modsEnabled;
		}
		if(config.workshopDir.length() > 0) {
			WORKSHOP_MODS_DIR = config.workshopDir;
		}
		if(config.profileDir.length() > 0) {
			PROFILE_DIR = config.profileDir;
		}
		setLocationRelativeTo(null);
		setTitle("StinchinStein's A3 Server Manager");
		setVisible(true);

		
		//cache thread
		new Thread(new Runnable() {
			public void run() {
				System.out.println("Caching Install Directory");
				reloadModDir(CACHE_MODLIST, INSTALL_DIR);
				System.out.println("Caching Workshop Directory");
				reloadModDir(CACHE_WORKSHOP, WORKSHOP_MODS_DIR);
				winWorkshop.onCacheLoaded();
				CACHE_FINISHED = true;
				System.out.println("Caching Finished");
			}
		}).start();
		
		//load windows
		winWorkshop = new A3WorkshopWindow(instance);
		
		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
			System.out.println("Welcome " + user.username + "#" + user.discriminator + "!");
		}).build();
		DiscordRPC.discordInitialize("554831675197095937", handlers, true);
		
		DiscordRichPresence rich = new DiscordRichPresence
				.Builder("Status: Not Running")
				.setDetails("StinchinStein's Arma 3 Server Manager")
				.build();
		DiscordRPC.discordUpdatePresence(rich);
	}
	
	
	
	public void updateModParameter() {
		String tMd = "-mod=\"";

		for(Object s : MODS_LIST) {
			tMd += s.toString() + ";";
		}
		MODS_PARAM = tMd + "\"";
		System.out.println(MODS_PARAM);	
	}
	
	public void reloadModDir(ArrayList<ArmaMod> modList, String path) {
		File[] fileList = new File(WORKSHOP_MODS_DIR).listFiles();
		for(File f : fileList) {
			if(f.getName().contains("@")) {
				try {
					JSONObject obj = ArmaCFGParser.parse(new File(f.getAbsolutePath() + File.separator + "mod.cpp"));
					if(obj.get("name") != null) {
						ArmaMod aMod = new ArmaMod(f.getName(), obj.get("name").toString());
						if((getFileFolderSize(new File(f.getAbsolutePath() + File.separator)) / 1024f) / 1024f > 1024) {
							aMod.fileSize = df.format((getFileFolderSize(new File(f.getAbsolutePath() + File.separator)) / 1024f) / 1024f / 1024f) + "GB";
						} else {
							aMod.fileSize = df.format((getFileFolderSize(new File(f.getAbsolutePath() + File.separator)) / 1024f) / 1024f) + "MB";
						}
						modList.add(aMod);
					} else {
						ArmaMod aMod = new ArmaMod(f.getName(), f.getName());
						if((getFileFolderSize(new File(f.getAbsolutePath() + File.separator)) / 1024f) / 1024f > 1024) {
							aMod.fileSize = df.format((getFileFolderSize(new File(f.getAbsolutePath() + File.separator)) / 1024f) / 1024f / 1024f) + "GB";
						} else {
							aMod.fileSize = df.format((getFileFolderSize(new File(f.getAbsolutePath() + File.separator)) / 1024f) / 1024f) + "MB";
						}
						modList.add(aMod);
					}
				} catch(Exception e) {}
			}
		}
		//refreshModList();
	}
	
	public static long getFileFolderSize(File dir) {
		long size = 0;
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isFile()) {
					size += file.length();
				} else
					size += getFileFolderSize(file);
			}
		} else if (dir.isFile()) {
			size += dir.length();
		}
		return size;
	}
}
