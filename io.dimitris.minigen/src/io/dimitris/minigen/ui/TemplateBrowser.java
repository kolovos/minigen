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
import io.dimitris.minigen.util.AppleScriptEngine;
import io.dimitris.minigen.util.FileUtil;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.util.ArrayList;

import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.explodingpixels.macwidgets.LabeledComponentGroup;
import com.explodingpixels.macwidgets.MacButtonFactory;
import com.explodingpixels.macwidgets.MacUtils;
import com.explodingpixels.macwidgets.UnifiedToolBar;
import com.explodingpixels.widgets.plaf.EPTabbedPaneUI;

public class TemplateBrowser extends JFrame {

	protected JTree tree;
	protected JTabbedPane tabbedPane;
	protected JEditorPane docArea;
	protected JEditorPane codeArea;
	//protected JEditorPane sandboxArea;
	protected UnifiedToolBar toolbar;
	
	public static void main(String[] args) {
		new TemplateBrowser().show();
	}
	
	public TemplateBrowser() {
		
		System.setProperty(
				   "Quaqua.tabLayoutPolicy", "wrap"
				);
		
		try {
			UIManager.setLookAndFeel(
			        ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel()
			    );
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MacUtils.makeWindowLeopardStyle(getRootPane());
		
//		// Create a new mac button based on the JButton.
//		AbstractButton macButton = MacButtonFactory.makeUnifiedToolBarButton(button);
//		// Add the button to the left side of the toolbar.
//		toolBar.addComponentToLeft(button);
//
//		// This is so that the window can be dragged from anywhere on the toolbar.
//		// This is optional, but will make your Java application feel more like an OSX app.
//		//macMainMenu.installWindowDraggerOnWindow(MainFrame);
//
//		// Add the toolbar to the frame.
//		this.add(toolBar.getComponent(), BorderLayout.NORTH);
		
		docArea = new JEditorPane();
		docArea.setEditable(false);
		//docArea.setEditorKit(new HTMLEditorKit());
		//docArea.setEditorKit(new StyledEditorKit());
		
		//sandboxArea = new JEditorPane();
		//sandboxArea.setEditorKit(new NumberedEditorKit());
		
		codeArea = new JEditorPane();
		codeArea.setEditable(false);
		codeArea.setEditorKit(new NumberedEditorKit());
		
		tree = new JTree();
		tabbedPane = new JTabbedPane();
		tabbedPane.setUI(new EPTabbedPaneUI());
		setTitle("MiniGen - Template Browser");
		setIconImage(Toolkit.getDefaultToolkit().createImage(new File("resources/application.png").getAbsolutePath()));
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.putClientProperty("Quaqua.SplitPane.style","bar");
		splitPane.setDividerSize(1);
		splitPane.setDividerLocation(150);
		splitPane.setLeftComponent(createJScrollPane(tree));
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		tabbedPane.addTab("Documentation", createJScrollPane(docArea));
		tabbedPane.addTab("Source", createJScrollPane(codeArea));
		//tabbedPane.addTab("Sandbox", new JScrollPane(sandboxArea));
		
		//tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		panel.add(createJScrollPane(docArea), BorderLayout.CENTER);
		splitPane.setRightComponent(panel);
		
		tree.setModel(new BrowserTreeModel());
		tree.setCellRenderer(new BrowseTreeModelCellRenderer());
		tree.getSelectionModel().addTreeSelectionListener(new BrowseTreeSelectionListener());
		setBounds(100,100,600,400);
		tree.setRootVisible(false);
		
		
		toolbar = new UnifiedToolBar();
		//toolbar.disableBackgroundPainter();
		toolbar.installWindowDraggerOnWindow(this);
		//toolbar.setRollover(true);
		//toolbar.setFloatable(false);
		
		/*
		JToggleButton leftButton = new JToggleButton(new ImageIcon("resources/text-html.png"));
		 leftButton.putClientProperty("JButton.buttonType", "segmentedTextured");
		 leftButton.putClientProperty("JButton.segmentPosition", "first");
		 leftButton.setMinimumSize(new Dimension(50, 50));
		 leftButton.setPreferredSize(new Dimension(50, 50));
		 

		 JToggleButton rightButton = new JToggleButton(new ImageIcon("resources/format-justify-left.png"));
		 rightButton.putClientProperty("JButton.buttonType", "segmentedTextured");
		 rightButton.putClientProperty("JButton.segmentPosition", "last");
		 rightButton.setMinimumSize(new Dimension(50, 50));
		 rightButton.setPreferredSize(new Dimension(50, 50));

		 LabeledComponentGroup group = new LabeledComponentGroup("Group", leftButton, rightButton);
		 */
		
		toolbar.addComponentToLeft(getUnifiedToolBarButton(new RefreshAction()));
		toolbar.addComponentToLeft(getUnifiedToolBarButton(new RevealSelectedAction()));
		toolbar.addComponentToLeft(getUnifiedToolBarButton(new PrintAction()));
		//toolbar.addComponentToLeft(getUnifiedToolBarButton(new AlwaysOnTopAction()));
		toolbar.addComponentToRight(getUnifiedToolBarButton(new ShowHelpAction()));
//		JCheckBox cb = new JCheckBox();
//		cb.setPreferredSize(new Dimension(30, 30));
//		cb.setMinimumSize(new Dimension(30, 30));
//		cb.setMaximumSize(new Dimension(30, 30));
//		toolbar.addComponentToLeft(new LabeledComponentGroup("Source", cb).getComponent());
		
		/*
		final JTextField searchField = new JTextField(10);
		searchField.putClientProperty("JTextField.variant", "search");
		searchField.putClientProperty("JTextField.Search.CancelAction", new AbstractAction("ClearSearch")
		{
			public void actionPerformed(ActionEvent event)
			{
				searchField.setText("");
			}
		});

		//toolbar.addComponentToLeft(group.getComponent());
		toolbar.addComponentToRight(new LabeledComponentGroup("Search", searchField).getComponent());
		*/
		
		getRootPane().setLayout(new BorderLayout());
		getRootPane().add(toolbar.getComponent(), BorderLayout.NORTH);
		getRootPane().add(splitPane, BorderLayout.CENTER);
		getRootPane().putClientProperty("apple.awt.brushMetalLook", true);
		//tree.setShowsRootHandles(false);
		/*
		getRootPane().setBorder(new EmptyBorder(0,0,0,0));
		tree.setBorder(new EmptyBorder(0,0,0,0));
		tabbedPane.setBorder(new EmptyBorder(0,0,0,0));
		codeArea.setBorder(new EmptyBorder(0,0,0,0));
		panel.setBorder(new EmptyBorder(0,0,0,0));
		splitPane.setBorder(new EmptyBorder(0,0,0,0));
		tree.setBorder(BorderFactory.createEmptyBorder());*/
		tree.putClientProperty(
				   "Quaqua.Tree.style", "sourceList"
				);
	} 
	
	protected AbstractButton getUnifiedToolBarButton(AbstractAction action) {
		JButton button = new JButton(action);
		button.setActionCommand("pressed");
		Dimension d = new Dimension(48,48);
		button.setPreferredSize(d);
		button.setMinimumSize(d);
		button.setMaximumSize(d);
		button.putClientProperty("JButton.buttonType", "textured");
		return MacButtonFactory.makeUnifiedToolBarButton(button);
	}
	
	protected JScrollPane createJScrollPane(JComponent component) {
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		//IAppWidgetFactory.makeIAppScrollPane(scrollPane);
		return scrollPane;
	}
	
	class RevealSelectedAction extends AbstractAction {
		
		public RevealSelectedAction() {
			super("Reveal", new ImageIcon(new File("resources/reveal.png").getAbsolutePath()));
			putValue(Action.SHORT_DESCRIPTION, "Reveals the selected folder/template in Finder");
		}

		public void actionPerformed(ActionEvent actionevent) {
			
			File file = null;
			
			if (tree.getSelectionPath() == null) file = new File("templates").getAbsoluteFile();
			else file = ((File)tree.getSelectionPath().getLastPathComponent());
			
			try {
				AppleScriptEngine.getInstance().eval(
					"set thePath to POSIX file \"" + file.getAbsolutePath() + "\"", 
					"tell application \"Finder\"",
					"	reveal thePath",
					"	activate",
					"end tell");
			} catch (ScriptException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	class RefreshAction extends AbstractAction {
		
		public RefreshAction() {
			super("Refresh", new ImageIcon(new File("resources/refresh.png").getAbsolutePath()));
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
			super("Print", new ImageIcon(new File("resources/print.png").getAbsolutePath()));
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
			super("Stay on top", new ImageIcon(new File("resources/alwaysontop.png").getAbsolutePath()));
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
						//temp.delete();
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
		
		protected ImageIcon templateIcon = new ImageIcon(new File("resources/emblem-system.png").getAbsolutePath());
		protected ImageIcon openFolderIcon = new ImageIcon(new File("resources/openfolder.png").getAbsolutePath());
		protected ImageIcon closedFolderIcon = new ImageIcon(new File("resources/closedfolder.png").getAbsolutePath());
		
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
						children.add(child);
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
			return new File("templates").getAbsoluteFile();
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
