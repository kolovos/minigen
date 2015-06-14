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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public 	class ShowHelpAction extends AbstractAction {
	
	public ShowHelpAction() {
		super("Help", new ImageIcon("resources/help-browser.png"));
		putValue(AbstractAction.SHORT_DESCRIPTION, "Online help");
	}

	public void actionPerformed(ActionEvent actionevent) {
		BrowserLauncher.openURL("http://kolovos.wiki.sourceforge.net/MiniGen");
	}
	
}
 