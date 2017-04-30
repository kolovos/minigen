package io.dimitris.minigen.delegates;

import java.io.File;

import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.eol.execute.context.Variable;

import apple.applescript.AppleScriptEngine;

public class EgxGeneratorDelegate implements IFileGeneratorDelegate {
	
	public static void main(String[] args) throws Exception {
		EgxGeneratorDelegate delegate = new EgxGeneratorDelegate();
		delegate.generate(new File("/Users/dkolovos/git/minigen/io.dimitris.minigen/templates/latex/paper.egx"));
	}
	
	@Override
	public boolean supports(File file) {
		return file.getName().endsWith("egx");
	}

	@Override
	public void generate(File egx) throws Exception {
		
		AppleScriptEngine appleScriptEngine = new AppleScriptEngine();
		String rootDirectoryPath = (String) appleScriptEngine.eval("tell application \"Finder\" \n set currentDir to (target of front Finder window) as text \n return POSIX path of currentDir \n end tell");
		
		EglFileGeneratingTemplateFactory templateFactory = new EglFileGeneratingTemplateFactory();
		templateFactory.setOutputRoot(rootDirectoryPath);
		EgxModule module = new EgxModule(templateFactory);
		module.parse(egx);
		String selectedFile = null;
		try {
			selectedFile = (String) appleScriptEngine.eval("tell application \"Finder\" \n set currentFile to (item 1 of (get selection)) \n return name of currentFile \n end tell");
		}
		catch (Exception ex) {}
		module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("dir", new File(rootDirectoryPath)));
		module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("file", new File(selectedFile)));
		module.execute();
		
	}

}
