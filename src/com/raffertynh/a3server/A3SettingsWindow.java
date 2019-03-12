package com.raffertynh.a3server;

import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.json.simple.JSONObject;
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JTextField;

public class A3SettingsWindow extends JFrame {

	private JPanel contentPane;
	public JSONObject obj = new JSONObject();
	A3ServerLauncher parent;
	private JTextField txtFieldProfDir;
	private JTextField txtFieldWorkshopDir;
	private JTextField txtFieldInstallDir;
	
	public A3SettingsWindow(final A3ServerLauncher parent) {
		this.parent = parent;
		try {
			setTitle("A3Server - Settings Window");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 417, 198);
			contentPane = new JPanel();
			contentPane.setBorder(null);
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JButton btnSaveVehicle = new JButton("Save Config");
			btnSaveVehicle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
		    		parent.WORKSHOP_MODS_DIR = txtFieldWorkshopDir.getText();
		    		parent.config.workshopDir = parent.WORKSHOP_MODS_DIR;

		    		parent.INSTALL_DIR = txtFieldInstallDir.getText();
		    		parent.config.installDir = parent.INSTALL_DIR;

		    		parent.PROFILE_DIR = txtFieldProfDir.getText();
		    		parent.config.profileDir = parent.PROFILE_DIR;
		    		
		    		parent.config.save();
					dispose();
				}
			});
			btnSaveVehicle.setBounds(300, 133, 100, 23);
			contentPane.add(btnSaveVehicle);
			
			JButton btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
			btnCancel.setBounds(10, 133, 100, 23);
			contentPane.add(btnCancel);
			
			txtFieldProfDir = new JTextField();
			txtFieldProfDir.setColumns(10);
			txtFieldProfDir.setBounds(10, 102, 390, 20);
			contentPane.add(txtFieldProfDir);
			
			JLabel label = new JLabel("Arma 3 Profile Directory");
			label.setBounds(12, 87, 255, 14);
			contentPane.add(label);
			
			txtFieldWorkshopDir = new JTextField();
			/*txtFieldWorkshopDir.addKeyListener(new KeyAdapter() {
		    	@Override
		    	public void keyTyped(KeyEvent arg0) {
		    		parent.WORKSHOP_MODS_DIR = txtFieldWorkshopDir.getText();
		    		parent.config.workshopDir = parent.WORKSHOP_MODS_DIR;
		    		parent.config.save();
		    	}
		    });*/
			txtFieldWorkshopDir.setColumns(10);
			txtFieldWorkshopDir.setBounds(10, 65, 390, 20);
			contentPane.add(txtFieldWorkshopDir);
			
			JLabel label_1 = new JLabel("Arma Workshop Directory (e.g. 'Arma 3\\!Workshop\\')");
			label_1.setBounds(12, 50, 255, 14);
			contentPane.add(label_1);
			
			txtFieldInstallDir = new JTextField();

		    /*txtFieldInstallDir.addKeyListener(new KeyAdapter() {
		    	@Override
		    	public void keyTyped(KeyEvent arg0) {
		    		parent.INSTALL_DIR = txtFieldInstallDir.getText();
		    		parent.config.installDir = parent.INSTALL_DIR;
		    		parent.config.save();
		    	}
		    });*/
		    txtFieldInstallDir.setColumns(10);
		    txtFieldInstallDir.setBounds(10, 26, 390, 20);
			contentPane.add(txtFieldInstallDir);
			
			JLabel label_2 = new JLabel("Arma 3 Server Installation Directory");
			label_2.setBounds(12, 11, 172, 14);
			contentPane.add(label_2);
			
			setLocationRelativeTo(null);
			setResizable(false);
			setVisible(true);
			

		    //load config
			if(parent.config.installDir.length() > 0) {
				txtFieldInstallDir.setText(parent.config.installDir);
			}
			if(parent.config.workshopDir.length() > 0) {
				txtFieldWorkshopDir.setText(parent.config.workshopDir);
			}
			if(parent.config.profileDir.length() > 0) {
				txtFieldProfDir.setText(parent.config.profileDir);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	
	}

}
