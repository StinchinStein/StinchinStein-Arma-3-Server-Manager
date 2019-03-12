package com.raffertynh.a3server;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.raffertynh.renderer.ArmaMod;
import com.raffertynh.renderer.InventoryRenderer;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import javax.swing.JProgressBar;

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
	
	public boolean SERVER_RUNNING = false;
	
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
				processA3Server = Runtime.getRuntime().exec(INSTALL_DIR + "\\arma3server.exe -name=A3Server -world=altis -config=CONFIG_server.cfg " + MODS_PARAM);
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
	
	private void openWorkshop() {
		if(WORKSHOP_MODS_DIR.length() > 0) {
			new A3WorkshopWindow(instance);
		} else {
			steamCMDConsole.append("Workshop directory is empty!\n");
			String s = JOptionPane.showInputDialog(null, "Enter A Valid Workshop Directory ( 'Arma 3/!Workshop' )", "Invalid Workshop Directory", JOptionPane.CLOSED_OPTION);
			System.out.println(s);
			if(s != null) {
	    		WORKSHOP_MODS_DIR = s;
	    		config.workshopDir = WORKSHOP_MODS_DIR;
	    		config.save();
	    		openWorkshop();
			}
		}
	}
	
	public void updateModParameter() {
		String tMd = "-mod=\"";

		for(Object s : MODS_LIST) {
			tMd += s.toString() + ";";
		}
		MODS_PARAM = tMd + "\"";
		System.out.println(MODS_PARAM);
		
		
	}
}
