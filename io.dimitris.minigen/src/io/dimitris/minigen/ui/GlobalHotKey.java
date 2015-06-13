package io.dimitris.minigen.ui;

import io.dimitris.minigen.Application;
import io.dimitris.minigen.GlobalKeyListenerExample;
import io.dimitris.minigen.util.OperatingSystem;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.melloware.jintellitype.JIntellitype;

import jxgrabkey.HotkeyListener;
import jxgrabkey.JXGrabKey;

public class GlobalHotKey {
	
	public static GlobalHotKey INSTANCE = new GlobalHotKey();
	protected ArrayList<GlobalHotKeyListener> listeners = new ArrayList<GlobalHotKeyListener>();
	protected Robot robot;
	
	public GlobalHotKey() {

	}
	
	protected void releaseHotKey() {
		if (OperatingSystem.isWindows()) {
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_ALT);
			robot.keyRelease(KeyEvent.VK_Q);			
		}
		else if (OperatingSystem.isLinux()) {
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
		else if (OperatingSystem.isLinux()) setupLinux();
		else setupMac();
	}
	
	public void teardown() throws Exception {
		if (OperatingSystem.isWindows()) teardownWindows();
		else if (OperatingSystem.isLinux()) teardownLinux();
		else teardownMac();
	}
	
	protected void setupMac() throws Exception {
		GlobalScreen.registerNativeHook();
		LogManager.getLogManager().getLogger("org.jnativehook").setLevel(Level.OFF);
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
			
			@Override
			public void nativeKeyTyped(NativeKeyEvent e) {}
			
			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {}
			
			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				if (e.getModifiers() == NativeKeyEvent.CTRL_L_MASK && e.getKeyCode() == NativeKeyEvent.VC_SEMICOLON) {
					notifyListeners();
				}
			}
		});
	}
	
	protected void teardownMac() throws Exception {
		GlobalScreen.unregisterNativeHook();
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
