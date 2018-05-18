
package edu.neu.csye6200.fd;

import java.util.Observable;

import javax.swing.SwingUtilities;

import edu.neu.csye6200.ui.BGCanvas;
import edu.neu.csye6200.ui.WaterApp;

/**
 * A single frame of a CA Fluid Frame
 * 
 */
public class FluidSimulator extends Observable implements Runnable {
	
	private boolean done = false; // Set true to exit (i.e. stop) the simulation
	private static boolean paused = false; // Set true to pause the simulation
	
	private int MAX_FRAME_SIZE = 1000; // How big is the simulation frame
	private int MAX_GENERATION = 5; // How many generations will we calculate before we're through?
	private int genCount = 0; // the count of the most recent generation
	
	public static boolean platePresent = false;
	private FluidFrame currentFrame = null;
	private  FluidFrameAvg avgFrame =null;
	public static int xStart=100,xEnd=200,yStart=100,yEnd=300,offset=50;
	
	//private RuleA rule = new RuleA(MAX_FRAME_SIZE);
	private RuleA rule = null;
	
	public FluidSimulator(int gen) {
		MAX_GENERATION = gen;
		platePresent= WaterApp.isPlateON;
		currentFrame = new FluidFrame(MAX_FRAME_SIZE);
		
		currentFrame.addRandomParticles(0.2); // Only 20% of the cells should have a particle
		rule = new BasicRule(MAX_FRAME_SIZE,WaterApp.collisionRule);
		avgFrame = new FluidFrameAvg(MAX_FRAME_SIZE/10);
	}

	public void runSim() {
		
		System.out.println("FluidFrame: 0" );
		//currentFrame.drawFrameToConsole();
		xStart= 100 ;
		xEnd = 200;
		while(!done) {
			
			// Move target if needed
			if(!paused)
			{
			FluidFrame nextFrame = rule.createNextFrame(currentFrame);
			
			// Average the results to create a lower-res display frame
			avgFrame.setFluidFrame(nextFrame);
			
			//avgFrame.magnitudeDisp(); can be used to debug
			
			// Advertise that we have a display displayable average FluidFrame and let it be drawn
			setChanged();
	        notifyObservers(avgFrame);

			genCount++; // Keep track of how many frames have been calculated
			System.out.println("\nFluidFrame: " + genCount);
		//	nextFrame.drawFrameToConsole();
			
			currentFrame = nextFrame;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (genCount > MAX_GENERATION) done = true;	 
			}
			else
			{try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
			}
			xStart = xEnd;
			xEnd = xEnd + 50;
		}
		WaterApp.reset();
		
	}
	
	//Simple method for button operation
	public static void pauseRun() {
		paused = true;
	}
	public static void resumeRun() {
		paused = false;
	}
	@Override
	public void run() {
		
		BGCanvas obsCanvas = new BGCanvas();
		addObserver(obsCanvas);
		runSim();
		
	}

}
