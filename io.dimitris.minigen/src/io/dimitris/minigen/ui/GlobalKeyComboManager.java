package io.dimitris.minigen.ui;

import java.util.logging.Level;
import java.util.logging.LogManager;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyComboManager {
	
	public static GlobalKeyComboManager INSTANCE = new GlobalKeyComboManager();
	
	public GlobalKeyComboManager() {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {}
		LogManager.getLogManager().getLogger("org.jnativehook").setLevel(Level.OFF);
	}
	
	public void addGlobalHotKeyListener(final GlobalKeyComboListener listener) {
		GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
			
        	int state = 0;
        	        	
			@Override
			public void nativeKeyTyped(NativeKeyEvent e) {}
			
			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {
				
				if (state == 2 && e.getKeyCode() == listener.getKey()) state = 3;
				else if (state == 3 && e.getKeyCode() == listener.getModifier()) {
					state = 0;
					listener.keyComboPressed();
				}
				else state = 0;
				listener.keyComboStateChanged(state);
			}
			
			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {	
				if (state == 0 && e.getKeyCode() == listener.getModifier()) state = 1;
				else if (state == 1 && e.getKeyCode() == listener.getKey()) state = 2;
				else state = 0;	
				listener.keyComboStateChanged(state);
			}
			
		});
	}
		
	public void teardown() throws Exception {
		GlobalScreen.unregisterNativeHook();
	}
	
}
