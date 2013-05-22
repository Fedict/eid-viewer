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

import be.fedict.eidviewer.gui.ViewerPrefs;
import be.fedict.eidviewer.gui.helper.PositiveIntegerDocument;
import be.fedict.eidviewer.gui.helper.ProxyUtils;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Proxy;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author Frank Marien
 */
public class PreferencesPanel extends JPanel
{
	private static final long	serialVersionUID	= 1985663938768950071L;

	private ResourceBundle bundle;
    
    private JPanel          multiplePrefsSectionsPanel,proxyPrefsPanel;
    private JTextField      httpProxyHost,httpProxyPort;
    private JLabel          httpProxyPortLabel;
    private JRadioButton    httpProxyDirectConnectionRadio,httpProxyUseSystemSettingsRadio,httpProxySpecificProxyRadio;
    private ButtonGroup     httpProxyGroup;
    private int             proxyType;

    public PreferencesPanel()
    {
        bundle = ResourceBundle.getBundle("be/fedict/eidviewer/gui/resources/PreferencesPanel");
        proxyType=ViewerPrefs.getProxyType();
        initComponents();
        initProxyPrefsPanel();
        fillProxyPrefs();
    }

    private void fillProxyPrefs()
    {
        proxyType=ViewerPrefs.getProxyType();
        
        if(ViewerPrefs.hasSystemProxy())
        {
            Proxy systemProxy=ProxyUtils.getSystemProxy();
            httpProxyUseSystemSettingsRadio.setText(bundle.getString("useSystemSettingsLabel") + " (" +  ProxyUtils.getHostName(systemProxy) + ":" + ProxyUtils.getPort(systemProxy) + ")");
            httpProxyUseSystemSettingsRadio.setEnabled(true);
        }
        else
        {
            httpProxyUseSystemSettingsRadio.setText(bundle.getString("useSystemSettingsLabel"));
            httpProxyUseSystemSettingsRadio.setEnabled(false);
        }
        
        httpProxyDirectConnectionRadio. setSelected(proxyType==ViewerPrefs.PROXY_DIRECT);
        httpProxyUseSystemSettingsRadio.setSelected(proxyType==ViewerPrefs.PROXY_SYSTEM);
        httpProxySpecificProxyRadio.    setSelected(proxyType==ViewerPrefs.PROXY_SPECIFIC);
        httpProxyHost.setText(ViewerPrefs.getSpecificProxyHost());
        httpProxyPort.setText(String.valueOf(ViewerPrefs.getSpecificProxyPort()));
        updateProxyComponentsEnabled();
    }

    public String getHttpProxyHost()
    {
        return httpProxyHost.getText();
    }

    public int getHttpProxyPort()
    {
        return Integer.parseInt(httpProxyPort.getText());
    }

    public int getHTTPProxyType()
    {   
        return proxyType;
    }
   
    private void initProxyPrefsPanel()
    {  
        httpProxyDirectConnectionRadio.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                proxyType=ViewerPrefs.PROXY_DIRECT;
                updateProxyComponentsEnabled();
            }
        });
        
        httpProxyUseSystemSettingsRadio.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                proxyType=ViewerPrefs.PROXY_SYSTEM;
                updateProxyComponentsEnabled();
            }
        });
        
        httpProxySpecificProxyRadio.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                proxyType=ViewerPrefs.PROXY_SPECIFIC;
                updateProxyComponentsEnabled();
            }
        });

        httpProxyPort.setDocument(new PositiveIntegerDocument(5));
    }

    private void updateProxyComponentsEnabled()
    {
        httpProxyHost.setEnabled(proxyType==ViewerPrefs.PROXY_SPECIFIC);
        httpProxyPortLabel.setEnabled(proxyType==ViewerPrefs.PROXY_SPECIFIC);
        httpProxyPort.setEnabled(proxyType==ViewerPrefs.PROXY_SPECIFIC);
    }

    private void initComponents()
	{
        java.awt.GridBagConstraints gridBagConstraints;

        multiplePrefsSectionsPanel = new JPanel();
        proxyPrefsPanel = new JPanel();
        
        httpProxyDirectConnectionRadio      =new JRadioButton();
        httpProxyUseSystemSettingsRadio     =new JRadioButton();
        httpProxySpecificProxyRadio         =new JRadioButton();
        
        httpProxyPortLabel = new JLabel();
     
        httpProxyHost = new JTextField();
        httpProxyPort = new JTextField();
        httpProxyGroup=new ButtonGroup();
        
        httpProxyGroup.add(httpProxyDirectConnectionRadio);
        httpProxyGroup.add(httpProxyUseSystemSettingsRadio);
        httpProxyGroup.add(httpProxySpecificProxyRadio);
       
        setLayout(new java.awt.BorderLayout());

        // increment first argument and add panels to add preferences sections
        multiplePrefsSectionsPanel.setLayout(new java.awt.GridLayout(1, 1));

        proxyPrefsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(bundle.getString("proxySettingsTitle")), BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        proxyPrefsPanel.setLayout(new java.awt.GridBagLayout());
        
        // direct connection
        httpProxyDirectConnectionRadio.setText(bundle.getString("directConnectionLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.gridwidth=GridBagConstraints.REMAINDER;
        proxyPrefsPanel.add(httpProxyDirectConnectionRadio, gridBagConstraints);
        
        // use system settings
        httpProxyUseSystemSettingsRadio.setText(bundle.getString("useSystemSettingsLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.gridwidth=GridBagConstraints.REMAINDER;
        proxyPrefsPanel.add(httpProxyUseSystemSettingsRadio, gridBagConstraints);
        
        // specific proxy
        httpProxySpecificProxyRadio.setText(bundle.getString("useLabel"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        //gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        proxyPrefsPanel.add(httpProxySpecificProxyRadio, gridBagConstraints);
        
        httpProxyHost.setMinimumSize(new java.awt.Dimension(128, 18));
        httpProxyHost.setPreferredSize(new java.awt.Dimension(256, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        proxyPrefsPanel.add(httpProxyHost, gridBagConstraints);
        
        httpProxyPortLabel.setText(bundle.getString("portLabel")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 0);
        proxyPrefsPanel.add(httpProxyPortLabel, gridBagConstraints);

        httpProxyPort.setMinimumSize(new java.awt.Dimension(48, 18));
        httpProxyPort.setPreferredSize(new java.awt.Dimension(48, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 4;
        proxyPrefsPanel.add(httpProxyPort, gridBagConstraints);

        multiplePrefsSectionsPanel.add(proxyPrefsPanel);

        add(multiplePrefsSectionsPanel, java.awt.BorderLayout.CENTER);
    }
}
