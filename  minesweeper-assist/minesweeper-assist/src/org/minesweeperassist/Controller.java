package org.minesweeperassist;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.minesweeperassist.MouseAction.ClickType;

/**
 * 控制器，控制鼠标动作
 * @author Isun
 *
 */
public class Controller {
	
	/**
	 * 有新信息待挖掘的格子
	 */
	private Queue<Point> newGrids;
	
	private Grid[][] grids;

	private int[][] groupId;
	
	private int curGroupId;
	
	private Random random = new Random();
	

	private static int dy[] = {-1, -1, 0, 1, 1, 1, 0, -1}; 
	private static int dx[] = {0, 1, 1, 1, 0, -1, -1, -1}; 

	
	public Controller() {
		newGrids = new LinkedList<Point>();
		grids = new Grid[MineFieldInfo.yGrids][MineFieldInfo.xGrids];
		groupId = new int[MineFieldInfo.yGrids][MineFieldInfo.xGrids];
		for (int y = 0; y < MineFieldInfo.yGrids; y++) {
			for (int x = 0; x < MineFieldInfo.xGrids; x++) {
				grids[y][x] = new Grid();
				grids[y][x].x = x;
				grids[y][x].y = y;
				grids[y][x].gridStatus = GridStatus.UNKNOWN;
			}
		}
	}
	

	/**
	 * 返回该组格子数目
	 * @param x
	 * @param y
	 * @return
	 */
	private int groupSize;
	private void findGroup(int x, int y) {
		groupId[y][x] = curGroupId;
		GridStatus thisStatus = grids[y][x].gridStatus;
		if (thisStatus != GridStatus.OPEN) {
			groupSize++;
		}
		for (int i = 0; i < 8; i++) {
			int tx = x + dx[i];
			int ty = y + dy[i];
			if (MineFieldInfo.isOut(tx, ty) || groupId[ty][tx] >= 0) {
				continue;
			}
			GridStatus thatStatus = grids[ty][tx].gridStatus;
			if (thatStatus == GridStatus.OPEN || thatStatus == GridStatus.UNKNOWN || thatStatus == GridStatus.NOT_MINE) {
				if (thisStatus != GridStatus.OPEN || thatStatus != GridStatus.OPEN) {
					findGroup(tx, ty);
				}
			}
		}
	}
	
	/**
	 * 获取下一步鼠标操作
	 * @return
	 */
	public List<MouseAction> getActions() {
		List<MouseAction> result = new ArrayList<MouseAction>();
		
		Point mouseScreenCoor = MouseInfo.getPointerInfo().getLocation();
		Point mouseGridCoord = MineFieldInfo.getGridCoord(mouseScreenCoor);
		
		for (int[] array : groupId) {
			Arrays.fill(array, -1);
		}
		curGroupId = 0;
		
		int minGroupSize = 0x7fffffff;
		int minGroupId = -1;
		for (int y = 0; y < MineFieldInfo.yGrids; y++) {
			for (int x = 0; x < MineFieldInfo.xGrids; x++) {
				if (groupId[y][x] < 0 && (grids[y][x].gridStatus == GridStatus.UNKNOWN || grids[y][x].gridStatus == GridStatus.NOT_MINE)) {
					groupSize = 0;
					findGroup(x, y);
					if (groupSize < minGroupSize) {
						minGroupSize = groupSize;
						minGroupId = curGroupId;
					}
					curGroupId++;
				}
			}
		}
		
		double maxValue = -10000.0;
		for (int y = 0; y < MineFieldInfo.yGrids; y++) {
			for (int x = 0; x < MineFieldInfo.xGrids; x++) {
				if (groupId[y][x] != minGroupId) {
					continue;
				}
				double value = -100000.0;
				List<MouseAction> tempResult = new ArrayList<MouseAction>();
				Point curGrid = new Point(x, y);
				if (grids[y][x].gridStatus == GridStatus.NOT_MINE) {
					value = -curGrid.distance(mouseGridCoord) / 4.0;
					tempResult.add(new MouseAction(curGrid, ClickType.LEFT));
				} else if (grids[y][x].gridStatus == GridStatus.OPEN && MainPanel.instance.flagBtn.isSelected()) {
					if (grids[y][x].isClear() && grids[y][x].mineNumber > 0) {
						value = -0.999 - curGrid.distance(mouseGridCoord) / 4.0;
						boolean existUncovered = false;
						for (int i = 0; i < 8; i++) {
							int tx = x + dx[i];
							int ty = y + dy[i];
							if (MineFieldInfo.isOut(tx, ty)) {
								continue;
							}
							if (grids[ty][tx].gridStatus == GridStatus.NOT_MINE) {
								value = value + 1.0;
								existUncovered = true;
							} else if (grids[ty][tx].gridStatus == GridStatus.MINE_NOT_FLAGGED) {
								value = value - 1.0;
								tempResult.add(new MouseAction(new Point(tx, ty), ClickType.RIGHT));
							}
						}
						if (existUncovered) {
							tempResult.add(new MouseAction(curGrid, ClickType.DOUBLE));
						} else {
							value = -100000.0;
						}
					}
				} else if (grids[y][x].gridStatus == GridStatus.UNKNOWN) {
					value = -100.00 - curGrid.distanceSq(mouseGridCoord) / 4.0;
					tempResult.add(new MouseAction(curGrid, ClickType.LEFT));
				}
				value += random.nextDouble() / 1000000.0;
				if (value > maxValue) {
					maxValue = value;
					result = tempResult;
				}
			}
		}
		
		for (MouseAction mouseAction : result) {
			if (mouseAction.clickType == ClickType.RIGHT) {
				grids[mouseAction.location.y][mouseAction.location.x].gridStatus = GridStatus.MINE_FLAGGED;
			}
		}
		
		return result;
	}
	


	public void informNewUncoverdGrid(int x, int y, Integer number) throws InterruptedException {
		if (number == null) {
			throw new RuntimeException();
		}
		grids[y][x].mineNumber = number;
		if (grids[y][x].gridStatus == GridStatus.UNKNOWN) {
			grids[y][x].confirmStatus(GridStatus.NOT_MINE);
		}
		grids[y][x].gridStatus = GridStatus.OPEN;
		newGrids.add(new Point(x, y));
	}
	
	public void think() throws InterruptedException {
		while (!newGrids.isEmpty()) {
//			System.out.println("newGrids size: " + newGrids.size());
			Point grid = newGrids.poll();
			if (grids[grid.y][grid.x].isClear()) {
				continue;
			}
			if (simpleJudge(grid)) {
				continue;
			}
			for (int y = grid.y - 2; y <= grid.y + 2; y++) {
				for (int x = grid.x - 2; x <= grid.x + 2; x++) {
					if ((x != grid.x || y != grid.y) && !MineFieldInfo.isOut(x, y) && grids[y][x].gridStatus == GridStatus.OPEN && !grids[y][x].isClear()) {
						advancedJudge(grid, new Point(x, y));
					}
				}
			}
		}
	}
	
	public void displayStatus() {
		System.out.println("Mine status:");
		for (int y = 0; y < MineFieldInfo.yGrids; y++) {
			for (int x = 0; x < MineFieldInfo.xGrids; x++) {
				if (grids[y][x].mineNumber != null) {
					System.out.print(grids[y][x].mineNumber);
				} else {
					System.out.print(" ");
				}
				System.out.print(
						grids[y][x].gridStatus == GridStatus.UNKNOWN ? " " :
						grids[y][x].gridStatus == GridStatus.NOT_MINE ? 1 :
						grids[y][x].gridStatus == GridStatus.MINE_FLAGGED ? 2 :
						grids[y][x].gridStatus == GridStatus.MINE_NOT_FLAGGED ? 3 :
						grids[y][x].gridStatus == GridStatus.OPEN ? 4 : 9 
				);
				System.out.print(" ");
			}
			System.out.println();
		}
	}
	
	private boolean simpleJudge(Point centerGrid) throws InterruptedException {
		int unknownMineCnts = grids[centerGrid.y][centerGrid.x].mineNumber;
		ArrayList<Grid> unknownGrids = new ArrayList<Grid>();
		for (int i = 0; i < 8; i++) {
			int x = centerGrid.x + dx[i];
			int y = centerGrid.y + dy[i];
			if (MineFieldInfo.isOut(x, y)) {
				continue;
			}
			GridStatus status = grids[y][x].gridStatus; 
			if (status == GridStatus.MINE_FLAGGED || status == GridStatus.MINE_NOT_FLAGGED) {
				unknownMineCnts--;
			} else if (status == GridStatus.UNKNOWN) {
				unknownGrids.add(grids[y][x]);
			}
		}
		if (unknownMineCnts == unknownGrids.size()) {
			for (Grid grid : unknownGrids) {
				grid.confirmStatus(GridStatus.MINE_NOT_FLAGGED);
			}
			return true;
		} else if (unknownMineCnts == 0) {
			for (Grid grid : unknownGrids) {
				grid.confirmStatus(GridStatus.NOT_MINE);
			}
			return true;
		} else {
			return false;
		}
	}
	
	private boolean advancedJudge(Point centerGrid1, Point centerGrid2) throws InterruptedException {
		ArrayList<Grid> g = new ArrayList<Grid>();
		ArrayList<Grid> g1 = new ArrayList<Grid>();
		ArrayList<Grid> g2 = new ArrayList<Grid>();
		int unknownMineCnts1 = grids[centerGrid1.y][centerGrid1.x].mineNumber;
		int unknownMineCnts2 = grids[centerGrid2.y][centerGrid2.x].mineNumber;

		for (int i = 0; i < 8; i++) {
			int x = centerGrid1.x + dx[i];
			int y = centerGrid1.y + dy[i];
			if (MineFieldInfo.isOut(x, y)) {
				continue;
			}
			if (grids[y][x].gridStatus == GridStatus.MINE_FLAGGED || grids[y][x].gridStatus == GridStatus.MINE_NOT_FLAGGED) {
				unknownMineCnts1--;
			} else if (grids[y][x].gridStatus == GridStatus.UNKNOWN) {
				if (Math.max(Math.abs(x - centerGrid2.x), Math.abs(y - centerGrid2.y)) > 1) {
					g1.add(grids[y][x]);
				} else {
					g.add(grids[y][x]);
				}
			}
		}
		for (int i = 0; i < 8; i++) {
			int x = centerGrid2.x + dx[i];
			int y = centerGrid2.y + dy[i];
			if (MineFieldInfo.isOut(x, y)) {
				continue;
			}
			if (grids[y][x].gridStatus == GridStatus.MINE_FLAGGED || grids[y][x].gridStatus == GridStatus.MINE_NOT_FLAGGED) {
				unknownMineCnts2--;
			} else if (grids[y][x].gridStatus == GridStatus.UNKNOWN) {
				if (Math.max(Math.abs(x - centerGrid1.x), Math.abs(y - centerGrid1.y)) > 1) {
					g2.add(grids[y][x]);
				}
			}
		}
		
		int lowerBound = Math.max(0, Math.max(unknownMineCnts1 - g1.size(), unknownMineCnts2 - g2.size()));
		int upperBound = Math.min(g.size(), Math.min(unknownMineCnts1, unknownMineCnts2));
		
		if (lowerBound < upperBound) {
			return false;
		}
		
		int commonMines = lowerBound;
		if (commonMines == g.size()) {
			for (Grid grid : g) {
				grid.confirmStatus(GridStatus.MINE_NOT_FLAGGED);
			}
		} else if (commonMines == 0) {
			for (Grid grid : g) {
				grid.confirmStatus(GridStatus.NOT_MINE);
			}
		}
		
		if (unknownMineCnts1 - commonMines == g1.size()) {
			for (Grid grid : g1) {
				grid.confirmStatus(GridStatus.MINE_NOT_FLAGGED);
			}
		} else if (unknownMineCnts1 - commonMines == 0) {
			for (Grid grid : g1) {
				grid.confirmStatus(GridStatus.NOT_MINE);
			}
		}
		
		if (unknownMineCnts2 - commonMines == g2.size()) {
			for (Grid grid : g2) {
				grid.confirmStatus(GridStatus.MINE_NOT_FLAGGED);
			}
		} else if (unknownMineCnts2 - commonMines == 0) {
			for (Grid grid : g2) {
				grid.confirmStatus(GridStatus.NOT_MINE);
			}
		}
		
		return true;
	}


	class Grid {
		int x;
		
		int y;
		
		/**
		 * 周围总雷数
		 */
		Integer mineNumber;
		
		/**
		 * 状态
		 */
		GridStatus gridStatus;
		
		/**
		 * 周围是否已无未知格子
		 */
		private boolean allClear;
		
		/**
		 * 周围是否已无未知格子
		 */
		boolean isClear() {
			if (this.allClear) {
				return true;
			}
			for (int i = 0; i < 8; i++) {
				int tx = x + dx[i];
				int ty = y + dy[i];
				if (!MineFieldInfo.isOut(tx, ty) && grids[ty][tx].gridStatus == GridStatus.UNKNOWN) {
					return false;
				}
			}
			this.allClear = true;
			return true;
		}
		
		/**
		 * 确定该格子的雷状态
		 * @param status
		 * @throws InterruptedException 
		 */
		void confirmStatus(GridStatus status) throws InterruptedException {
			if (status != GridStatus.NOT_MINE && status != GridStatus.MINE_NOT_FLAGGED) {
				throw new RuntimeException();
			}
			this.gridStatus = status;
			for (int i = 0; i < 8; i++) {
				Point p = new Point(x + dx[i], y + dy[i]);
				if (!MineFieldInfo.isOut(p) && grids[p.y][p.x].gridStatus == GridStatus.OPEN && !newGrids.contains(p)) {
					newGrids.add(p);
				}
			}
		}
	}
	
	enum GridStatus{
		UNKNOWN,            //未开，不知是否是雷
		NOT_MINE,           //未开，已知不是雷
		MINE_FLAGGED,       //未开，已知是雷，插了旗子
		MINE_NOT_FLAGGED,   //未开，已知是雷，未插旗子
		OPEN                //已开
	}
	

}

