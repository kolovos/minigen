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

package io.dimitris.minigen.ui;


import io.dimitris.minigen.Generator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataListener;

//Originally a JDialog but changed to JFrame 
//because in Linux if a JDialog is closed the focus
//is not returned to the previous window
public class SelectTemplateDialog extends JFrame {
	
	protected JList list;
	protected JTextField field;
	protected JButton okButton;
	protected JButton previewButton;
	protected JButton cancelButton;
	protected String selectedTemplate;

	public void setSelectedTemplate(String selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}

	public String getSelectedTemplate() {
		return selectedTemplate;
	}
	
	public static void main(String[] args) throws Exception {
		SelectTemplateDialog d = new  SelectTemplateDialog();
		d.popup();
		System.err.println(d.getSelectedTemplate());
		System.exit(0);
	}
	
	public SelectTemplateDialog() {
		super();
		setTitle("MiniGen :: Select Template");
		field = new JTextField();
		field.addKeyListener(new FieldKeyListener());
		field.addKeyListener(new EscapeKeyListener());
		
		list = new JList();
		list.setCellRenderer(new SimpleCellRenderer());
		JRootPane root = getRootPane();
		root.setBorder(new EmptyBorder(8,8,8,8));
		root.setLayout(new BorderLayout());
		root.add(createLabeledComponent("Select a template (type in a few letters):", field), BorderLayout.NORTH);
		root.add(createLabeledComponent("Matching templates:", new JScrollPane(list)), BorderLayout.CENTER);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		root.add(createButtonsPanel(), BorderLayout.SOUTH);
		list.addKeyListener(new EscapeKeyListener());
	}
	
	public void popup() {
		list.setModel(new SimpleListModel(Generator.getInstance().getTemplates()));
		//list.setSelectedIndex(0);
		if (selectedTemplate != null) {
			list.setSelectedValue(selectedTemplate, true);
		}
		else {
			list.setSelectedIndex(0);
		}
		list.updateUI();
		
		//System.err.println("Upon popup: " + list.getSelectedValue());
		
		this.setBounds(100, 100, 300, 500);
		//this.setModal(true);
		this.setVisible(true);
		field.requestFocus();
		while (this.isVisible()) {
			try {
				Thread.sleep(500);
			}
			catch (Exception ex) {
				
			}
		}
	}
	
	public void disappear() {
		this.setVisible(false);
	}

	public JPanel createLabeledComponent(String label, JComponent component) {
		JPanel panel = new JPanel();
		JLabel jLabel = new JLabel();
		jLabel.setText(label);
		jLabel.setBorder(new EmptyBorder(0,0,8,0));
		panel.setLayout(new BorderLayout());
		panel.add(jLabel, BorderLayout.NORTH);
		panel.add(component, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(0,0,8,0));
		return panel;
	}
	
	public JPanel createButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		okButton = new JButton("   OK   ");
		//previewButton = new JButton("Preview >>");
		cancelButton = new JButton("Cancel");
		okButton.setSize(cancelButton.getSize().width, cancelButton.getSize().height);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(okButton);
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				ok();	
			}
			
		});
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				cancel();
			}
			
		});
		//buttonsPanel.add(previewButton);
		buttonsPanel.add(cancelButton);
		panel.add(buttonsPanel, BorderLayout.EAST);

		return panel;
	}
	
	class SimpleCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList arg0, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(arg0, value, index, isSelected, cellHasFocus);
			JLabel label = (JLabel) c;
			label.setBorder(new EmptyBorder(0,5,0,0));
			label.setIcon(new ImageIcon("resources/template.png"));
			return c;
		}
		
	}
	
	class EscapeKeyListener implements KeyListener {

		public void keyPressed(KeyEvent keyevent) {
			
		}
		
		public void keyReleased(KeyEvent keyEvent) {
			if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER && okButton.isEnabled()) {
				if (list.getSelectedValue() != null) {
					ok();
				}
			}
			else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
				cancel();
			}
		}

		public void keyTyped(KeyEvent keyevent) {

		}
		
	}

	public void ok() {
		setSelectedTemplate(list.getSelectedValue().toString());
		disappear();
	}
	
	public void cancel() {
		setSelectedTemplate(null);
		disappear();
	}
	
	class FieldKeyListener implements KeyListener {

		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_UP) {
				if (list.getSelectedIndex() >= 0) {
					list.setSelectedIndex(list.getSelectedIndex() - 1);
					list.ensureIndexIsVisible(list.getSelectedIndex());
				}
			}
			else if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
				list.setSelectedIndex(list.getSelectedIndex() + 1);
				list.ensureIndexIsVisible(list.getSelectedIndex());
			}
		}
		
		public void keyReleased(KeyEvent arg0) {
			
			if (arg0.getKeyCode() != KeyEvent.VK_ENTER && arg0.getKeyCode() != KeyEvent.VK_UP && arg0.getKeyCode() != KeyEvent.VK_DOWN) {
				list.updateUI();
				list.setSelectedIndex(0);
				okButton.setEnabled(list.getModel().getSize() > 0);
			}
		}

		public void keyTyped(KeyEvent arg0) {
			
		}
		
	}
	
	class SimpleListModel implements ListModel {
		
		protected Collection<? extends Object> collection;
		
		public SimpleListModel(Collection<? extends Object> collection) {
			this.collection = collection;
		}
		
		public void addListDataListener(ListDataListener l) {
			
		}
		
		public Collection<Object> filter() {
			ArrayList<Object> filtered = new ArrayList<Object>();
			if (field.getText().length() == 0) { 
				filtered.addAll(collection);
			}
			else {
				for (Object o : collection) {
					if (o.toString().toLowerCase().indexOf(field.getText().toLowerCase()) > -1) {
						filtered.add(o);
					}
				}
			}
			Collections.sort(filtered, new Comparator<Object>() {

				public int compare(Object arg0, Object arg1) {
					if (arg0 != null && arg1 != null) {
						return arg0.toString().compareTo(arg1.toString());
					}
					return -1;
				}
				
			});
			return filtered;
		}
		
		public Object getElementAt(int index) {
			ArrayList<Object> list = new ArrayList<Object>();
			list.addAll(filter());
			return list.get(index);
		}

		public int getSize() {
			return filter().size();
		}

		public void removeListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
 