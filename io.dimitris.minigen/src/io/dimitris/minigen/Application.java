/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/

package io.dimitris.minigen;

import io.dimitris.minigen.ui.Console;
import io.dimitris.minigen.ui.GlobalHotKey;
import io.dimitris.minigen.ui.GlobalHotKeyListener;
import io.dimitris.minigen.ui.JTrayIcon;
import io.dimitris.minigen.ui.OpenTemplatesFolderAction;
import io.dimitris.minigen.ui.ShowHelpAction;
import io.dimitris.minigen.ui.TemplateBrowser;
import io.dimitris.minigen.util.OperatingSystem;

import java.awt.Image;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import sun.awt.datatransfer.ClipboardTransferable;
import sun.awt.datatransfer.SunClipboard;
import net.java.plaf.windows.WindowsLookAndFeel;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class Application implements HotkeyListener {
	
	public static Application INSTANCE = new Application();
	
	protected SystemTray systemTray;
	protected JTrayIcon trayIcon;
	protected Image icon;
	protected JIntellitype intellitype;
	protected Console console;
	protected TemplateBrowser browser;
	protected ClipboardManager clipboardManager;
	
	public void shutdown() {
		try {
			GlobalHotKey.INSTANCE.teardown();
		} catch (Exception e) {}
		System.exit(0);
	}
		
	public void launch() {
		
		clipboardManager = new ClipboardManager(Toolkit.getDefaultToolkit().getSystemClipboard());
		
		try {
			if (OperatingSystem.isWindowsVista()) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			else if (OperatingSystem.isWindows()){
				UIManager.setLookAndFeel(new WindowsLookAndFeel());
			}
			else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (Exception e1) { 
			// Do nothing
		}
		
		if (SystemTray.isSupported()) {
			
			// Turn NUMLOCK off
			// Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_NUM_LOCK, false);
			
			systemTray = SystemTray.getSystemTray();

			JPopupMenu popup = new JPopupMenu();
			popup.add(new JMenuItem(new ShowConsoleAction()));
			popup.add(new JMenuItem(new RefreshAction()));
			popup.add(new JMenuItem(new ShowBrowserAction()));
			popup.add(new JMenuItem(new OpenTemplatesFolderAction()));
			popup.addSeparator();
			popup.add(new JMenuItem(new ShowHelpAction()));
			
			popup.addSeparator();
			
			JMenuItem exit = new JMenuItem("Exit");
			exit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					shutdown();
				}
			});
			
			popup.add(exit);
			//if (OperatingSystem.isWindows()) {
				icon = new ImageIcon("resources/application.png").getImage();
			//}
			//else {
			//	icon = new ImageIcon("resources/application24.png").getImage();
			//}
			
			trayIcon = new JTrayIcon(icon);
			
			if (OperatingSystem.isWindows()) {
				trayIcon.setToolTip("MiniGen: Press Ctrl+Alt+Q to invoke");
			}
			else {
				trayIcon.setToolTip("MiniGen: Press Ctrl+; to invoke");	
			}
			trayIcon.setJPopupMenu(popup);
			trayIcon.setImageAutoSize(true);
			
			trayIcon.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					showBrowser();
				}
				
			});
			
			console = Console.INSTANCE;
			browser = new TemplateBrowser();
			
			try {
				Generator.getInstance().generate("hello:Mitsos");
			}
			catch (Exception ex) {
				// Ignore
			}
			
			try {
				systemTray.add(trayIcon);
				GlobalHotKey.INSTANCE.setup();
				
				GlobalHotKey.INSTANCE.addGlobalHotKeyListener(new GlobalHotKeyListener() {
					public void hotKeyPressed() {
						run();
					}
				});
				
			} catch (Exception e) {
				shutdown();
			}
			
		}
	}
	
	public void showBrowser() {
		browser.setVisible(true);
	}
	
	public void showConsole() {
		//trayIcon.setImage(Toolkit.getDefaultToolkit().createImage("application.png"));
		console.appear();

	}
	
	class ShowBrowserAction extends AbstractAction {
		
		public ShowBrowserAction() {
			super("Show templates browser", new ImageIcon("resources/browser.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Show templates browser");
		}

		public void actionPerformed(ActionEvent actionevent) {
			showBrowser();
		}
		
	}
	
	class RefreshAction extends AbstractAction {
		
		public RefreshAction() {
			super("Refresh templates", new ImageIcon("resources/refresh.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Refreshes the templates");
		}

		public void actionPerformed(ActionEvent actionevent) {
			Generator.getInstance().refresh();
			browser.refresh();
		}
		
	}
	
	class ShowConsoleAction extends AbstractAction {
		
		public ShowConsoleAction() {
			super("Show console", new ImageIcon("resources/console.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Show console");
		}

		public void actionPerformed(ActionEvent actionevent) {
			showConsole();
		}
		
	}
	
	protected boolean isTextSelected() {
		try {
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			Object data = null;
			try {
				data = c.getData(DataFlavor.stringFlavor);
			}
			catch (Exception ex) {
				data = "";
			}
			String selectedText = getSelectedText();
			
			if (data.toString().compareTo(selectedText) != 0) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (Exception ex) {
			return false;
		}
	}
	
	protected String getSelectedText() {
		try {
			String oldData = null;
			Object data = null;
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			try {
				data = c.getData(DataFlavor.stringFlavor);
			}
			catch (Exception ex) {
				data = "";
			}
			
			oldData = data.toString();
			
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_C);
			delay(robot, 4);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_C);
			data = c.getData(DataFlavor.stringFlavor);

			c.setContents(new StringSelection(oldData), null);
			return data.toString();
		}
		catch (Exception ex) {
			return "";
		}
	}

	public void delay(Robot robot) {
		delay(robot, 1);
	}
	
	public void delay(Robot robot, int times) {
		robot.delay(50 * times);
	}
	
	public void run() {
		
		console.clear();
		String oldData = null;
		
		try {
			
			Robot robot = new Robot();
			
			oldData = clipboardManager.getClipboardContents();
			
			pressShiftHome(robot);	
			pressCtrlC(robot);
			
			String data = clipboardManager.getClipboardContents();
			System.out.println("Data: " + data);
			String generated = Generator.getInstance().generate(data.toString());
			
			if (generated == null) return;
			
			if (generated.trim().isEmpty()) {
				displayWarningMessage("Nothing generated", "The template was invoked with no errors, but did not generate any text.");
			}
			else {
				clipboardManager.setClipboardContents(generated);
				pressCtrlV(robot);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			clipboardManager.setClipboardContents(oldData);
		}
	}
	
	public void pressShiftHome(Robot robot) {
		
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
	
	public void pressCtrlC(Robot robot) {
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_C);
		delay(robot);
		robot.keyRelease(KeyEvent.VK_META);
		robot.keyRelease(KeyEvent.VK_C);
		delay(robot);
	}

	public void pressCtrlV(Robot robot) {
		robot.keyPress(KeyEvent.VK_META);
		robot.keyPress(KeyEvent.VK_V);
		delay(robot);
		robot.keyRelease(KeyEvent.VK_META);
		robot.keyRelease(KeyEvent.VK_V);
	}
	
	public static void main(String[] args) throws Exception {
		
		Application.INSTANCE.launch();
	}
	
	
	public void displayErrorMessage(String title, String message) {
		trayIcon.displayMessage(title, message, TrayIcon.MessageType.ERROR);
	}

	public void displayWarningMessage(String title, String message) {
		trayIcon.displayMessage(title, message, TrayIcon.MessageType.WARNING);
	}

	public void onHotKey(int id) {
		run();
	}
}
