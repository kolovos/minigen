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

import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class VelocityGeneratorDelegate implements ITextGeneratorDelegate {

	public String generate(File template, String text, Dataset dataset)
			throws Exception {
		VelocityEngine engine = new VelocityEngine();
		
		VelocityContext context = new VelocityContext();

		context.put("dataset", dataset);
		context.put("rows", dataset.getRows());
		context.put("fields", dataset.getAllFields());
		context.put("text", text);
		
		StringWriter sw = new StringWriter();

		engine.evaluate(context, sw, "", new FileReader(template));
		
		return sw.toString();
	}

	public boolean supports(File file) {
		return file.getName().endsWith("vm");
	}

}
 