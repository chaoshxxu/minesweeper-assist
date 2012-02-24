package org.minesweeperassist;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 格子识别逻辑
 * @author Isun
 *
 */
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
	private Color gridSign1[] = new Color[11];
	
	private Color gridSign2[] = new Color[11];

	private Point offset[] = new Point[11];
	
	private BufferedImage bufferedImage;
	
	private Robot robot;
	
	public GridNumberRecognizer(File file) throws IOException, AWTException {
		System.out.println(file.getAbsolutePath());
		Pattern pattern1 = Pattern.compile("\\D*(\\d+)\\D*");
		Pattern pattern2 = Pattern.compile("\\D*(\\d+)\\D+(\\d+)\\D*");
		Pattern pattern3 = Pattern.compile("\\D*(\\d+)\\D+(\\d+)\\D+(\\d+)\\D*");
		Pattern pattern4 = Pattern.compile("\\D*(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+(\\d+)\\D+(\\d+)\\D*");
		
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		Integer number = null;
		while (br.ready()) {
			String line = br.readLine();

			Matcher matcher4 = pattern4.matcher(line);
			if (matcher4.find()) {
				int r1 = Integer.parseInt(matcher4.group(1));
				int g1 = Integer.parseInt(matcher4.group(2));
				int b1 = Integer.parseInt(matcher4.group(3));
				int r2 = Integer.parseInt(matcher4.group(4));
				int g2 = Integer.parseInt(matcher4.group(5));
				int b2 = Integer.parseInt(matcher4.group(6));
				gridSign1[number] = new Color(r1, g1, b1);
				gridSign2[number] = new Color(r2, g2, b2);
				continue;
			}

			Matcher matcher3 = pattern3.matcher(line);
			if (matcher3.find()) {
				int r = Integer.parseInt(matcher3.group(1));
				int g = Integer.parseInt(matcher3.group(2));
				int b = Integer.parseInt(matcher3.group(3));
				gridSign1[number] = new Color(r, g, b);
				gridSign2[number] = new Color(r, g, b);
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
		int baseX = gridX * MineFieldInfo.gridWidth;
		int baseY = gridY * MineFieldInfo.gridHeight;

		Color sb = new Color(bufferedImage.getRGB(baseX + offset[10].x, baseY + offset[10].y));
		Point3D p = new Point3D(sb.getRed(), sb.getGreen(), sb.getBlue());
		Point3D l1 = new Point3D(gridSign1[10].getRed(), gridSign1[10].getGreen(), gridSign1[10].getBlue());
		Point3D l2 = new Point3D(gridSign2[10].getRed(), gridSign2[10].getGreen(), gridSign2[10].getBlue());
		System.out.println(p);
		System.out.println(l1);
		System.out.println(l2 + "\n");
		System.out.println(p.disToLineSq(l1, l2));
		
		for (int i = 10; i >= 0; i--) {
			Color factColor = new Color(bufferedImage.getRGB(baseX + offset[i].x, baseY + offset[i].y));
			if (almostSame(factColor, gridSign1[i], gridSign2[i])) {
				return i;
			}
		}
		return null;
	}

	/**
	 * 判断c是否处于c1和c2的渐变之间
	 * @param factColor
	 * @param color
	 * @return
	 */
	public boolean almostSame(Color c, Color c1, Color c2) {
		int threshold = 10;
		Point3D p = new Point3D(c.getRed(), c.getGreen(), c.getBlue());
		Point3D l1 = new Point3D(c1.getRed(), c1.getGreen(), c1.getBlue());
		Point3D l2 = new Point3D(c2.getRed(), c2.getGreen(), c2.getBlue());
		return p.disSq(l1) < threshold || p.disSq(l2) < threshold || p.disToLineSq(l1, l2) < threshold;
	}

	public void refresh() {
		int left = MineFieldInfo.ltOriginCoord.x;
		int top = MineFieldInfo.ltOriginCoord.y;
		int width = left + MineFieldInfo.gridWidth * MineFieldInfo.xGrids;
		int height = top + MineFieldInfo.gridHeight * MineFieldInfo.yGrids;
		bufferedImage = robot.createScreenCapture(new Rectangle(left, top, width, height));
	}

}

class Point3D {
	private double x;
	private double y;
	private double z;

	public Point3D(){}
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D sub(Point3D p) {
		return new Point3D(x - p.x, y - p.y, z - p.z);
	}

	public Point3D add(Point3D p) {
		return new Point3D(x + p.x, y + p.y, z + p.z);
	}
	
	public Point3D cross(Point3D p) {
		return new Point3D(y * p.z - z * p.y, z * p.x - x * p.z, x * p.y - y * p.x);
	}
	
	public double dot(Point3D p) {
		return (x * p.x + y * p.y + z * p.z);
	}
	
	public double lenSq() {
		return x * x + y * y + z * z;
	}
	
	public double disSq(Point3D p) {
		return (x - p.x) * (x - p.x) + (y - p.y) * (y - p.y) + (z - p.z) * (z - p.z);
	}
	
	//点到直线距离的平方
	public double disToLineSq(Point3D l1, Point3D l2){
		return this.sub(l1).cross(l2.sub(l1)).lenSq() / l1.disSq(l2);
	}
	
	public String toString() {
		return x + "," + y + "," + z;
	}
	
};
