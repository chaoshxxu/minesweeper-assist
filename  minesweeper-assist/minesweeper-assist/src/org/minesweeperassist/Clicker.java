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
	
	private Controller controller;
	private GridNumberRecognizer recognizer;
	private Robot robot;
	
	private boolean visited[][];
	
	private static int dy[] = {-1, -1, -1, 0, 0, 0, 1, 1, 1}; 
	private static int dx[] = {-1, 0, 1, -1, 0, 1, -1, 0, 1}; 
	
	public Clicker(Controller controller, GridNumberRecognizer recognizer) throws AWTException {
		this.controller = controller;
		this.recognizer = recognizer;
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
		MineFieldInfo.minX = curLocation1.x;
		MineFieldInfo.minY = curLocation1.y;
		
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
		MineFieldInfo.maxX = curLocation2.x;
		MineFieldInfo.maxY = curLocation2.y;
		
		MineFieldInfo.gridWidth = (MineFieldInfo.maxX - MineFieldInfo.minX + MineFieldInfo.xGrids / 2) / MineFieldInfo.xGrids;
		MineFieldInfo.gridHeight = (MineFieldInfo.maxY - MineFieldInfo.minY + MineFieldInfo.yGrids / 2) / MineFieldInfo.yGrids;

		visited = new boolean[MineFieldInfo.yGrids][MineFieldInfo.xGrids];
		
		System.out.println(MineFieldInfo.maxX - MineFieldInfo.minX);
		System.out.println(MineFieldInfo.maxY - MineFieldInfo.minY);
		System.out.println(MineFieldInfo.gridWidth);
		System.out.println(MineFieldInfo.gridHeight);
	}
	
	@Override
	public void run() {
		int cnt = 10000;
		while (cnt-- > 0) {
			List<MouseAction> actions = controller.getActions();
			for (MouseAction mouseAction : actions) {
				System.out.println(mouseAction.location.x + ", " + mouseAction.location.y);
				int screenX = MineFieldInfo.minX + MineFieldInfo.gridWidth * mouseAction.location.x + MineFieldInfo.gridWidth / 2;
				int screenY = MineFieldInfo.minY + MineFieldInfo.gridHeight * mouseAction.location.y + MineFieldInfo.gridHeight / 2;
				robot.mouseMove(screenX, screenY);
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
				detectNewGrids(mouseAction.location.x, mouseAction.location.y);
				robot.delay(5);
			}
		}
		
	}



	private void detectNewGrids(int sx, int sy) {
		for (int i = 0; i < 9; i++) {
			int tx = sx + dx[i];
			int ty = sy + dy[i];
			if (tx >= 0 && tx < MineFieldInfo.xGrids && ty >= 0 && ty < MineFieldInfo.yGrids && !visited[ty][tx]) {
				visited[ty][tx] = true;
				Integer number = recognizer.tellGridNumber(tx, ty);
				controller.informNewUncoverdGrid(tx, ty, number);
			}
		}
	}
	
}
