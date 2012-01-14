package org.minesweeperassist;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import org.minesweeperassist.MouseAction.ClickType;

/**
 * 控制器，控制鼠标动作
 * @author Isun
 *
 */
public class Controller extends Thread {
	
	/**
	 * y方向有多少格子
	 */
	private int yGrids;
	
	/**
	 * x方向有多少格子
	 */
	private int xGrids;
	
	/**
	 * 有信息待挖掘的格子
	 */
	private LinkedBlockingQueue<Point> newGrids;
	
	/**
	 * 格子上的数字
	 */
	private Integer[][] mineNumber;

	
	private Random random = new Random();
	
	public Controller() {
		newGrids = new LinkedBlockingQueue<Point>();
	}
	
	
	
	/**
	 * 初始化参数
	 */
	public void init() {
		yGrids = 16;
		xGrids = 30;
		mineNumber = new Integer[yGrids][xGrids];
	}

	
	/**
	 * 获取下一步鼠标操作
	 * @return
	 */
	public List<MouseAction> getActions() {
		List<MouseAction> result = new ArrayList<MouseAction>();
		result.add(new MouseAction(random.nextInt(xGrids), random.nextInt(yGrids), ClickType.RIGHT));
		return result;
	}
	
	@Override
	public void run() {
		try {
			think();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void informNewUncoverdGrid(Point point, Integer number) {
		newGrids.add(point);
		mineNumber[point.y][point.x] = number;
	}
	
	private void think() throws InterruptedException {
		while (true) {
			Point coord = newGrids.take();
			System.out.println("Get it!");
		}

	}



	public int getyGrids() {
		return yGrids;
	}
	public void setyGrids(int yGrids) {
		this.yGrids = yGrids;
	}
	public int getxGrids() {
		return xGrids;
	}
	public void setxGrids(int xGrids) {
		this.xGrids = xGrids;
	}
	

}
