/**
 * 
 */
package edu.neu.csye6200.fd;

public abstract class RuleA {

	/**
	 * 
	 */
	protected int SIZE = 0;
    protected boolean collisionRule;
	public RuleA() {

	}
	
	// Method for new frame creation 
	public FluidFrame createNextFrame(FluidFrame inFrame) {
		FluidFrame nxtFrame = new FluidFrame(inFrame.getSize());

		for (int x = 0; x < inFrame.getSize(); x++) {
			for (int y = 0; y < inFrame.getSize(); y++) {

				int inboundVal = inFrame.getCellInValue(x, y); // Read all neighbors and create opposite inbound values
																// from their outbound ones
				int nextOutCelVal = inboundVal;
				// Collison prerequiste
				
				int a[] = new int[6];
				a = inboundValChck(inboundVal);
				int nofParticles = particleCount(a);
				
				if(collisionRule)
					nextOutCelVal = createNextCell(inboundVal, nofParticles);
				else
					nextOutCelVal = generalRule(inboundVal);
				
				// Wall Check
				if (isEgde(x, y, inFrame.getSize())) {
					
					nextOutCelVal = setReflections(nextOutCelVal, x, y, inFrame.getSize());
				}
				
				// Plate Check
				if (FluidSimulator.platePresent) {
					if (isPlateEdge(x, y)) {
						for (int dir = 0; dir < 6; dir++) {

							if ((y & 1) == 0) { // y is even
								switch (dir) {

								case 0:
									if (isBoundary(x - 1, y, SIZE))
										nextOutCelVal = updateReflection(nextOutCelVal, dir,
												ParticleCell.getOppositeDirection(dir));
									break; // Left
								case 1:
									if (isBoundary(x - 1, y - 1, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break; // UL
								case 2:
									if (isBoundary(x, y - 1, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break;// UR
								case 3:
									if (isBoundary(x + 1, y, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break; // Right
								case 4:
									if (isBoundary(x, y + 1, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break;// LR
								case 5:
									if (isBoundary(x - 1, y + 1, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break; // LL
								}
							} else { // y is odd
								switch (dir) {

								case 0:
									if (isBoundary(x - 1, y, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break;// Left
								case 1:
									if (isBoundary(x, y - 1, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break;// UL
								case 2:
									if (isBoundary(x + 1, y - 1, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break;// UR
								case 3:
									if (isBoundary(x + 1, y, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break;// Right
								case 4:
									if (isBoundary(x + 1, y + 1, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break;// LR
								case 5:
									if (isBoundary(x, y + 1, SIZE))
										;
									nextOutCelVal = updateReflection(nextOutCelVal, dir,
											ParticleCell.getOppositeDirection(dir));
									break;// LL
								}
							}
						}
					}

				}

				nxtFrame.setCellOutValue(x, y, nextOutCelVal);
			}
		}
		return nxtFrame;
	}

	private boolean isEgde(int x, int y, int size) {
		if (x <= 0)
			return true;
		if (x >= (size - 1))
			return true;
		if (y <= 0)
			return true;
		if (y >= (size - 1))
			return true;
		return false;
	}

	private boolean isPlateEdge(int x, int y) {
		if ((x == FluidSimulator.xStart - 1 || x == FluidSimulator.xEnd + 1)
				&& (y >= FluidSimulator.yStart && y <= FluidSimulator.yEnd))
			return true;
		if ((x == FluidSimulator.xStart || x == FluidSimulator.xEnd)
				&& (y == FluidSimulator.yStart - 1 || y == FluidSimulator.yEnd + 1))
			return true;
		int nofRows = (FluidSimulator.yEnd - FluidSimulator.yStart);

		int yS = FluidSimulator.yStart;
		int yE = FluidSimulator.yEnd;
		int xS = FluidSimulator.xStart;
		int xE = FluidSimulator.xEnd;

		if ((nofRows % 2 == 0) && (yS % 2 != 0)) {
			if ((x == xE + 1) && (y == yS - 1 || y == yE + 1))
				return true;
		}
		if ((nofRows % 2 != 0) && (yS % 2 != 0)) {
			if (((x == xS - 1) && (y == yE + 1)) || ((x == xE + 1) && (y == yS - 1)))
				return true;
		}
		if ((nofRows % 2 == 0) && (yS % 2 == 0)) {
			if ((x == xS - 1) && (y == yS - 1 || y == yE + 1))
				return true;
		}
		if ((nofRows % 2 != 0) && (yS % 2 == 0)) {
			if (((x == xS - 1) && (y == yS - 1)) || ((x == xE + 1) && (y == yE + 1)))
				return true;
		}
		return false;
	}

	private int setReflections(int nextOutCelVal, int x, int y, int size) {

		boolean topLeft = false, bottomLeft = false, topRight = false, bottomRight = false;
		boolean top = false, bottom = false, left = false, right = false;
		topLeft = isTopLeft(x, y);
		bottomLeft = isBottomLeft(x, y);
		topRight = isTopRight(x, y);
		bottomRight = isbottomRight(x, y);
		top = isTop(x, y);
		bottom = isBottom(x, y);
		left = isLeft(x, y);
		right = isRight(x, y);
		
		//Setting Outbound direction with request to position
		//Topright,Topleft,right,left,top,bottom,bottomleft,bottomright
		
		for (int dir = 0; dir < 6; dir++) {
			if ((y & 1) == 0) { // y is even
				switch (dir) {
				default:
				case 0:
					if (isBoundary(x - 1, y, size)) {
						if (bottomLeft)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 2);
						else if (topLeft)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 4);
						else if (left)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 3);
					}
					break; // Left
				case 1:
					if (isBoundary(x - 1, y - 1, size)) {
						if (topLeft)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 3);
						else if (topRight)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 5);
						else if (left)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 2);
						else if (top)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 5);
					}
					break; // UL
				case 2:
					if (isBoundary(x, y - 1, size)) {
						if (topRight)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 4);
						else if (top)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 4);
					}
					break; // UR
				case 3:
					if (isBoundary(x + 1, y, size)) {
						if (topRight)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 5);
						else if (bottomRight)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 1);
						else if (right)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 0);
					}
					break; // Right
				case 4:
					if (isBoundary(x, y + 1, size)) {
						if (bottomLeft)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 2);
						else if (bottomRight)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 0);
						else if (bottom)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 2);
					}
					break; // LR
				case 5:
					if (isBoundary(x - 1, y + 1, size)) {
						if (bottomLeft)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 1);
						else if (left)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 4);
						else if (bottom)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 1);
					}
					break; // LL
				}
			} else { // y is odd
				switch (dir) {
				default:
				case 0:
					if (isBoundary(x - 1, y, size)) {
						if (bottomLeft)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 2);
						else if (left)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 3);
					}
					break; // Left
				case 1:
					if (isBoundary(x, y - 1, size)) {
						if (top)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 5);
					}
					break; // UL
				case 2:
					if (isBoundary(x + 1, y - 1, size)) {
						if (right)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 1);
						else if (top)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 4);
					}
					break; // UR
				case 3:
					if (isBoundary(x + 1, y, size)) {
						if (bottomRight)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 1);
						else if (right)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 0);
					}
					break; // Right
				case 4:
					if (isBoundary(x + 1, y + 1, size)) {
						if (bottomRight)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 0);
						else if (right)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 5);
						else if (bottom)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 2);
					}
					break; // LR
				case 5:
					if (isBoundary(x, y + 1, size)) {
						if (bottomLeft)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 3);
						else if (bottomRight)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 1);
						else if (bottom)
							nextOutCelVal = updateReflection(nextOutCelVal, dir, 1);
					}
					break; // LL
				}

			}
			
		}

		return nextOutCelVal;
	}

	private boolean isRight(int x, int y) {
		if ((y > 0 && y < (SIZE - 1)) && x == (SIZE - 1))
			return true;
		return false;
	}

	private boolean isLeft(int x, int y) {
		if ((y > 0 && y < (SIZE - 1)) && x == 0)
			return true;
		return false;
	}

	private boolean isBottom(int x, int y) {
		if ((x > 0 && x < (SIZE - 1)) && y == (SIZE - 1))
			return true;
		return false;
	}

	private boolean isTop(int x, int y) {
		if ((x > 0 && x < (SIZE - 1)) && y == 0)
			return true;
		return false;
	}

	private boolean isbottomRight(int x, int y) {
		if (x == (SIZE - 1) && y == (SIZE - 1))
			return true;
		return false;
	}

	private boolean isTopRight(int x, int y) {
		if (x == (SIZE - 1) && y == 0)
			return true;
		return false;
	}

	private boolean isBottomLeft(int x, int y) {
		if (x == 0 && y == (SIZE - 1))
			return true;
		return false;
	}

	private boolean isTopLeft(int x, int y) {
		if (x == 0 & y == 0)
			return true;
		return false;
	}

	private int updateReflection(int nextOutCelVal, int dir, int res) {
		
		if (ParticleCell.hasDirectionFlag(nextOutCelVal, dir)) { 
			nextOutCelVal = ParticleCell.setFlag(nextOutCelVal, res);
			nextOutCelVal = ParticleCell.removeFlag(nextOutCelVal, dir);

		}
		return nextOutCelVal;
	}

	
	public abstract boolean isBoundary(int x, int y, int size);

	private int particleCount(int array[]) {
		int parVal = 0;
		for (int i = 0; i < 6; i++) {
			if (array[i] > 0)
				parVal++;
		}
		return parVal;
	}

	
	public abstract int createNextCell(int inVal, int nofPart);

	public abstract int[] inboundValChck(int inboundVal);

	public abstract int generalRule(int inVal);

}
