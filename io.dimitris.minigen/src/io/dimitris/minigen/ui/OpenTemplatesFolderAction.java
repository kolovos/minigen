package io.dimitris.minigen.ui;
import io.dimitris.minigen.util.AppleScriptEngine;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

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

public class OpenTemplatesFolderAction extends AbstractAction {
	
	public OpenTemplatesFolderAction() {
		super("Reveal Templates Folder", new ImageIcon("resources/templatesfolder.png"));
		putValue(AbstractAction.SHORT_DESCRIPTION, "Opens the templates folder");
	}

	public void actionPerformed(ActionEvent actionevent) {
		try {
			AppleScriptEngine.getInstance().eval(
				"set thePath to POSIX file \"" + new File("templates").getAbsolutePath() + "\"", 
				"tell application \"Finder\"",
				"	reveal thePath",
				"	activate",
				"end tell");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
}
 