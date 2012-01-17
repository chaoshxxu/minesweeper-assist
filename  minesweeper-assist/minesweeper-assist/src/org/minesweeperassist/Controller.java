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
	 * 有新信息待挖掘的格子对
	 */
	private LinkedBlockingQueue<PointPair> newGrids;
	
	/**
	 * 格子上的数字
	 */
	private Integer[][] mineNumber;

	
	private Random random = new Random();
	
	public Controller() {
		newGrids = new LinkedBlockingQueue<PointPair>();
	}
	
	
	
	/**
	 * 初始化参数
	 */
	public void init() {
		mineNumber = new Integer[MineFieldInfo.yGrids][MineFieldInfo.xGrids];
	}

	
	/**
	 * 获取下一步鼠标操作
	 * @return
	 */
	public List<MouseAction> getActions() {
		List<MouseAction> result = new ArrayList<MouseAction>();
		result.add(new MouseAction(random.nextInt(MineFieldInfo.xGrids), random.nextInt(MineFieldInfo.yGrids), ClickType.RIGHT));
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
	
	public void informNewUncoverdGrid(int x, int y, Integer number) {
		newGrids.add(new PointPair(new Point(x, y), null));
		mineNumber[y][x] = number;
	}
	
	private void think() throws InterruptedException {
		while (true) {
			PointPair pair = newGrids.take();
			System.out.println("Get it!");
			
			
		}

	}

	

}

class PointPair {
	public Point a;
	public Point b;
	
	public PointPair(Point a, Point b) {
		this.a = a;
		this.b = b;
	}
}
