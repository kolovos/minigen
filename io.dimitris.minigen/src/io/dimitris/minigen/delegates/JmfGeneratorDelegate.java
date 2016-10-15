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

package io.dimitris.minigen.delegates;

import io.dimitris.minigen.model.Dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.ArrayList;

public class JmfGeneratorDelegate implements ITextGeneratorDelegate {

	public String generate(File template, String text, Dataset dataset)
			throws Exception {
		String contents = "";
		BufferedReader br = new BufferedReader(new FileReader(template));
		String line = br.readLine();
		
		while (line != null) {
			contents = contents + line + "\r\n";
			line = br.readLine();
		}
		
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(0, text);
		fields.addAll(dataset.getAllFields());
		
		return MessageFormat.format(contents, fields.toArray());
	}

	public boolean supports(File file) {
		return file.getName().endsWith("jmf");
	}

}
 