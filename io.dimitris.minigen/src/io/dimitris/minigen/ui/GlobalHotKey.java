package io.dimitris.minigen.ui;

import io.dimitris.minigen.Application;
import io.dimitris.minigen.util.OperatingSystem;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.melloware.jintellitype.JIntellitype;

import jxgrabkey.HotkeyListener;
import jxgrabkey.JXGrabKey;

public class GlobalHotKey {
	
	public static GlobalHotKey INSTANCE = new GlobalHotKey();
	protected ArrayList<GlobalHotKeyListener> listeners = new ArrayList<GlobalHotKeyListener>();
	protected Robot robot;
	
	public static void main(String[] args) throws Exception {
		GlobalHotKey.INSTANCE.setup();
		final Robot robot = new Robot();
		GlobalHotKey.INSTANCE.addGlobalHotKeyListener(new GlobalHotKeyListener() {
			
			public void hotKeyPressed() {
				
				//robot.keyRelease(KeyEvent.VK_PERIOD);
				//robot.keyRelease(KeyEvent.VK_CONTROL);
				
				Application.INSTANCE.pressCtrlC(robot);
				
				JFrame dialog = new JFrame();
				dialog.setBounds(100,100,200,200);
				//dialog.setModal(true);
				dialog.setVisible(true);
				try {
					Thread.sleep(2000);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				dialog.setVisible(false);
				
				Application.INSTANCE.delay(robot, 2);
				
				Application.INSTANCE.pressCtrlV(robot);
				Application.INSTANCE.pressCtrlV(robot);
			}
			
		});
		while (1 > 0) {
			Thread.sleep(1000);
		}
	}
	
	public GlobalHotKey() {

	}
	
	protected void releaseHotKey() {
		if (OperatingSystem.isWindows()) {
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_ALT);
			robot.keyRelease(KeyEvent.VK_Q);			
		}
		else {
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_SEMICOLON);
		}

	}
	
	public void addGlobalHotKeyListener(GlobalHotKeyListener listener) {
		this.listeners.add(listener);
	}
	
	protected void notifyListeners() {
		for (GlobalHotKeyListener listener : listeners) {
			listener.hotKeyPressed();
		}
	}
	
	public void setup() throws Exception {
		robot = new Robot();
		if (OperatingSystem.isWindows()) setupWindows();
		else /*if (OperatingSystem.isLinux())*/ setupLinux();
	}
	
	public void teardown() {
		if (OperatingSystem.isWindows()) teardownWindows();
		else if (OperatingSystem.isLinux()) teardownLinux();
	}
	
	protected JIntellitype intellitype;
	
	protected void setupWindows() {
		intellitype = new JIntellitype();

		intellitype.registerHotKey(1, JIntellitype.MOD_ALT
				+ JIntellitype.MOD_CONTROL, KeyEvent.VK_Q);
		//intellitype.registerHotKey(1, JIntellitype.MOD_CONTROL, hotKey);
		
		intellitype.addHotKeyListener(new com.melloware.jintellitype.HotkeyListener() {
			public void onHotKey(int arg0) {
				releaseHotKey();
				notifyListeners();
			}
		});		
	}

	protected void teardownWindows() {
		intellitype.cleanUp();
	}
	
	protected void setupLinux() throws Exception {
		System.load(new File("libJXGrabKey.so").getCanonicalPath());
		//int key = KeyEvent.VK_Q, mask = KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK; //Conflicts on GNOME
		int key = KeyEvent.VK_SEMICOLON, mask = KeyEvent.CTRL_MASK;
		JXGrabKey.getInstance().registerAwtHotkey(1, mask, key);
		JXGrabKey.getInstance().addHotkeyListener(new HotkeyListener(){
			public void onHotkey(int arg0) {
				//No need to release key in Linux
				//releaseHotKey();
				notifyListeners();
			}
		});
	}
	
	protected void teardownLinux() {
		JXGrabKey.getInstance().cleanUp();
	}
	
}
