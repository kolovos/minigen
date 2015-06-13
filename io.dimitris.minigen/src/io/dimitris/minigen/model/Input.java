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


public class Input {
	
	protected String template;
	protected String text;
	protected Dataset dataset;
	protected String leadingWhitespace;

	public void setLeadingWhitespace(String leadingWhitespace) {
		this.leadingWhitespace = leadingWhitespace;
	}

	public String getLeadingWhitespace() {
		return leadingWhitespace;
	}

	public Input(String str) {
		parse(str);
	}
	
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	
	public Dataset getDataset() {
		return dataset;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public String getText() {
		return text;
	}
	
	public static void main(String[] args) {
		Input i = new Input("foo");
		String leadingWhitespace = i.getLeadingWhitespace("   	text");
		System.err.println("#" + leadingWhitespace + "#" + leadingWhitespace.length());
	}
	
	public String getLeadingWhitespace(String input) {
		boolean inWhitespace = true;
		String whitespace = "";
		int index = 0;
		while (inWhitespace && index < input.length()) {
			char nextChar = input.charAt(index);
			if (nextChar == '\r' || nextChar == '\n') {
				whitespace = "";
				index++;
			}
			else if (Character.isWhitespace(nextChar)) {
				whitespace = whitespace + nextChar;
				index++;
			}
			else {
				inWhitespace = false;
			}
		}
		return whitespace;
	}
	
	public void parse(String input) {
		
		leadingWhitespace = getLeadingWhitespace(input);
		
		input = input.trim();
		
		int semiColumn = input.indexOf(":");
		
		if (semiColumn == -1) {
			template = input;
			text = input;
		}
		else {
			template = input.substring(0, semiColumn).trim();
			
			if (semiColumn < input.length()) {
				text = input.substring(semiColumn+1);
			}
		}
		
		dataset = new Dataset();
		int lineCount = 0;
		for (String line : text.trim().split("\n")) {
			line = line.trim();
			Row row = new Row();
			row.setText(line);
			row.setIndex(lineCount);
			lineCount++;
			for (String part : line.split(",")) {
				row.getFields().add(part.trim());
			}
			dataset.getRows().add(row);
		}
	
	}
	
}
 