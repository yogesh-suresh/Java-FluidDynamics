/**
 * 
 */
package edu.neu.csye6200.fd;

public class BasicRule extends RuleA {

	/**
	 * 
	 */

	public BasicRule(int maxSize,boolean rule) {
		SIZE = maxSize;
		collisionRule = rule;
	}

	/*
	 * This is a steady state rule - nothing changes (non-Javadoc)
	 * 
	 * @see edu.neu.csye6200.fluid.RuleI#createNextCell(int)
	 */
	@Override
	public int createNextCell(int inVal, int nofPart) {
		int outVal = 0;
		if (nofPart == 2) {
			// System.out.println("2 part");
			switch (inVal) {
			case 0b001001:outVal = 0b100100;break;
			case 0b010010:outVal = 0b001001;break;
			case 0b100100:outVal = 0b010010;break;
			default:
				outVal = generalRule(inVal);
				break;
			}
		} else if (nofPart == 3) {
			// System.out.println("3 part");
			switch (inVal) {
			case 0b010101:outVal = inVal;break;
			case 0b101010:outVal = inVal;break;
			default:
				outVal = (inVal ^ 0b111111);
				break;
			}
		} else if (nofPart == 4) {
			// System.out.println("4 part");
			switch (inVal) {
			case 0b110110:outVal = 0b011011;break;
			case 0b011011:outVal = 0b101101;break;
			case 0b101101:outVal = 0b110110;break;
			default:
				outVal = generalRule(inVal);
				break;
			}
		} else
			outVal = generalRule(inVal);
		return outVal;
	}

	@Override
	public int[] inboundValChck(int inboundVal) {
		int a[] = new int[6];
		int temp = inboundVal;
		for (int i = 0; i < 6; i++) {
			if ((temp % 2) > 0)
				a[i] = 1;
			else
				a[i] = 0;
			temp = temp / 2;
		}

		return a;
	}

	@Override
	public int generalRule(int inVal) {
		int outIntVal = 0;
		for (int dir = 0; dir < 6; dir++) {
			if (ParticleCell.hasDirectionFlag(inVal, dir))
				outIntVal = ParticleCell.setFlag(outIntVal, ParticleCell.getOppositeDirection(dir));
		}
		return outIntVal;
	}

	@Override
	public boolean isBoundary(int x, int y, int size)  {
		if (x < 0)
			return true;
		if (x >= size)
			return true;
		if (y < 0)
			return true;
		if (y >= size)
			return true;
		if (FluidSimulator.platePresent) {
			if ((FluidSimulator.xStart <= x) && (x <= FluidSimulator.xEnd) && (FluidSimulator.yStart <= y)
					&& (y <= FluidSimulator.yEnd))
				return true;
		}
		return false;
	}
	
	
}
