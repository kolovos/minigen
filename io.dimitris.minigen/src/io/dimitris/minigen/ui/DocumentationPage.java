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

import io.dimitris.minigen.Generator;
import io.dimitris.minigen.util.FileUtil;
import io.dimitris.minigen.util.StringUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.eol.execute.context.Variable;

public class DocumentationPage {
	
	protected File file;
	
	public DocumentationPage(File file) {
		this.file = file;
	}
	
	public File process() {
		String contents = FileUtil.getContents(file);
		List<String> calls = new ArrayList<String>();
		File temp = null;
		try {
			temp = File.createTempFile("minigen", ".html");
		} catch (IOException e) {
			// Not going to happen
		}
		int end = 0;
		int start = 0;
		
		//System.err.println(contents);
		String summary = null;
		if (contents.indexOf("***") > -1) {
			summary = StringUtil.escapeHTML(contents.substring(0, contents.indexOf("***")).trim());
		}
		else {
			summary = StringUtil.escapeHTML(contents);
		}
		
		while (start != -1 && end != -1) {
			start = contents.indexOf("***", end);
			end = contents.indexOf("***", start + 1);
			end = end + 1;
			//start = end + 3;
			if (start != -1 && end != -1) {
				int callStart = start;
				int callEnd = end + 2;
				calls.add(contents.substring(callStart, callEnd));
				//System.err.println("Found call: " + contents.substring(callStart, callEnd).trim());
			
				//Generator.INSTANCE.generate(call);
			}
		}
		
		ArrayList<String> examples = new ArrayList<String>();
		ArrayList<String> examplesGen = new ArrayList<String>();
		
		for (String call : calls) {
			String example = call.substring(3, call.length()-3);
			String generated = Generator.getInstance().generate(example);
			if (generated == null) { generated = "<error>";}
			examples.add(StringUtil.escapeHTML(example.trim()));
			examplesGen.add(StringUtil.escapeHTML(generated.trim()));
		}
		
		String docPageContents = "";
		
		try {
			EglTemplateFactoryModuleAdapter module = new EglTemplateFactoryModuleAdapter(new EglFileGeneratingTemplateFactory());
			module.parse(new File("resources/docgen.egl"));
			module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("summary", summary));
			module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("examples", examples));
			module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("examplesGen", examplesGen));
			//module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("file", temp.getAbsolutePath()));
			docPageContents = (String) module.execute();
			/*
			String ln = System.getProperty("line.separator");
			String newContents = "";
			for (String line : docPageContents.split(ln)) {
				newContents += line + "<br>";
			}
			docPageContents = newContents;*/
			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			FileWriter filewriter = new FileWriter(temp);
			filewriter.write(docPageContents);
			filewriter.flush();
			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return temp;
	}
	
	public String replace(String src, int from, int to, String replace) {
		return src.substring(0, from) + replace + src.substring(to);
	}
	
}
 