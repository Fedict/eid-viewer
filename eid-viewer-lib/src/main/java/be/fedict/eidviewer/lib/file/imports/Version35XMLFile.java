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

package be.fedict.eidviewer.lib.file.imports;

import be.fedict.eid.applet.service.Address;
import be.fedict.eid.applet.service.DocumentType;
import be.fedict.eid.applet.service.Gender;
import be.fedict.eid.applet.service.Identity;
import be.fedict.eid.applet.service.SpecialStatus;
import be.fedict.eid.applet.service.impl.tlv.DataConvertorException;
import be.fedict.eid.applet.service.impl.tlv.DateOfBirthDataConvertor;
import be.fedict.eidviewer.lib.EidData;
import be.fedict.eidviewer.lib.X509Utilities;
import be.fedict.eidviewer.lib.file.helper.TextFormatHelper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Frank Marien
 */
public class Version35XMLFile extends DefaultHandler
{
    private static final Logger logger=Logger.getLogger(Version35XMLFile.class.getName());
    private static final String[] discreteValueLabels=new String[] {"card_type","type","name","surname","gender","date_of_birth","location_of_birth","nobility",
                                                                    "nationality","national_nr","special_status","logical_nr","chip_nr","date_begin","date_end",
                                                                    "issuing_municipality","street","zip","municipality","country","duplicata"};
    
    public static enum STAGE
    {
        NONE                ("none"),
        BIOGRAPHIC          ("biographic"),
        BIOMETRIC           ("biometric"),
        BIOMETRIC_PICTURE   ("picture"),
        CRYPTOGRAPHIC       ("cryptographic");
        
        private final String state;

        private STAGE(String state)
        {
            this.state = state;
        }

        public String getState()
        {
            return this.state;
        }

        @Override
        public String toString()
        {
            return this.state;
        }
    };
    
    
    private Map<String,String>  discreteValues;
    private byte[]              pictureData;
    private StringBuilder       accumulatedCData;
    private CertificateFactory  certificateFactory = null;
    private X509Certificate     rootCert = null;
    private X509Certificate     citizenCert = null;
    private X509Certificate     authenticationCert = null;
    private X509Certificate     signingCert = null;
    private X509Certificate     rrnCert = null;
    private EidData             eidData;
    private STAGE               stage;
    private String              certLabel;

    public static void load(File file, EidData eidData) throws CertificateException, IOException, FileNotFoundException, SAXException, ParseException, DataConvertorException
    {
        Version35XMLFile v35XMLFile=new Version35XMLFile(eidData);
                         v35XMLFile.load(file);
    }

    public Version35XMLFile(EidData eidData)
    {
        this.eidData=eidData;
        this.discreteValues=new HashMap<String,String>();
        for(int i=0;i<discreteValueLabels.length;i++)
            discreteValues.put(discreteValueLabels[i], null);
    }

    public void load(File file) throws CertificateException, FileNotFoundException, SAXException, IOException, ParseException, DataConvertorException
    {
        logger.fine("Loading Version 35X XML File");
        
        XMLReader reader = null;

        certificateFactory = CertificateFactory.getInstance("X.509");
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        FileInputStream fis = new FileInputStream(file);

        reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(this);
        reader.setErrorHandler(this);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        reader.parse(new InputSource(in));

        Identity identity=new Identity();
                 identity.cardDeliveryMunicipality=discreteValues.get("issuing_municipality");
                 identity.cardNumber=discreteValues.get("logical_nr");
                 GregorianCalendar  validityStartCalendar=new GregorianCalendar();
                                    validityStartCalendar.setTime(dateFormat.parse(discreteValues.get("date_begin")));
                 identity.cardValidityDateBegin=validityStartCalendar;
                 GregorianCalendar  validityEndCalendar=new GregorianCalendar();
                                    validityEndCalendar.setTime(dateFormat.parse(discreteValues.get("date_end")));
                 identity.cardValidityDateEnd=validityEndCalendar;
                 identity.chipNumber=discreteValues.get("chip_nr");
                 identity.dateOfBirth=(new DateOfBirthDataConvertor()).convert(discreteValues.get("date_of_birth").getBytes());
                 identity.documentType=DocumentType.toDocumentType(discreteValues.get("type").getBytes());
                 identity.duplicate=discreteValues.get("duplicata");
                 identity.gender=discreteValues.get("gender").equals("M")?Gender.MALE:Gender.FEMALE;
                 
                 TextFormatHelper.setFirstNamesFromString(identity, discreteValues.get("name"));
                 identity.name=discreteValues.get("surname");
                 
                 identity.nationalNumber=discreteValues.get("national_nr");
                 identity.nationality=discreteValues.get("nationality");
                 identity.nobleCondition=discreteValues.get("nobility");
                 identity.placeOfBirth=discreteValues.get("location_of_birth");
                 identity.specialStatus=SpecialStatus.toSpecialStatus(discreteValues.get("specialStatus"));
                 eidData.setIdentity(identity);
                 
         Address address=new Address();
                 address.municipality=discreteValues.get("municipality");
                 address.zip=discreteValues.get("zip");
                 address.streetAndNumber=discreteValues.get("street");
                 eidData.setAddress(address);
                 
                 eidData.setPhoto(pictureData);
                 
        X509Utilities.setCertificateChainsFromCertificates(eidData,rootCert, citizenCert, authenticationCert, signingCert, rrnCert);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        accumulatedCData.append(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException
    {
        logger.finest("XML Document Ends");
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        logger.log(Level.FINEST, "</{0}>", localName);

        if(stage==STAGE.BIOGRAPHIC)
        {
            String data=getCDATA().trim();
            if(discreteValues.containsKey(localName))
                discreteValues.put(localName, data);
            resetCDATA();
        }
        else if(stage==STAGE.BIOMETRIC_PICTURE)
        {
            String data=getCDATA().trim();
            if(localName.equalsIgnoreCase("data"))
                pictureData=Base64.decodeBase64(data);
            resetCDATA();
        } 
        else if(stage==STAGE.CRYPTOGRAPHIC)
        {
            if(localName.equalsIgnoreCase("label"))
            {
                certLabel=getCDATA().trim();
            }
            else if(localName.equalsIgnoreCase("data"))
            {
                if(certLabel.equals("RRN"))
                {
                    rrnCert=certificateFromBase64Data(getCDATA(),certLabel);
                }
                else if(certLabel.equals("Authentication"))
                {
                    authenticationCert=certificateFromBase64Data(getCDATA(),certLabel);
                }
                else if(certLabel.equals("Signature"))
                {
                    signingCert=certificateFromBase64Data(getCDATA(),certLabel);
                }
                else if(certLabel.equals("CA"))
                {
                   citizenCert=certificateFromBase64Data(getCDATA(),certLabel);
                }
                else if(certLabel.equals("Root"))
                {
                    rootCert=certificateFromBase64Data(getCDATA(),certLabel);
                }
            }
            resetCDATA();
        } 
    }


    @Override
    public void startDocument() throws SAXException
    {
        logger.finest("XML Document Starts");
        resetCDATA();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        logger.log(Level.FINEST, "<{0}>", localName);
        if(localName.equalsIgnoreCase(STAGE.BIOGRAPHIC.getState()))
            stage=STAGE.BIOGRAPHIC;
        else if(localName.equalsIgnoreCase(STAGE.BIOMETRIC.getState()))
            stage=STAGE.BIOMETRIC;
        else if(localName.equalsIgnoreCase(STAGE.BIOMETRIC_PICTURE.getState()))
            stage=STAGE.BIOMETRIC_PICTURE;
        else if(localName.equalsIgnoreCase(STAGE.CRYPTOGRAPHIC.getState()))
            stage=STAGE.CRYPTOGRAPHIC;
    }

    private void resetCDATA()
    {
        accumulatedCData = new StringBuilder(16);
    }

    private String getCDATA()
    {
        return accumulatedCData.toString();
    }
    
    
    private X509Certificate certificateFromBase64Data(String cdata, String label)
    {
        logger.log(Level.FINER, "Gathering {0} Certificate", label);
        try
        {
            return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(cdata.trim())));
        }
        catch (CertificateException ex)
        {
            logger.log(Level.SEVERE, "Failed to Convert " + label + " Certificate", ex);
            return null;
        }
    }
}
