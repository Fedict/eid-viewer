/*
 * eID Middleware Project.
 * Copyright (C) 2010-2011 FedICT.
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

import be.fedict.eidviewer.lib.PCSCEidController;
import be.fedict.eid.applet.service.Identity;
import be.fedict.eidviewer.gui.DynamicLocale;
import be.fedict.eidviewer.gui.ViewerPrefs;
import be.fedict.eidviewer.gui.helper.ImageUtilities;
import be.fedict.eidviewer.lib.file.helper.TextFormatHelper;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Frank Marien
 */
public class CardPanel extends JPanel implements Observer, DynamicLocale
{
    private static final Logger logger = Logger.getLogger(CardPanel.class.getName());
    private ResourceBundle      bundle;
    
    private JLabel cardInfoBusyIcon;
    private JLabel cardNumber;
    private JLabel cardNumberLabel;
    private JButton changePinButton;
    private JLabel chipNumber;
    private JLabel chipNumberLabel;
    private JLabel placeOfIssue;
    private JLabel placeOfIssueLabel;
    private JLabel spacer;
    private JLabel spacer1;
    private JLabel validFrom;
    private JLabel validFromLabel;
    private JLabel validUntil;
    private JLabel validUntilLabel;
    private JButton verifyPinButton;
    
    private DateFormat          dateFormat;
    private PCSCEidController       eidController;

    public CardPanel()
    {
        initComponents();
        initI18N();
        fillCardInfo(null,false);
    }

    public CardPanel setEidController(PCSCEidController eidController)
    {
        logger.finest("Setting eidController");
        this.eidController = eidController;
        return this;
    }

    public void update(Observable o, Object o1)
    {
        if (eidController == null)
            return;

        logger.finest("Updating..");
        updateVisibleState();

        if (eidController.getState() == PCSCEidController.STATE.EID_PRESENT  || eidController.getState()==PCSCEidController.STATE.EID_YIELDED || eidController.getState()==PCSCEidController.STATE.FILE_LOADED)
        {
            if(eidController.hasIdentity())
            {
                logger.finest("Filling Out Card Data..");
                fillCardInfo(eidController.getIdentity(), false);
            }
            else
            {
                logger.finest("Clear Card Data But Loading..");
                fillCardInfo(null, true);
            }
        }
        else
        {
            logger.finest("Clear Card Data And Not Loading");
            fillCardInfo(null, false);
        }    
    }

    private void updateVisibleState()
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
               boolean enablePinActions=eidController.isReadyForCommand() && eidController.getState()==PCSCEidController.STATE.EID_PRESENT;
               verifyPinButton.setEnabled(enablePinActions);
               changePinButton.setEnabled(enablePinActions);
            }
        });
    }

    private void fillCardInfo(final Identity identity, final boolean loading)
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                cardInfoBusyIcon.setVisible(loading);
                if (identity != null)
                {
                    cardNumber.setText(TextFormatHelper.formatCardNumber(identity.getCardNumber()));
                    cardNumber.setEnabled(true);
                    cardNumberLabel.setEnabled(true);

                    placeOfIssue.setText(identity.getCardDeliveryMunicipality());
                    placeOfIssue.setEnabled(true);
                    placeOfIssueLabel.setEnabled(true);

                    chipNumber.setText(identity.getChipNumber());
                    chipNumber.setEnabled(true);
                    chipNumberLabel.setEnabled(true);

                    validFrom.setText(dateFormat.format(identity.getCardValidityDateBegin().getTime()));
                    validFrom.setEnabled(true);
                    validFromLabel.setEnabled(true);

                    validUntil.setText(dateFormat.format(identity.getCardValidityDateEnd().getTime()));
                    validUntil.setEnabled(true);
                    validUntilLabel.setEnabled(true);
                }
                else
                {
                    cardNumber.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    cardNumber.setEnabled(false);
                    cardNumberLabel.setEnabled(false);

                    placeOfIssue.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    placeOfIssue.setEnabled(false);
                    placeOfIssueLabel.setEnabled(false);

                    chipNumber.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    chipNumber.setEnabled(false);
                    chipNumberLabel.setEnabled(false);

                    validFrom.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    validFrom.setEnabled(false);
                    validFromLabel.setEnabled(false);

                    validUntil.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    validUntil.setEnabled(false);
                    validUntilLabel.setEnabled(false);
                }
            }
        });
    }

    private void initComponents()
	{
        java.awt.GridBagConstraints gridBagConstraints;

        cardNumberLabel = new JLabel();
        placeOfIssueLabel = new JLabel();
        chipNumberLabel = new JLabel();
        validFromLabel = new JLabel();
        cardNumber = new JLabel();
        placeOfIssue = new JLabel();
        chipNumber = new JLabel();
        validUntil = new JLabel();
        cardInfoBusyIcon = new JLabel();
        spacer = new JLabel();
        validUntilLabel = new JLabel();
        validFrom = new JLabel();
        spacer1 = new JLabel();
        changePinButton = new JButton();
        verifyPinButton = new JButton();

        setBorder(ImageUtilities.getEIDBorder());
        setLayout(new java.awt.GridBagLayout());

        cardNumberLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(cardNumberLabel, gridBagConstraints);

        placeOfIssueLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(placeOfIssueLabel, gridBagConstraints);

        chipNumberLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(chipNumberLabel, gridBagConstraints);

        validFromLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(validFromLabel, gridBagConstraints);

        cardNumber.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(cardNumber, gridBagConstraints);

        placeOfIssue.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(placeOfIssue, gridBagConstraints);

        chipNumber.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(chipNumber, gridBagConstraints);

        validUntil.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(validUntil, gridBagConstraints);

        cardInfoBusyIcon.setIcon(new ImageIcon(getClass().getResource("/be/fedict/eidviewer/gui/resources/busyicons/busy_anim_small.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(cardInfoBusyIcon, gridBagConstraints);

        spacer.setEnabled(false);
        spacer.setMaximumSize(new java.awt.Dimension(16, 16));
        spacer.setMinimumSize(new java.awt.Dimension(16, 16));
        spacer.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(spacer, gridBagConstraints);

        validUntilLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(validUntilLabel, gridBagConstraints);

        validFrom.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(validFrom, gridBagConstraints);

        spacer1.setEnabled(false);
        spacer1.setMaximumSize(new java.awt.Dimension(16, 16));
        spacer1.setMinimumSize(new java.awt.Dimension(16, 16));
        spacer1.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(spacer1, gridBagConstraints);

        changePinButton.setEnabled(false);
        changePinButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePinButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(changePinButton, gridBagConstraints);

        verifyPinButton.setEnabled(false);
        verifyPinButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyPinButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(verifyPinButton, gridBagConstraints);
    }
    
    private void initI18N()
	{
        Locale.setDefault(ViewerPrefs.getLocale());
        bundle = ResourceBundle.getBundle("be/fedict/eidviewer/gui/resources/CardPanel");
        dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
        cardNumberLabel.setText(bundle.getString("cardNumberLabel")); // NOI18N
        placeOfIssueLabel.setText(bundle.getString("placeOfIssueLabel")); // NOI18N
        chipNumberLabel.setText(bundle.getString("chipNumberLabel")); // NOI18N
        validFromLabel.setText(bundle.getString("validFromLabel")); // NOI18N
        validUntilLabel.setText(bundle.getString("validUntilLabel")); // NOI18N
        changePinButton.setText(bundle.getString("changePinButton")); // NOI18N
        verifyPinButton.setText(bundle.getString("verifyPinButton")); // NOI18N
    }

    private void changePinButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        eidController.changePin();
    }

    private void verifyPinButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        eidController.verifyPin();
    }

    public void setDynamicLocale(Locale locale)
    {
        initI18N();
        update(null,null);
    }


}
