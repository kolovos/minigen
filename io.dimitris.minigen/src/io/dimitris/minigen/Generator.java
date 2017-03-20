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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.dimitris.minigen.delegates.EglGeneratorDelegate;
import io.dimitris.minigen.delegates.EgxGeneratorDelegate;
import io.dimitris.minigen.delegates.FreemarkerGeneratorDelegate;
import io.dimitris.minigen.delegates.IFileGeneratorDelegate;
import io.dimitris.minigen.delegates.IGeneratorDelegate;
import io.dimitris.minigen.delegates.ITextGeneratorDelegate;
import io.dimitris.minigen.delegates.JmfGeneratorDelegate;
import io.dimitris.minigen.delegates.VelocityGeneratorDelegate;
import io.dimitris.minigen.model.Input;
import io.dimitris.minigen.ui.SelectTemplateDialog;
import io.dimitris.minigen.util.NotificationEngine;

public class Generator {
	
	public HashMap<String, File> templates = new LinkedHashMap<String, File>();
	protected ArrayList<IGeneratorDelegate> delegates = new ArrayList<IGeneratorDelegate>();
	protected SelectTemplateDialog selectTemplateDialog = new SelectTemplateDialog();
	protected File root = new File("templates").getAbsoluteFile();
	
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
		delegates.add(new EgxGeneratorDelegate());
		delegates.add(new FreemarkerGeneratorDelegate());
		loadTemplates();
	}
	
	public IGeneratorDelegate getDelegate(String template) {
		File templateFile = templates.get(template);
		if (templateFile != null) {
			return getDelegate(templateFile);
		}
		return null;
	}
	
	public IGeneratorDelegate getDelegate(File file) {
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
		addTemplate(root);
	}
	
	public void addTemplate(File file) {
		if (file.isDirectory() && !file.isHidden()) {
			if (file == root || file.getParentFile().equals(root)) {
				for (File f : file.listFiles()) {
					addTemplate(f);
				}
			}
		}
		else {
			if (supports(file)) {
				String[] parts = file.getName().split("\\.");
				if (parts.length == 2) {
					templates.put(parts[0], file);
				}
			}
		}
	}
	
	public Map<String, ? extends Collection<String>> getTemplateGroups() {
		LinkedHashMap<String, ArrayList<String>> templateGroups = new LinkedHashMap<String, ArrayList<String>>();
		for (String template : templates.keySet()) {
			File templateFile = templates.get(template);
			File parentFile = templateFile.getParentFile();
			if (!templateGroups.containsKey(parentFile.getName())) {
				templateGroups.put(parentFile.getName(), new ArrayList<String>());
			}
			templateGroups.get(parentFile.getName()).add(template);
		}
		return templateGroups;
	}
	
	public Collection<String> getTemplates() {
		return templates.keySet();
	}
	
	protected boolean popup = false;
	public String generate(Input input) {
		File template = templates.get(input.getTemplate());
		
		if (template == null) {
			
			if (popup) {
				selectTemplateDialog.popup();
				try {
					Thread.sleep(300);
				}
				catch (Exception ex){}
				if (selectTemplateDialog.getSelectedTemplate() != null) {
					input = new Input(input.getLeadingWhitespace() + selectTemplateDialog.getSelectedTemplate() + ":" + input.getText());
					template = templates.get(input.getTemplate());
				}
				else {
					return null;
				}
			}
			else {
				NotificationEngine.getInstance().show("Template " + input.getTemplate()  + " not found", "Please add a template named " + input.getTemplate() + " under your templates folder and then refresh.");
				return null;
			}
		}
		
		String generated = null;
		
		try {
			IGeneratorDelegate delegate = getDelegate(template);
			
			if (delegate instanceof ITextGeneratorDelegate) {
				ITextGeneratorDelegate textGeneratorDelegate = (ITextGeneratorDelegate) delegate;
				generated = textGeneratorDelegate.generate(template, input.getText(), input.getDataset());
				
				StringBuffer buffer = new StringBuffer();
				String[] lines = generated.split(System.lineSeparator());
				int lineIndex = 0;
				for (String line : lines) {
					if (lineIndex < lines.length) {
						buffer.append(input.getLeadingWhitespace());
						buffer.append(line);
						buffer.append(System.lineSeparator());
					}
				}
				
				generated = buffer.toString();
			}
			else if (delegate instanceof IFileGeneratorDelegate) {
				IFileGeneratorDelegate fileGeneratorDelegate = (IFileGeneratorDelegate) delegate;
				fileGeneratorDelegate.generate(template);
			}
		}
		
		catch (Exception ex) {
			ex.printStackTrace();
			NotificationEngine.getInstance().show("Oh, snap!", ex.getMessage().replace("\t", " "));
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
 