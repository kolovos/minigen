package io.dimitris.minigen.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class AppleScriptEngine {
	
	protected static AppleScriptEngine instance;
	protected ScriptEngine engine = null;
	
	public static AppleScriptEngine getInstance() {
		if (instance == null) {
			instance = new AppleScriptEngine();
		}
		return instance;
	}
	
	private AppleScriptEngine() {
		engine = new apple.applescript.AppleScriptEngine();
	}
	
	public void eval(String... lines) throws Exception {
		String script = "";
		for (String line : lines) {
			script = script + line + "\n";
		}
		try {
			engine.eval(script);
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println(script);
			throw ex;
		}
	}
	
	
}
