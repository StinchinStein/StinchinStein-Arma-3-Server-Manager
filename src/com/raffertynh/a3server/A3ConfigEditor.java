package com.raffertynh.a3server;

import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JOptionPane;
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

public class A3ConfigEditor extends JFrame implements IArmaWindow {

	private JPanel contentPane;
	public JSONObject obj = new JSONObject();
	A3ServerLauncher parent;
	private RSyntaxTextArea textArea;
	private File fileToMod;
	
	public A3ConfigEditor(final A3ServerLauncher parent, File fileToModify) {
		this.parent = parent;
		this.fileToMod = fileToModify;
		try {
			setTitle("A3Server - Configuration Editor - " + fileToMod.getName());
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 620, 402);
			contentPane = new JPanel();
			contentPane.setBorder(null);
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JButton btnSaveVehicle = new JButton("Save Config");
			btnSaveVehicle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						FileOutputStream fis = new FileOutputStream(fileToMod);
						fis.write(textArea.getText().getBytes());
						fis.close();
						System.out.println("Wrote file!");
					} catch(Exception e2) {
						e2.printStackTrace();
					}
					dispose();
				}
			});
			btnSaveVehicle.setBounds(504, 339, 100, 23);
			contentPane.add(btnSaveVehicle);
			
			JButton btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
			btnCancel.setBounds(11, 339, 100, 23);
			contentPane.add(btnCancel);
			
			textArea = new RSyntaxTextArea(20, 60);
			textArea.setCurrentLineHighlightColor(new Color(245, 245, 245));
		    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
		    textArea.setCodeFoldingEnabled(true);
		    RTextScrollPane sp = new RTextScrollPane(textArea);
			sp.setBounds(11, 28, 593, 300);
		    contentPane.add(sp);
		      
			
			JLabel lblNewLabel = new JLabel("Configuration Editor - " + fileToMod.getName());
			lblNewLabel.setBounds(10, 7, 594, 14);
			contentPane.add(lblNewLabel);

			setLocationRelativeTo(null);
			setResizable(false);
			
			//load file to textarea
			System.out.println("EXISTS: " + fileToMod.exists());
			if(fileToMod.exists()) {
				FileInputStream fis = new FileInputStream(fileToMod);
				byte[] data = new byte[(int) fileToMod.length()];
				fis.read(data);
				fis.close();
				textArea.setText(new String(data));
				textArea.setSelectionStart(0);
				textArea.setSelectionEnd(0);
				setVisible(true);
			} else {
				setVisible(false);
				JOptionPane.showConfirmDialog(null, "File doesn't exist in\n'" + fileToMod.getParentFile().getAbsolutePath() + "'.", "File Doesn't Exist.", JOptionPane.CLOSED_OPTION);
				dispose();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	
	}

	@Override
	public void onCacheLoaded() {
		
	}

}
