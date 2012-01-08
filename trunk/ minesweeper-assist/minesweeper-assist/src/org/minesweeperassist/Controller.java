package org.minesweeperassist;

import java.util.Random;

public class Controller {
	
	int height;
	int width;
	
	/**
	 * 
	 * @return
	 */
	public Integer[] getMoves() {
		Random random = new Random();
		return new Integer[] {
				random.nextInt(height),
				random.nextInt(width),
				0
		};
	}

}
