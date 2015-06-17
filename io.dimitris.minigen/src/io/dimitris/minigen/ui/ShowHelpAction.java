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

import io.dimitris.minigen.AppleScriptEngine;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public 	class ShowHelpAction extends AbstractAction {
	
	public ShowHelpAction() {
		super("Help", new ImageIcon(new File("resources/help-browser.png").getAbsolutePath()));
		putValue(AbstractAction.SHORT_DESCRIPTION, "Online help");
	}

	public void actionPerformed(ActionEvent actionevent) {
		try {
			AppleScriptEngine.getInstance().eval("open location \"https://github.com/kolovos/minigen\"");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
}
 