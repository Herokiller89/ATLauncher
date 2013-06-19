/**
 * Copyright 2013 by ATLauncher and Contributors
 *
 * ATLauncher is licensed under CC BY-NC-ND 3.0 which allows others you to
 * share this software with others as long as you credit us by linking to our
 * website at http://www.atlauncher.com. You also cannot modify the application
 * in any way or make commercial use of this software.
 *
 * Link to license: http://creativecommons.org/licenses/by-nc-nd/3.0/
 */
package com.atlauncher.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class LauncherConsole extends JFrame {

    // Size of initial window
    private final Dimension WINDOW_SIZE = new Dimension(600, 400);
    // Minimum size the window can be
    private final Dimension MINIMUM_SIZE = new Dimension(350, 200);
    private final BorderLayout LAYOUT_MANAGER = new BorderLayout();
    private final Color BASE_COLOR = new Color(40, 45, 50);

    private JEditorPane console;
    private ConsoleBottomBar bottomBar;
    private JPopupMenu contextMenu; // Right click menu

    public LauncherConsole() {
        setSize(WINDOW_SIZE);
        setTitle("ATLauncher Console");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setIconImage(Utils.getImage("/resources/Icon.png"));
        setMinimumSize(MINIMUM_SIZE);
        setLayout(LAYOUT_MANAGER);

        setupLookAndFeel(); // Setup the look and feel for the Console

        console = new JEditorPane("text/plain", "") {
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }
        };
        console.setEditable(false);
        console.setSelectionColor(Color.GRAY);

        setupContextMenu(); // Setup the right click menu

        bottomBar = new ConsoleBottomBar();

        add(new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

        // Make sure the size doesn't go below the minimum size
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension d = LauncherConsole.this.getSize();
                Dimension minD = LauncherConsole.this.getMinimumSize();
                if (d.width < minD.width) {
                    d.width = minD.width;
                }
                if (d.height < minD.height) {
                    d.height = minD.height;
                }
                LauncherConsole.this.setSize(d);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
                setVisible(false);
            }
        });
    }

    private void setupContextMenu() {
        contextMenu = new JPopupMenu();

        JMenuItem copy = new JMenuItem("Copy");
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringSelection text = new StringSelection(console.getSelectedText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(text, null);
            }
        });
        contextMenu.add(copy);

        console.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (console.getSelectedText() != null) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        contextMenu.show(console, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void setupLookAndFeel() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // if (OSUtils.isMac()) {
        // UIManager.getLookAndFeelDefaults().put("defaultFont", new
        // Font("SansSerif", Font.PLAIN, 11));
        // }

        UIManager.put("control", BASE_COLOR);
        UIManager.put("text", Color.WHITE);
        UIManager.put("nimbusBase", Color.BLACK);
        UIManager.put("nimbusFocus", BASE_COLOR);
        UIManager.put("nimbusBorder", BASE_COLOR);
        UIManager.put("nimbusLightBackground", BASE_COLOR);
        UIManager.put("info", BASE_COLOR);
    }

    /**
     * Logs text to the console window
     * 
     * @param text
     *            The text to show in the console
     */
    public void log(String text) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        if (console.getText().isEmpty()) {
            console.setText("[" + timestamp + "] " + text);
        } else {
            console.setText(console.getText() + "\n[" + timestamp + "] " + text);
        }
        console.setCaretPosition(console.getDocument().getLength());
    }

    /**
     * Returns a string with the text currently in the console
     * 
     * @return String Console Text
     */
    public String getLog() {
        return console.getText();
    }
}