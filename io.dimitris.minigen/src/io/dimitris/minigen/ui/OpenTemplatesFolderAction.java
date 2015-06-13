package io.dimitris.minigen.ui;
import io.dimitris.minigen.util.OperatingSystem;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

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
		super("Open templates folder", new ImageIcon("resources/templatesfolder.png"));
		putValue(AbstractAction.SHORT_DESCRIPTION, "Opens the templates folder");
	}

	public void actionPerformed(ActionEvent actionevent) {
		try {
			String command = null;
			if (OperatingSystem.isWindows()) {
				command = "explorer";
			}
			if (OperatingSystem.isLinux()) {
				command = "xdg-open";
			}
			Runtime.getRuntime().exec(command + " " + new File("templates").getAbsolutePath());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
 