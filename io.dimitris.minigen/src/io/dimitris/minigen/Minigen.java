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

import io.dimitris.minigen.delegates.IFileGeneratorDelegate;
import io.dimitris.minigen.delegates.IGeneratorDelegate;
import io.dimitris.minigen.delegates.ITextGeneratorDelegate;
import io.dimitris.minigen.ui.GlobalKeyComboListener;
import io.dimitris.minigen.ui.GlobalKeyComboManager;
import io.dimitris.minigen.ui.OpenTemplatesFolderAction;
import io.dimitris.minigen.ui.ShowHelpAction;
import io.dimitris.minigen.ui.TemplateBrowser;
import io.dimitris.minigen.util.AppleScriptEngine;
import io.dimitris.minigen.util.ClipboardManager;
import io.dimitris.minigen.util.NotificationEngine;
import io.dimitris.minigen.util.KeyboardManager;
import io.dimitris.minigen.util.StringComparator;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.eclipse.epsilon.egl.merge.output.Output;
import org.jnativehook.keyboard.NativeKeyEvent;

public class Minigen {
	
	protected SystemTray systemTray;
	protected TrayIcon trayIcon;
	protected Image icon;
	protected TemplateBrowser browser;
	protected ClipboardManager clipboardManager;
	protected KeyboardManager keyboardManager = new KeyboardManager();
	protected ActionMenuItem registerKeyboardShortcutsMenuItem;
	protected PopupMenu popupMenu;
	protected PopupMenu templatesMenu;
	
	public void shutdown() {
		try {
			GlobalKeyComboManager.INSTANCE.teardown();
		} catch (Exception e) {}
		System.exit(0);
	}
	
	public TrayIcon getTrayIcon() {
		return trayIcon;
	}
	
	public void launch() {
		
		clipboardManager = new ClipboardManager(Toolkit.getDefaultToolkit().getSystemClipboard());
		
		if (SystemTray.isSupported()) {
			
			systemTray = SystemTray.getSystemTray();
			GlobalKeyComboManager.INSTANCE.registerNativeHook();
			boolean nativeHookRegistered = GlobalKeyComboManager.INSTANCE.isNativeHookRegistered();
			
			popupMenu = new PopupMenu();
			templatesMenu = new PopupMenu("Templates");
			
			if (!nativeHookRegistered) {
				registerKeyboardShortcutsMenuItem = new ActionMenuItem(new RegisterKeyboardShortcutsAction());
				popupMenu.add(registerKeyboardShortcutsMenuItem);
			}
			
			ActionMenuItem replaceTextMenuItem = new ActionMenuItem(new ReplaceTextAction());
			popupMenu.add(replaceTextMenuItem);
			popupMenu.add(new ActionMenuItem(new SelectAndReplaceTextAction()));
			popupMenu.addSeparator();
			refreshTemplatesMenu();
			popupMenu.add(templatesMenu);
			popupMenu.add(new ActionMenuItem(new ShowBrowserAction()));
			popupMenu.add(new ActionMenuItem(new RefreshAction()));
			popupMenu.add(new ActionMenuItem(new OpenTemplatesFolderAction()));
			popupMenu.addSeparator();
			popupMenu.add(new ActionMenuItem(new ShowHelpAction()));
			popupMenu.add(new ActionMenuItem(new ExitAction()));
			
			popupMenu.addSeparator();
			
			trayIcon = new TrayIcon( new ImageIcon(new File("resources/menubar.png").getAbsolutePath()).getImage());
			
			trayIcon.setToolTip("Waiting for authorisation (Preferences->Security and Privacy->Privacy->Accessibility)...");
			trayIcon.setPopupMenu(popupMenu);
			trayIcon.setImageAutoSize(true);
			
			browser = new TemplateBrowser();
			
			try { Generator.getInstance().generate("lorem:10"); }
			catch (Exception ex) {}
			
			try {
				systemTray.add(trayIcon);
				if (nativeHookRegistered) {
					new RegisterKeyboardShortcutsAction().actionPerformed(null);
				}
			} catch (AWTException e) {
				shutdown();
			}
			
		}
	}
	
	protected void refreshTemplatesMenu() {
		templatesMenu.removeAll();
		
		List<String> subitems = new ArrayList<String>(Generator.getInstance().getTemplates());
		
		Collections.sort(subitems, new StringComparator());
		
		for (String template : subitems) {
			templatesMenu.add(new ActionMenuItem(new RunTemplateAction(template)));
		}
	}
	
	class RunTemplateAction extends AbstractAction {
		
		protected String template = null;
		
		public RunTemplateAction(String template) {
			super(template);
			this.template = template;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			run(template);
		}
	}
	
	class ReplaceTextAction extends AbstractAction {
		
		public ReplaceTextAction() {
			super("Replace Selected Text (Ctrl + .)");
		}

		public void actionPerformed(ActionEvent actionevent) {
			run(false);
		}
		
	}
	
	class SelectAndReplaceTextAction extends AbstractAction {
		
		public SelectAndReplaceTextAction() {
			super("Select Line and Replace Text (Ctrl + \\)");
		}

		public void actionPerformed(ActionEvent actionevent) {
			run(true);
		}
		
	}

	class ShowBrowserAction extends AbstractAction {
		
		public ShowBrowserAction() {
			super("Show Template Browser", new ImageIcon("resources/browser.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Show template browser");
		}

		public void actionPerformed(ActionEvent actionevent) {
			browser.setVisible(true);
			try {
				AppleScriptEngine.getInstance().eval("tell me to activate");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class RegisterKeyboardShortcutsAction extends AbstractAction {
		
		public RegisterKeyboardShortcutsAction() {
			super("Register Keyboard Shortcuts", new ImageIcon("resources/refresh.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Registers the keyboard shortcuts");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			GlobalKeyComboManager.INSTANCE.registerNativeHook();
			
			if (GlobalKeyComboManager.INSTANCE.isNativeHookRegistered()) {
				
				GlobalKeyComboManager.INSTANCE.addGlobalHotKeyListener(new GlobalKeyComboListener() {
					public void keyComboPressed() {
						Minigen.this.run(true);
					}
					
					@Override
					public int getKey() {
						return NativeKeyEvent.VC_BACK_SLASH;
					}
					
					@Override
					public int getModifier() {
						return NativeKeyEvent.VC_CONTROL_L;
					}
	
					@Override
					public void keyComboStateChanged(int state) {
						getTrayIcon().setImage(new ImageIcon(new File("resources/menubar" + state + ".png").getAbsolutePath()).getImage());
					}
					
				});
				
				GlobalKeyComboManager.INSTANCE.addGlobalHotKeyListener(new GlobalKeyComboListener() {
					
					@Override
					public void keyComboStateChanged(int state) {
						//getTrayIcon().setImage(new ImageIcon(new File("resources/menubar" + state + ".png").getAbsolutePath()).getImage());
					}
					
					@Override
					public void keyComboPressed() {
						Minigen.this.run(false);	
					}
					
					@Override
					public int getModifier() {
						return NativeKeyEvent.VC_CONTROL_L;
					}
					
					@Override
					public int getKey() {
						return NativeKeyEvent.VC_PERIOD;
					}
				});
				
				if (registerKeyboardShortcutsMenuItem != null) {
					popupMenu.remove(registerKeyboardShortcutsMenuItem);
				}
				trayIcon.setToolTip("Press Ctrl+\\ or Ctrl+");
			}
		}
		
	}
	
	class RefreshAction extends AbstractAction {
		
		public RefreshAction() {
			super("Refresh Templates", new ImageIcon("resources/refresh.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Refreshes the templates");
		}

		public void actionPerformed(ActionEvent actionevent) {
			Generator.getInstance().refresh();
			browser.refresh();
			refreshTemplatesMenu();
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
	
	public void run(String template) {
		
		IGeneratorDelegate delegate = Generator.getInstance().getDelegate(template);
		if (delegate instanceof ITextGeneratorDelegate) {
			run(template, false);
		}
		else if (delegate instanceof IFileGeneratorDelegate){
			Generator.getInstance().generate(template);
		}
		
	}
	
	public void run(boolean selectLine) {
		run(null, selectLine);
	}
	
	public void run(String template, boolean selectLine) {
		
		String oldData = null;
		
		try {
			
			oldData = clipboardManager.getClipboardContents();
			if (selectLine) keyboardManager.pressCommandShiftLeft();	
			keyboardManager.pressCtrlC();
			
			String data = clipboardManager.getClipboardContents();
			
			String input = data.toString();
			System.out.println("Input " + input);
			if (template != null) { input = template + ":" + input; }
			String generated = Generator.getInstance().generate(input);
			System.out.println("Output " + generated);
			if (generated == null) return;
			
			if (generated.trim().isEmpty()) {
				NotificationEngine.getInstance().show("Zilch generated", "The template executed without errors, but did not generate any text.");
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
		new Minigen().launch();
	}
	
}
