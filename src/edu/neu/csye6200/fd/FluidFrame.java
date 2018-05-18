
package edu.neu.csye6200.fd;


public class FluidFrame {

	
	int frame[][] = null;
	int size = 16; // the current grid size
	boolean egdeFlag = false;
	boolean platePresent = false;
	double avgDirection = 0.0;
	double avgMagnitude = 0.0; 
	/**
	 * Constructor
	 */
	public FluidFrame(int size) {
		this.size = size;
		frame = new int[size][size]; // Dynamically build the grid based on the input size
	
	}

	
	/**
	 * How big is this frame
	 * @return the grid size (square)
	 */
    public int getSize() {
		return size;
	}


	/**
     * get the outbound particle state (direction of eminating particles)
     * @param x
     * @param y
     * @return a FluidCell integer value
     */
	public int getCellOutValue(int x, int y) {
		if (x < 0) {x = 0; egdeFlag = true;return 0; } 
		if (x >= size) { x = size-1;egdeFlag = true;return 0;} 
		if (y < 0) {y = 0;egdeFlag = true;return 0;} 
		if (y >= size) { y = size-1;egdeFlag = true;return 0;} 
		if(FluidSimulator.platePresent)
		{ 
			if((FluidSimulator.xStart <= x ) && (x <= FluidSimulator.xEnd) && 
					(FluidSimulator.yStart <= y ) && (y<=FluidSimulator.yEnd)) 
					return 0;			
		}
		return frame[x][y];
	}
	
	/**
	 * Set the outbound particle state after an evaluation
	 * @param x
	 * @param y
	 * @param val
	 */
	public void setCellOutValue(int x, int y, int val) {
		if (x < 0) return;
		if (x >= size) return;
		if (y < 0) return;
		if (y >= size) return;
		frame[x][y] = val;
	}
	
	/**
	 * For a specified direction, set the output flag, indicating an exiting particle
	 * @param x
	 * @param y
	 * @param direction
	 */
	public void addCellOutParticle(int x, int y, int direction) {
		int curVal = getCellOutValue(x,y);
		curVal = ParticleCell.setFlag(curVal, direction);
		setCellOutValue(x,y,curVal);
	}
	
	/**
	 * Calculate the inbound particles that are headed towards a cell
	 * Do this by evaluating all neighbor cells, and calculating the input vectors 
	 * @param x
	 * @param y
	 */
	public int getCellInValue(int x, int y) {
		
		// Walk through all surrounding cells, and get the input value (header towards us or not)
		// Add this to our value
		// return the summarized result
		int inVal = 0;
		// Walk through each direction
		for (int dir = 0; dir < 6; dir++) {
			// Get the cell in a given direction from our current cell
			int neighborOutCell = getNeighborCellOutValue(x, y, dir);
			// Does it have a particle in the opposite direction?
			if (ParticleCell.hasDirectionFlag(neighborOutCell, ParticleCell.getOppositeDirection(dir)))
				inVal = ParticleCell.setFlag(inVal, dir); // If so, then add that direction to our inValue
		}
		return inVal;
	}
	
	/**
	 * Based on a given position and direction, locate a neighbor cell using a supplied direction
	 * and return its outbound particle values
	 * @param x input frame x coordinate
	 * @param y input frame y coordinate
	 * @param direction the direction to look in
	 * @return the outbound particle value for the neighbor cell
	 */
	private int getNeighborCellOutValue(int x, int y, int direction) {
		if ((y & 1) == 0) { // y is even
			switch (direction) {
			  default:
			  case 0: return (getCellOutValue(x-1, y));   // Left
			  case 1: return (getCellOutValue(x-1, y-1)); // UL
			  case 2: return (getCellOutValue(x  , y-1)); // UR
			  case 3: return (getCellOutValue(x+1, y));   // Right
			  case 4: return (getCellOutValue(x  , y+1)); // LR
			  case 5: return (getCellOutValue(x-1, y+1)); // LL	
			}
		}
		else { // y is odd
			switch (direction) {
			  default:
			  case 0: return (getCellOutValue(x-1, y));   // Left
			  case 1: return (getCellOutValue(x  , y-1)); // UL
			  case 2: return (getCellOutValue(x+1, y-1)); // UR
			  case 3: return (getCellOutValue(x+1, y));   // Right
			  case 4: return (getCellOutValue(x+1, y+1)); // LR
			  case 5: return (getCellOutValue(x  , y+1));   // LL	
			}
		}
	}
	
	/**
	 * Fill up the current frame up a a specified percentage (i.e. 100% yields an average of one particle direction per cell)
	 * @param percent
	 */
	public void addRandomParticles(double percent) {
		if (percent > 1.0) percent = 1.0; // Don't allow us to fill all cells
		
		int total = size * size; // This is the maximum number of cells
		
		total *= percent;
		
		for (int i = 0; i < total; i++)
			addRandomParticle();
	}
	
	/**
	 * Add a single random particle - give it a direction value
	 */
	private void addRandomParticle() {
		double maxSize = size  ; // Well want a range from 0.00 to size.99. When integerized we'll get 0, 1, 2, ... size
		// Create a random X
		int x = (int) (Math.random() * maxSize);
		int y = (int) (Math.random() * maxSize);
		// Create a random y
		// Create a random direction
		int direction = (int) (Math.random() * 5.99);
//		int direction = 3; // Test - force all particle to move right
		boolean skipPlate =false;
		if(FluidSimulator.platePresent)
		{  //System.out.println("Plate is present Random ");
			if((FluidSimulator.xStart <= x ) && (x <= FluidSimulator.xEnd) && 
					(FluidSimulator.yStart <= y ) && (y<=FluidSimulator.yEnd)) 
					skipPlate=true;			
		}
	    if(!skipPlate)  
	    	addCellOutParticle(x,y,direction); // add it, or if the particle already exists, just overlay it
		
	}
	
	/**
	 * Draw the current frame to the console
	 */
	public void drawFrameToConsole() {
		char dispChar = ' ';
		for (int y = 0; y <getSize(); y++) {

			if ((y & 1) > 0) // y is odd if true
				System.out.print(" ");
			for (int x = 0; x < getSize(); x++) {
				
		       int cel = getCellOutValue(x, y);
		       System.out.print(cel + " ");
		       if (ParticleCell.hasDirectionFlag(cel, 0)) dispChar = '0';
		       else if (ParticleCell.hasDirectionFlag(cel, 1)) dispChar = '1';
		       else if (ParticleCell.hasDirectionFlag(cel, 2)) dispChar = '2';
		       else if (ParticleCell.hasDirectionFlag(cel, 3)) dispChar = '3';
		       else if (ParticleCell.hasDirectionFlag(cel, 4)) dispChar = '4';
		       else if (ParticleCell.hasDirectionFlag(cel, 5)) dispChar = '5';
		       else dispChar = '-';
		    	 
		      // System.out.print(dispChar + " ");
			}
			System.out.println(""); // Carriage return
		}
	}


	public void calcAvgRegion(int yBegin, int xBegin, int stepSize) {
		double sumX = 0.0;
		double sumY = 0.0;
		int yEnd = yBegin + stepSize;
		int xEnd = xBegin + stepSize;
		
		for (int y= yBegin;y < yEnd;y++) {
			for (int x=xBegin;x < xEnd;x++) {
				int cellVal = getCellInValue(x,y);
				if(ParticleCell.hasDirectionFlag(cellVal, 0)) {
					sumX += -1.0;
				}
				if(ParticleCell.hasDirectionFlag(cellVal, 1)) {
					sumX += -0.5;
					sumY += -0.87;
				}	
				if(ParticleCell.hasDirectionFlag(cellVal, 2)) {
					sumX +=  0.5;
					sumY += -0.87;
				}		
				if(ParticleCell.hasDirectionFlag(cellVal, 3)) {
					sumX += +1.0;
					
				}
				if(ParticleCell.hasDirectionFlag(cellVal, 4)) {
					sumX += 0.5;
					sumY += 0.87;
				}
				if(ParticleCell.hasDirectionFlag(cellVal, 5)) {
					sumX += -0.5;
					sumY += 0.87;
				}
			}
		}
		
		double divisor = stepSize*stepSize *3;
		
		double avgX = sumX / divisor;
		double avgY = sumY / divisor;
		
		avgMagnitude = Math.sqrt( avgX*avgX + avgY*avgY);
		avgDirection = Math.sqrt( avgX/avgY);
	}


	public double getAvgDirection() {		
		return avgDirection;
	}


	public double getAvgMagnitude() {
		return avgMagnitude;
	}


	


	
}
