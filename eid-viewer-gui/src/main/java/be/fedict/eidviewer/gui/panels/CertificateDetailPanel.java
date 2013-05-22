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

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Frank Marien
 */
public class CertificateDetailPanel extends JPanel
{
	private static final long	serialVersionUID	= -6542474253289312096L;
	private JTextArea 			certDetailText;
    
    public CertificateDetailPanel(String certDetail)
    {
        initComponents(certDetail);
    }

    private void initComponents(String certDetail)
	{
    	certDetailText = new JTextArea(50, 120);
    	JScrollPane scrollPane = new JScrollPane(certDetailText); 
    	certDetailText.setEditable(false);
    	certDetailText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    	certDetailText.setLineWrap(true);
    	certDetailText.setText(certDetail);
        setLayout(new java.awt.BorderLayout());
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        add(scrollPane, java.awt.BorderLayout.CENTER);
    }
}
