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

public interface IGeneratorDelegate {
	
	public boolean supports(File file);
	
	public String generate(File template, String text, Dataset dataset) throws Exception;

}
 