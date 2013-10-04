/*
 * eID Middleware Project.
 * Copyright (C) 2010-2013 FedICT.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see
 * http://www.gnu.org/licenses/.
 */

package be.fedict.eidviewer.gui.panels;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import be.fedict.eidviewer.gui.ViewerPrefs;
import be.fedict.eidviewer.gui.helper.ImageUtilities;
import be.fedict.eidviewer.gui.helper.LogHelper;

/**
 * 
 * @author Frank Marien
 */
public class LogPanel extends JPanel {
    private static final long serialVersionUID = 2383624070620121028L;
    private static final Logger viewerLogger = Logger.getLogger("be.fedict");

    private JPanel bottomPanel;
    private JButton clearButton;
    private JScrollPane jScrollPane1;
    private JComboBox levelCombo;
    private JTextPane logTextPanel;

    private Document logDocument;
    private Map<Level, EnumMap<ATTR, AttributeSet>> attributes;
    private ComboBoxModel levelComboModel;
    private String lastMessage;
    private long repeatCount;
    private Handler logHandler;

    public LogPanel() {
	initComponents();
	logTextPanel.setEditable(false);

	initLevelAttributes();
	levelCombo.setSelectedItem(ViewerPrefs.getLogLevel());
	viewerLogger.setLevel(ViewerPrefs.getLogLevel());

	logDocument = logTextPanel.getDocument();

	levelCombo.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent actionEvent) {
		viewerLogger.setLevel((Level) levelCombo.getSelectedItem());
		ViewerPrefs.setLogLevel((Level) levelCombo.getSelectedItem());
		if (ViewerPrefs.getLogLevel().equals(Level.ALL)) {
		    LogHelper.logJavaSpecs(viewerLogger);
		}
	    }
	});

	clearButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		logTextPanel.setText("");
	    }
	});

	logHandler = new Handler() {
	    @Override
	    public void publish(LogRecord logRecord) {
		EnumMap<ATTR, AttributeSet> currentAttr = attributes
			.get(logRecord.getLevel());
		StringBuilder message = new StringBuilder();

		if (currentAttr == null)
		    currentAttr = attributes.get(Level.ALL);

		String className = logRecord.getSourceClassName();
		if (className == null)
		    className = logRecord.getLoggerName();

		String[] parts = className.split("\\.");

		if (parts != null && parts.length > 0) {
		    message.append('[');
		    message.append(parts[parts.length - 1]);
		    message.append("] ");
		}

		if (logRecord.getParameters() != null)
		    message.append(MessageFormat.format(logRecord.getMessage(),
			    logRecord.getParameters()));
		else
		    message.append(logRecord.getMessage());

		append(message.toString(), currentAttr.get(ATTR.BOLD));

		if (viewerLogger.getLevel() != Level.ALL)
		    return;

		Throwable throwable = logRecord.getThrown();

		if (throwable != null && throwable.getMessage() != null) {
		    append(throwable.getMessage(), currentAttr.get(ATTR.BOLD));
		    StackTraceElement[] trace = throwable.getStackTrace();
		    for (StackTraceElement ste : trace)
			append(ste.toString(), currentAttr.get(ATTR.REGULAR));
		}
	    }

	    @Override
	    public void flush() {
	    }

	    @Override
	    public void close() throws SecurityException {
	    }
	};
    }

    public LogPanel start() {
	viewerLogger.addHandler(logHandler);
	return this;
    }

    public LogPanel stop() {
	viewerLogger.removeHandler(logHandler);
	return this;
    }

    private void initLevelAttributes() {
	Level[] comboItems = new Level[7];
	SimpleAttributeSet sas;
	EnumMap<ATTR, AttributeSet> attribute;

	attributes = new HashMap<Level, EnumMap<ATTR, AttributeSet>>();
	attribute = new EnumMap<ATTR, AttributeSet>(ATTR.class);

	attribute = new EnumMap<ATTR, AttributeSet>(ATTR.class);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.red);
	StyleConstants.setFontSize(sas, 14);
	attribute.put(ATTR.REGULAR, sas);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.red);
	StyleConstants.setBold(sas, true);
	StyleConstants.setFontSize(sas, 14);
	attribute.put(ATTR.BOLD, sas);
	attributes.put(Level.SEVERE, attribute);
	comboItems[0] = Level.SEVERE;

	attribute = new EnumMap<ATTR, AttributeSet>(ATTR.class);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.orange);
	attribute.put(ATTR.REGULAR, sas);
	StyleConstants.setFontSize(sas, 13);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.orange);
	StyleConstants.setBold(sas, true);
	StyleConstants.setFontSize(sas, 13);
	attribute.put(ATTR.BOLD, sas);
	attributes.put(Level.WARNING, attribute);
	comboItems[1] = Level.WARNING;

	attribute = new EnumMap<ATTR, AttributeSet>(ATTR.class);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.black);
	StyleConstants.setFontSize(sas, 12);
	attribute.put(ATTR.REGULAR, sas);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.black);
	StyleConstants.setFontSize(sas, 12);
	StyleConstants.setBold(sas, true);
	attribute.put(ATTR.BOLD, sas);
	attributes.put(Level.INFO, attribute);
	comboItems[2] = Level.INFO;

	attribute = new EnumMap<ATTR, AttributeSet>(ATTR.class);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.blue.darker().darker());
	StyleConstants.setFontSize(sas, 11);
	attribute.put(ATTR.REGULAR, sas);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.blue.darker().darker());
	StyleConstants.setBold(sas, true);
	StyleConstants.setFontSize(sas, 11);
	attribute.put(ATTR.BOLD, sas);
	attributes.put(Level.FINE, attribute);
	comboItems[3] = Level.FINE;

	attribute = new EnumMap<ATTR, AttributeSet>(ATTR.class);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.blue.darker());
	StyleConstants.setFontSize(sas, 10);
	attribute.put(ATTR.REGULAR, sas);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.blue.darker());
	StyleConstants.setBold(sas, true);
	StyleConstants.setFontSize(sas, 10);
	attribute.put(ATTR.BOLD, sas);
	attributes.put(Level.FINER, attribute);
	comboItems[4] = Level.FINER;

	attribute = new EnumMap<ATTR, AttributeSet>(ATTR.class);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.blue);
	attribute.put(ATTR.REGULAR, sas);
	StyleConstants.setFontSize(sas, 9);
	sas = new SimpleAttributeSet();
	StyleConstants.setForeground(sas, Color.blue);
	StyleConstants.setBold(sas, true);
	StyleConstants.setFontSize(sas, 9);
	attribute.put(ATTR.BOLD, sas);
	attributes.put(Level.FINEST, attribute);
	comboItems[5] = Level.FINEST;
	comboItems[6] = Level.ALL;

	levelComboModel = new DefaultComboBoxModel(comboItems);
	levelCombo.setModel(levelComboModel);
    }

    private synchronized void append(String message, AttributeSet attributes) {
	try {
	    if (lastMessage != null && message.equals(lastMessage)) {
		if (++repeatCount % 1000 == 0) {
		    logDocument.insertString(logDocument.getLength(),
			    "(Last Message Repeated " + repeatCount
				    + " Times)\n", attributes);
		    scrollToBottom();
		}
	    } else {
		logDocument.insertString(logDocument.getLength(), message,
			attributes);
		logDocument.insertString(logDocument.getLength(), "\n", null);
		scrollToBottom();
		lastMessage = message;
		repeatCount = 0;
	    }
	} catch (BadLocationException ex) {
	    System.err.println("Logger Failed To Insert: "
		    + ex.getLocalizedMessage());
	}
    }

    private void scrollToBottom() {
	Rectangle visible = logTextPanel.getVisibleRect();
	visible.y = logTextPanel.getBounds().height - visible.height;
	logTextPanel.scrollRectToVisible(visible);
    }

    private void initComponents() {
	bottomPanel = new JPanel();
	levelCombo = new JComboBox();
	clearButton = new JButton();
	jScrollPane1 = new JScrollPane();
	logTextPanel = new JTextPane();

	setBorder(ImageUtilities.getEIDBorder());
	setLayout(new java.awt.BorderLayout());

	bottomPanel.setName("bottomPanel"); // NOI18N
	bottomPanel.setLayout(new java.awt.FlowLayout(
		java.awt.FlowLayout.CENTER, 32, 5));

	levelCombo.setMaximumRowCount(4);
	levelCombo.setModel(new DefaultComboBoxModel(new String[] {
		"Errors Only", "Errors,Warnings", "Errors,Warnings and Info",
		"Everything" }));
	levelCombo.setName("levelCombo"); // NOI18N
	bottomPanel.add(levelCombo);

	clearButton.setText("Clear Log");
	clearButton.setName("clearButton"); // NOI18N
	bottomPanel.add(clearButton);

	add(bottomPanel, java.awt.BorderLayout.PAGE_END);

	jScrollPane1.setName("jScrollPane1"); // NOI18N

	logTextPanel.setName("logTextPanel"); // NOI18N
	jScrollPane1.setViewportView(logTextPanel);

	add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }

    public static enum ATTR {
	REGULAR(0), BOLD(1);
	private final int attr;

	private ATTR(int attr) {
	    this.attr = attr;
	}

	public int getAttr() {
	    return attr;
	}
    };
}
