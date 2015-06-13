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
import io.dimitris.minigen.util.OperatingSystem;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;



/**
 * JPopupMenu compatible TrayIcon based on Alexander Potochkin's JXTrayIcon
 * (http://weblogs.java.net/blog/alexfromsun/archive/2008/02/jtrayicon_updat.html)
 * but uses a JWindow instead of a JDialog to workaround some bugs on linux.
 *
 * @author Michael Bien
 */
public class JTrayIcon extends TrayIcon {

    private JPopupMenu menu;
    
    private Window window;
    private PopupMenuListener popupListener;
    
    private final static boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");

    public JTrayIcon(Image image) {
        super(image);
        init();
    }

    public JTrayIcon(Image image, String tooltip) {
        super(image, tooltip);
        init();
    }

    public JTrayIcon(Image image, String tooltip, PopupMenu popup) {
        super(image, tooltip, popup);
        init();
    }

    public JTrayIcon(Image image, String tooltip, JPopupMenu popup) {
        super(image, tooltip);
        init();
        setJPopupMenu(popup);
    }


    private final void init() {


        popupListener = new PopupMenuListener() {


            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//                System.out.println("popupMenuWillBecomeVisible");
            }


            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//                System.out.println("popupMenuWillBecomeInvisible");
                if(window != null) {
                    window.dispose();
                    window = null;
                }
            }


            public void popupMenuCanceled(PopupMenuEvent e) {
//                System.out.println("popupMenuCanceled");
                if(window != null) {
                    window.dispose();
                    window = null;
                }
            }
        };

        addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
//                System.out.println(e.getPoint());
                if (OperatingSystem.isLinux()) {
                	showJPopupMenu(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
//                System.out.println(e.getPoint());
                if (OperatingSystem.isWindows()) {
                	showJPopupMenu(e);
                }
            }

			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				//showJPopupMenu(e);
				
			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        });

    }

    private final void showJPopupMenu(MouseEvent e) {
        if(e.isPopupTrigger() && menu != null) {
            if (window == null) {

                if(IS_WINDOWS) {
                    window = new JDialog((Frame)null);
                    ((JDialog)window).setUndecorated(true);
                }else{
                    window = new JWindow((Frame)null);
                }
                window.setAlwaysOnTop(true);
                Dimension size = menu.getPreferredSize();

                Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
                if(e.getY() > centerPoint.getY())
                    window.setLocation(e.getX(), e.getY() - size.height);
                else
                    window.setLocation(e.getX(), e.getY());

                window.setVisible(true);
                
                menu.show(((RootPaneContainer)window).getContentPane(), 0, 0);

                // popup works only for focused windows
                window.toFront();

            }
        }
    }


    public final JPopupMenu getJPopupMenu() {
        return menu;
    }

    public final void setJPopupMenu(JPopupMenu menu) {
        if (this.menu != null) {
            this.menu.removePopupMenuListener(popupListener);
        }
        this.menu = menu;
        menu.addPopupMenuListener(popupListener);
    }

}