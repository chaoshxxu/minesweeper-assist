package org.minesweeperassist;

import java.awt.AWTException;
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
	
	private Double reactionTime;
	private double move1GridTime;
	private double move10GridTime;
	private double move20GridTime;
	private double speedRatio;
	
	private double a;
	private double b;
	private double c;
	
	private boolean visited[][];
	
	private static int dy[] = {-1, -1, -1, 0, 0, 0, 1, 1, 1}; 
	private static int dx[] = {-1, 0, 1, -1, 0, 1, -1, 0, 1}; 
	
	public Clicker(Controller controller, GridNumberRecognizer recognizer) throws AWTException {
		this.controller = controller;
		this.recognizer = recognizer;
		this.robot = new Robot();
		visited = new boolean[MineFieldInfo.yGrids][MineFieldInfo.xGrids];
		reactionTime = Double.parseDouble(MainPanel.instance.reactionTimeTF.getText());
		move1GridTime = Double.parseDouble(MainPanel.instance.move1GridTimeTF.getText());
		move10GridTime = Double.parseDouble(MainPanel.instance.move10GridTimeTF.getText());
		move20GridTime = Double.parseDouble(MainPanel.instance.move20GridTimeTF.getText());
		speedRatio = Double.parseDouble(MainPanel.instance.speedRatioTF.getText());

		double s1 = 1 * (MineFieldInfo.gridWidth + MineFieldInfo.gridHeight) / 2;
		double s2 = 10 * (MineFieldInfo.gridWidth + MineFieldInfo.gridHeight) / 2;
		double s3 = 20 * (MineFieldInfo.gridWidth + MineFieldInfo.gridHeight) / 2;
		double t1 = move1GridTime;
		double t2 = move10GridTime;
		double t3 = move20GridTime;
		a = ((s1 - s3) * (t1 - t2) - (s1 - s2) * (t1 - t3)) / ((s1 - s2) * (s1 - s3) * (s2 - s3));
		b = ((t1 - t2) - a * (s1 * s1 - s2 * s2)) / (s1 - s2);
		c = t1 - a * s1 * s1 - b * s1;
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
		recognizer.refresh();
		for (int y = 0; y < MineFieldInfo.yGrids; y++) {
			for (int x = 0; x < MineFieldInfo.xGrids; x++) {
				Integer number = recognizer.tellGridNumber(x, y); 
				System.out.println(x + " " + y + " - " + number);
				if (number == null) {
					throw new RuntimeException("Grid recognition exception");
				} else if (number != 10) {
					throw new RuntimeException("Please reset the game first");
				}
			}
		}
	}

	private Point lastPosition;
	private void moveTo(Point target) {
		Point screenCoord = MineFieldInfo.getScreenCoord(target);
		if (lastPosition == null) {
			robot.mouseMove(screenCoord.x, screenCoord.y);
			lastPosition = screenCoord;
			return;
		}
		double dist = lastPosition.distance(screenCoord);
		double time = a * dist * dist + b * dist + c;
		
		double v = 2 * dist / (1 + speedRatio) / time;
		double acc = (speedRatio - 1) * v / time;
//		System.out.println(dist + "px " + time + "ms");
		int stepNum = (int) Math.round(time / 10);
		for (int i = 1; i <= stepNum; i++) {
			double t = time * i / stepNum; 
			double offsetRatio = (v * t + acc * t * t / 2.0) / dist;
			Integer x = ((Long) Math.round(lastPosition.x + (screenCoord.x - lastPosition.x) * offsetRatio)).intValue();
			Integer y = ((Long) Math.round(lastPosition.y + (screenCoord.y - lastPosition.y) * offsetRatio)).intValue();
			robot.mouseMove(x, y);
			robot.delay(10);
		}
		robot.mouseMove(screenCoord.x, screenCoord.y);
		lastPosition = screenCoord;
	}
	private void letsMove() throws InterruptedException {
		moveTo(new Point(MineFieldInfo.xGrids / 2, MineFieldInfo.yGrids / 2));
		while (true) {
			List<MouseAction> actions = controller.getActions();
			if (actions.isEmpty()) {
				break;
			}
			for (MouseAction mouseAction : actions) {
				System.out.println(mouseAction.location.x + "," + mouseAction.location.y + " " + mouseAction.clickType);
				moveTo(mouseAction.location);
				if (mouseAction.clickType == ClickType.LEFT) {
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				} else if (mouseAction.clickType == ClickType.RIGHT) {
					robot.mousePress(InputEvent.BUTTON3_MASK);
					robot.mouseRelease(InputEvent.BUTTON3_MASK);
					continue;
				} else if (mouseAction.clickType == ClickType.DOUBLE) {
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mousePress(InputEvent.BUTTON3_MASK);
					robot.mouseRelease(InputEvent.BUTTON3_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				}
				robot.delay(reactionTime.intValue());
				recognizer.refresh();
				for (int i = 0; i < 9; i++) {
					exploreNewGrids(mouseAction.location.x + dx[i], mouseAction.location.y + dy[i]);
				}
				if (!visited[mouseAction.location.y][mouseAction.location.x]) {
					throw new RuntimeException("!");
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
