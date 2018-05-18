package edu.neu.csye6200.fd;

public class FluidFrameAvg {

	private double[][] direction;
	private double[][] magnitude;
	private int size;
	
	public FluidFrameAvg(int size){
		direction = new double[size][size];
		magnitude = new double[size][size];
		this.size = size;
		initArray();
	}
	
	public void setFluidFrame(FluidFrame inFrame)
	{
		int stepSize = inFrame.size/size;
		System.out.println("Step Size :" +stepSize);
		
		for(int x=0;x<size;x++) {
			for(int y=0;y<size;y++) {
				inFrame.calcAvgRegion(y*stepSize,x*stepSize,stepSize);
				direction[x][y]= inFrame.getAvgDirection();
				magnitude[x][y]= inFrame.getAvgMagnitude();
			 }	
		}
	}
	
	
	private void initArray() {

		for(int x=0;x<size;x++) {
			for(int y=0;y<size;y++) {
				direction[x][y]= 0.0;
				magnitude[x][y]= 0.0;
			}
		}
		
		
	}
	
	public void directionDisp() {
		
		System.out.println(" Direction Value :");
		for(int x=0;x<size;x++) {
			for(int y=0;y<size;y++) {
				System.out.print(" " +direction[x][y]);
				
			}
			System.out.println("");
		}
		
	}
	 
	public void magnitudeDisp() {
		System.out.println(" Magnitude Value :");
		for(int x=0;x<size;x++) {
			for(int y=0;y<size;y++) {
				System.out.print(" " +magnitude[x][y]);
				
			}
			System.out.println("");
		}
		
	}

	public int getsize() {
		return size;
	}

	public double getDirection(int i, int j) {
		return direction[i][j];
	}

	public double getMagnitude(int i, int j) {
		return magnitude[i][j];
	}
}
