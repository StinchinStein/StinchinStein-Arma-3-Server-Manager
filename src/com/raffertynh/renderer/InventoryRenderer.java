package com.raffertynh.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class InventoryRenderer extends JLabel implements ListCellRenderer<ArmaMod> {
	 
	
	@Override
    public Component getListCellRendererComponent(final JList<? extends ArmaMod> list, final ArmaMod veh, int index, final boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel() {
            public void paintComponent(Graphics g) {
                list.setFixedCellHeight(48);
                list.setDoubleBuffered(true);
                if(veh.isSelected()) {
                	g.setColor(new Color(0.8f, 0.9f, 0.8f));
                	g.fillRect(0, 0, getWidth(), getHeight());
                }
            	g.setColor(Color.BLACK);
            	g.fillRect(0, getHeight()-1, getWidth(), 1);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(Color.BLACK);
                //if(isSelected) g.setColor(Color.GRAY);
                //String miles =  Integer.valueOf(veh.mileage)!=0?" - " + new DecimalFormat("#,###").format(Integer.valueOf(veh.mileage)) + " mi":"";
                //String mkMdl = veh.year + " " + veh.make + " " + veh.model + " " + veh.trim + miles;
                //String vin = veh.vin.length()!=0?veh.vin:"No VIN Provided";

                g.setFont(g.getFont().deriveFont(16f));
                g.drawString(veh.localizedName, 5, 18);
                g.setColor(Color.GRAY);
                g.drawString(veh.folderName, 5, 38);
                g.setColor(Color.DARK_GRAY);
                g.drawString(veh.fileSize, getWidth() - g.getFontMetrics().stringWidth(veh.fileSize) - 5, 16);
                g.setColor(Color.GRAY);
                g.setFont(g.getFont().deriveFont(12f));
                //g.drawString(vin, 92, 34);
                g.setColor(new Color(0, 0.7f, 0.5f));
                //g.drawString("$" + veh.sellPrice, 92, 60);
					
                /*if(veh.imgIcon == null) {
                	//try {
	                	//veh.imgIcon = getOnlineResourceImage("http://raffertynh.com/raffertyauto/uploads/" + veh.vin + "/" + files[0].getName(), 86, 62);
	                	FTPFile[] files = Main.listFiles("RaffertyAuto/uploads/" + veh.vin + "/");
	                	if(files.length > 0) {
	                		veh.imgIcon = getOnlineResourceImage("http://raffertynh.com/raffertyauto/uploads/" + veh.vin + "/" + files[0].getName(), 86, 62);
	                	}
                	//} catch(Exception e) {}
                }*/
                //Image imageIcon = getResourceImage("C:/Users/stinc/Desktop/TestPics/20180928_111252.jpg", 86, 62);
                //g.drawImage(veh.imgIcon, 2, 2, 86, 62, null);
                
				/*g.setFont(g.getFont().deriveFont(Font.BOLD));
                g.setColor(Color.BLACK);
                g.drawString("" + veh.id, 6, 17);
                g.setColor(Color.WHITE);
                g.drawString("" + veh.id, 4, 18);*/
           }
        };
        return panel;
    }
	
	public Image getResourceImage(String filepath, int w, int h){
		try {
			Image rawImage = ImageIO.read(getClass().getResource(filepath));
	        Image renderedImage = rawImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
	        return renderedImage;
		} catch(Exception e) {}
		return null;

   }
	public Image getOnlineResourceImage(String filepath, int w, int h){
		try {
			Image rawImage = ImageIO.read(new URL(filepath));
	        Image renderedImage = rawImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
	        return renderedImage;
		} catch(Exception e) {}
		return null;

   }
}