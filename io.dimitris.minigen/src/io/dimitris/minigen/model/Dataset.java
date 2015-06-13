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

public class Dataset {
	
	protected ArrayList<Row> rows = new ArrayList<Row>();

	public ArrayList<Row> getRows() {
		return rows;
	}
	
	public ArrayList<String> getAllFields() {
		ArrayList<String> allFields = new ArrayList<String>();
		for (Row row : rows) {
			allFields.addAll(row.getFields());
		}
		return allFields;
	}
	
	public int maxRowSize() {
		int max = 0;
		for (Row row : rows) {
			if (row.getFields().size() > max) {
				max = row.getFields().size();
			}
		}
		return max;
	}
	
}
 