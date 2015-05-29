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

package be.fedict.eidviewer.lib;

import be.fedict.eid.applet.Messages.MESSAGE_ID;
import be.fedict.eid.applet.Messages;
import be.fedict.eid.applet.View;
import be.fedict.eid.applet.sc.PcscEid;
import be.fedict.eid.applet.service.Address;
import be.fedict.eid.applet.service.Identity;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import be.fedict.eid.applet.service.impl.tlv.TlvParser;
import be.fedict.eidviewer.lib.file.helper.TextFormatHelper;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class PCSCEid
{
    private static final Logger             logger = Logger.getLogger(PCSCEid.class.getName());
    private final View                      view;
    private Messages                        messages;
    private ResourceBundle                  bundle;
    private final PcscEid                   pcscEidImpl;
    private Map<byte[], byte[]>             fileCache;
    private Map<byte[], X509Certificate>    certCache;

    public PCSCEid(View view, Locale locale)
    {
        this.view = view;
        initI18N(locale);
        pcscEidImpl = new PcscEid(view, messages);
        pcscEidImpl.addObserver(new Observer()
        {
            public void update(Observable o, Object o1)
            {
                logger.log(Level.FINEST, "update [{0},{1}]", new Object[]{o, o1});
            } 
        });   
       
        fileCache = new HashMap<byte[], byte[]>();
        certCache = new HashMap<byte[], X509Certificate>();
    }

    public List<String> getReaderList()
    {
        return pcscEidImpl.getReaderList();
    }

    public byte[] readFile(byte[] fileId) throws Exception
    {
        logger.finest("readFile");
        return pcscEidImpl.readFile(fileId);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public Address getAddress() throws Exception
    {
        logger.fine("getAddress");
        return TlvParser.parse(getFile(PcscEid.ADDRESS_FILE_ID), Address.class);
    }

    public Identity getIdentity() throws Exception
    {
        logger.fine("getIdentity");
        Identity identity=TlvParser.parse(getFile(PcscEid.IDENTITY_FILE_ID), Identity.class);
        TextFormatHelper.setFirstNamesFromStrings(identity, identity.getFirstName(), identity.getMiddleName());
        return identity;
    }

    public Image getPhoto() throws Exception
    {
        logger.fine("getPhoto");
        byte[] data = readFile(PcscEid.PHOTO_FILE_ID);
        return ImageIO.read(new ByteArrayInputStream(data));
    }

    public Image getPhotoImage() throws Exception
    {
        logger.fine("getPhotoImage");
        byte[] data = readFile(PcscEid.PHOTO_FILE_ID);
        return ImageIO.read(new ByteArrayInputStream(data));
    }

    public byte[] getPhotoJPEG() throws Exception
    {
        logger.fine("getPhotoJPEG");
        return readFile(PcscEid.PHOTO_FILE_ID);
    }

    public void close()
    {
        pcscEidImpl.close();
        clear();
    }

    public boolean isEidPresent() throws Exception
    {
    	return pcscEidImpl.isEidPresent();  
    }

    public boolean hasCardReader() throws Exception
    {
    	return pcscEidImpl.hasCardReader();
    }

    public void waitForCardReader() throws Exception
    {
    	pcscEidImpl.waitForCardReader();
    }

    public void waitForEidPresent() throws Exception
    {
    	pcscEidImpl.waitForEidPresent();  
    }

    public void removeCard() throws Exception
    {
    	pcscEidImpl.removeCard();
    	clear();
    }

    public boolean isCardStillPresent() throws Exception
    {
        if (pcscEidImpl.isCardStillPresent())
        {
            return true;
        }
        clear();
        return false;
    }

    public void changePin() throws Exception
    {
        pcscEidImpl.changePin();
    }

    public void changePin(boolean requireSecureReader) throws Exception
    {
        pcscEidImpl.changePin();
    }

    public void verifyPin(boolean requireSecureReader) throws Exception
    {
        byte[] dummyData=new byte[128];
        logger.fine("Logging Off To Make Sure PIN Cache is Cleared");
        pcscEidImpl.logoff();
        logger.fine("Signing 128 Zero Bytes to Trigger PIN Check");
        pcscEidImpl.signAuthn(dummyData);
        logger.fine("Logging Off To Clear PIN Cache");
        pcscEidImpl.logoff();
        logger.fine("PIN Check OK");
        JOptionPane.showMessageDialog(view.getParentComponent(),bundle.getString("pinVerifiedOKDialogMessage"),bundle.getString("pinVerifiedOKDialogTitle"), JOptionPane.INFORMATION_MESSAGE);
    }

    public void addObserver(Observer observer)
    {
        pcscEidImpl.addObserver(observer);
    }

    public void yieldExclusive(boolean yield) throws Exception
    {
        pcscEidImpl.yieldExclusive(yield);
    }
    
    public void beginExclusive() throws Exception
    {
    	pcscEidImpl.yieldExclusive(false);
    }
    
    public void endExclusive()
    {
    	try
    	{
    		pcscEidImpl.yieldExclusive(true);
    	}
	catch(Exception cex)
    	{
    		//
    	}
    }

    public boolean isIdentityTrusted()
    {
        logger.fine("isIdentityTrusted");
        try
        {
            logger.finest("isValidSignature");
            return X509Utilities.isValidSignature(getRRNCert(), getFile(PcscEid.IDENTITY_FILE_ID), getIdentitySignature());
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Identity Signature Validation Failed", ex);
            return false;
        }
    }

    public boolean isAddressTrusted()
    {
        logger.fine("isAddressTrusted");
        try
        {
            logger.finest("isValidSignature");
            return X509Utilities.isValidSignature(getRRNCert(), trimRight(getFile(PcscEid.ADDRESS_FILE_ID)), getIdentitySignature(), getAddressSignature());
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Address Signature Validation Failed", ex);
            return false;
        }
    }

    public List<X509Certificate> getRRNCertificateChain() throws Exception
    {
        List<X509Certificate> authnCertificateChain = new LinkedList<X509Certificate>();
        authnCertificateChain.add(getRRNCert());
        authnCertificateChain.add(getRootCACert());
        return authnCertificateChain;
    }
    
    public List<X509Certificate> getCCACertificateChain() throws Exception
    {
        List<X509Certificate> ccaCertificateChain = new LinkedList<X509Certificate>();
        ccaCertificateChain.add(getCitizenCACert());
        ccaCertificateChain.add(getRootCACert());
        return ccaCertificateChain;
    }

    public List<X509Certificate> getAuthnCertificateChain() throws Exception
    {
        if(getAuthCert()==null)     // some cards have no auth or signing certs
            return null;
        List<X509Certificate> authnCertificateChain = new LinkedList<X509Certificate>();
        authnCertificateChain.add(getAuthCert());
        authnCertificateChain.add(getCitizenCACert());
        authnCertificateChain.add(getRootCACert());
        return authnCertificateChain;
    }

    public List<X509Certificate> getSignCertificateChain() throws Exception
    {
        if(getSignCert()==null)     // some cards (e.g. Kids Cards) have no non-repudiation certs
            return null;
        List<X509Certificate> authnCertificateChain = new LinkedList<X509Certificate>();
        authnCertificateChain.add(getSignCert());
        authnCertificateChain.add(getCitizenCACert());
        authnCertificateChain.add(getRootCACert());
        return authnCertificateChain;
    }

    public X509Certificate getAuthCert() throws Exception
    {
        return getCertificate(PcscEid.AUTHN_CERT_FILE_ID);
    }

    public X509Certificate getSignCert() throws Exception
    {
        return getCertificate(PcscEid.SIGN_CERT_FILE_ID);
    }

    public X509Certificate getRRNCert() throws Exception
    {
        return getCertificate(PcscEid.RRN_CERT_FILE_ID);
    }

    public X509Certificate getRootCACert() throws Exception
    {
        return getCertificate(PcscEid.ROOT_CERT_FILE_ID);
    }

    public X509Certificate getCitizenCACert() throws Exception
    {
        return getCertificate(PcscEid.CA_CERT_FILE_ID);
    }

    public byte[] getIdentitySignature() throws Exception
    {
        return getFile(PcscEid.IDENTITY_SIGN_FILE_ID);
    }

    public byte[] getAddressSignature() throws Exception
    {
        return getFile(PcscEid.ADDRESS_SIGN_FILE_ID);
    }

    private X509Certificate getCertificate(byte[] fileID) throws Exception
    {
        X509Certificate certificate = certCache.get(fileID);
        if(certificate == null)
        {
            byte[] data = readFile(fileID);
            if(data[0]!=0)
            {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(data));
                certCache.put(fileID, certificate);
            }
        }
        return certificate;
    }

    private byte[] getFile(byte[] fileID) throws Exception
    {
        byte[] data = fileCache.get(fileID);
        if (data == null)
        {
            data = readFile(fileID);
            fileCache.put(fileID, data);
        }
        return data;
    }
    
    public PCSCEid setLocale(Locale locale)
    {
        initI18N(locale);
        pcscEidImpl.setMessages(messages);
        return this;
    }
    
    public String getMessageString(MESSAGE_ID mESSAGE_ID)
    {
        return messages.getMessage(mESSAGE_ID);
    }
    
    public void clear()
    {
        fileCache.clear();
        certCache.clear();
    }

    private byte[] trimRight(byte[] addressFile)
    {
        int idx;
        for (idx = 0; idx < addressFile.length; idx++)
        {
            if (addressFile[idx] == 0)
            {
                break;
            }
        }
        byte[] result = new byte[idx];
        System.arraycopy(addressFile, 0, result, 0, idx);
        return result;
    }
    
    private void initI18N(Locale locale)
    {
        Locale.setDefault(locale);
        messages = new Messages(locale);
        bundle = ResourceBundle.getBundle("be/fedict/eidviewer/lib/resources/PCSCEidImpl"); 
    }   
}
