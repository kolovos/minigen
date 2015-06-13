package io.dimitris.minigen;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListenerExample implements NativeKeyListener {
    
	boolean ctrlIsDown = false;
	
	public void nativeKeyPressed(NativeKeyEvent e) {
    	
		if (e.getModifiers() == NativeKeyEvent.CTRL_L_MASK && e.getKeyCode() == NativeKeyEvent.VC_SEMICOLON) {
			
		}
		
        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
        	try {
        		GlobalScreen.unregisterNativeHook();
        	}
        	catch (Exception ex) {
        		ex.printStackTrace();
        	}
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        //System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
        //System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
    }

    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
            LogManager.getLogManager().getLogger("org.jnativehook").setLevel(Level.OFF);
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new GlobalKeyListenerExample());
    }
}