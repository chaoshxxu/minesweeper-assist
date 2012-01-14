package org.minesweeperassist;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;

public class Client {
	
	Controller controller;
	Clicker clicker;
	
	
	public void work() throws AWTException, InterruptedException {
		controller = new Controller();
		clicker = new Clicker(controller);
		
		controller.init();
		clicker.init();
		
		controller.start();
		clicker.start();
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		try {
			client.work();
		} catch (Exception e) {
			System.out.println("坑爹了, something is wrong!");
			e.printStackTrace();
		}
	}
	

}
