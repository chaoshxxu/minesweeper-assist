package org.minesweeperassist;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Date;
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
		visited = new boolean[MineFieldInfo.yGrids][MineFieldInfo.xGrids];
	}
	
	@Override
	public void run() {
		try {
			checkIfNotPlayed();
			letsMove();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			controller.displayStatus();
			MainPanel.instance.startBtn.setEnabled(true);
		}
	}
	
	private void checkIfNotPlayed() {
		for (int y = 0; y < MineFieldInfo.yGrids; y++) {
			for (int x = 0; x < MineFieldInfo.xGrids; x++) {
				Integer number = recognizer.tellGridNumber(x, y); 
				if (number == null) {
					throw new RuntimeException("Grid recognition exception");
				} else if (number != 10) {
					throw new RuntimeException("Please reset the game first");
				}
			}
		}
	}

	private void letsMove() throws InterruptedException {
		while (true) {
			List<MouseAction> actions = controller.getActions();
			if (actions.isEmpty()) {
				break;
			}
			for (MouseAction mouseAction : actions) {
				System.out.println(mouseAction.location.x + "," + mouseAction.location.y);
				Point screenCoord = MineFieldInfo.getScreenCoord(mouseAction.location);
				robot.mouseMove(screenCoord.x, screenCoord.y);
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
				robot.delay(30);
				for (int i = 0; i < 9; i++) {
					exploreNewGrids(mouseAction.location.x + dx[i], mouseAction.location.y + dy[i]);
				}
			}
			controller.think();
		}
		
	}


	private void exploreNewGrids(int sx, int sy) throws InterruptedException {
		if (MineFieldInfo.isOut(sx, sy) || visited[sy][sx]) {
			return;
		}
		Integer number = recognizer.tellGridNumber(sx, sy);
//		System.out.println(sx + "," + sy + " - " + number);
		if (number == null) {
			throw new RuntimeException("Grid recognition exception");
		}
		if (number == 9) {
			throw new RuntimeException("Bomb!");
		}
		if (number == 10) {
			return;
		}
		controller.informNewUncoverdGrid(sx, sy, number);
		visited[sy][sx] = true;
		for (int i = 0; i < 9; i++) {
			exploreNewGrids(sx + dx[i], sy + dy[i]);
		}
	}
	
}
