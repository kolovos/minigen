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

import io.dimitris.minigen.delegates.EglGeneratorDelegate;
import io.dimitris.minigen.delegates.FreemarkerGeneratorDelegate;
import io.dimitris.minigen.delegates.IGeneratorDelegate;
import io.dimitris.minigen.delegates.JmfGeneratorDelegate;
import io.dimitris.minigen.delegates.VelocityGeneratorDelegate;
import io.dimitris.minigen.model.Input;
import io.dimitris.minigen.ui.SelectTemplateDialog;
import io.dimitris.minigen.util.GrowlEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Generator {
	
	public HashMap<String, String> templates = new HashMap<String, String>();
	protected ArrayList<IGeneratorDelegate> delegates = new ArrayList<IGeneratorDelegate>();
	protected SelectTemplateDialog selectTemplateDialog = new SelectTemplateDialog();
	
	protected static Generator instance;
	
	public static Generator getInstance() {
		if (instance == null) {
			instance = new Generator();
		}
		return instance;
	}
	
	public ArrayList<IGeneratorDelegate> getDelegates() {
		return delegates;
	}
	
	public Generator() {
		delegates.add(new JmfGeneratorDelegate());
		delegates.add(new VelocityGeneratorDelegate());
		delegates.add(new EglGeneratorDelegate());
		delegates.add(new FreemarkerGeneratorDelegate());
		loadTemplates();
	}
	
	protected IGeneratorDelegate getDelegate(File file) {
		for (IGeneratorDelegate delegate : delegates) {
			if (delegate.supports(file)) {
				return delegate;
			}
		}
		return null;
	}
	
	public boolean supports(File file) {
		return getDelegate(file) != null;
	}
	
	public void refresh(){
		loadTemplates();
	}
	
	public void loadTemplates() {
		templates.clear();
		File file = new File("templates").getAbsoluteFile();
		addTemplate(file);
	}
	
	public void addTemplate(File file) {
		System.out.println("file -> " + file.getAbsolutePath() + " - " + file.exists());
		
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addTemplate(f);
			}
		}
		else {
			if (supports(file)) {
				String[] parts = file.getName().split("\\.");
				if (parts.length == 2) {
					templates.put(parts[0], file.getAbsolutePath());
				}
			}
		}
	}
	
	public Collection<String> getTemplates() {
		return templates.keySet();
	}
	
	protected boolean popup = false;
	public String generate(Input input) {
		String templatePath = templates.get(input.getTemplate());
		
		if (templatePath == null) {
			
			if (popup) {
				selectTemplateDialog.popup();
				try {
					Thread.sleep(300);
				}
				catch (Exception ex){}
				if (selectTemplateDialog.getSelectedTemplate() != null) {
					input = new Input(input.getLeadingWhitespace() + selectTemplateDialog.getSelectedTemplate() + ":" + input.getText());
					templatePath = templates.get(input.getTemplate());
				}
				else {
					return null;
				}
			}
			else {
				GrowlEngine.getInstance().show("Template " + input.getTemplate()  + " not found", "Please add a template named " + input.getTemplate() + " under your templates folder and then refresh.");
				return null;
			}
		}

		File template = new File(templatePath);
		
		String generated = null;
		
		try {
			IGeneratorDelegate delegate = getDelegate(template);
			
			if (delegate != null) {
				generated = delegate.generate(template, input.getText(), input.getDataset());
				
				String nl = System.getProperty("line.separator");
				StringBuffer buffer = new StringBuffer();
				//String generatedWithLeadingWhitespace = "";
				String[] lines = generated.split(nl);
				int lineIndex = 0;
				for (String line : lines) {
					if (lineIndex < lines.length) {
						buffer.append(input.getLeadingWhitespace());
						buffer.append(line);
						buffer.append(System.lineSeparator());
						//generatedWithLeadingWhitespace += 
						//	input.getLeadingWhitespace() + line + nl;
					}
				}
				
				generated = buffer.toString(); //generatedWithLeadingWhitespace;
				
			}
			
		}
		catch (Exception ex) {
			GrowlEngine.getInstance().show("Error invoking template: " + input.getTemplate(), ex.getLocalizedMessage());
		}
		return generated;		
	}
	
	public static void main(String[] args) {
		System.out.println(Generator.getInstance().getTemplates());
	}
	
	public String generate(String input) {
		return generate(new Input(input));
	}
	
}
 