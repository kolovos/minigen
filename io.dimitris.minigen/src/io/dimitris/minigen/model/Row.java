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

package io.dimitris.minigen.model;

import java.util.ArrayList;

public class Row {
	
	protected ArrayList<String> fields = new ArrayList<String>();
	protected int index;
	protected String text;

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	public ArrayList<String> getFields() {
		return fields;
	}
	
}
 