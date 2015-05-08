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

import be.fedict.eidviewer.lib.PCSCEidController;
import be.fedict.eid.applet.service.Address;
import be.fedict.eid.applet.service.Gender;
import be.fedict.eid.applet.service.Identity;
import be.fedict.eid.applet.service.SpecialStatus;
import be.fedict.eidviewer.gui.BelgianEidViewer;
import be.fedict.eidviewer.gui.DynamicLocale;
import be.fedict.eidviewer.gui.ViewerPrefs;
import be.fedict.eidviewer.gui.helper.ImageUtilities;
import be.fedict.eidviewer.lib.file.helper.TextFormatHelper;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

/**
 *
 * @author Frank Marien
 */
public class IdentityPanel extends JPanel implements Observer, DynamicLocale
{
	private static final long	serialVersionUID	= 4356064467571081978L;
	private static final Logger logger = Logger.getLogger(IdentityPanel.class.getName());
    private ResourceBundle      bundle;
    
    private JLabel addressBusyIcon;
    private JLabel addressTrustedIcon;
    private JLabel dateOfBirth;
    private JLabel dateOfBirthLabel;
    private JLabel givenNames;
    private JLabel givenNamesLabel;
    private JSeparator idAddressSeparator;
    private JSeparator idAddressSeparator1;
    private JLabel identityBusyIcon;
    private JLabel identityTrustedIcon;
    private JLabel municipality;
    private JLabel municipalityLabel;
    private JLabel name;
    private JLabel nameLabel;
    private JLabel nationalNumber;
    private JLabel nationalNumberLabel;
    private JLabel nationality;
    private JLabel nationalityLabel;
    // JLabel doesn't support being a source for drag-and-drop operations, so make it a JList instead.
    private JList<Icon> photo;
    private DefaultListModel<Icon> photoModel;
    private JLabel placeOfBirth;
    private JLabel placeOfBirthLabel;
    private JLabel postalCode;
    private JLabel postalCodeLabel;
    private JLabel sex;
    private JLabel sexLabel;
    private JLabel spacer1;
    private JLabel specialStatus;
    private JLabel specialStatusLabel;
    private JLabel street;
    private JLabel streetLabel;
    private JLabel title;
    private JLabel titleLabel;
    private JLabel type;
    
    private DateFormat          dateFormat;
    private ImageIcon           largeBusyIcon;
    private PCSCEidController       eidController;

    public IdentityPanel()
    {
        initComponents();
        initI18N();
        initIcons();
        clearIdentity();
        identityBusyIcon.setVisible     (false);
        identityTrustedIcon.setVisible  (false);
        addressTrustedIcon.setVisible   (false);
    }

    public IdentityPanel setEidController(PCSCEidController eidController)
    {
        this.eidController = eidController;
        return this;
    }

    public void update(Observable o, Object o1)
    {
        logger.finest("Update..");

        if (eidController.getState() == PCSCEidController.STATE.EID_PRESENT || eidController.getState() == PCSCEidController.STATE.EID_YIELDED || eidController.getState() == PCSCEidController.STATE.FILE_LOADED)
        {
            if (eidController.hasIdentity())
            {
                logger.finest("Filling out Identity Data");
                fillIdentity(eidController.getIdentity(), false);
            }
            else
            {
                logger.finest("No Identity Data But Loading");
                fillIdentity(null, true);
            }

            if (eidController.hasAddress())
            {
                logger.finest("Filling Address Data");
                fillAddress(eidController.getAddress(), false);
            }
            else
            {
                logger.finest("No Identity Data But Loading");
                fillAddress(null, true);
            }

            if (eidController.hasPhoto())
            {
                logger.finest("Filling Picture");
                
                try
                {
                    fillPhoto(eidController.getPhotoImage(), false);
                }
                catch (IOException ex)
                {
                    logger.log(Level.SEVERE, "Photo Conversion from JPEG Failed", ex);
                }
            }
            else
            {
                logger.finest("No Picture But Loading");
                fillPhoto(null, true);
            }
        }
        else
        {
            logger.finest("Clearing all data because source is not available");
            clearIdentity();
        }
    }

    private void clearIdentity()
    {
        fillIdentity                    (null, false);
        fillAddress                     (null, false);
        fillPhoto                       (null, false);
    }

    private void fillIdentity(final Identity identity, final boolean loading)
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                identityBusyIcon.setVisible     (loading);
                identityTrustedIcon.setVisible  (eidController!=null && (!eidController.isIdentityTrusted()) && eidController.isIdentityValidated() && eidController.hasRRNCertChain() && eidController.getRRNCertChain().isTrusted());
                addressTrustedIcon.setVisible   (eidController!=null && (!eidController.isAddressTrusted()) && eidController.isAddressValidated() && eidController.hasRRNCertChain() && eidController.getRRNCertChain().isTrusted());


                if(identity != null)
                {
                	String specialOrganisation=TextFormatHelper.getSpecialOrganisationString(bundle, identity.getSpecialOrganisation());
                	if(specialOrganisation!=null && !specialOrganisation.isEmpty())
                		type.setText(bundle.getString("type_" + identity.getDocumentType().toString()) + " (" + specialOrganisation + ")");
                	else
                		type.setText(bundle.getString("type_" + identity.getDocumentType().toString()));
                    type.setEnabled(true);

                    name.setText(identity.getName());
                    name.setEnabled(true);
                    nameLabel.setEnabled(true);

                    givenNames.setText(identity.getFirstName() + (identity.getMiddleName()==null?"":" " + identity.getMiddleName()));
                    givenNames.setEnabled(true);
                    givenNamesLabel.setEnabled(true);

                    placeOfBirth.setText(identity.getPlaceOfBirth());
                    placeOfBirth.setEnabled(true);
                    placeOfBirthLabel.setEnabled(true);

                    dateOfBirth.setText(dateFormat.format(identity.getDateOfBirth().getTime()));
                    dateOfBirth.setEnabled(true);
                    dateOfBirthLabel.setEnabled(true);

                    sex.setText(identity.getGender() == Gender.FEMALE ? bundle.getString("genderFemale") : bundle.getString("genderMale"));
                    sex.setEnabled(true);
                    sexLabel.setEnabled(true);

                    nationality.setText(identity.getNationality());
                    nationality.setEnabled(true);
                    nationalityLabel.setEnabled(true);

                    nationalNumber.setText(TextFormatHelper.formatNationalNumber(identity.getNationalNumber()));
                    nationalNumber.setEnabled(true);
                    nationalNumberLabel.setEnabled(true);

                    if(identity.getNobleCondition()!=null && (!identity.getNobleCondition().isEmpty()))
                    {
                        title.setText(identity.getNobleCondition());
                        title.setEnabled(true);
                        titleLabel.setEnabled(true);
                    }
                    else
                    {
                        title.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                        title.setEnabled(false);
                        titleLabel.setEnabled(false);
                    }

                    
                    if(identity.getSpecialStatus()!=null && identity.getSpecialStatus()!=SpecialStatus.NO_STATUS)
                    {
                        specialStatus.setText(TextFormatHelper.getSpecialStatusString(bundle, identity.getSpecialStatus()));
                        specialStatus.setEnabled(true);
                        specialStatusLabel.setEnabled(true);
                    }
                    else
                    {
                        specialStatus.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                        specialStatus.setEnabled(false);
                        specialStatusLabel.setEnabled(false);
                    }
                }
                else
                {
                    type.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    type.setEnabled(false);

                    name.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    name.setEnabled(false);
                    nameLabel.setEnabled(false);

                    givenNames.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    givenNames.setEnabled(false);
                    givenNamesLabel.setEnabled(false);

                    placeOfBirth.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    placeOfBirth.setEnabled(false);
                    placeOfBirthLabel.setEnabled(false);

                    dateOfBirth.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    dateOfBirth.setEnabled(false);
                    dateOfBirthLabel.setEnabled(false);

                    sex.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    sex.setEnabled(false);
                    sexLabel.setEnabled(false);

                    nationality.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    nationality.setEnabled(false);
                    nationalityLabel.setEnabled(false);

                    nationalNumber.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    nationalNumber.setEnabled(false);
                    nationalNumberLabel.setEnabled(false);

                    title.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    title.setEnabled(false);
                    titleLabel.setEnabled(false);

                    specialStatus.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    specialStatus.setEnabled(false);
                    specialStatusLabel.setEnabled(false);
                }
            }
        });
    }

    private void fillAddress(final Address address, final boolean loading)
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                addressBusyIcon.setVisible(loading);
                if (address != null)
                {
                    street.setText(address.getStreetAndNumber());
                    street.setEnabled(true);
                    streetLabel.setEnabled(true);

                    postalCode.setText(address.getZip());
                    postalCode.setEnabled(true);
                    postalCodeLabel.setEnabled(true);

                    municipality.setText(address.getMunicipality());
                    municipality.setEnabled(true);
                    municipalityLabel.setEnabled(true);
                }
                else
                {
                    street.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    street.setEnabled(false);
                    streetLabel.setEnabled(false);

                    postalCode.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    postalCode.setEnabled(false);
                    postalCodeLabel.setEnabled(false);

                    municipality.setText(TextFormatHelper.UNKNOWN_VALUE_TEXT);
                    municipality.setEnabled(false);
                    municipalityLabel.setEnabled(false);
                }
            }
        });
    }

    private void fillPhoto(final Image image, final boolean loading)
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {

            public void run()
            {
            	Icon i;
                if (image != null)
                {
                	i = new ImageIcon(image);
                }
                else
                {
                	i = loading ? largeBusyIcon : null;
                }
            	try {
            		photoModel.set(0, i);
            	} catch (ArrayIndexOutOfBoundsException ex) {
            		// there is no photo currently, so add rather than replace
            		photoModel.add(0, i);
            	} catch (NullPointerException ex) {
            		// we probably don't have a photo currently
            		if(photoModel.size() > 0) {
            			photoModel.remove(0);
            		}
            	}
            }
        });
    }

    private void initComponents()
	{
        java.awt.GridBagConstraints gridBagConstraints;

        photoModel = new DefaultListModel<Icon>();
        photo = new JList<Icon>(photoModel);
        type = new JLabel();
        givenNamesLabel = new JLabel();
        placeOfBirthLabel = new JLabel();
        dateOfBirthLabel = new JLabel();
        nationalityLabel = new JLabel();
        nationalNumberLabel = new JLabel();
        sexLabel = new JLabel();
        titleLabel = new JLabel();
        specialStatusLabel = new JLabel();
        streetLabel = new JLabel();
        postalCodeLabel = new JLabel();
        municipalityLabel = new JLabel();
        idAddressSeparator = new JSeparator();
        name = new JLabel();
        givenNames = new JLabel();
        placeOfBirth = new JLabel();
        dateOfBirth = new JLabel();
        sex = new JLabel();
        nationalNumber = new JLabel();
        nationality = new JLabel();
        title = new JLabel();
        specialStatus = new JLabel();
        street = new JLabel();
        postalCode = new JLabel();
        municipality = new JLabel();
        addressBusyIcon = new JLabel();
        identityBusyIcon = new JLabel();
        spacer1 = new JLabel();
        nameLabel = new JLabel();
        idAddressSeparator1 = new JSeparator();
        addressTrustedIcon = new JLabel();
        identityTrustedIcon = new JLabel();

        setBorder(ImageUtilities.getEIDBorder());
        setLayout(new java.awt.GridBagLayout());

        photo.setBackground(new java.awt.Color(255, 255, 255));
        photo.setMaximumSize(new java.awt.Dimension(140, 200));
        photo.setMinimumSize(new java.awt.Dimension(140, 200));
        photo.setOpaque(true);
        photo.setPreferredSize(new java.awt.Dimension(140, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 13;
        gridBagConstraints.insets = new java.awt.Insets(23, 8, 23, 29);
        add(photo, gridBagConstraints);

        type.setHorizontalAlignment(SwingConstants.CENTER);
        type.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(type, gridBagConstraints);

        givenNamesLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(givenNamesLabel, gridBagConstraints);

        placeOfBirthLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(placeOfBirthLabel, gridBagConstraints);

        dateOfBirthLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(dateOfBirthLabel, gridBagConstraints);

        nationalityLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(nationalityLabel, gridBagConstraints);

        nationalNumberLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(nationalNumberLabel, gridBagConstraints);

        sexLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(sexLabel, gridBagConstraints);

        titleLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(titleLabel, gridBagConstraints);

        specialStatusLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(specialStatusLabel, gridBagConstraints);

        streetLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(streetLabel, gridBagConstraints);

        postalCodeLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(postalCodeLabel, gridBagConstraints);

        municipalityLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(municipalityLabel, gridBagConstraints);

        idAddressSeparator.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 7, 0);
        add(idAddressSeparator, gridBagConstraints);

        name.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(name, gridBagConstraints);

        givenNames.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(givenNames, gridBagConstraints);

        placeOfBirth.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(placeOfBirth, gridBagConstraints);

        dateOfBirth.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(dateOfBirth, gridBagConstraints);

        sex.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(sex, gridBagConstraints);

        nationalNumber.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(nationalNumber, gridBagConstraints);

        nationality.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(nationality, gridBagConstraints);

        title.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(title, gridBagConstraints);

        specialStatus.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(specialStatus, gridBagConstraints);

        street.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(street, gridBagConstraints);

        postalCode.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(postalCode, gridBagConstraints);

        municipality.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(municipality, gridBagConstraints);

        addressBusyIcon.setIcon(new ImageIcon(getClass().getResource("/be/fedict/eidviewer/gui/resources/busyicons/busy_anim_small.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(addressBusyIcon, gridBagConstraints);

        identityBusyIcon.setIcon(new ImageIcon(getClass().getResource("/be/fedict/eidviewer/gui/resources/busyicons/busy_anim_small.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(identityBusyIcon, gridBagConstraints);

        spacer1.setEnabled(false);
        spacer1.setMaximumSize(new java.awt.Dimension(16, 16));
        spacer1.setMinimumSize(new java.awt.Dimension(16, 16));
        spacer1.setPreferredSize(new java.awt.Dimension(16, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(spacer1, gridBagConstraints);

        nameLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(nameLabel, gridBagConstraints);

        idAddressSeparator1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 7, 0);
        add(idAddressSeparator1, gridBagConstraints);

        addressTrustedIcon.setIcon(new ImageIcon(getClass().getResource("/be/fedict/eidviewer/gui/resources/icons/warning_small.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(addressTrustedIcon, gridBagConstraints);

        identityTrustedIcon.setIcon(new ImageIcon(getClass().getResource("/be/fedict/eidviewer/gui/resources/icons/warning_small.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(identityTrustedIcon, gridBagConstraints);
    }

    @Override
    public void setTransferHandler(TransferHandler hndlr) {
    	photo.setTransferHandler(hndlr);
    	photo.setDragEnabled(hndlr != null);
    }

    private void initI18N()
	{
        Locale.setDefault(ViewerPrefs.getLocale());
        bundle          = ResourceBundle.getBundle("be/fedict/eidviewer/gui/resources/IdentityPanel");
        dateFormat      = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());        
        givenNamesLabel.setText(bundle.getString("givenNamesLabel")); // NOI18N     
        placeOfBirthLabel.setText(bundle.getString("placeOfBirthLabel")); // NOI18N    
        dateOfBirthLabel.setText(bundle.getString("dateOfBirthLabel")); // NOI18N    
        nationalityLabel.setText(bundle.getString("nationalityLabel")); // NOI18N
        nationalNumberLabel.setText(bundle.getString("nationalNumberLabel")); // NOI18N       
        sexLabel.setText(bundle.getString("sexLabel")); // NOI18N
        titleLabel.setText(bundle.getString("titleLabel")); // NOI18N
        specialStatusLabel.setText(bundle.getString("specialStatusLabel")); // NOI18N
        streetLabel.setText(bundle.getString("streetLabel")); // NOI18N
        postalCodeLabel.setText(bundle.getString("postalCodeLabel")); // NOI18N
        municipalityLabel.setText(bundle.getString("municipalityLabel")); // NOI18N      
        nameLabel.setText(bundle.getString("nameLabel")); // NOI18N
    }

    private void initIcons()
    {
        largeBusyIcon   = new ImageIcon(Toolkit.getDefaultToolkit().getImage(BelgianEidViewer.class.getResource("resources/busyicons/busy_anim_large.gif")));
    }

    public void setDynamicLocale(Locale locale)
    {
        initI18N();
        update(null,null);
    }
}
