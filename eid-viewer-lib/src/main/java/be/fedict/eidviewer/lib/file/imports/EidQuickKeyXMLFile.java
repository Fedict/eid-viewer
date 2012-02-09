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
import be.fedict.eid.applet.service.Identity;
import be.fedict.eid.applet.service.impl.tlv.TlvParser;
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
public class EidQuickKeyXMLFile extends DefaultHandler
{
    private static final Logger logger=Logger.getLogger(EidQuickKeyXMLFile.class.getName());
    
    public static enum STAGE
    {
        NONE        ("none"),
        AUTHCERT    ("authenticationCertificate"),
        SIGNCERT    ("nonRepudiationCertificate"),
        CACERT      ("caCertificate"),
        ROOTCERT    ("rootCaCertificate"),
        RRNCERT     ("rrnCertificate"),
        IDFILE      ("identityFile"),
        ADDRFILE    ("addressFile"),
        PHOTOFILE   ("photofile");
        
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
    
    private StringBuilder       accumulatedCData;
    private CertificateFactory  certificateFactory = null;
    private X509Certificate     rootCert = null;
    private X509Certificate     citizenCert = null;
    private X509Certificate     authenticationCert = null;
    private X509Certificate     signingCert = null;
    private X509Certificate     rrnCert = null;
    private STAGE               stage;
    private EidData             eidData;

    public static void load(File file, EidData eidData) throws CertificateException, IOException, FileNotFoundException, SAXException
    {
        EidQuickKeyXMLFile v35XMLFile=new EidQuickKeyXMLFile(eidData);
                         v35XMLFile.load(file);
    }

    public EidQuickKeyXMLFile(EidData eidData)
    {
        this.eidData=eidData;
    }

    public void load(File file) throws CertificateException, FileNotFoundException, SAXException, IOException
    {
        logger.fine("Loading eID Quick Keys XML File");
        
        XMLReader reader = null;

        certificateFactory = CertificateFactory.getInstance("X.509");
        FileInputStream fis = new FileInputStream(file);

        reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(this);
        reader.setErrorHandler(this);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis));
        reader.parse(new InputSource(in));

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

        if (localName.equalsIgnoreCase("fileData"))
        {
            byte[] data=Base64.decodeBase64(getCDATA().trim());
            logger.finest("Base64 Data Decoded");
            
            switch (stage)
            {
                case IDFILE:
                {
                    logger.fine("Setting Identity");
                    Identity identity=TlvParser.parse(data, Identity.class);
                    TextFormatHelper.setFirstNamesFromStrings(identity, identity.getFirstName(), identity.getMiddleName());
                    eidData.setIdentity(identity);
                }
                break;

                case ADDRFILE:
                    logger.fine("Setting Address");
                    eidData.setAddress(TlvParser.parse(data, Address.class));
                    break;

                case PHOTOFILE:
                    logger.fine("Setting Photo");
                    eidData.setPhoto(data);
                    break;

                case AUTHCERT:
                    logger.finer("Gathering Authentication Certificate");
                    authenticationCert=certificateFromBase64Data(data,"Authentication");
                    break;

                case SIGNCERT:
                    logger.finer("Gathering Signing Certificate");
                    signingCert=certificateFromBase64Data(data,"Signature");
                break;

                case CACERT:
                    logger.finer("Gathering Citizen CA Certificate");
                    citizenCert=certificateFromBase64Data(data,"CA");
                break;

                case ROOTCERT:
                    logger.finer("Gathering Root Certificate");
                    rootCert=certificateFromBase64Data(data,"Root");
                break;

                case RRNCERT:
                    logger.finer("Gathering RRN Certificate");
                    rrnCert=certificateFromBase64Data(data,"RRN");
                break;

            }
        }
        else
        {
            stage = STAGE.NONE;
        }

        resetCDATA();
    }

    @Override
    public void startDocument() throws SAXException
    {
        logger.finest("XML Document Starts");
        resetCDATA();
        stage = STAGE.NONE;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        logger.log(Level.FINEST, "<{0}>", localName);

        if (localName.equalsIgnoreCase(STAGE.IDFILE.getState()))
        {
            stage = STAGE.IDFILE;
            logger.finest("Expecting Identity File Data");
        }
        else if (localName.equalsIgnoreCase(STAGE.ADDRFILE.getState()))
        {
            stage = STAGE.ADDRFILE;
            logger.finest("Expecting Address File Data");
        }
        else if (localName.equalsIgnoreCase(STAGE.PHOTOFILE.getState()))
        {
            stage = STAGE.PHOTOFILE;
            logger.finest("Expecting JPEG Photo Data");
        }
        else if (localName.equalsIgnoreCase(STAGE.AUTHCERT.getState()))
        {
            stage = STAGE.AUTHCERT;
            logger.finest("Expecting Authentication Certificate");
        }
        else if (localName.equalsIgnoreCase(STAGE.SIGNCERT.getState()))
        {
            stage = STAGE.SIGNCERT;
            logger.finest("Expecting Signing Certificate");
        }
        else if (localName.equalsIgnoreCase(STAGE.CACERT.getState()))
        {
            stage = STAGE.CACERT;
            logger.finest("Expecting Citizen CA Certificate");
        }
        else if (localName.equalsIgnoreCase(STAGE.ROOTCERT.getState()))
        {
            stage = STAGE.ROOTCERT;
            logger.finest("Expecting Belgian root Certificate");
        }
        else if (localName.equalsIgnoreCase(STAGE.RRNCERT.getState()))
        {
            stage = STAGE.RRNCERT;
            logger.finest("Expecting RRN Certificate");
        }
    }

    private void resetCDATA()
    {
        accumulatedCData = new StringBuilder(16);
    }

    public String getCDATA()
    {
        return accumulatedCData.toString();
    }
    
    private X509Certificate certificateFromBase64Data(byte[] data, String label)
    {
        logger.log(Level.FINER, "Gathering {0} Certificate", label);
        try
        {
            return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(data));
        }
        catch (CertificateException ex)
        {
            logger.log(Level.SEVERE, "Failed to Convert " + label + " Certificate", ex);
            return null;
        }
    }
}
