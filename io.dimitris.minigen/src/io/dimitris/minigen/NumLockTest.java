package io.dimitris.minigen;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

public class NumLockTest {
	
	
	public static void main(String[] args) {
		
		new NumLockTest().test();
		
	}
	
	
	public void test() {
		setNumLock(false);
		for (int i=0;i<1000;i++)
		debugNumLock();
		/*
		setNumLock(true);
		sleep();
		debugNumLock();
		*/
	}
	
	public void sleep() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setNumLock(boolean on) {
		Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, on);
	}
	
	public void debugNumLock() {
		System.err.println("NumLock is " + Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_NUM_LOCK));
	}
}
