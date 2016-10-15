package io.dimitris.minigen.delegates;

import java.io.File;

public class EgxGeneratorDelegate implements IFileGeneratorDelegate {

	@Override
	public boolean supports(File file) {
		return file.getName().endsWith("egx");
	}

	@Override
	public void generate(File template, File root) {
		System.out.println("Running " + template);
	}

}
