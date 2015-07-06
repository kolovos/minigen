package io.dimitris.minigen.util;

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

	public void pressCommandShiftLeft() {
		
		// Release control
		//robot.keyPress(KeyEvent.VK_CONTROL);
		//delay(robot, 4);
		//robot.keyRelease(KeyEvent.VK_CONTROL);
		//robot.keyRelease(KeyEvent.VK_BACK_SLASH);
		
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_SHIFT);
		robot.keyPress(KeyEvent.VK_LEFT);
		delay(5);
		robot.keyRelease(KeyEvent.VK_LEFT);
		robot.keyRelease(KeyEvent.VK_SHIFT);
		robot.keyRelease(KeyEvent.VK_META);
	}
	
	public void pressCtrlC() {
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_C);
		delay();
		robot.keyRelease(KeyEvent.VK_META);
		robot.keyRelease(KeyEvent.VK_C);
		delay(5);
	}

	public void pressCtrlV() {
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_V);
		delay();
		robot.keyRelease(KeyEvent.VK_META);
		robot.keyRelease(KeyEvent.VK_V);
	}
	
	public void delay() {
		delay(1);
	}
	
	public void delay(int times) {
		robot.delay(50 * times);
	}
}
