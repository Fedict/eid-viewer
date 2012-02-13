/*
 * eID Middleware Project.
 * Copyright (C) 2010-2012 FedICT.
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

import be.fedict.eidviewer.gui.ViewerPrefs;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Frank Marien
 */
public class AboutPanel extends JPanel
{
    private JLabel aboutCopyrightText;
    private JLabel jLabel2;
    private JLabel jLabel4;
    private JPanel jPanel2;
    
    public AboutPanel()
    {
        initComponents();
        initI18N();
    }

    private void initComponents()
	{
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new JPanel();
        jLabel2 = new JLabel();
        aboutCopyrightText = new JLabel();
        jLabel4 = new JLabel();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel2.setIcon(new ImageIcon(getClass().getResource("/be/fedict/eidviewer/gui/resources/icons/state_eidpresent.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jLabel2, gridBagConstraints);

        aboutCopyrightText.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        aboutCopyrightText.setText("Copyright (C) 2010 - 2011 Fedict");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 8;
        gridBagConstraints.ipady = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(aboutCopyrightText, gridBagConstraints);

        jLabel4.setMaximumSize(new java.awt.Dimension(16, 16));
        jLabel4.setMinimumSize(new java.awt.Dimension(16, 16));
        jLabel4.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel2.add(jLabel4, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }
    
    private void initI18N()
    {
        String aboutHTML=ResourceBundle.getBundle("be/fedict/eidviewer/gui/resources/AboutPanel").getString("about_html");
        aboutCopyrightText.setText(aboutHTML.replace("__FULLVERSION__", ViewerPrefs.getFullVersion()));
    }
}
