package io.dimitris.minigen.delegates;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.egl.dom.GenerationRule;
import org.eclipse.epsilon.egl.execute.context.IEglContext;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
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
		EgxModule module = new EgxModule(templateFactory) {
			@Override
			protected GenerationRule createGenerationRule(AST generationRuleAst) {
				GenerationRule superGenerationRule = super.createGenerationRule(generationRuleAst);
				
				if (generationRuleAst.getAnnotationsAst() != null) {
					return new GenerationRule() {
						public void generateAll(IEglContext context, EglTemplateFactory templateFactory, EgxModule module) throws EolRuntimeException {
							String template = (templateBlock == null) ? "" : templateBlock.execute(context, false);
							String target = targetBlock.execute(context, false);
							
							File templateFile = new File(((EglFileGeneratingTemplateFactory) templateFactory).resolveTemplate(template));
							File targetFile = new File(((EglFileGeneratingTemplateFactory) templateFactory).getOutputRoot(), target);
							
							try {
								FileUtils.copyFile(templateFile, targetFile);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						};
					};
				}
				else return superGenerationRule;
			}
		};
		module.parse(egx);
		//String selectedFile = null;
		//try {
		//	selectedFile = (String) appleScriptEngine.eval("tell application \"Finder\" \n set currentFile to (item 1 of (get selection)) \n return name of currentFile \n end tell");
		//}
		//catch (Exception ex) {}
		module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("dir", new File(rootDirectoryPath)));
		//module.getContext().getFrameStack().put(Variable.createReadOnlyVariable("file", new File(selectedFile)));
		module.execute();
		
	}

}
