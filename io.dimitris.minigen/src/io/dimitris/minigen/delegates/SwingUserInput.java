package io.dimitris.minigen.delegates;

import java.util.Collection;

import javax.swing.JOptionPane;

import org.eclipse.epsilon.eol.exceptions.EolUserException;
import org.eclipse.epsilon.eol.userinput.AbstractUserInput;

public class SwingUserInput extends AbstractUserInput {

	@Override
	public Object choose(String arg0, Collection<?> arg1, Object arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object chooseMany(String arg0, Collection<?> arg1, Collection<?> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean confirm(String arg0, boolean arg1) throws EolUserException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void inform(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String prompt(String question, String default_) {
		return JOptionPane.showInputDialog(question, default_);
	}

	@Override
	public int promptInteger(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float promptReal(String arg0, float arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double promptReal(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

}
