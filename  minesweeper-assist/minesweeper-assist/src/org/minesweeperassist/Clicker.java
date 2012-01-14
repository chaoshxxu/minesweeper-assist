package org.minesweeperassist;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.List;

import org.minesweeperassist.MouseAction.ClickType;


/**
 * 点击实施者
 * @author Isun
 *
 */
public class Clicker extends Thread {
	
	/**
	 * 雷区的左端x坐标
	 */
	private int minX;

	/**
	 * 雷区的右端x坐标
	 */
	private int maxX;

	/**
	 * 雷区的上端y坐标
	 */
	private int minY;

	/**
	 * 雷区的下端y坐标
	 */
	private int maxY;
	
	/**
	 * 每个格子y方向的像素数
	 */
	private int gridHeight;
	
	/**
	 * 每个格子x方向的像素数
	 */
	private int gridWidth;

	
	private Controller controller;
	private Robot robot;
	
	public Clicker(Controller controller) throws AWTException {
		this.controller = controller;
		this.robot = new Robot();
	}
	
	
	public void init() throws InterruptedException {
		Point lastLocation, curLocation1, curLocation2;
		
		System.out.println("move to left-top");
		lastLocation = MouseInfo.getPointerInfo().getLocation();
		while (true) {
			Thread.sleep(1000);
			curLocation1 = MouseInfo.getPointerInfo().getLocation();
			if (curLocation1.equals(lastLocation)) {
				break;
			}
			System.out.println("different location, try again...");
			lastLocation = curLocation1;
		}
		minX = curLocation1.x;
		minY = curLocation1.y;
		
		System.out.println("move to right-bottom");
		lastLocation = MouseInfo.getPointerInfo().getLocation();
		while (true) {
			Thread.sleep(1000);
			curLocation2 = MouseInfo.getPointerInfo().getLocation();
			if (curLocation2.equals(lastLocation) && curLocation1.distance(curLocation2) > 100.0) {
				break;
			}
			System.out.println("different location, try again...");
			lastLocation = curLocation2;
		}
		maxX = curLocation2.x;
		maxY = curLocation2.y;
		
		gridWidth = (maxX - minX + controller.getxGrids() / 2) / controller.getxGrids();
		gridHeight = (maxY - minY + controller.getyGrids() / 2) / controller.getyGrids();
		System.out.println(maxX - minX);
		System.out.println(maxY - minY);
		System.out.println(gridWidth);
		System.out.println(gridHeight);
	}
	
	@Override
	public void run() {
		int cnt = 10000;
		while (cnt-- > 0) {
			List<MouseAction> actions = controller.getActions();
			for (MouseAction mouseAction : actions) {
				System.out.println(mouseAction.location.x + ", " + mouseAction.location.y);
				robot.mouseMove(minX + gridWidth * mouseAction.location.x + gridWidth / 2, minY + gridHeight * mouseAction.location.y + gridHeight / 2);
				if (mouseAction.clickType == ClickType.LEFT) {
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				} else if (mouseAction.clickType == ClickType.RIGHT) {
					robot.mousePress(InputEvent.BUTTON3_MASK);
					robot.mouseRelease(InputEvent.BUTTON3_MASK);
				} else if (mouseAction.clickType == ClickType.DOUBLE) {
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mousePress(InputEvent.BUTTON3_MASK);
					robot.mouseRelease(InputEvent.BUTTON3_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				}
				robot.delay(5);
			}
		}
		
	}



	public void setController(Controller controller) {
		this.controller = controller;
	}
}
