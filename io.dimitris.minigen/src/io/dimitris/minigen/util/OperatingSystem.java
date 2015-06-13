package io.dimitris.minigen.util;

public class OperatingSystem {
	
	public static boolean isLinux() {
		return System.getProperty("os.name").indexOf("Linux") > -1;
	}
	
	public static boolean isWindows() {
		return System.getProperty("os.name").indexOf("Windows") > -1;
	}
	
	public static boolean isWindowsVista() {
		return System.getProperty("os.name").indexOf("Windows") > -1
			&& System.getProperty("os.name").indexOf("Vista") > -1;
	}
	
}
