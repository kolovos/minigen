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
import io.dimitris.minigen.util.FileUtil;
import io.dimitris.minigen.util.OperatingSystem;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.StyledEditorKit;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class TemplateBrowser extends JFrame{

	protected JTree tree;
	protected JTabbedPane tabbedPane;
	protected JEditorPane docArea;
	protected JEditorPane codeArea;
	//protected JEditorPane sandboxArea;
	protected JToolBar toolbar;
	
	public TemplateBrowser() {
		docArea = new JEditorPane();
		docArea.setEditable(false);
		//docArea.setEditorKit(new HTMLEditorKit());
		docArea.setEditorKit(new StyledEditorKit());
		
		//sandboxArea = new JEditorPane();
		//sandboxArea.setEditorKit(new NumberedEditorKit());
		
		codeArea = new JEditorPane();
		codeArea.setEditable(false);
		codeArea.setEditorKit(new NumberedEditorKit());
		
		tree = new JTree();
		tabbedPane = new JTabbedPane();
		setTitle("MiniGen :: Browser");
		setIconImage(Toolkit.getDefaultToolkit().createImage("resources/application.png"));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(150);
		splitPane.setLeftComponent(new JScrollPane(tree));
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		tabbedPane.addTab("Documentation", new JScrollPane(docArea));
		tabbedPane.addTab("Source", new JScrollPane(codeArea));
		//tabbedPane.addTab("Sandbox", new JScrollPane(sandboxArea));
		
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		panel.add(tabbedPane, BorderLayout.CENTER);
		splitPane.setRightComponent(panel);
		
		tree.setModel(new BrowserTreeModel());
		tree.setCellRenderer(new BrowseTreeModelCellRenderer());
		tree.getSelectionModel().addTreeSelectionListener(new BrowseTreeSelectionListener());
		setBounds(100,100,600,400);
		
		toolbar = new JToolBar();
		toolbar.setRollover(true);
		//toolbar.setFloatable(false);
		
		toolbar.add(new RefreshAction());
		toolbar.add(new PrintAction());
		toolbar.add(new AlwaysOnTopAction());
		toolbar.add(new OpenSelectedAction());
		toolbar.addSeparator();
		toolbar.add(new ShowHelpAction());
		
		getRootPane().setLayout(new BorderLayout());
		getRootPane().add(toolbar, BorderLayout.NORTH);
		getRootPane().add(splitPane, BorderLayout.CENTER);
		//getRootPane().setBorder(new EmptyBorder(8,8,8,8));
	}
	
	class OpenSelectedAction extends AbstractAction {
		
		public OpenSelectedAction() {
			super("Opens the selected folder/template", new ImageIcon("resources/templatesfolder.png"));
			putValue(Action.SHORT_DESCRIPTION, "Opens the selected folder/template");
		}

		public void actionPerformed(ActionEvent actionevent) {
			try {
				File file = ((File)tree.getSelectionPath().getLastPathComponent());
				String command = "";
				if (OperatingSystem.isWindows()) {
					if (file.isFile()) {
						command = "RunDll32.exe url.dll,FileProtocolHandler";
					}
					else {
						command = "explorer";
					}
				}
				else if (OperatingSystem.isLinux()) {
					command = "xdg-open";
				}
				Runtime.getRuntime().exec(command + " " + file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class RefreshAction extends AbstractAction {
		
		public RefreshAction() {
			super("RefreshAction", new ImageIcon("resources/refresh.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Refreshes the templates index");
		}

		public void actionPerformed(ActionEvent actionevent) {
			Generator.getInstance().refresh();
			refresh();
		}
		
	}
	
	public void refresh() {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				tree.updateUI();
			}
		});		
	}
	
	class PrintAction extends AbstractAction {
		
		public PrintAction() {
			super("PrintAction", new ImageIcon("resources/print.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Prints the documentation for the current template");
		}

		public void actionPerformed(ActionEvent actionevent) {
			try {
				docArea.print();
			} catch (PrinterException e) {
				e.printStackTrace();
			}	
		}
		
	}
	
	
	class AlwaysOnTopAction extends AbstractAction {
		
		public AlwaysOnTopAction() {
			super("AlwaysOnTopAction", new ImageIcon("resources/alwaysontop.gif"));
			putValue(AbstractAction.SHORT_DESCRIPTION, "Locks the window always on top");
		}

		public void actionPerformed(ActionEvent actionevent) {
			setAlwaysOnTop(!isAlwaysOnTop());
		}
		
	}
	
	class BrowseTreeSelectionListener implements TreeSelectionListener{

		public void valueChanged(TreeSelectionEvent treeselectionevent) {
			File file = (File) treeselectionevent.getPath().getLastPathComponent();
			if (file.isFile()) {
				try {
					EglDocument document = new EglDocument();
					codeArea.setDocument(document);
					codeArea.setText(FileUtil.getContents(file));
					//sandboxArea.setText(file.getName().split("\\.")[0] + ":");
					File docFile = new File(file.getAbsoluteFile() + ".txt");
					if (docFile.exists()) {
						DocumentationPage page = new DocumentationPage(docFile);
						File temp = page.process();
						
						//Profiler.INSTANCE.print();
						docArea.setPage(temp.toURI().toURL());
						temp.delete();
					}
					else {
						docArea.setPage(new File("resources/nodoc.html").toURI().toURL());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					docArea.setPage(new File("resources/empty.html").toURI().toURL());
				} catch (Exception e) {
					e.printStackTrace();
				}
				codeArea.setText("");
			}
		}
		
	}
	
	class BrowseTreeModelCellRenderer extends DefaultTreeCellRenderer {
		
		protected ImageIcon templateIcon = new ImageIcon("resources/template.png");
		protected ImageIcon openFolderIcon = new ImageIcon("resources/openfolder.png");
		protected ImageIcon closedFolderIcon = new ImageIcon("resources/closedfolder.png");
		
		public Component getTreeCellRendererComponent(JTree jtree, Object obj,
				boolean flag, boolean flag1, boolean expanded, int i, boolean flag3) {
			
			DefaultTreeCellRenderer c = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(jtree, obj, flag, flag1, expanded, i, flag3);
			File file = (File) obj;
			c.setText(file.getName().split("\\.")[0]);
			if ((file.isFile())) {
				c.setIcon(templateIcon);
			}
			else if (file.isDirectory()) {
				//if (expanded) {
					c.setIcon(openFolderIcon);
				//}
				//else {
				//	c.setIcon(closedFolderIcon);
				//}
			}
			return c;
			/*
			JLabel label = new JLabel();
			label.setOpaque(false);
			label.setText(((File)obj).getName());
			return label;*/
		}
		
	}
	
	class BrowserTreeModel implements TreeModel {

		public void addTreeModelListener(TreeModelListener treemodellistener) {
			
		}
		
		public ArrayList<File> getChildren(File file) {
			ArrayList<File> children = new ArrayList<File>();
			for (File child : file.listFiles()) {
				if (child.isDirectory()) {
					if (!child.getName().startsWith(".")) {
						children.add(child);
					}
				}
			}
			
			for (File child : file.listFiles()) {
				if (child.isFile()) {
					if (Generator.getInstance().supports(child)) {
					//if (child.getName().endsWith(".egl") || child.getName().endsWith("jmf") || child.getName().endsWith("vm")) {
						children.add(child);
					//}
					}
				}
			}
			return children;
		}
		
		public Object getChild(Object obj, int i) {
			return getChildren((File) obj).get(i);
		}

		public int getChildCount(Object obj) {
			return getChildren((File) obj).size();
		}

		public int getIndexOfChild(Object obj, Object obj1) {
			return 0;
		}

		public Object getRoot() {
			return new File("templates");
		}

		public boolean isLeaf(Object obj) {
			return !((File)obj).isDirectory();
		}

		public void removeTreeModelListener(TreeModelListener treemodellistener) {
			
		}

		public void valueForPathChanged(TreePath treepath, Object obj) {
			
		}
		
	}
	
}
 