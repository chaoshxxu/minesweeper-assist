package org.minesweeperassist;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridNumberRecognizer {
	
	/**
	 * 0	底色
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
	 *
	 */
	private Color gridSign[] = new Color[11];
	
	private Point offset[] = new Point[11];
	
	private Robot robot;
	
	public GridNumberRecognizer(File file) throws IOException, AWTException {
		System.out.println(file.getAbsolutePath());
		Pattern pattern1 = Pattern.compile("\\D*(\\d+)\\D*");
		Pattern pattern2 = Pattern.compile("\\D*(\\d+)\\D+(\\d+)\\D*");
		Pattern pattern3 = Pattern.compile("\\D*(\\d+)\\D+(\\d+)\\D+(\\d+)\\D*");
		
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		Integer number = null;
		while (br.ready()) {
			String line = br.readLine();

			Matcher matcher3 = pattern3.matcher(line);
			if (matcher3.find()) {
				int r = Integer.parseInt(matcher3.group(1));
				int g = Integer.parseInt(matcher3.group(2));
				int b = Integer.parseInt(matcher3.group(3));
				gridSign[number] = new Color(r, g, b);
				continue;
			}

			Matcher matcher2 = pattern2.matcher(line);
			if (matcher2.find()) {
				int x = Integer.parseInt(matcher2.group(1));
				int y = Integer.parseInt(matcher2.group(2));
				offset[number] = new Point(x, y);
				continue;
			}

			Matcher matcher1 = pattern1.matcher(line);
			if (matcher1.find()) {
				number = Integer.parseInt(matcher1.group(1));
				continue;
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
		for (int i = 10; i >= 0; i--) {
			int baseX = MineFieldInfo.ltOriginCoord.x + gridX * MineFieldInfo.gridWidth;
			int baseY = MineFieldInfo.ltOriginCoord.y + gridY * MineFieldInfo.gridHeight;
			Color factColor = robot.getPixelColor(baseX + offset[i].x, baseY + offset[i].y);
			if (factColor.equals(gridSign[i])) {
				return i;
			}
		}
		return null;
	}

}
