package org.minesweeperassist;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridNumberRecognizer {
	
	/**
	 * 0	开
	 * 1	开
	 * 2	开
	 * 3	开
	 * 4	开
	 * 5	开
	 * 6	开
	 * 7	开
	 * 8	开
	 * 9	Bomb!
	 * 10	未开
	 * 11	插旗
	 *
	 * -----
	 * |0|1|
	 * |- -|
	 * |2|3|
	 * -----
	 * 
	 */
	private Color gridSign[][] = new Color[12][4];
	
	private Robot robot;
	
	
	public GridNumberRecognizer(File file) throws IOException, AWTException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		Integer number = null, idx = 0;
		while (br.ready()) {
			String line = br.readLine();
			if (line.matches("\\d+")) {
				number = Integer.parseInt(line);
			} else {
				Matcher matcher = Pattern.compile("(\\d+)\\D+(\\d+)\\D+(\\d+)").matcher(line);
				if (matcher.find()) {
					int r = Integer.parseInt(matcher.group(1));
					int g = Integer.parseInt(matcher.group(2));
					int b = Integer.parseInt(matcher.group(3));
					gridSign[number][idx] = new Color(r, g, b);
					idx = (idx + 1) % 4;
				}
			}
		}
		br.close();
		fr.close();
		
		robot = new Robot();
	}
	
	/**
	 * 获取格子代表的数字
	 * @param gridX
	 * @param gridY
	 * @return 返回找打的数字
	 */
	public Integer tellGridNumber(int gridX, int gridY) {
		boolean blankFound = false;
		for (int y = MineFieldInfo.minY + gridY * MineFieldInfo.gridHeight; y < MineFieldInfo.minY + (gridY + 1) * MineFieldInfo.gridHeight - 1; y++) {
			for (int x = MineFieldInfo.minX + gridX * MineFieldInfo.gridWidth; x < MineFieldInfo.minX + (gridX + 1) * MineFieldInfo.gridWidth - 1; x++) {
				Color ltColor = robot.getPixelColor(x, y);
				Color rtColor = robot.getPixelColor(x + 1, y);
				Color lbColor = robot.getPixelColor(x, y + 1);
				Color rbColor = robot.getPixelColor(x + 1, y + 1);
				for (int i = 0; i <= 11; i++) {
					if (ltColor.equals(gridSign[i][0]) && rtColor.equals(gridSign[i][1]) && lbColor.equals(gridSign[i][2]) && rbColor.equals(gridSign[i][3])) {
						if (i > 0) {
							return i;
						} else {
							blankFound = true;
						}
					}
				}
			}
		}
		if (!blankFound) {
			throw new RuntimeException("格子识别异常!");
		}
		return 0;
	}
	
	

}
