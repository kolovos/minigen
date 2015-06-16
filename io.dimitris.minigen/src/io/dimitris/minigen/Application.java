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
import io.dimitris.minigen.ui.OpenTemplatesFolderAction;
import io.dimitris.minigen.ui.ShowHelpAction;
import io.dimitris.minigen.ui.TemplateBrowser;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class Application {
	
	public static Application INSTANCE = new Application();
	
	protected SystemTray systemTray;
	protected TrayIcon trayIcon;
	protected Image icon;
	protected Console console;
	protected TemplateBrowser browser;
	protected ClipboardManager clipboardManager;
	protected KeyboardManager keyboardManager = new KeyboardManager();
	
	public void shutdown() {
		try {
			GlobalHotKey.INSTANCE.teardown();
		} catch (Exception e) {}
		System.exit(0);
	}
		
	public void launch() {
		
		clipboardManager = new ClipboardManager(Toolkit.getDefaultToolkit().getSystemClipboard());
		
		if (SystemTray.isSupported()) {
			
			systemTray = SystemTray.getSystemTray();

			final PopupMenu popup = new PopupMenu();
			popup.add(new ActionMenuItem(new ShowBrowserAction()));
			popup.add(new ActionMenuItem(new ShowConsoleAction()));
			popup.add(new ActionMenuItem(new RefreshAction()));
			popup.add(new ActionMenuItem(new OpenTemplatesFolderAction()));
			popup.addSeparator();
			popup.add(new ActionMenuItem(new ShowHelpAction()));
			popup.add(new ActionMenuItem(new ExitAction()));
			
			popup.addSeparator();
			
			trayIcon = new TrayIcon( new ImageIcon("resources/application.png").getImage());
			trayIcon.setToolTip("MiniGen: Press Ctrl+\\ to invoke");	
			trayIcon.setPopupMenu(popup);
			trayIcon.setImageAutoSize(true);
			
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
	
	class ShowBrowserAction extends AbstractAction {
		
		public ShowBrowserAction() {
			super("Show templates browser", new ImageIcon("resources/browser.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Show templates browser");
		}

		public void actionPerformed(ActionEvent actionevent) {
			browser.setVisible(true);
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
	
	class ExitAction extends AbstractAction {
		
		public ExitAction() {
			super("Exit");
			putValue(AbstractAction.SHORT_DESCRIPTION, "Refreshes the templates");
		}

		public void actionPerformed(ActionEvent actionevent) {
			shutdown();
		}
		
	}
	class ActionMenuItem extends MenuItem {
		
		public ActionMenuItem(final AbstractAction action) {
			super("" + action.getValue(AbstractAction.NAME));
			this.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					action.actionPerformed(null);
				}
			});
		}
		
	}
	
	class ShowConsoleAction extends AbstractAction {
		
		public ShowConsoleAction() {
			super("Show console", new ImageIcon("resources/console.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Show console");
		}

		public void actionPerformed(ActionEvent actionevent) {
			console.appear();
		}
		
	}
	/*
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
	}*/

	
	
	public void run() {
		
		console.clear();
		String oldData = null;
		
		try {
			
			oldData = clipboardManager.getClipboardContents();
			keyboardManager.pressShiftHome();	
			keyboardManager.pressCtrlC();
			
			String data = clipboardManager.getClipboardContents();
			String generated = Generator.getInstance().generate(data.toString());
			
			if (generated == null) return;
			
			if (generated.trim().isEmpty()) {
				displayWarningMessage("Nothing generated", "The template was invoked with no errors, but did not generate any text.");
			}
			else {
				clipboardManager.setClipboardContents(generated);
				keyboardManager.pressCtrlV();
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			clipboardManager.setClipboardContents(oldData);
		}
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
