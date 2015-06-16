package io.dimitris.minigen;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class KeyboardManager {
	
	protected Robot robot;
	
	public KeyboardManager() {
		try {
			robot = new Robot();
		}
		catch (Exception ex) {
			System.exit(-1);
		}
	}

	public void pressShiftHome() {
		
		// Release control
		robot.keyPress(KeyEvent.VK_CONTROL);
		delay(robot, 4);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_SHIFT);
		robot.keyPress(KeyEvent.VK_LEFT);
		delay(robot, 5);
		robot.keyRelease(KeyEvent.VK_LEFT);
		robot.keyRelease(KeyEvent.VK_SHIFT);
		robot.keyRelease(KeyEvent.VK_META);
	}
	
	public void pressCtrlC() {
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_C);
		delay(robot);
		robot.keyRelease(KeyEvent.VK_META);
		robot.keyRelease(KeyEvent.VK_C);
		delay(robot);
	}

	public void pressCtrlV() {
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_V);
		delay(robot);
		robot.keyRelease(KeyEvent.VK_META);
		robot.keyRelease(KeyEvent.VK_V);
	}
	
	public void delay(Robot robot) {
		delay(robot, 1);
	}
	
	public void delay(Robot robot, int times) {
		robot.delay(50 * times);
	}
}
