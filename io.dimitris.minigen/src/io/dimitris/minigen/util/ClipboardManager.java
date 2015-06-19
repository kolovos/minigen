package io.dimitris.minigen.util;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ClipboardManager {
	
	protected Clipboard clipboard;
	
	public static void main(String[] args) throws Exception {
		ClipboardManager m = new ClipboardManager(Toolkit.getDefaultToolkit().getSystemClipboard());
		//System.out.println(m.getClipboardContents());
		//m.setClipboardContents("contents");
		System.out.println(m.getClipboardContents());
		Thread.sleep(10000);
		//m.pressCtrlC(new Robot());
		//m.setClipboard(Toolkit.getDefaultToolkit().getSystemClipboard());
		m.inspect();
		System.out.println(m.getClipboardContents());
	}
	
	public ClipboardManager(Clipboard clipboard) {
		this.clipboard = clipboard;
	}
	
	public void setClipboard(Clipboard clipboard) {
		this.clipboard = clipboard;
	}
	
	public void inspect() throws Exception {
		
		for (DataFlavor flavor : clipboard.getAvailableDataFlavors()) {
			try {
				System.out.println("  " + flavor + ": " + clipboard.getData(flavor));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected DataFlavor getPlainTextFlavour(Class c) {
		for (DataFlavor flavor : clipboard.getAvailableDataFlavors()) {
			if (flavor.getMimeType().startsWith("text/plain") && flavor.getRepresentationClass() == c) {
				return flavor;
			}
		}
		return null;
	}
	
	protected DataFlavor getStringFlavor() {
		return getPlainTextFlavour(String.class);
	}
	
	protected DataFlavor getInputStreamFlavor() {
		return getPlainTextFlavour(InputStream.class);
	}
	
	public String getClipboardContents() throws Exception {
		
		DataFlavor inputStreamFlavor = getInputStreamFlavor();
		if (inputStreamFlavor != null) {
			Object data = clipboard.getData(inputStreamFlavor);
			if (data instanceof StringReader) {
				StringReader stringReader = (StringReader) data;
				return new BufferedReader(stringReader).readLine();
			}
			else {
				InputStream inputStream = (InputStream) data;
				return new BufferedReader(new InputStreamReader(inputStream)).readLine();
			}
		}
		
		DataFlavor stringFlavor = getStringFlavor();
		if (stringFlavor != null) {
			return "" + clipboard.getData(stringFlavor);
		}
		return null;
	}
	
	public void setClipboardContents(String contents) {
	
		String script = "set the clipboard to \"" + contents.replace("\"", "\\\"") + "\"";
		
		try {
			AppleScriptEngine.getInstance().eval(script);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	
	}
	
	public void pressCtrlC(Robot robot) {
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_C);
		delay(robot);
		robot.keyRelease(KeyEvent.VK_META);
		robot.keyRelease(KeyEvent.VK_C);
		delay(robot);
	}
	
	public void delay(Robot robot) {
		delay(robot, 1);
	}
	
	public void delay(Robot robot, int times) {
		robot.delay(50 * times);
	}
	
}
