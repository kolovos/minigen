package io.dimitris.minigen.delegates;

import java.io.File;

public interface IFileGeneratorDelegate extends IGeneratorDelegate {
	
	public void generate(File template) throws Exception;
	
}
