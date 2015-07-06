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

package io.dimitris.minigen.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

public class Console extends JFrame {
	
	public static Console INSTANCE = new Console();
	
	protected JEditorPane textArea; 
	protected JToolBar toolbar;
	
	private Console() {
		super();
		setTitle("Minigen - Console");
		setIconImage(Toolkit.getDefaultToolkit().createImage("resources/application.png"));
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width - 350, d.height - 350, 250, 250);
		setAlwaysOnTop(true);
		getRootPane().setLayout(new BorderLayout());
		textArea = new JEditorPane();
		textArea.setEditorKit(new NumberedEditorKit());
		textArea.requestFocus();
		getRootPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
		textArea.setEditable(false);
		
		toolbar = new JToolBar();
		//toolbar.setFloatable(false);
		toolbar.setRollover(true);
		getRootPane().add(toolbar, BorderLayout.NORTH);
		toolbar.add(new ClearAction());
		toolbar.add(new AlwaysOnTopAction());
	}
	
	public void write (int i) {
		if ((char) i != '\r')
		textArea.setText(textArea.getText() + (char) i);
		
		textArea.setCaretPosition(textArea.getText().length() - 1);
	}
	
	public void clear() {
		textArea.setText("");
	}
	
	class AlwaysOnTopAction extends AbstractAction {
		
		public AlwaysOnTopAction() {
			super("AlwaysOnTopAction", new ImageIcon("resources/alwaysontop.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Always on top");
		}

		public void actionPerformed(ActionEvent actionevent) {
			setAlwaysOnTop(!isAlwaysOnTop());
		}
		
	}
	
	class ClearAction extends AbstractAction {
		
		public ClearAction() {
			super("ClearAction", new ImageIcon("resources/clear.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Clears the console");
		}

		public void actionPerformed(ActionEvent actionevent) {
			clear();
		}
		
	}
	
}
 