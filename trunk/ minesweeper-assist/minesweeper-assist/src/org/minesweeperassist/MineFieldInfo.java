package org.minesweeperassist;

import java.awt.Point;

public class MineFieldInfo {
	
	/**
	 * 左上格子的原点坐标
	 */
	public static Point ltOriginCoord;

	/**
	 * 右下格子的原点坐标
	 */
	public static Point rbOriginCoord;

	/**
	 * 每个格子y方向的像素数
	 */
	public static Integer gridHeight;
	
	/**
	 * 每个格子x方向的像素数
	 */
	public static Integer gridWidth;
	
	/**
	 * y方向有多少格子
	 */
	public static Integer yGrids;
	
	/**
	 * x方向有多少格子
	 */
	public static Integer xGrids;
	
	public static Point getScreenCoord(Point p) {
		int screenX = MineFieldInfo.ltOriginCoord.x + MineFieldInfo.gridWidth * p.x + MineFieldInfo.gridWidth / 2;
		int screenY = MineFieldInfo.ltOriginCoord.y + MineFieldInfo.gridHeight * p.y + MineFieldInfo.gridHeight / 2;
		return new Point(screenX, screenY);
	}
	
	public static Point getGridCoord(Point location) {
		int gridX = (location.x - MineFieldInfo.ltOriginCoord.x) / MineFieldInfo.gridWidth;
		int gridY = (location.y - MineFieldInfo.ltOriginCoord.y) / MineFieldInfo.gridHeight;
		return new Point(gridX, gridY);
	}
	
	public static boolean isOut(Point p) {
		return p.x < 0 || p.x >= xGrids || p.y < 0 || p.y >= yGrids;
	}

	public static boolean isOut(int x, int y) {
		return x < 0 || x >= xGrids || y < 0 || y >= yGrids;
	}



}
