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
import io.dimitris.minigen.ui.Console;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.eol.execute.context.Variable;

public class EglGeneratorDelegate implements IGeneratorDelegate {

	public String generate(File template, String text, Dataset dataset)
			throws Exception {
		
		
		EglTemplateFactoryModuleAdapter module = new EglTemplateFactoryModuleAdapter(new EglFileGeneratingTemplateFactory());
		
		module.parse(template);
		
		if (!module.getParseProblems().isEmpty()) {
			StringBuffer buffer = new StringBuffer();
			int i = 0;
			for (ParseProblem problem : module.getParseProblems()) {
				if (i != 0) buffer.append("\r\n");
				buffer.append(problem.toString());
				i++;
			}
			throw new Exception(buffer.toString());
		}
		
		module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("dataset", dataset));
		module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("rows", dataset.getRows()));
		module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("fields", dataset.getAllFields()));
		module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("text", text));
		
		module.getContext().setOutputStream(new PrintStream(new OutputStream(){

			@Override
			public void write(int i) throws IOException {
				Console.INSTANCE.write(i);
			}
			
		}));
		
		return (String) module.execute();
	}

	public boolean supports(File file) {
		return file.getName().endsWith("egl");
	}

}
 