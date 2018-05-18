package edu.neu.csye6200.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.neu.csye6200.fd.FluidFrameAvg;
import edu.neu.csye6200.fd.FluidSimulator;



/**
 * A sample canvas that draws a rainbow of lines
 * @author MMUNSON
 */
public class BGCanvas extends JPanel implements ActionListener,Observer {

	private static final long serialVersionUID = 1L;
	private Logger log = Logger.getLogger(BGCanvas.class.getName());
    private int lineSize = 20;
    private Color col = null;
    private long counter = 0L;
    private static BGCanvas bgPanel = null;
    private final int ARR_SIZE = 4;
    public static int offset=0;
    private static FluidFrameAvg ffa = null;

	
    /**
     * CellAutCanvas constructor
     */
	public BGCanvas() {
		col = Color.WHITE;
	}

	/**
	 * The UI thread calls this method when the screen changes, or in response
	 * to a user initiated call to repaint();
	 */
	synchronized  public void paint(Graphics g) {
		System.out.println("Updaying Canvas");
		if(ffa == null)
		{	offset=100;
			System.out.println("BackGround Draw");
			drawBG(g); // Our Added-on drawing
		}
		else
		{
			System.out.println("CAnvas Drawing");
			drawFD(g);
		}
    }
	
	

	/**
	 * Draw the CA graphics panel
	 * @param g
	 */
	public void drawBG(Graphics g) {
		log.info("Drawing BG " + counter++);
		Graphics2D g2d = (Graphics2D) g;
		g2d = (Graphics2D) g;
		Dimension size = getSize();
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, size.width, size.height);
		
		g2d.setColor(Color.RED);
		g2d.drawString("Yogesh", 10, 15);
		
		int maxRows = size.height / lineSize;
		int maxCols = size.width / lineSize;
		for (int j = 0; j < maxRows; j++) {
		   for (int i = 0; i < maxCols; i++) {
			   int redVal = validColor(i*5);
			   int greenVal = validColor(255-j*5);
			   int blueVal = validColor((j*5)-(i*2));
			   col = new Color(redVal, greenVal, blueVal);
			   // Draw box, one pixel less to create a black outline
			   int startx = i*lineSize;
			   int starty = j*lineSize;
			   int endx = startx + 15;
			   int endy = starty + 15;
			   paintLine( g2d, startx, starty, endx, endy, col); 
		   }
		}
	}
	
	//main vector draw 
	
	
	private void drawFD(Graphics g) {
		// TODO Auto-generated method stub
		log.info("Drawing FD " + counter++);
		Graphics2D g2d = (Graphics2D) g;
		g2d = (Graphics2D) g;
		Dimension size = getSize();
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, size.width, size.height);
		
		g2d.setColor(Color.RED);
		g2d.drawString("BG 2D", 10, 15);
		
		int maxRows = size.height / lineSize;
		int maxCols = size.width / lineSize;
		
		int ffasize = ffa.getsize();
		if( maxRows > ffasize) maxRows = ffasize;
		if( maxCols > ffasize) maxCols = ffasize;
		
		for (int j = 0; j < maxRows; j++) {
			   for (int i = 0; i < maxCols; i++) {
				   int redVal = validColor(i*5);
				   int greenVal = validColor(255-j*5);
				   int blueVal = validColor((j*5)-(i*2));
				   
				   double dirVal = ffa.getDirection(i,j);
				   double magVal = ffa.getMagnitude(i,j);
				   
				 //  System.out.print("> " + dirVal);
				   col = new Color(redVal, greenVal, blueVal);
				   // Draw box, one pixel less to create a black outline
				   int startx = i*lineSize;
				   int starty = j*lineSize;
				   magVal = 0.5;
				   int endx = startx + (int)(magVal * 15.0 * Math.sin(dirVal));
				   int endy = starty + (int)(magVal * 15.0 * Math.cos(dirVal));
				   if(magVal != 0)
				   {
				   paintLine( g2d, startx, starty, endx, endy, col); 
				 //  drawArrow(g, startx, starty, endx, endy);
				   }
				   else
				   {	if(FluidSimulator.platePresent)
				   		{
					   		g2d.setColor(Color.white);
					   		g2d.fillRect(201,201, 200+offset,400);
				   		}
				   }
				   }
			   //System.out.println();
			}
			offset += 100;
	}
	
	
    

    void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) g1.create();

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                      new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
    }
	/*
	 * A local routine to ensure that the color value is in the 0 to 255 range.
	 */
	private int validColor(int colorVal) {
		if (colorVal > 255)
			colorVal = 255;
		if (colorVal < 0)
			colorVal = 0;
		return colorVal;
	}
	

	//Paint line 
	private void paintLine(Graphics2D g2d, int startx, int starty, int endx, int endy, Color color) {
		g2d.setColor(color);
		g2d.drawLine(startx, starty, endx, endy);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		 System.out.println("New Frame Came");

		 if(arg1 instanceof FluidFrameAvg)
		 {
			 System.out.print(" yes correct");
			 this.ffa= (FluidFrameAvg) arg1;

			 SwingUtilities.updateComponentTreeUI(WaterApp.bgPanel);
			 revalidate();
			 repaint();

			
		 }
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
	}
	
}

