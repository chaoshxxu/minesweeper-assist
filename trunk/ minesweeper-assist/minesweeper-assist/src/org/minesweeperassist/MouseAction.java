package org.minesweeperassist;

import java.awt.Point;

/**
 * 鼠标操作
 * @author Isun
 *
 */
public class MouseAction {
	
	/**
	 * 操作坐标
	 */
	Point location;

	ClickType clickType;
	
	public MouseAction(int x, int y, ClickType clickType) {
		this.location = new Point(x, y);
		this.clickType = clickType;
	}
	
	public MouseAction(Point point, ClickType clickType) {
		this.location = point;
		this.clickType = clickType;
	}
	
	enum ClickType {
		LEFT, RIGHT, DOUBLE
	}
}
