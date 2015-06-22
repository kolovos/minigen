package io.dimitris.minigen.ui;

import java.awt.Robot;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;

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
			
			@Override
			public void nativeKeyTyped(NativeKeyEvent e) {}
			
			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {}
			
			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				if (e.getModifiers() == NativeKeyEvent.CTRL_L_MASK && e.getKeyCode() == key) {
					notifyListeners();
				}
			}
		});
	}
	
	protected void teardownMac() throws Exception {
		GlobalScreen.unregisterNativeHook();
	}
	
}
