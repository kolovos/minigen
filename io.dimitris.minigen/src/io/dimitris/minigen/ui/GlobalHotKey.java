package io.dimitris.minigen.ui;

import io.dimitris.minigen.Application;

import java.awt.Robot;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.swing.ImageIcon;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalHotKey {
	
	public static GlobalHotKey INSTANCE = new GlobalHotKey();
	protected ArrayList<GlobalHotKeyListener> listeners = new ArrayList<GlobalHotKeyListener>();
	protected Robot robot;
	protected int key = NativeKeyEvent.VC_BACK_SLASH;
	
	public GlobalHotKey() {

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
		setupMac();
	}
	
	public void teardown() throws Exception {
		teardownMac();
	}
	
	protected void setupMac() throws Exception {
		GlobalScreen.registerNativeHook();
		LogManager.getLogManager().getLogger("org.jnativehook").setLevel(Level.OFF);
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
			
        	int stage = 0;
        	        	
			@Override
			public void nativeKeyTyped(NativeKeyEvent e) {}
			
			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {
				
				if (stage == 2 && e.getKeyCode() == key) stage = 3;
				else if (stage == 3 && e.getKeyCode() == NativeKeyEvent.VC_CONTROL_L) {
					stage = 0;
					notifyListeners();
				}
				else stage = 0;
				updateIcon();
				
			}
			
			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {	
				if (stage == 0 && e.getKeyCode() == NativeKeyEvent.VC_CONTROL_L) stage = 1;
				else if (stage == 1 && e.getKeyCode() == key) stage = 2;
				else stage = 0;	
				updateIcon();
			}
			
			protected void updateIcon() {
				Application.INSTANCE.getTrayIcon().setImage(new ImageIcon(new File("resources/process" + stage + ".png").getAbsolutePath()).getImage());
			}
		});
	}
	
	protected void teardownMac() throws Exception {
		GlobalScreen.unregisterNativeHook();
	}
	
}
