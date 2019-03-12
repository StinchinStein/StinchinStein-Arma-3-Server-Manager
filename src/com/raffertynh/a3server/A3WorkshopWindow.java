package com.raffertynh.a3server;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import com.raffertynh.renderer.ArmaMod;
import com.raffertynh.renderer.InventoryRenderer;

public class A3WorkshopWindow extends JFrame {

	private JPanel contentPane;
	private DefaultTableModel tblModel;
	private Canvas canvas;
	private int noteID = 0;
	public String winType;
	public JSONObject obj = new JSONObject();
	A3ServerLauncher parent;
	private ArmaList list;
	private JButton btnSaveVehicle;
	private ArrayList<String> WORKSHOP_SELECTED = new ArrayList<String>();
	private JTextField textField;
	private DecimalFormat df = new DecimalFormat("#,###.#");
	
	private ArrayList<ArmaMod> CACHE = new ArrayList<ArmaMod>();
	
	
	class CopyFilesThread extends Thread {
		public void run() {
			btnSaveVehicle.setEnabled(false);
			dispose();
			//COPY MODS FROM ARMA WORKSHOP TO SERVER FOLDER!
			int containIndex = 0;
			for(String mod : WORKSHOP_SELECTED) {
				if(installDirMods().contains(mod)) {
					containIndex++;
				}
				//TODO CHECK FOLDER
			}
			if(containIndex > 0) {
				int opt = JOptionPane.showConfirmDialog(null, "Overwrite " + containIndex + " mods?\nThis may take a while.", "Overwrite Files", JOptionPane.OK_OPTION);
				System.out.println("OPTION : " + opt);
				if(opt == 1) return;
			}
			updateServerMods();
			if(WORKSHOP_SELECTED.size() > 0) {
				JOptionPane.showConfirmDialog(null, "Finished Copying " + WORKSHOP_SELECTED.size() + " files!", "Finished Copying", JOptionPane.CLOSED_OPTION);
			}
			btnSaveVehicle.setEnabled(true);
			//dispose();
		}
	}
	
	public ArrayList<String> installDirMods() {
		ArrayList<String> ml = new ArrayList<String>();
		File[] fileList = new File(parent.INSTALL_DIR).listFiles();
		for(File f : fileList) {
			if(f.getName().contains("@")) {
				ml.add(f.getName());
			}
		}
		return ml;
	}
	public void updateServerMods() {
		for(String mod : WORKSHOP_SELECTED) {
			try {
				File modFile = new File(parent.INSTALL_DIR + File.separator + mod + File.separator);
				File modWorkshopFile = new File(parent.WORKSHOP_MODS_DIR + File.separator + mod + File.separator);
				if(modFile.exists()) {/*
					int op = JOptionPane.showConfirmDialog(null, "The mod '" + mod + "' already exists! Replace?", "File Copy Failure", JOptionPane.YES_NO_OPTION);
					if(op == 0) {
					}*/
					parent.steamCMDConsole.append("Removing old '" + mod + "'\n");
					FileUtils.deleteDirectory(new File(parent.INSTALL_DIR + File.separator + mod + File.separator));
					parent.steamCMDConsole.append("Copying " + mod + " - " + df.format((getFileFolderSize(modWorkshopFile) / 1024f) / 1024f) + "MB...\n");
					FileUtils.copyDirectory(new File(parent.WORKSHOP_MODS_DIR + File.separator + mod + File.separator), new File(parent.INSTALL_DIR + File.separator + mod + File.separator));
					parent.steamCMDConsole.append("Finished " + mod + "!\n"); 
				} else {
					parent.steamCMDConsole.append("Copying " + mod + " - " + df.format((getFileFolderSize(modWorkshopFile) / 1024f) / 1024f) + "MB...\n");
					FileUtils.copyDirectory(new File(parent.WORKSHOP_MODS_DIR + File.separator + mod + File.separator), new File(parent.INSTALL_DIR + File.separator + mod + File.separator));
					parent.steamCMDConsole.append("Finished " + mod + "!\n"); 
				}
			} catch (IOException e1) {}
		}
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
	
	public A3WorkshopWindow(final A3ServerLauncher parent) {
		this.parent = parent;
		try {
			setTitle("A3ServerLauncher.JAVA - WORKSHOP");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 596, 290);
			contentPane = new JPanel();
			contentPane.setBorder(null);
			setContentPane(contentPane);
			contentPane.setLayout(null);
			btnSaveVehicle = new JButton("Update Mods");
			btnSaveVehicle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Thread(new CopyFilesThread()).start();
				}
			});
			btnSaveVehicle.setBounds(378, 229, 100, 23);
			contentPane.add(btnSaveVehicle);
			
			JButton btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
			btnCancel.setBounds(11, 229, 100, 23);
			contentPane.add(btnCancel);
			
			JButton btnDeleteNote = new JButton("Delete Mod");
			btnDeleteNote.setEnabled(false);
			btnDeleteNote.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
			btnDeleteNote.setBounds(284, 229, 90, 23);
			contentPane.add(btnDeleteNote);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(11, 28, 569, 194);
			contentPane.add(scrollPane);

			list = new ArmaList();
			DefaultListModel<ArmaMod> md = new DefaultListModel<>();
			list.setModel(md);
			list.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					boolean isSelected = !((ArmaMod) list.getModel().getElementAt(list.getSelectedIndex())).isSelected();
					String mod = list.getModel().getElementAt(list.getSelectedIndex()).toString();
					if(isSelected) {
						if(!WORKSHOP_SELECTED.contains(mod)) {
							WORKSHOP_SELECTED.add(list.getModel().getElementAt(list.getSelectedIndex()).toString());
						}
					} else {
						if(WORKSHOP_SELECTED.contains(mod)) {
							WORKSHOP_SELECTED.remove(list.getModel().getElementAt(list.getSelectedIndex()).toString());
						}
					}
	    			updateModel();
	    			/*config.modsEnabled = MODS_LIST;
	    			config.save();
					*/
				}
			});
			
		    list.setCellRenderer(new InventoryRenderer());
		    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    
			scrollPane.setViewportView(list);
			
			JButton btnAll = new JButton("All");
			btnAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int i = 0; i < list.getModel().getSize(); i++) {
		    			String mod = list.getModel().getElementAt(i).toString();
		    			if(!WORKSHOP_SELECTED.contains(mod)) {
		    				WORKSHOP_SELECTED.add(list.getModel().getElementAt(i).toString());
			    		}
		    		}
	    			updateModel();
				}
			});
			btnAll.setBounds(537, 3, 43, 23);
			contentPane.add(btnAll);
			
			JButton btnRefreshMods = new JButton("Refresh Mods");
			btnRefreshMods.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					reloadModDir();
				}
			});
			btnRefreshMods.setBounds(480, 229, 100, 23);
			contentPane.add(btnRefreshMods);
			
			JButton btnNone_1 = new JButton("None");
			btnNone_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					for(int i = 0; i < list.getModel().getSize(); i++) {
		    			String mod = list.getModel().getElementAt(i).toString();
		    			if(WORKSHOP_SELECTED.contains(mod)) {
		    				WORKSHOP_SELECTED.remove(list.getModel().getElementAt(i).toString());
			    		}
		    		}
	    			updateModel();
				}
			});
			btnNone_1.setBounds(480, 3, 57, 23);
			contentPane.add(btnNone_1);
			
			JLabel lblNewLabel = new JLabel("Workshop Mods");
			lblNewLabel.setBounds(10, 8, 76, 14);
			contentPane.add(lblNewLabel);
			
			textField = new JTextField();
        	textField.setForeground(Color.GRAY);
        	textField.setText("Search");
			textField.addFocusListener(new FocusListener() {
			    @Override
			    public void focusGained(FocusEvent e) {
			        if (textField.getText().equals("Search")) {
			        	textField.setText("");
			            textField.setForeground(Color.BLACK);
			        }
			    }
			    @Override
			    public void focusLost(FocusEvent e) {
			        if (textField.getText().isEmpty()) {
			        	textField.setForeground(Color.GRAY);
			        	textField.setText("Search");
			        }
			    }
			});
			textField.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent arg0) {
					refreshModList(textField.getText());
					updateModel();
				}
			});
			textField.setBounds(278, 4, 200, 21);
			contentPane.add(textField);
			textField.setColumns(10);
			
			
			setLocationRelativeTo(null);
			setResizable(false);
			setVisible(true);

			if(parent.WORKSHOP_MODS_DIR.length() > 0) {
				//refreshModList();
				reloadModDir();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	
	}

	
	private void updateModel() {
		for(int i = 0; i < getModel().size(); i++) {
			if(WORKSHOP_SELECTED.contains(getModel().getElementAt(i).toString())) {
				getModel().getElementAt(i).setSelected(true);
			} else {
				getModel().getElementAt(i).setSelected(false);
			}
		}
		list.repaint();
	}
	
	public void reloadModDir() {
		File[] fileList = new File(parent.WORKSHOP_MODS_DIR).listFiles();
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
						CACHE.add(aMod);
					} else {
						ArmaMod aMod = new ArmaMod(f.getName(), f.getName());
						if((getFileFolderSize(new File(f.getAbsolutePath() + File.separator)) / 1024f) / 1024f > 1024) {
							aMod.fileSize = df.format((getFileFolderSize(new File(f.getAbsolutePath() + File.separator)) / 1024f) / 1024f / 1024f) + "GB";
						} else {
							aMod.fileSize = df.format((getFileFolderSize(new File(f.getAbsolutePath() + File.separator)) / 1024f) / 1024f) + "MB";
						}
						CACHE.add(aMod);
					}
				} catch(Exception e) {}
			}
		}
		refreshModList();
	}
	private void refreshModList(String filter) {
		getModel().clear();
		for(ArmaMod mod: CACHE) {
			if(mod.folderName.toLowerCase().contains(filter.toLowerCase())) {
				getModel().addElement(mod);
			}
		}
		
		
		list.setModel(getModel());
		//.setText("Mods (" + getModel().size() + " total | " + modList.size() + " enabled)");
	}
	private void refreshModList() {
		refreshModList("");
	}
	
	private DefaultListModel<ArmaMod> getModel() {
		return (DefaultListModel<ArmaMod>) list.getModel();
	}
}
