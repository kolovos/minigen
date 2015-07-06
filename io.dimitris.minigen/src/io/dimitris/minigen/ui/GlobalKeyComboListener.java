package io.dimitris.minigen.ui;

public interface GlobalKeyComboListener {
	
	public void keyComboPressed();
	
	public int getKey();
	
	public int getModifier();
	
	public void keyComboStateChanged(int state);
}
